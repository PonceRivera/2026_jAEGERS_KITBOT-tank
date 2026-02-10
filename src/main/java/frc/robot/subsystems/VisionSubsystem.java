package frc.robot.subsystems;

import edu.wpi.first.apriltag.AprilTagDetection;
import edu.wpi.first.apriltag.AprilTagDetector;
import edu.wpi.first.apriltag.AprilTagPoseEstimator;
import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.cscore.CvSink;
import edu.wpi.first.cscore.CvSource;
import edu.wpi.first.cscore.UsbCamera;
import edu.wpi.first.math.geometry.Transform3d;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import static frc.robot.Constants.VisionConstants.*;

/**
 * Subsistema de visión que detecta AprilTags usando la cámara USB (Microsoft
 * LifeCam)
 * directamente en el RoboRIO. Calcula la distancia en metros a cada tag
 * detectado.
 *
 * La detección corre en un hilo en segundo plano para no bloquear el loop
 * principal.
 */
public class VisionSubsystem extends SubsystemBase {

    // Resultados compartidos con el hilo principal (volatile para seguridad)
    private volatile boolean hasTarget = false;
    private volatile int closestTagId = -1;
    private volatile double distanceMeters = 0.0;
    private volatile int tagCount = 0;
    private volatile String visionStatus = "Iniciando...";

    private Thread visionThread;

    public VisionSubsystem() {
        visionThread = new Thread(this::visionLoop);
        visionThread.setDaemon(true);
        visionThread.setName("AprilTagVision");
        visionThread.start();
    }

    /**
     * Hilo de visión que captura frames, detecta AprilTags y calcula distancias.
     * Corre continuamente en segundo plano.
     */
    private void visionLoop() {
        try {
            visionStatus = "Configurando camara...";
            // Configurar cámara
            UsbCamera camera = CameraServer.startAutomaticCapture(CAMERA_INDEX);
            camera.setResolution(CAMERA_WIDTH, CAMERA_HEIGHT);
            camera.setFPS(CAMERA_FPS);

            // CvSink para obtener frames de OpenCV
            CvSink cvSink = CameraServer.getVideo();

            // CvSource para enviar el video procesado al dashboard
            CvSource outputStream = CameraServer.putVideo("AprilTag Vision", CAMERA_WIDTH, CAMERA_HEIGHT);

            // Crear detector de AprilTags
            AprilTagDetector detector = new AprilTagDetector();
            detector.addFamily("tag36h11");

            // Configurar parámetros para rendimiento en hardware limitado
            AprilTagDetector.Config config = detector.getConfig();
            config.quadDecimate = 2.0f; // Reducir resolución para más velocidad
            config.quadSigma = 0.0f; // Sin blur gaussiano
            config.numThreads = 2; // Usar 2 hilos del RoboRIO
            config.decodeSharpening = 0.25;
            detector.setConfig(config);

            // Estimador de pose 3D
            AprilTagPoseEstimator.Config poseConfig = new AprilTagPoseEstimator.Config(
                    TAG_SIZE_METERS, CAMERA_FX, CAMERA_FY, CAMERA_CX, CAMERA_CY);
            AprilTagPoseEstimator poseEstimator = new AprilTagPoseEstimator(poseConfig);

            // Buffers de imagen
            Mat mat = new Mat();
            Mat grayMat = new Mat();

            // Colores para dibujar (BGR)
            Scalar greenColor = new Scalar(0, 255, 0);
            Scalar redColor = new Scalar(0, 0, 255);
            Scalar whiteColor = new Scalar(255, 255, 255);

            visionStatus = "Camara lista, buscando tags...";

            while (!Thread.interrupted()) {
                // Capturar frame
                long timestamp = cvSink.grabFrame(mat);
                if (timestamp == 0) {
                    visionStatus = "Error frame: " + cvSink.getError();
                    continue;
                }

                // Convertir a escala de grises para la detección
                Imgproc.cvtColor(mat, grayMat, Imgproc.COLOR_BGR2GRAY);

                // Detectar AprilTags
                AprilTagDetection[] detections = detector.detect(grayMat);

                // Variables locales para este frame
                boolean frameHasTarget = false;
                int frameClosestId = -1;
                double frameClosestDist = Double.MAX_VALUE;
                int frameTagCount = detections.length;

                for (AprilTagDetection detection : detections) {
                    frameHasTarget = true;

                    Transform3d pose = poseEstimator.estimate(detection);

                    double dist = Math.sqrt(
                            pose.getX() * pose.getX() +
                                    pose.getY() * pose.getY() +
                                    pose.getZ() * pose.getZ());

                    if (dist < frameClosestDist) {
                        frameClosestDist = dist;
                        frameClosestId = detection.getId();
                    }

                    for (int i = 0; i < 4; i++) {
                        int next = (i + 1) % 4;
                        Point p1 = new Point(detection.getCornerX(i), detection.getCornerY(i));
                        Point p2 = new Point(detection.getCornerX(next), detection.getCornerY(next));
                        Imgproc.line(mat, p1, p2, greenColor, 2);
                    }

                    // Dibujar punto central
                    Point center = new Point(detection.getCenterX(), detection.getCenterY());
                    Imgproc.circle(mat, center, 5, redColor, -1);

                    // Mostrar ID y distancia sobre el tag
                    String label = String.format("ID:%d  %.2fm", detection.getId(), dist);
                    Imgproc.putText(mat, label,
                            new Point(detection.getCenterX() - 30, detection.getCenterY() - 15),
                            Imgproc.FONT_HERSHEY_SIMPLEX, 0.5, whiteColor, 1);
                }

                // Actualizar variables compartidas (thread-safe con volatile)
                hasTarget = frameHasTarget;
                closestTagId = frameHasTarget ? frameClosestId : -1;
                distanceMeters = frameHasTarget ? frameClosestDist : 0.0;
                tagCount = frameTagCount;
                visionStatus = "OK - Tags: " + frameTagCount;

                outputStream.putFrame(mat);
            }

            detector.close();
        } catch (Exception e) {
            visionStatus = "ERROR: " + e.getMessage();
        }
    }

    /**
     * Publicar datos de visión al SmartDashboard cada ciclo.
     */
    @Override
    public void periodic() {
        SmartDashboard.putString("Vision/Status", visionStatus);
        SmartDashboard.putBoolean("Vision/HasTarget", hasTarget);
        SmartDashboard.putNumber("Vision/TagID", closestTagId);
        SmartDashboard.putNumber("Vision/DistanceMeters", distanceMeters);
        SmartDashboard.putNumber("Vision/TagCount", tagCount);
    }

    // ===== GETTERS para que otros subsistemas lean los datos =====

    /** ¿Se detectó al menos un AprilTag? */
    public boolean hasTarget() {
        return hasTarget;
    }

    /** ID del AprilTag más cercano, o -1 si no hay detección. */
    public int getClosestTagId() {
        return closestTagId;
    }

    /** Distancia en metros al AprilTag más cercano, o 0.0 si no hay detección. */
    public double getDistanceMeters() {
        return distanceMeters;
    }

    /** Cantidad de AprilTags detectados en el último frame. */
    public int getTagCount() {
        return tagCount;
    }
}

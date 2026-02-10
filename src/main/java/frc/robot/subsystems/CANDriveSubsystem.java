package frc.robot.subsystems;

import java.util.function.DoubleSupplier;

import com.revrobotics.RelativeEncoder;
import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.config.SparkBaseConfig.IdleMode;
import com.revrobotics.spark.config.SparkMaxConfig;
import com.revrobotics.spark.SparkBase.PersistMode;
import com.revrobotics.spark.SparkBase.ResetMode;

import choreo.trajectory.DifferentialSample;

import edu.wpi.first.math.controller.LTVUnicycleController;
import edu.wpi.first.math.geometry.*;
import edu.wpi.first.math.kinematics.*;
import edu.wpi.first.wpilibj.ADXRS450_Gyro;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.*;

import static frc.robot.Constants.DriveConstants.*;

public class CANDriveSubsystem extends SubsystemBase {

  private static final double MAX_SPEED_MPS = 3.0;

  private final SparkMax m_motor_neo1 = new SparkMax(LEFT_LEADER_ID, MotorType.kBrushless);
  private final SparkMax m_motor_neo2 = new SparkMax(LEFT_FOLLOWER_ID, MotorType.kBrushless);
  private final SparkMax m_motor_neo3 = new SparkMax(RIGHT_LEADER_ID, MotorType.kBrushless);
  private final SparkMax m_motor_neo4 = new SparkMax(RIGHT_FOLLOWER_ID, MotorType.kBrushless);

  private final RelativeEncoder leftEncoder = m_motor_neo1.getEncoder();
  private final RelativeEncoder rightEncoder = m_motor_neo3.getEncoder();

  private final ADXRS450_Gyro gyro = new ADXRS450_Gyro();

  private final DifferentialDriveKinematics kinematics = new DifferentialDriveKinematics(TRACK_WIDTH_METERS);

  private final DifferentialDriveOdometry odometry = new DifferentialDriveOdometry(getHeading(), 0, 0);

  private final DifferentialDrive m_drive = new DifferentialDrive(m_motor_neo1, m_motor_neo3);

  // Controlador para seguimiento de trayectorias diferenciales (Choreo)
  private final LTVUnicycleController trajectoryController = new LTVUnicycleController(0.02);

  public CANDriveSubsystem() {

    gyro.reset();

    // Config para motores del lado IZQUIERDO (invertidos)
    SparkMaxConfig leftConfig = new SparkMaxConfig();
    leftConfig.inverted(true).idleMode(IdleMode.kBrake);
    leftConfig.encoder
        .positionConversionFactor(ENCODER_POSITION_CONVERSION)
        .velocityConversionFactor(ENCODER_VELOCITY_CONVERSION);

    // Config para motores del lado DERECHO (NO invertidos)
    SparkMaxConfig rightConfig = new SparkMaxConfig();
    rightConfig.inverted(false).idleMode(IdleMode.kBrake);
    rightConfig.encoder
        .positionConversionFactor(ENCODER_POSITION_CONVERSION)
        .velocityConversionFactor(ENCODER_VELOCITY_CONVERSION);

    SparkMaxConfig followerLeft = new SparkMaxConfig();
    followerLeft.follow(LEFT_LEADER_ID);

    SparkMaxConfig followerRight = new SparkMaxConfig();
    followerRight.follow(RIGHT_LEADER_ID);

    m_motor_neo1.configure(leftConfig, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);
    m_motor_neo2.configure(followerLeft, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);
    m_motor_neo3.configure(rightConfig, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);
    m_motor_neo4.configure(followerRight, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);

    leftEncoder.setPosition(0);
    rightEncoder.setPosition(0);

    m_drive.setSafetyEnabled(false);
  }

  @Override
  public void periodic() {

    odometry.update(
        getHeading(),
        leftEncoder.getPosition(),
        rightEncoder.getPosition());

    SmartDashboard.putNumber("Gyro", gyro.getAngle());
    SmartDashboard.putString("Pose", getPose().toString());
  }

  /* ================= TELEOP ================= */

  public void arcadeDrive(double fwd, double rot) {
    m_drive.arcadeDrive(fwd, rot);
  }

  public Command driveArcade(DoubleSupplier fwd, DoubleSupplier rot) {
    return Commands.run(() -> arcadeDrive(fwd.getAsDouble(), rot.getAsDouble()), this);
  }

  /* ================= CHOREO 2026 ================= */

  public Pose2d getPose() {
    return odometry.getPoseMeters();
  }

  public void resetPose(Pose2d pose) {
    leftEncoder.setPosition(0);
    rightEncoder.setPosition(0);

    odometry.resetPosition(getHeading(), 0, 0, pose);
  }

  /**
   * Sigue una muestra de trayectoria diferencial de Choreo.
   * Usa LTVUnicycleController para feedback y convierte a wheel speeds.
   */
  public void followTrajectory(DifferentialSample sample) {
    // Pose actual del robot
    Pose2d pose = getPose();

    // Velocidades feedforward de la trayectoria
    ChassisSpeeds ff = sample.getChassisSpeeds();

    // Calcular velocidades corregidas con feedback del controlador
    ChassisSpeeds speeds = trajectoryController.calculate(
        pose,
        sample.getPose(),
        ff.vxMetersPerSecond,
        ff.omegaRadiansPerSecond);

    // Aplicar velocidades al drivetrain
    driveChassisSpeeds(speeds);
  }

  /**
   * Aplica ChassisSpeeds al drivetrain convirtiendo de m/s a valores [-1, 1].
   */
  public void driveChassisSpeeds(ChassisSpeeds speeds) {
    DifferentialDriveWheelSpeeds wheelSpeeds = kinematics.toWheelSpeeds(speeds);

    // Normalizar velocidades de m/s a [-1, 1]
    double leftOutput = wheelSpeeds.leftMetersPerSecond / MAX_SPEED_MPS;
    double rightOutput = wheelSpeeds.rightMetersPerSecond / MAX_SPEED_MPS;

    // [-1, 1]
    leftOutput = Math.max(-1.0, Math.min(1.0, leftOutput));
    rightOutput = Math.max(-1.0, Math.min(1.0, rightOutput));

    m_drive.tankDrive(leftOutput, rightOutput, false);
  }

  /* ================= UTIL ================= */

  public Rotation2d getHeading() {
    return Rotation2d.fromDegrees(-gyro.getAngle());
  }

  public Command spinInPlaceFixed() {
    final double speed = 0.5;

    return Commands.runEnd(() -> {
      m_motor_neo1.set(-speed);
      m_motor_neo2.set(-speed);
      m_motor_neo3.set(-speed);
      m_motor_neo4.set(-speed);
    }, () -> {
      m_motor_neo1.set(0);
      m_motor_neo2.set(0);
      m_motor_neo3.set(0);
      m_motor_neo4.set(0);
    }, this).withTimeout(.5);
  }
}

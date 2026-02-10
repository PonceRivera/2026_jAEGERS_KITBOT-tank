package frc.robot;

public final class Constants {

  public static final class DriveConstants {

    public static final int LEFT_LEADER_ID = 1;
    public static final int LEFT_FOLLOWER_ID = 2;
    public static final int RIGHT_LEADER_ID = 3;
    public static final int RIGHT_FOLLOWER_ID = 4;

    public static final int DRIVE_MOTOR_CURRENT_LIMIT = 60;

    // ===== CHOREO / ODOMETRY =====
    public static final double WHEEL_DIAMETER_METERS = 0.1524; // 6 pulgadas
    public static final double GEAR_RATIO = 10.71; // típico kitbot
    public static final double TRACK_WIDTH_METERS = 0.6;

    public static final double ENCODER_POSITION_CONVERSION = (Math.PI * WHEEL_DIAMETER_METERS) / GEAR_RATIO;

    public static final double ENCODER_VELOCITY_CONVERSION = ENCODER_POSITION_CONVERSION / 60.0;
  }

  public static final class FuelConstants {
    public static final int FEEDER_MOTOR_ID = 6;
    public static final int INTAKE_LAUNCHER_MOTOR_ID = 5;

    public static final int FEEDER_MOTOR_CURRENT_LIMIT = 100;
    public static final int LAUNCHER_MOTOR_CURRENT_LIMIT = 100;

    public static final double INTAKING_FEEDER_VOLTAGE = -100;
    public static final double INTAKING_INTAKE_VOLTAGE = 100;
    public static final double LAUNCHING_FEEDER_VOLTAGE = 1000;
    public static final double LAUNCHING_LAUNCHER_VOLTAGE = 1000;
    public static final double SPIN_UP_FEEDER_VOLTAGE = -1000;
    public static final double SPIN_UP_SECONDS = 4;

    public static final boolean USE_ABSOLUTE_ENCODER = true;

    public static final double FEEDER_ABS_ZERO_OFFSET = 0.0;
    public static final double INTAKE_ABS_ZERO_OFFSET = 0.0;

    public static final int ALTERNATE_ENCODER_CPR = 4096;
  }

  public static final class OperatorConstants {
    public static final int DRIVER_CONTROLLER_PORT = 0;
    public static final int OPERATOR_CONTROLLER_PORT = 0;

    public static final double DRIVE_SCALING = 0;
    public static final double ROTATION_SCALING = 0;

    public static final double TURN_180_SPEED = 0.5;
    public static final double TURN_180_TIME = 1;
  }

  public static final class VisionConstants {
    // USB camera index (puerto donde está conectada la LifeCam)
    public static final int CAMERA_INDEX = 0; // CameraServer usa índice 0-based
    public static final int CAMERA_WIDTH = 320;
    public static final int CAMERA_HEIGHT = 240;
    public static final int CAMERA_FPS = 15;

    public static final double CAMERA_FX = 300.0;
    public static final double CAMERA_FY = 300.0;
    public static final double CAMERA_CX = 160.0;
    public static final double CAMERA_CY = 120.0;

    // Tamaño del AprilTag FRC estándar (borde exterior a borde exterior) en metros
    public static final double TAG_SIZE_METERS = 0.1651;

    public static final double[][] SHOT_TABLE = {
        { 1.0, -0.60, -0.40 },
        { 1.5, -0.70, -0.50 },
        { 2.0, -0.80, -0.60 },
        { 2.5, -0.90, -0.70 },
        { 3.0, -1.00, -0.75 },
    };
  }
}

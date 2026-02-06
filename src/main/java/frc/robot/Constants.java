// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

/**
 * Clase de constantes del robot. Aquí se definen valores numéricos y booleanos
 * usados en todo el proyecto (IDs, límites, escalados, offsets, etc.). No
 * coloques lógica ejecutable en esta clase, sólo datos.
 *
 * Se recomienda importar estáticamente las constantes cuando se usen.
 */
public final class Constants {
  public static final class DriveConstants {
    // Motor controller IDs for drivetrain motors
    public static final int LEFT_LEADER_ID = 1;
    public static final int LEFT_FOLLOWER_ID = 2;
    public static final int RIGHT_LEADER_ID = 3;
    public static final int RIGHT_FOLLOWER_ID = 4;

    // Current limit for drivetrain motors. 60A is a reasonable maximum to reduce
    // likelihood of tripping breakers or damaging CIM motors
    public static final int DRIVE_MOTOR_CURRENT_LIMIT = 60;
  }

  public static final class FuelConstants {
    // Motor controller IDs for Fuel Mechanism motors
    public static final int FEEDER_MOTOR_ID = 6;
    public static final int INTAKE_LAUNCHER_MOTOR_ID = 5;

    // Current limit and nominal voltage for fuel mechanism motors.
    public static final int FEEDER_MOTOR_CURRENT_LIMIT = 100;
    public static final int LAUNCHER_MOTOR_CURRENT_LIMIT = 100;

    // Voltage values for various fuel operations. These values may need to be tuned
    // based on exact robot construction.
    // See the Software Guide for tuning information
    public static final double INTAKING_FEEDER_VOLTAGE = -100;
    public static final double INTAKING_INTAKE_VOLTAGE = 100;
    public static final double LAUNCHING_FEEDER_VOLTAGE = 1000;
    public static final double LAUNCHING_LAUNCHER_VOLTAGE = 1000;
    public static final double SPIN_UP_FEEDER_VOLTAGE = -1000;
    public static final double SPIN_UP_SECONDS = 4;
    // Encoder configuration for Through Bore / alternate encoders attached to
    // the SPARK MAX data port. Set to true to use the Absolute encoder adapter
    // (REV-11-3326) and read absolute position. Set to false to use the
    // Alternate (quadrature) adapter (REV-11-1881) as an incremental encoder.
    public static final boolean USE_ABSOLUTE_ENCODER = true;

    // Zero offsets (rotations) to apply when using absolute encoders. Tune
    // these values to align the mechanical zero with your code's reference.
    public static final double FEEDER_ABS_ZERO_OFFSET = 0.0;
    public static final double INTAKE_ABS_ZERO_OFFSET = 0.0;

    // Counts-per-revolution to provide when creating an alternate (quadrature)
    // encoder. REV recommends 4096 for many through-bore setups; change if
    // you have a different adapter or gearing.
    public static final int ALTERNATE_ENCODER_CPR = 4096;
  }

  public static final class OperatorConstants {
    // Port constants for driver and operator controllers. These should match the
    // values in the Joystick tab of the Driver Station software
    public static final int DRIVER_CONTROLLER_PORT = 0;
    public static final int OPERATOR_CONTROLLER_PORT = 0;

    // This value is multiplied by the joystick value when driving the robot to
    // help avoid driving and turning too fast and being difficult to control
    public static final double DRIVE_SCALING = 0;
    public static final double ROTATION_SCALING = 0;
    // Open-loop turn parameters for a 180° turn when no gyro is available.
    // These are approximate and should be tuned on the robot. TURN_180_SPEED is
    // the rotation input (0..1) applied during the turn; TURN_180_TIME is the
    // duration in seconds to run that rotation to approximate 180°.
    public static final double TURN_180_SPEED = 0.6;
    public static final double TURN_180_TIME = 1.6;
  }
}

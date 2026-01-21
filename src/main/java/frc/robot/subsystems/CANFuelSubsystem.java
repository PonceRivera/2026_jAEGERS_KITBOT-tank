// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.spark.config.SparkMaxConfig;
import com.revrobotics.spark.SparkMax;
import java.lang.reflect.Method;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import static frc.robot.Constants.FuelConstants.*;

public class CANFuelSubsystem extends SubsystemBase {
  private final SparkMax feederRoller;
  private final SparkMax intakeLauncherRoller;
  // Encoder references (reflection) - may point to Absolute or Alternate encoder
  private Object feederEncoder = null;
  private Object intakeEncoder = null;

  /** Creates a new CANBallSubsystem. */
  public CANFuelSubsystem() {
    // create brushed motors for each of the motors on the launcher mechanism
    intakeLauncherRoller = new SparkMax(INTAKE_LAUNCHER_MOTOR_ID, MotorType.kBrushed);
    feederRoller = new SparkMax(FEEDER_MOTOR_ID, MotorType.kBrushed);

    // put default values for various fuel operations onto the dashboard
    // all methods in this subsystem pull their values from the dashbaord to allow
    // you to tune the values easily, and then replace the values in Constants.java
    // with your new values. For more information, see the Software Guide.
    SmartDashboard.putNumber("Intaking feeder roller value", INTAKING_FEEDER_VOLTAGE);
    SmartDashboard.putNumber("Intaking intake roller value", INTAKING_INTAKE_VOLTAGE);
    SmartDashboard.putNumber("Launching feeder roller value", LAUNCHING_FEEDER_VOLTAGE);
    SmartDashboard.putNumber("Launching launcher roller value", LAUNCHING_LAUNCHER_VOLTAGE);
    SmartDashboard.putNumber("Spin-up feeder roller value", SPIN_UP_FEEDER_VOLTAGE);

    // create the configuration for the feeder roller, set a current limit and apply
    // the config to the controller
    SparkMaxConfig feederConfig = new SparkMaxConfig();
    feederConfig.smartCurrentLimit(FEEDER_MOTOR_CURRENT_LIMIT);
    feederRoller.configure(feederConfig, SparkMax.ResetMode.kResetSafeParameters, SparkMax.PersistMode.kPersistParameters);

    // create the configuration for the launcher roller, set a current limit, set
    // the motor to inverted so that positive values are used for both intaking and
    // launching, and apply the config to the controller
    SparkMaxConfig launcherConfig = new SparkMaxConfig();
    launcherConfig.inverted(true);
    launcherConfig.smartCurrentLimit(LAUNCHER_MOTOR_CURRENT_LIMIT);
    intakeLauncherRoller.configure(launcherConfig, SparkMax.ResetMode.kResetSafeParameters, SparkMax.PersistMode.kPersistParameters);

    // Initialize encoders depending on the selected mode in Constants.
    // We use reflection here so the code will compile even if the REVLib encoder
    // types are not present in the compile classpath for some environments.
    try {
      Class<?> sparkClass = feederRoller.getClass();
      if (USE_ABSOLUTE_ENCODER) {
        // Try to find getAbsoluteEncoder(...) method and call it with the first
        // enum constant for its parameter (usually the Type enum).
        Method absMethod = null;
        for (Method m : sparkClass.getMethods()) {
          if (m.getName().equals("getAbsoluteEncoder")) {
            absMethod = m;
            break;
          }
        }
        if (absMethod != null) {
          Class<?>[] params = absMethod.getParameterTypes();
          Object arg = null;
          if (params.length == 1 && params[0].isEnum()) {
            arg = params[0].getEnumConstants()[0];
          }
          feederEncoder = absMethod.invoke(feederRoller, arg);
          intakeEncoder = absMethod.invoke(intakeLauncherRoller, arg);

          // Try to set zero offset if available
          try {
            Method setZero = feederEncoder.getClass().getMethod("setZeroOffset", double.class);
            setZero.invoke(feederEncoder, FEEDER_ABS_ZERO_OFFSET);
            Method setZero2 = intakeEncoder.getClass().getMethod("setZeroOffset", double.class);
            setZero2.invoke(intakeEncoder, INTAKE_ABS_ZERO_OFFSET);
          } catch (NoSuchMethodException ignored) {
            // It's fine if the method doesn't exist on this API version
          }
        }
      }

      if (feederEncoder == null) {
        // Try alternate encoder form: getAlternateEncoder(Type, int)
        Method altMethod = null;
        for (Method m : sparkClass.getMethods()) {
          if (m.getName().equals("getAlternateEncoder")) {
            altMethod = m;
            break;
          }
        }
        if (altMethod != null) {
          Class<?>[] params = altMethod.getParameterTypes();
          Object[] args = new Object[params.length];
          for (int i = 0; i < params.length; i++) {
            if (params[i].isEnum()) {
              args[i] = params[i].getEnumConstants()[0];
            } else if (params[i] == int.class || params[i] == Integer.class) {
              args[i] = ALTERNATE_ENCODER_CPR;
            } else {
              args[i] = null;
            }
          }
          feederEncoder = altMethod.invoke(feederRoller, args);
          intakeEncoder = altMethod.invoke(intakeLauncherRoller, args);
        }
      }
    } catch (Exception e) {
      System.out.println("Warning: failed to initialize encoders via reflection: " + e.getMessage());
      feederEncoder = null;
      intakeEncoder = null;
    }
  }

  // A method to set the rollers to values for intaking
  public void intake() {
    feederRoller.setVoltage(SmartDashboard.getNumber("Intaking feeder roller value", INTAKING_FEEDER_VOLTAGE));
    intakeLauncherRoller
        .setVoltage(SmartDashboard.getNumber("Intaking intake roller value", INTAKING_INTAKE_VOLTAGE));
  }

  // A method to set the rollers to values for ejecting fuel out the intake. Uses
  // the same values as intaking, but in the opposite direction.
  public void eject() {
    feederRoller
        .setVoltage(-1 * SmartDashboard.getNumber("Intaking feeder roller value", INTAKING_FEEDER_VOLTAGE));
    intakeLauncherRoller
        .setVoltage(-1 * SmartDashboard.getNumber("Intaking launcher roller value", INTAKING_INTAKE_VOLTAGE));
  }

  // A method to set the rollers to values for launching.
  public void launch() {
    feederRoller.setVoltage(SmartDashboard.getNumber("Launching feeder roller value", LAUNCHING_FEEDER_VOLTAGE));
    intakeLauncherRoller
        .setVoltage(SmartDashboard.getNumber("Launching launcher roller value", LAUNCHING_LAUNCHER_VOLTAGE));
  }

  // A method to stop the rollers
  public void stop() {
    feederRoller.set(0);
    intakeLauncherRoller.set(0);
  }

  // A method to spin up the launcher roller while spinning the feeder roller to
  // push Fuel away from the launcher
  public void spinUp() {
    feederRoller
        .setVoltage(SmartDashboard.getNumber("Spin-up feeder roller value", SPIN_UP_FEEDER_VOLTAGE));
    intakeLauncherRoller
        .setVoltage(SmartDashboard.getNumber("Launching launcher roller value", LAUNCHING_LAUNCHER_VOLTAGE));
  }

  // A command factory to turn the spinUp method into a command that requires this
  // subsystem
  public Command spinUpCommand() {
    return this.run(() -> spinUp());
  }

  // A command factory to turn the launch method into a command that requires this
  // subsystem
  public Command launchCommand() {
    return this.run(() -> launch());
  }
  // Reflection helpers to call encoder methods without depending on encoder
  // types at compile time.
  private double invokeEncoderPosition(Object encoder) {
    if (encoder == null)
      return Double.NaN;
    try {
      Method m = encoder.getClass().getMethod("getPosition");
      Object res = m.invoke(encoder);
      if (res instanceof Number)
        return ((Number) res).doubleValue();
    } catch (Exception e) {
      // ignore and return NaN
    }
    return Double.NaN;
  }

  private double invokeEncoderVelocity(Object encoder) {
    if (encoder == null)
      return Double.NaN;
    try {
      Method m = encoder.getClass().getMethod("getVelocity");
      Object res = m.invoke(encoder);
      if (res instanceof Number)
        return ((Number) res).doubleValue();
    } catch (Exception e) {
      // ignore and return NaN
    }
    return Double.NaN;
  }

  /** Returns the feeder encoder position in rotations (absolute or relative). */
  public double getFeederPosition() {
    return invokeEncoderPosition(feederEncoder);
  }

  /** Returns the intake/launcher encoder position in rotations (absolute or relative). */
  public double getIntakePosition() {
    return invokeEncoderPosition(intakeEncoder);
  }

  /** Returns the feeder encoder velocity (units depend on API; check REV docs). */
  public double getFeederVelocity() {
    return invokeEncoderVelocity(feederEncoder);
  }

  @Override
  public void periodic() {
    // This method will be called once per scheduler run
  }
}

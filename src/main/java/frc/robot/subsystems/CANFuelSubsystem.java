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
  // Diagnostic counters for start/stop calls
  private int intakeStartCount = 0;
  private int intakeStopCount = 0;
  private int feederStartCount = 0;
  private int feederStopCount = 0;
  // State flags to make start/stop idempotent and avoid pulsing
  private boolean intakeRunning = false;
  private boolean feederRunning = false;

  /** Crea un nuevo subsistema de fuel (rodillos). */
  public CANFuelSubsystem() {
  // Crear controladores para el mecanismo de fuel:
  // - motor 5 (INTAKE_LAUNCHER_MOTOR_ID) es brushless (launcher/intake)
  // - motor 6 (FEEDER_MOTOR_ID) es brushed (feeder)
  intakeLauncherRoller = new SparkMax(INTAKE_LAUNCHER_MOTOR_ID, MotorType.kBrushed);
  feederRoller = new SparkMax(FEEDER_MOTOR_ID, MotorType.kBrushed);

    // Publicar valores por defecto en el Dashboard para poder ajustarlos en
    // tiempo de ejecución y tunear sin recompilar.
    SmartDashboard.putNumber("Intaking feeder roller value", INTAKING_FEEDER_VOLTAGE);
    SmartDashboard.putNumber("Intaking intake roller value", INTAKING_INTAKE_VOLTAGE);
    SmartDashboard.putNumber("Launching feeder roller value", LAUNCHING_FEEDER_VOLTAGE);
    SmartDashboard.putNumber("Launching launcher roller value", LAUNCHING_LAUNCHER_VOLTAGE);
    SmartDashboard.putNumber("Spin-up feeder roller value", SPIN_UP_FEEDER_VOLTAGE);

    // Configurar el feeder: limitar corriente y aplicar configuración al motor.
    SparkMaxConfig feederConfig = new SparkMaxConfig();
    feederConfig.smartCurrentLimit(FEEDER_MOTOR_CURRENT_LIMIT);
  // Make motor 6 (feederRoller) inverted per user request
  feederConfig.inverted(true);
  feederRoller.configure(feederConfig, SparkMax.ResetMode.kResetSafeParameters, SparkMax.PersistMode.kPersistParameters);

    // Configurar el motor del lanzador: invertir según sea necesario y limitar
    // corriente, luego aplicar la configuración.
  SparkMaxConfig launcherConfig = new SparkMaxConfig();
  // Make motor 5 (intakeLauncherRoller) not inverted per user request
  launcherConfig.inverted(false);
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
          }
        }
      }

      if (feederEncoder == null) {
  
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

  // Establece valores para intaking (recoger fuel)
  public void intake() {
    // Run motor 5 (intakeLauncherRoller) at maximum forward voltage for intake
    double intakeV = 9.0;
    SmartDashboard.putString("FuelMode", "intake");
    SmartDashboard.putNumber("Fuel.IntakeVoltage", intakeV);
    intakeLauncherRoller.setVoltage(clampVoltage(intakeV));
  }

  // Start/stop control for individual motors so buttons can control them
  public void startIntakeMotor() {
    if (intakeRunning) {
      return; // already running, avoid pulsing
    }
    intakeRunning = true;
    // Start intake motor 5 at maximum forward voltage
    double intakeV = 9.0;
    SmartDashboard.putString("FuelMode", "intake_single");
    SmartDashboard.putNumber("Fuel.IntakeVoltage", intakeV);
    intakeLauncherRoller.setVoltage(clampVoltage(intakeV));
    intakeStartCount++;
    SmartDashboard.putNumber("Diagnostics.IntakeStartCount", intakeStartCount);
    System.out.println("[CANFuelSubsystem] startIntakeMotor called #" + intakeStartCount);
  }

  public void stopIntakeMotor() {
    if (!intakeRunning) {
      return;
    }
    intakeRunning = false;
    intakeLauncherRoller.set(0);
    intakeStopCount++;
    SmartDashboard.putNumber("Diagnostics.IntakeStopCount", intakeStopCount);
    System.out.println("[CANFuelSubsystem] stopIntakeMotor called #" + intakeStopCount);
  }

  public void startFeederMotor() {
    if (feederRunning) {
      return;
    }
    feederRunning = true;
    double feederV = SmartDashboard.getNumber("Intaking feeder roller value", INTAKING_FEEDER_VOLTAGE);
    SmartDashboard.putString("FuelMode", "feeder_single");
    SmartDashboard.putNumber("Fuel.FeederVoltage", feederV);
    feederRoller.setVoltage(clampVoltage(feederV));
    feederStartCount++;
    SmartDashboard.putNumber("Diagnostics.FeederStartCount", feederStartCount);
    System.out.println("[CANFuelSubsystem] startFeederMotor called #" + feederStartCount);
  }

  public void stopFeederMotor() {
    if (!feederRunning) {
      return;
    }
    feederRunning = false;
    feederRoller.set(0.0);
    feederStopCount++;
    SmartDashboard.putNumber("Diagnostics.FeederStopCount", feederStopCount);
    System.out.println("[CANFuelSubsystem] stopFeederMotor called #" + feederStopCount);
  }

  // Establece valores para expulsar fuel
  public void eject() {
    // Eject (reverse intake) should act only on motor 5 (intakeLauncherRoller) at max reverse
    double intakeV = -12.0;
    SmartDashboard.putString("FuelMode", "eject");
    SmartDashboard.putNumber("Fuel.IntakeVoltage", intakeV);
    intakeLauncherRoller.setVoltage(clampVoltage(intakeV));
  }

  
  public void launch() {
    double feederV = SmartDashboard.getNumber("Launching feeder roller value", LAUNCHING_FEEDER_VOLTAGE);
    double intakeV = SmartDashboard.getNumber("Launching launcher roller value", LAUNCHING_LAUNCHER_VOLTAGE);
    SmartDashboard.putString("FuelMode", "launch");
    SmartDashboard.putNumber("Fuel.FeederVoltage", feederV);
    SmartDashboard.putNumber("Fuel.IntakeVoltage", intakeV);
    feederRoller.setVoltage(clampVoltage(feederV));
    intakeLauncherRoller.setVoltage(clampVoltage(intakeV));
  }

  // Detiene los rodillos
  public void stop() {
    feederRoller.set(0);
    intakeLauncherRoller.set(0);
  }

  // Enciende el lanzador y hace girar el feeder para preparar el disparo
  public void spinUp() {
    double feederV = SmartDashboard.getNumber("Spin-up feeder roller value", SPIN_UP_FEEDER_VOLTAGE);
    double intakeV = SmartDashboard.getNumber("Launching launcher roller value", LAUNCHING_LAUNCHER_VOLTAGE);
    SmartDashboard.putString("FuelMode", "spinUp");
    SmartDashboard.putNumber("Fuel.FeederVoltage", feederV);
    SmartDashboard.putNumber("Fuel.IntakeVoltage", intakeV);
    feederRoller.setVoltage(clampVoltage(feederV));
    intakeLauncherRoller.setVoltage(clampVoltage(intakeV));
  }

  // Clamp voltages to a safe range (SPARK MAX accepts voltages roughly -12..12).
  private double clampVoltage(double volts) {
    if (Double.isNaN(volts))
      return 0.0;
    if (volts > 12.0)
      return 12.0;
    if (volts < -12.0)
      return -12.0;
    return volts;
  }

  // Fabrica un comando que ejecuta spinUp mientras requiere este subsistema
  public Command spinUpCommand() {
    return this.run(() -> spinUp());
  }

  // Fabrica un comando que ejecuta launch mientras requiere este subsistema
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
  }
}

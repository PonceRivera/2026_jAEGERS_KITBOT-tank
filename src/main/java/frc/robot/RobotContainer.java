package frc.robot;

import choreo.auto.AutoFactory;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.*;
import edu.wpi.first.wpilibj2.command.button.Trigger;

import static frc.robot.Constants.OperatorConstants.*;
import frc.robot.commands.Autos;

import frc.robot.subsystems.CANDriveSubsystem;
import frc.robot.subsystems.MyKitBot;
import frc.robot.subsystems.VisionSubsystem;

public class RobotContainer {

  private final CANDriveSubsystem driveSubsystem = new CANDriveSubsystem();
  private final MyKitBot myKitBot = new MyKitBot();
  private final VisionSubsystem visionSubsystem = new VisionSubsystem();

  private final Joystick driverJoystick = new Joystick(DRIVER_CONTROLLER_PORT);

  private final SendableChooser<Command> autoChooser = new SendableChooser<>();

  private final AutoFactory autoFactory;

  public RobotContainer() {

    configureBindings();

    // AutoFactory para Choreo — usa followTrajectory que acepta DifferentialSample
    autoFactory = new AutoFactory(
        driveSubsystem::getPose,
        driveSubsystem::resetPose,
        driveSubsystem::followTrajectory,
        true,
        driveSubsystem);

    // EVENT MARKERS
    autoFactory.bind("disparar", myKitBot.DisparDelay());
    autoFactory.bind("tomar", myKitBot.Take2());
    autoFactory.bind("parar", myKitBot.TakeOFF());
    autoFactory.bind("sacar", myKitBot.TakeOUT());

    autoChooser.setDefaultOption("Do Nothing", new InstantCommand());

    autoChooser.addOption(
        "Auto 1",
        Commands.sequence(
            autoFactory.resetOdometry("MedioDisparo"),
            autoFactory.trajectoryCmd("DisparoApelotas"),
            autoFactory.trajectoryCmd("PelotasAdisparo"),
            autoFactory.trajectoryCmd("DisparoAneutral")));

    // Auto 2: simple por tiempos (retrocede + dispara)
    autoChooser.addOption("Auto 2", Autos.autoTiempos(driveSubsystem, myKitBot));

    // Auto 3: secuencia completa por tiempos (replica Choreo sin trayectorias)
    autoChooser.addOption("Auto 3", Autos.auto1Tiempos(driveSubsystem, myKitBot));

    SmartDashboard.putData("Auto Chooser", autoChooser);
  }

  private void configureBindings() {

    new Trigger(() -> driverJoystick.getRawButton(4))
        .onTrue(driveSubsystem.spinInPlaceFixed());

    new Trigger(() -> driverJoystick.getRawButton(1))
        .whileTrue(myKitBot.DisparDelay());

    new Trigger(() -> driverJoystick.getRawButton(12))
        .whileTrue(myKitBot.TakeOUT());

    new Trigger(() -> driverJoystick.getRawButton(2))
        .whileTrue(myKitBot.Take2());

    new Trigger(() -> driverJoystick.getRawButton(7))
        .onTrue(myKitBot.TakeOFF());

    driveSubsystem.setDefaultCommand(
        driveSubsystem.driveArcade(

            () -> {
              // MAX_SPEED: ajusta este valor para limitar la velocidad máxima (0.0 a 1.0)
              final double MAX_SPEED = 0.70; // 70% de velocidad máxima
              double throttle = (1 - driverJoystick.getRawAxis(3)) / 2.0;

              double forward = MathUtil.applyDeadband(driverJoystick.getY(), 0.08);
              forward = Math.copySign(forward * forward, forward);

              return -forward * throttle * MAX_SPEED;
            },

            () -> {
              // más bajo que la velocidad para evitar brownouts al girar
              final double MAX_TURN_SPEED = 0.55;
              double throttle = (1 - driverJoystick.getRawAxis(3)) / 2.0;

              double rotation = MathUtil.applyDeadband(driverJoystick.getZ(), 0.08);
              rotation = Math.copySign(rotation * rotation, rotation);

              return rotation * throttle * MAX_TURN_SPEED;
            }));
  }

  public Command getAutonomousCommand() {
    return autoChooser.getSelected();
  }
}

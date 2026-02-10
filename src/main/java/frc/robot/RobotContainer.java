package frc.robot;

import choreo.auto.AutoFactory;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.*;
import edu.wpi.first.wpilibj2.command.button.Trigger;

import static frc.robot.Constants.OperatorConstants.*;

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

    autoChooser.setDefaultOption("Do Nothing", new InstantCommand());

    // Auto2: sigue la trayectoria generada en Choreo
    autoChooser.addOption(
        "Auto2",
        Commands.sequence(
            autoFactory.resetOdometry("Auto2"),
            autoFactory.trajectoryCmd("Auto2")));

    // Auto1: NO tiene .traj generado todavía — genera la trayectoria en Choreo
    // primero
    // autoChooser.addOption("Auto1", Commands.sequence(
    // autoFactory.resetOdometry("Auto1"),
    // autoFactory.trajectoryCmd("Auto1")
    // ));

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
              double throttle = (1 - driverJoystick.getRawAxis(3)) / 2.0;

              double forward = MathUtil.applyDeadband(driverJoystick.getY(), 0.08);
              forward = Math.copySign(forward * forward, forward);

              return -forward * throttle;
            },

            () -> {
              double throttle = (1 - driverJoystick.getRawAxis(3)) / 2.0;

              double rotation = MathUtil.applyDeadband(driverJoystick.getZ(), 0.08);
              rotation = Math.copySign(rotation * rotation, rotation);

              return rotation * throttle;
            }));
  }

  public Command getAutonomousCommand() {
    return autoChooser.getSelected();
  }
}

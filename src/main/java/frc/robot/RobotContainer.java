// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import edu.wpi.first.wpilibj2.command.button.Trigger;

import static frc.robot.Constants.OperatorConstants.*;
import static frc.robot.Constants.FuelConstants.*;
import frc.robot.commands.Autos;
import frc.robot.subsystems.CANDriveSubsystem;
import frc.robot.subsystems.CANFuelSubsystem;

/**
 * Contiene la mayoría de las declaraciones del robot: subsistemas, comandos y
 * mapeos de botones. En el enfoque Command-based la lógica de ejecución se
 * organiza declarativamente, por lo que aquí sólo se construyen y conectan
 * las piezas del robot.
 */
public class RobotContainer {
  // The robot's subsystems
  private final CANDriveSubsystem driveSubsystem = new CANDriveSubsystem();
  private final CANFuelSubsystem ballSubsystem = new CANFuelSubsystem();

  // The driver's controller
  private final edu.wpi.first.wpilibj.Joystick driverJoystick = new edu.wpi.first.wpilibj.Joystick(
      DRIVER_CONTROLLER_PORT);

  // The autonomous chooser
  private final SendableChooser<Command> autoChooser = new SendableChooser<>();

  /**
   * The container for the robot. Contains subsystems, OI devices, and commands.
   */
  public RobotContainer() {
    configureBindings();

    // Set the options to show up in the Dashboard for selecting auto modes. If you
    // add additional auto modes you can add additional lines here with
    // autoChooser.addOption
    autoChooser.setDefaultOption("Autonomous", Autos.exampleAuto(driveSubsystem, ballSubsystem));
  }

  /**
   * Define los mapeos de disparadores->comandos (botones -> acciones).
   * Se usan {@link Trigger} con predicados sobre el HID para activar comandos.
   */
  private void configureBindings() {

  // Botón 2 (pulgar): activar intake (recoger fuel)
    new Trigger(() -> driverJoystick.getRawButton(2))
        .whileTrue(ballSubsystem.runEnd(() -> ballSubsystem.intake(), () -> ballSubsystem.stop()));

  // Gatillo (Botón 1): hacer spin up y luego lanzar
  // Combina spinUpCommand y launchCommand
    new Trigger(driverJoystick::getTrigger)
        .whileTrue(ballSubsystem.spinUpCommand().withTimeout(SPIN_UP_SECONDS)
            .andThen(ballSubsystem.launchCommand())
            .finallyDo(() -> ballSubsystem.stop()));

  // Botón 3 (parte superior): expulsar fuel (feeder inverso)
    new Trigger(() -> driverJoystick.getRawButton(3))
        .whileTrue(ballSubsystem.runEnd(() -> ballSubsystem.eject(), () -> ballSubsystem.stop()));

  // Button 4: open-loop 180° turn to the right (timed). This is approximate
  // — tune TURN_180_SPEED and TURN_180_TIME in Constants.OperatorConstants.
  new Trigger(() -> driverJoystick.getRawButton(4))
    .onTrue(driveSubsystem.driveArcade(() -> 0.0, () -> TURN_180_SPEED).withTimeout(TURN_180_TIME));

  // Establece el comando por defecto del subsistema de conducción usando
  // los ejes del joystick. El eje Y se invierte para que empujar hacia
  // adelante avance el robot. El eje Z controla la rotación. Se aplican
  // escalados para que el manejo sea más suave.
    driveSubsystem.setDefaultCommand(
        driveSubsystem.driveArcade(
            () -> -driverJoystick.getY() * DRIVE_SCALING,
            () -> -driverJoystick.getZ() * ROTATION_SCALING));
  }

  /**
   * Use this to pass the autonomous command to the main {@link Robot} class.
   *
   * @return the command to run in autonomous
   */
  public Command getAutonomousCommand() {
    // An example command will be run in autonomous
    return autoChooser.getSelected();
  }
}

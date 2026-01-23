// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import frc.robot.subsystems.CANDriveSubsystem;
import edu.wpi.first.wpilibj2.command.Command;

/**
 * Comando ejemplo que usa el subsistema de conducción. Manténlo como plantilla
 * para crear comandos reales.
 */
public class ExampleCommand extends Command {
  @SuppressWarnings("PMD.UnusedPrivateField")
  private final CANDriveSubsystem m_subsystem;

  /**
   * Crea un nuevo ExampleCommand.
   *
   * @param subsystem Subsistema que usa este comando.
   */
  public ExampleCommand(CANDriveSubsystem subsystem) {
    m_subsystem = subsystem;
    // Declara las dependencias del subsistema.
    addRequirements(subsystem);
  }

  // Se llama cuando el comando se programa inicialmente.
  @Override
  public void initialize() {}

  // Se llama cada vez que el scheduler ejecuta este comando.
  @Override
  public void execute() {}

  // Se llama una vez que el comando termina o es interrumpido.
  @Override
  public void end(boolean interrupted) {}

  // Devuelve true cuando el comando debe finalizar.
  @Override
  public boolean isFinished() {
    return false;
  }
}

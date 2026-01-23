// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.hal.HAL;
import edu.wpi.first.hal.FRCNetComm.tResourceType;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;

/**
 * Clase principal del robot: el framework llama automáticamente a los métodos
 * correspondientes a cada modo (init/periodic). Si cambias el nombre de esta
 * clase o del paquete, actualiza también la configuración de compilación.
 */
public class Robot extends TimedRobot {
  private Command m_autonomousCommand;

  private RobotContainer m_robotContainer;

  /**
   * Se ejecuta cuando el robot arranca y debe usarse para inicializaciones.
   */
  @Override
  public void robotInit() {
  // Crea RobotContainer: registra mapeos de botones y muestra el selector de
  // modo autónomo en el dashboard.
    m_robotContainer = new RobotContainer();

  // Registro de uso del framework (no eliminar).
    HAL.report(tResourceType.kResourceType_Framework, 10);
  }

  /**
   * Se llama cada 20 ms en todos los modos. Úsalo para diagnósticos o tareas
   * periódicas globales que deben ejecutarse siempre.
   */
  @Override
  public void robotPeriodic() {
  // Ejecuta el Scheduler: comprueba botones, programa/completa comandos y
  // llama a los periodic() de los subsistemas. Debe llamarse desde
  // robotPeriodic para que el framework Command-based funcione.
    CommandScheduler.getInstance().run();
  }

  /** This function is called once each time the robot enters Disabled mode. */
  @Override
  public void disabledInit() {
  }

  @Override
  public void disabledPeriodic() {
  }

  /**
   * This autonomous runs the autonomous command selected by your
   * {@link RobotContainer} class.
   */
  @Override
  public void autonomousInit() {
    m_autonomousCommand = m_robotContainer.getAutonomousCommand();

    // schedule the autonomous command (example)
    if (m_autonomousCommand != null) {
      CommandScheduler.getInstance().schedule(m_autonomousCommand);;
    }
  }

  /** This function is called periodically during autonomous. */
  @Override
  public void autonomousPeriodic() {
  }

  @Override
  public void teleopInit() {
    // This makes sure that the autonomous stops running when
    // teleop starts running. If you want the autonomous to
    // continue until interrupted by another command, remove
    // this line or comment it out.
    if (m_autonomousCommand != null) {
      m_autonomousCommand.cancel();
    }
  }

  /** This function is called periodically during operator control. */
  @Override
  public void teleopPeriodic() {
  }

  @Override
  public void testInit() {
    // Cancels all running commands at the start of test mode.
    CommandScheduler.getInstance().cancelAll();
  }

  /** This function is called periodically during test mode. */
  @Override
  public void testPeriodic() {
  }

  /** This function is called once when the robot is first started up. */
  @Override
  public void simulationInit() {
  }

  /** This function is called periodically whilst in simulation. */
  @Override
  public void simulationPeriodic() {
  }
}
//https://github.com/PonceRivera/2026_jAEGERS_KITBOT-tank.git
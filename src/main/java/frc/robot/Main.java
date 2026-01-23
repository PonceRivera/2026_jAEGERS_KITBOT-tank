// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj.RobotBase;

/**
 * Clase de arranque mínima. No añadas variables estáticas ni inicializaciones
 * aquí; modifica solo el parámetro de startRobot si cambias la clase Robot.
 */
public final class Main {
  private Main() {}

  /**
   * Punto de entrada principal. No realices inicializaciones aquí; el
   * constructor/robotInit de la clase Robot debe encargarse de eso.
   */
  public static void main(String... args) {
    RobotBase.startRobot(Robot::new);
  }
}

// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import frc.robot.subsystems.CANDriveSubsystem;
import frc.robot.subsystems.CANFuelSubsystem;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;

public final class Autos {
  /**
   * Fábrica de ejemplo para un comando autonómo. Sustituye por tu rutina.
   */
  public static Command exampleAuto(CANDriveSubsystem driveSubsystem, CANFuelSubsystem fuelSubsystem) {
    return Commands.sequence(
      Commands.runOnce(() -> System.out.println("Auto started!"))
    );
  }

  private Autos() {
    throw new UnsupportedOperationException("This is a utility class!");
  }
}

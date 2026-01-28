// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import java.util.function.DoubleSupplier;

import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.config.SparkMaxConfig;

import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class CANDriveSubsystem extends SubsystemBase {
  private static final int kCanID_neo1 = 1;
  private static final int kCanID_neo2 = 2;
  private static final int kCanID_neo3 = 3;
  private static final int kCanID_neo4 = 4;

  private static final MotorType kMotorType_neo = MotorType.kBrushless;

  private final SparkMax m_motor_neo1 = new SparkMax(kCanID_neo1, kMotorType_neo);
  private final SparkMax m_motor_neo2 = new SparkMax(kCanID_neo2, kMotorType_neo);
  private final SparkMax m_motor_neo3 = new SparkMax(kCanID_neo3, kMotorType_neo);
  private final SparkMax m_motor_neo4 = new SparkMax(kCanID_neo4, kMotorType_neo);

  private final DifferentialDrive m_drive = new DifferentialDrive(
    (double speed) -> {
      m_motor_neo1.set(speed);
      m_motor_neo2.set(speed);
    },
    (double speed) -> {
      m_motor_neo3.set(speed);
      m_motor_neo4.set(speed);
    }
  );

  public CANDriveSubsystem() {
    //  m_motor_neo3.setInverted(true);
    //  m_motor_neo4.setInverted(true);
  }

  
  public void arcadeDrive(double fwd, double rot) {
    m_drive.arcadeDrive(fwd, rot);
  }


  public Command driveArcade(DoubleSupplier fwd, DoubleSupplier rot) {
    return Commands.run(() -> arcadeDrive(fwd.getAsDouble(), rot.getAsDouble()), this);
  }
}
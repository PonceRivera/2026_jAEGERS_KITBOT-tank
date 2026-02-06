// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import java.util.function.DoubleSupplier;
import java.lang.reflect.Method;

import com.revrobotics.spark.FeedbackSensor;
import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.spark.SparkMax;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import com.revrobotics.spark.config.SparkBaseConfig.IdleMode;
import com.revrobotics.spark.config.SparkMaxConfig;
import com.revrobotics.spark.SparkBase.PersistMode;
import com.revrobotics.spark.SparkBase.ResetMode;
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

  private Object encoder1 = null;
  private Object encoder2 = null;
  private Object encoder3 = null;
  private Object encoder4 = null;

  private final DifferentialDrive m_drive = new DifferentialDrive(
      m_motor_neo1,
      m_motor_neo3
  );

  public CANDriveSubsystem() {
    SparkMaxConfig config = new SparkMaxConfig();
    SparkMaxConfig config2 = new SparkMaxConfig();
    SparkMaxConfig config3 = new SparkMaxConfig();
    

    config
    .inverted(true)
    .idleMode(IdleMode.kBrake);
    config.encoder
    .positionConversionFactor(1000)
    .velocityConversionFactor(1000);
    config.closedLoop
    .feedbackSensor(FeedbackSensor.kPrimaryEncoder)
    .pid(1.0, 0.0, 0.0); 

    config2
    .inverted(false)
    .idleMode(IdleMode.kBrake);
    
    config3
    .inverted(false)
    .idleMode(IdleMode.kBrake);
    

    config2.follow(kCanID_neo1);
    config3.follow(kCanID_neo3);

    m_motor_neo1.configure(config, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);
    m_motor_neo2.configure(config2, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);
    m_motor_neo3.configure(config, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);
    m_motor_neo4.configure(config3, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);
    m_drive.setSafetyEnabled(false);
    try {
      Method getEnc = m_motor_neo1.getClass().getMethod("getEncoder");
      encoder1 = getEnc.invoke(m_motor_neo1);
      encoder2 = getEnc.invoke(m_motor_neo2);
      encoder3 = getEnc.invoke(m_motor_neo3);
      encoder4 = getEnc.invoke(m_motor_neo4);

      Method setPos = encoder1.getClass().getMethod("setPosition", double.class);
      setPos.invoke(encoder1, 0.0);
      setPos.invoke(encoder2, 0.0);
      setPos.invoke(encoder3, 0.0);
      setPos.invoke(encoder4, 0.0);
    } catch (Exception e) {
      System.out.println("Warning: encoders not available via getEncoder(): " + e.getMessage());
      encoder1 = encoder2 = encoder3 = encoder4 = null;
    }
  }

  @Override
  public void periodic() {

    try {
      if (encoder1 != null) {
        Method getPos = encoder1.getClass().getMethod("getPosition");
        double p1 = ((Number) getPos.invoke(encoder1)).doubleValue();
        double p2 = ((Number) getPos.invoke(encoder2)).doubleValue();
        double p3 = ((Number) getPos.invoke(encoder3)).doubleValue();
        double p4 = ((Number) getPos.invoke(encoder4)).doubleValue();

        SmartDashboard.putNumber("Drive/Encoder1_Position", p1);
        SmartDashboard.putNumber("Drive/Encoder2_Position", p2);
        SmartDashboard.putNumber("Drive/Encoder3_Position", p3);
        SmartDashboard.putNumber("Drive/Encoder4_Position", p4);

        double leftAvg = (p1 + p2) / 2.0;
        double rightAvg = (p3 + p4) / 2.0;
        SmartDashboard.putNumber("Drive/Left_Avg_Position", leftAvg);
        SmartDashboard.putNumber("Drive/Right_Avg_Position", rightAvg);
      }
    } catch (Exception e) {
      
      System.out.println("Warning: failed to read encoder positions: " + e.getMessage());
    }
  }

  
  public void arcadeDrive(double fwd, double rot) {
    m_drive.arcadeDrive(fwd, rot);
  }


  public Command driveArcade(DoubleSupplier fwd, DoubleSupplier rot) {
    return Commands.run(() -> arcadeDrive(fwd.getAsDouble(), rot.getAsDouble()), this);
  }
}
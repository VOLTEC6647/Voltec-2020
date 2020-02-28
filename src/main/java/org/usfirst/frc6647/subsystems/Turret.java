/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package org.usfirst.frc6647.subsystems;

import com.revrobotics.CANEncoder;
import com.revrobotics.CANPIDController;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import org.usfirst.frc6647.robot.Constants;
import org.usfirst.lib6647.subsystem.SuperSubsystem;

public class Turret extends SuperSubsystem {
  private CANSparkMax turret = new CANSparkMax(Constants.turret.id, MotorType.kBrushless);
  private CANPIDController PIDs;
  private CANEncoder encoder;
  /**
   * Creates a new Turret.
   */
  public Turret() {
    super("turret");
    PIDs = turret.getPIDController();
    encoder = turret.getEncoder();

    turret.restoreFactoryDefaults();
    PIDs.setP(Constants.shooter.kP);
    PIDs.setD(Constants.shooter.kD);
    PIDs.setFF(Constants.shooter.kF);
    PIDs.setOutputRange(0, 3000);
  }

  @Override
  public void periodic() {
    // This method will be called once per scheduler run
  }

  public void setDesired(){

  }
}

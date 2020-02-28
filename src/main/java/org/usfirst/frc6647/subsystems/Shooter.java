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
import com.revrobotics.ControlType;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import org.usfirst.frc6647.robot.Constants;
import org.usfirst.lib6647.subsystem.SuperSubsystem;

import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.Servo;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;

public class Shooter extends SuperSubsystem {

  private CANSparkMax shooter = new CANSparkMax(Constants.shooter.id, MotorType.kBrushless);
  private CANPIDController PIDs;
  private CANEncoder encoder;
  private double speed, ty, distance;
  private Servo hood = new Servo(0);
  private Servo stop = new Servo(1);

  /**
   * Creates a new Shooter.
   */
  public Shooter() {
    super("shooter");
    PIDs = shooter.getPIDController();
    encoder = shooter.getEncoder();

    shooter.restoreFactoryDefaults();
    PIDs.setP(Constants.shooter.kP);
    PIDs.setD(Constants.shooter.kD);
    PIDs.setFF(Constants.shooter.kF);
    PIDs.setOutputRange(0, 3000);
  }

  @Override
  public void periodic() {
    Shuffleboard.getTab("Robot")
        .add("Shooter Neo-RPM", encoder.getVelocity())
        .withWidget(BuiltInWidgets.kGraph);

  }

  public void setSpeed() {
    PIDs.setReference(calculateSpeed(), ControlType.kVelocity);
  }

  public double calculateSpeed() {
    ty = NetworkTableInstance.getDefault().getTable("limelight").getEntry("ty").getDouble(0);
    distance = ((3.8883 * Math.pow(10, 6)) * Math.pow((ty + 31.2852), -4.9682) + 113.358) / (ty + 31.2852) - 0.978398;
    if (distance < 3) {

    } else if (distance < 6) {

    } else if (distance < 9) {

    } else {

    }
    return speed;
  }

}

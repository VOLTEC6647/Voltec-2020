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
import com.revrobotics.CANSparkMax.IdleMode;

import org.usfirst.frc6647.robot.Constants;
import org.usfirst.lib6647.subsystem.SuperSubsystem;
import org.usfirst.lib6647.subsystem.supercomponents.SuperServo;
import org.usfirst.lib6647.subsystem.supercomponents.SuperSolenoid;
import org.usfirst.lib6647.subsystem.supercomponents.SuperSparkMax;

import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.Servo;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInLayouts;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardLayout;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;

public class Shooter extends SuperSubsystem implements SuperServo, SuperSolenoid, SuperSparkMax {
	private Servo hood;
	private Solenoid stop;

	private CANSparkMax shooter;
	private CANPIDController shooterPID;
	private CANEncoder shooterEncoder;

	private double speed, ty, distance;

	private ShuffleboardTab tab = Shuffleboard.getTab("Robot");
	private ShuffleboardLayout layout;

	private double setPoint;

	/**
	 * Creates a new {@link Shooter}.
	 */
	public Shooter() {
		super("shooter");

		initServos(robotMap, getName());
		initSolenoids(robotMap, getName());
		initSparks(robotMap, getName());

		hood = getServo("hood");
		stop = getSolenoid("stop");

		shooter = getSpark("shooter");
		shooterPID = getSparkPID("shooter");
		shooterEncoder = getSparkEncoder("shooter");
		shooter.setIdleMode(IdleMode.kCoast);

		layout = tab.getLayout("Shooter", BuiltInLayouts.kList);
	}

	@Override
	public void periodic() {
		outputToSmartDashboard();
	}

	public void setSpeed() {
		setPoint=calculateSpeed();
		shooterPID.setReference(setPoint, ControlType.kVelocity);
	}

	public void stop() {
		shooter.stopMotor();
	}

	public double getSetpoint() {
		return setPoint;
	}

	private double getError() {
		return shooterEncoder.getVelocity() - getSetpoint();
	}

	public boolean isOnTarget() {
		return Math.abs(getError())<Constants.shooter.onTargetTolerance;
	}

	public double calculateSpeed() {
		ty = NetworkTableInstance.getDefault().getTable("limelight").getEntry("ty").getDouble(0);
		distance = ((3.8883 * Math.pow(10, 6)) * Math.pow((ty + 31.2852), -4.9682) + 113.358) / (ty + 31.2852)
				- 0.978398;
		if (distance < 3) {

		} else if (distance < 6) {

		} else if (distance < 9) {

		} else {

		}
		return speed;
	}

	public void outputToSmartDashboard() {
		layout.add("shooter_RPM", shooterEncoder.getVelocity()).withWidget(BuiltInWidgets.kGraph);
		layout.add("hood_Angle", hood.getAngle());
		layout.add("hood_Break", stop.get()).withWidget(BuiltInWidgets.kBooleanBox);
		layout.add("target_distance", distance);
		layout.add("shooter_on_target", isOnTarget()).withWidget(BuiltInWidgets.kBooleanBox);
	}
}
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
import com.revrobotics.CANSparkMax.SoftLimitDirection;

import org.usfirst.frc6647.robot.Constants;
import org.usfirst.lib6647.loops.ILooper;
import org.usfirst.lib6647.loops.Loop;
import org.usfirst.lib6647.loops.LoopType;
import org.usfirst.lib6647.subsystem.SuperSubsystem;
import org.usfirst.lib6647.subsystem.supercomponents.SuperDigitalInput;
import org.usfirst.lib6647.subsystem.supercomponents.SuperSparkMax;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.geometry.Rotation2d;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInLayouts;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardLayout;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;

public class Turret extends SuperSubsystem implements SuperDigitalInput, SuperSparkMax {
	private CANSparkMax turret;
	private CANPIDController turretPID;
	private CANEncoder turretEncoder;

	private boolean aiming = false;

	private DigitalInput limitReverse, limitForward;
	private double setPoint;

	private ShuffleboardTab tab = Shuffleboard.getTab("Robot");
	private ShuffleboardLayout layout;

	public Turret() {
		super("turret");

		initDigitalInputs(robotMap, getName());
		initSparks(robotMap, getName());

		turret = getSpark("turret");
		turretPID = getSparkPID("turret");
		turretEncoder = getSparkEncoder("turret");
		limitReverse = getDigitalInput("limitReverse");
		limitForward = getDigitalInput("limitForward");
		turret.setIdleMode(IdleMode.kBrake);

		turretPID.setP(Constants.turret.kP);

		turret.enableSoftLimit(SoftLimitDirection.kForward, true);
		turret.enableSoftLimit(SoftLimitDirection.kReverse, true);

		turret.setSoftLimit(SoftLimitDirection.kForward, Constants.turret.fowardLimit);
		turret.setSoftLimit(SoftLimitDirection.kReverse, Constants.turret.reverseLimit);
		layout = tab.getLayout("Turret", BuiltInLayouts.kList);
	}

	public void setDesiredAngle(Rotation2d angle) {
		setPoint = (angle.getRadians() / (2 * Math.PI * Constants.turret.rotationsPerTick));
		turretPID.setReference(setPoint, ControlType.kPosition);

	}

	public void setOpenLoop(double speed) {
		turret.set(speed);
	}

	public void reset(Rotation2d actual_rotation) {
		turretEncoder.setPosition(actual_rotation.getRadians() / (2 * Math.PI * Constants.turret.rotationsPerTick));
	}

	public Rotation2d getAngle() {
		return new Rotation2d(Constants.turret.rotationsPerTick * turretEncoder.getPosition() * 2 * Math.PI);
	}

	public boolean getForwardLimitSwitch() {

		return limitForward.get();
	}

	public boolean getReverseLimitSwitch() {
		return limitReverse.get();
	}

	public double getSetpoint() {
		return setPoint;
	}

	private double getError() {
		return getAngle().getDegrees() - getSetpoint();
	}

	// We are "OnTarget" if we are in position mode and close to the setpoint.
	public boolean isOnTarget() {
		return Math.abs(getError()) < Constants.turret.onTargetTolerance;

	}

	public void stop() {
		setOpenLoop(0);
	}

	public void outputToSmartDashboard() {
		layout.add("turret_error", getError());
		layout.add("turret_angle", getAngle().getDegrees()).withWidget(BuiltInWidgets.kGyro);
		layout.add("turret_setpoint", getSetpoint());
		layout.add("turret_fwd_limit", getForwardLimitSwitch()).withWidget(BuiltInWidgets.kBooleanBox);
		layout.add("turret_rev_limit", getReverseLimitSwitch()).withWidget(BuiltInWidgets.kBooleanBox);
		layout.add("turret_on_target", isOnTarget()).withWidget(BuiltInWidgets.kBooleanBox);
	}

	public void zeroSensors() {
		reset(new Rotation2d());
	}

	@Override
	public void periodic() {
		outputToSmartDashboard();
	}

	@Override
	public void registerLoops(ILooper looper) {
		looper.register(new Loop() { // Auto-aim loop.
			private NetworkTable limelight;
			private NetworkTableEntry ty;

			@Override
			public void onFirstStart(double timestamp) {
			}

			@Override
			public void onStart(double timestamp) {
				synchronized (Turret.this) {
					limelight = NetworkTableInstance.getDefault().getTable("limelight");
					ty = limelight.getEntry("ty");

					System.out.println("Started Turret Auto-aim at: " + timestamp + "!");
				}
			}

			@Override
			public void onLoop(double timestamp) {
				if (!aiming)
					return;

				synchronized (Turret.this) {
					turretPID.setReference(ty.getDouble(turretEncoder.getPosition()), ControlType.kPosition);
				}
			}

			@Override
			public void onStop(double timestamp) {
				System.out.println("Stopped Turret Auto-aim at: " + timestamp + ".");
			}

			@Override
			public LoopType getType() {
				return LoopType.ENABLED;
			}

		});

	}
}

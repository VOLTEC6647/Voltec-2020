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

import org.usfirst.lib6647.loops.ILooper;
import org.usfirst.lib6647.loops.Loop;
import org.usfirst.lib6647.loops.LoopType;
import org.usfirst.lib6647.subsystem.SuperSubsystem;
import org.usfirst.lib6647.subsystem.supercomponents.SuperSparkMax;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;

public class Turret extends SuperSubsystem implements SuperSparkMax {
	private CANSparkMax turret;
	private CANPIDController turretPID;
	private CANEncoder turretEncoder;

	private boolean aiming = false;

	public Turret() {
		super("turret");

		turret = getSpark("turret");
		turretPID = getSparkPID("turret");
		turretEncoder = getSparkEncoder("turret");
	}

	public void setAiming(boolean aiming) {
		this.aiming = aiming;
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

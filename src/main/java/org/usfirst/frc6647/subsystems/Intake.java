package org.usfirst.frc6647.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;

import org.usfirst.frc6647.robot.Robot;
import org.usfirst.lib6647.subsystem.SuperSubsystem;
import org.usfirst.lib6647.subsystem.hypercomponents.HyperTalon;
import org.usfirst.lib6647.subsystem.supercomponents.SuperTalon;

/**
 * Ball intake mechanism {@link SuperSubsystem} implementation.
 */
public class Intake extends SuperSubsystem implements SuperTalon {
	/** Main {@link HyperTalon} used by the Robot's {@link Intake}. */
	private HyperTalon intake;

	/**
	 * A lambda of every {@link SuperSubsystem Subsystem} must be provided to the
	 * {@link Robot} via the {@link LooperRobot#registerSubsystems} method.
	 */
	public Intake() {
		super("intake");

		initTalons(robotMap, getName());
		intake = getTalon("intake");
	}

	/**
	 * Set the {@link Intake}'s main {@link #intake motor} to a specific speed, for
	 * the given {@link ControlMode}.
	 * 
	 * @param mode  The mode at which to set the {@link HyperTalon}
	 * @param speed The speed at which to set the {@link HyperTalon}
	 */
	public void setMotor(ControlMode mode, double speed) {
		intake.set(mode, speed);
	}

	/**
	 * Stops the main {@link #intake} motor dead in its tracks.
	 */
	public void stopMotor() {
		intake.stopMotor();
	}
}
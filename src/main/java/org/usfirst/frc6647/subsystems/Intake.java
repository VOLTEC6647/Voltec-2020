package org.usfirst.frc6647.subsystems;

import org.usfirst.frc6647.robot.Robot;
import org.usfirst.lib6647.loops.LooperRobot;
import org.usfirst.lib6647.oi.JController;
import org.usfirst.lib6647.subsystem.SuperSubsystem;
import org.usfirst.lib6647.subsystem.hypercomponents.HyperTalon;
import org.usfirst.lib6647.subsystem.supercomponents.SuperTalon;

/**
 * Ball intake mechanism {@link SuperSubsystem} implementation.
 */
public class Intake extends SuperSubsystem implements SuperTalon {
	/** Main {@link HyperTalon} used by the Robot's {@link Intake}. */
	private HyperTalon intake;
	/** {@link JController} instance used by the Robot. */
	private JController joystick;

	/**
	 * A lambda of every {@link SuperSubsystem Subsystem} must be provided to the
	 * {@link Robot} via the {@link LooperRobot#registerSubsystems} method.
	 */
	public Intake() {
		super("intake");

		initTalons(robotMap, getName());

		intake = getTalon("intake");
		joystick = Robot.getInstance().getJoystick("driver1");

		configureButtonBindings();
	}

	/**
	 * Throw all Command initialization and {@link JController} binding for this
	 * {@link SuperSubsystem} into this method.
	 */
	private void configureButtonBindings() {
		joystick.get("L1").whenPressed(() -> setVoltage(30), this).whenReleased(() -> intake.stopMotor(), this); // Out
		joystick.get("R1").whenPressed(() -> setVoltage(-30), this).whenReleased(() -> intake.stopMotor(), this); // In
	}

	/**
	 * Sets main {@link #intake} motor to the given voltage.
	 * 
	 * @param outputVolts The voltage to set the {@link #intake} motor to.
	 */
	private void setVoltage(double outputVolts) {
		intake.setVoltage(outputVolts);
		intake.feed();
	}
}
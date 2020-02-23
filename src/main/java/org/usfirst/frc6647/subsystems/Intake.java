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
		Runnable ballOut = () -> intake.setVoltage(30); // Ball Out
		Runnable ballIn = () -> intake.setVoltage(-30); // Ball In
		Runnable stopIntake = () -> intake.stopMotor(); // Stop intake motor

		if (joystick.getName().equals("Wireless Controller")) {
			joystick.get("L1").whenPressed(ballOut, this).whenReleased(stopIntake, this);
			joystick.get("R1").whenPressed(ballIn, this).whenReleased(stopIntake, this);
		} else if (joystick.getName().equals("Generic   USB  Joystick")
				|| joystick.getName().toLowerCase().contains("xbox")) {
			joystick.get("LBumper").whenPressed(ballOut, this).whenReleased(stopIntake, this);
			joystick.get("RBumper").whenPressed(ballIn, this).whenReleased(stopIntake, this);
		}
	}
}
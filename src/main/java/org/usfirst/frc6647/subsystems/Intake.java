package org.usfirst.frc6647.subsystems;

import org.usfirst.frc6647.robot.Robot;
import org.usfirst.lib6647.loops.LooperRobot;
import org.usfirst.lib6647.oi.JController;
import org.usfirst.lib6647.subsystem.SuperSubsystem;
import org.usfirst.lib6647.subsystem.hypercomponents.HyperTalon;
import org.usfirst.lib6647.subsystem.hypercomponents.HyperVictor;
import org.usfirst.lib6647.subsystem.supercomponents.SuperVictor;

/**
 * Ball intake mechanism {@link SuperSubsystem} implementation.
 */
public class Intake extends SuperSubsystem implements SuperVictor {
	/** {@link HyperVictor HyperVictors} used by this {@link SuperSubsystem}. */
	private HyperVictor intakeMotor;
	/** {@link JController} instance used by the Robot. */
	private JController joystick;

	/**
	 * A lambda of every {@link SuperSubsystem Subsystem} must be provided to the
	 * {@link Robot} via the {@link LooperRobot#registerSubsystems} method.
	 */
	public Intake() {
		super("intake");
		initVictors(robotMap, getName());

		intakeMotor = getVictor("intakeMotor");
		joystick = Robot.getInstance().getJoystick("driver1");

		configureButtonBindings();
	}

	/**
	 * Throw all Command initialization and {@link JController} binding for this
	 * {@link SuperSubsystem} into this method.
	 */
	private void configureButtonBindings() {
		joystick.get("X").whenPressed(() -> setIntake(9));
	}

	/**
	 * Use {@link HyperTalon talons} as intake.
	 * 
	 * @param voltage The amount of volts to turn intake counterclockwise
	 */
	private void setIntake(double voltage) {
		intakeMotor.setVoltage(voltage);
	}
}
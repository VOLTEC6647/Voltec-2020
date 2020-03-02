package org.usfirst.frc6647.subsystems;

import com.revrobotics.CANPIDController;
import com.revrobotics.CANSparkMax;
import com.revrobotics.ControlType;

import org.usfirst.frc6647.robot.RobotContainer;
import org.usfirst.lib6647.subsystem.SuperSubsystem;
import org.usfirst.lib6647.subsystem.supercomponents.SuperSparkMax;

/**
 * Ball Intake {@link SuperSubsystem} implementation.
 */
public class Intake extends SuperSubsystem implements SuperSparkMax {
	/** Main {@link CANSparkMax} used by this {@link Intake}. */
	private CANSparkMax intake;
	/** {@link CANPIDController} instance of the main {@link #intake motor}. */
	private CANPIDController intakePID;

	/**
	 * Should only need to create a single of instance of {@link Intake this class};
	 * inside the {@link RobotContainer}.
	 */
	public Intake() {
		super("intake");

		// All SuperComponents must be initialized like this. The 'robotMap' Object is
		// inherited from the SuperSubsystem class, while the second argument is simply
		// this Subsystem's name.
		initSparks(robotMap, getName());

		// Additional initialiation & configuration.
		intake = getSpark("intake");
		intakePID = getSparkPID("intake");
	}

	/**
	 * Set the {@link Intake}'s main {@link #intake motor}'s {@link #intakePID PID
	 * Controller} to a specific voltage, in {@link ControlType#kCurrent}.
	 * 
	 * @param voltage The voltage at which to set the {@link #intake motor}'s
	 *                {@link #intakePID PID Controller} to
	 */
	public void setMotorVoltage(double voltage) {
		intakePID.setReference(voltage, ControlType.kCurrent);
	}

	/**
	 * Stops the {@link Intake}'s main {@link #intake motor} dead in its tracks.
	 */
	public void stopMotor() {
		intake.stopMotor();
	}
}
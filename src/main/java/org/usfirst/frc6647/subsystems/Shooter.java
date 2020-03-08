package org.usfirst.frc6647.subsystems;

import com.revrobotics.CANPIDController;
import com.revrobotics.CANSparkMax;
import com.revrobotics.ControlType;

import org.usfirst.frc6647.robot.Constants;
import org.usfirst.frc6647.robot.RobotContainer;
import org.usfirst.lib6647.subsystem.SuperSubsystem;
import org.usfirst.lib6647.subsystem.hypercomponents.HyperDoubleSolenoid;
import org.usfirst.lib6647.subsystem.hypercomponents.HyperSolenoid;
import org.usfirst.lib6647.subsystem.hypercomponents.HyperSparkMax;
import org.usfirst.lib6647.subsystem.supercomponents.SuperDoubleSolenoid;
import org.usfirst.lib6647.subsystem.supercomponents.SuperServo;
import org.usfirst.lib6647.subsystem.supercomponents.SuperSparkMax;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Servo;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;

/**
 * {@link SuperSubsystem} implementation of a {@link Shooter}.
 */
public class Shooter extends SuperSubsystem implements SuperDoubleSolenoid, SuperServo, SuperSparkMax {
	/** {@link Servo} that controls the {@link Shooter}'s {@link #hood}. */
	private Servo hood;
	/**
	 * {@link HyperDoubleSolenoid} to stop the {@link Shooter}'s {@link CANSparkMax
	 * motor}.
	 */
	private HyperDoubleSolenoid stop;
	/** {@link HyperSparkMax} used by this {@link Shooter subsystem}. */
	private HyperSparkMax shooter;

	/** Stores current {@link #setpoint speed goal}. */
	private double setpoint;

	/**
	 * Should only need to create a single of instance of {@link Shooter this
	 * class}; inside the {@link RobotContainer}.
	 */
	public Shooter() {
		super("shooter");

		// All SuperComponents must be initialized like this. The 'robotMap' Object is
		// inherited from the SuperSubsystem class, while the second argument is simply
		// this Subsystem's name.
		initDoubleSolenoids(robotMap, getName());
		initServos(robotMap, getName());
		initSparks(robotMap, getName());

		// Additional initialiation & configuration.
		hood = getServo("hood");
		stop = getDoubleSolenoid("stop");

		shooter = getSpark("shooter");
		// ...
	}

	@Override
	public void outputToShuffleboard() {
		try {
			layout.addNumber("shooterRPM", shooter.getEncoder()::getVelocity);
			layout.addNumber("hoodAngle", hood::getAngle);
			layout.addNumber("setpoint", this::getSetpoint);
			layout.addBoolean("onTarget", this::onTarget).withWidget(BuiltInWidgets.kBooleanBox);
		} catch (NullPointerException e) {
			var error = String.format("[!] COULD NOT OUTPUT SUBSYSTEM '%1$s':\n\t%2$s.", getName(),
					e.getLocalizedMessage());

			System.out.println(error);
			DriverStation.reportWarning(error, false);
		}
	}

	/**
	 * Sets the {@link Shooter}'s {@link #shooter motor} to the specified rpm, and
	 * updates both the setpoint {@link #layout layout} and {@link #setpoint
	 * variable}.
	 * 
	 * @param rpm The rpm at which to turn the {@link #shooter}
	 */
	public void setMotor(double rpm) {
		setpoint = rpm;
		shooter.getPIDController().setReference(rpm, ControlType.kVelocity);
	}

	/**
	 * Sets the {@link Shooter}'s {@link #shooter motor} to the specified
	 * percentage.
	 * 
	 * @param percentage
	 */
	public void setMotorPercentage(double percentage) {
		shooter.set(percentage);
	}

	/**
	 * Stops the {@link #shooter shooter motor} dead in its tracks.
	 */
	public void stopMotor() {
		shooter.stopMotor();
	}

	/**
	 * Toggles the {@link Shooter}'s {@link #stop brake} {@link HyperSolenoid}, to
	 * stop the {@link Shooter}'s {@link CANSparkMax motor}.
	 */
	public void toggleHoodBrake() {
		stop.toggle();
	}

	/**
	 * Checks if the {@link Shooter}'s {@link CANSparkMax motor} is running at the
	 * target speed.
	 * 
	 * @return Whether or not the {@link Shooter}'s {@link CANSparkMax motor} has
	 *         reached the target speed.
	 */
	public boolean onTarget() {
		return Math.abs(getError()) < Constants.ShooterConstants.tolerance;
	}

	/**
	 * Gets the error between the current {@link Shooter}'s {@link CANSparkMax
	 * motor} and the {@link #setpoint goal speed}.
	 * 
	 * @return The {@link #shooterPID}'s error
	 */
	public double getError() {
		return shooter.getEncoder().getVelocity() - setpoint;
	}

	/**
	 * Gets the {@link #shooter}'s {@link CANPIDController}'s {@link #setpoint}.
	 * 
	 * @return The {@link CANPIDController}'s {@link #setpoint}
	 */
	public double getSetpoint() {
		return setpoint;
	}
}
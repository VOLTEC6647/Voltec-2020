package org.usfirst.frc6647.subsystems;

import org.usfirst.frc6647.robot.RobotContainer;
import org.usfirst.lib6647.subsystem.SuperSubsystem;
import org.usfirst.lib6647.subsystem.hypercomponents.HyperDoubleSolenoid;
import org.usfirst.lib6647.subsystem.hypercomponents.HyperFalcon;
import org.usfirst.lib6647.subsystem.supercomponents.SuperDoubleSolenoid;
import org.usfirst.lib6647.subsystem.supercomponents.SuperFalcon;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;

/**
 * Ball {@link Intake} {@link SuperSubsystem} implementation.
 */
public class Intake extends SuperSubsystem implements SuperDoubleSolenoid, SuperFalcon {
	/** Main {@link HyperFalcon} used by this {@link Intake subsystem}. */
	private HyperFalcon intake;
	/** {@link HyperDoubleSolenoid} used by this {@link Intake subsystem}. */
	private HyperDoubleSolenoid intakePiston;

	/**
	 * Should only need to create a single of instance of {@link Intake this class};
	 * inside the {@link RobotContainer}.
	 */
	public Intake() {
		super("intake");

		// All SuperComponents must be initialized like this. The 'robotMap' Object is
		// inherited from the SuperSubsystem class, while the second argument is simply
		// this Subsystem's name.
		initDoubleSolenoids(robotMap, getName());
		initFalcons(robotMap, getName());

		// Additional initialiation & configuration.
		intake = getFalcon("intake");

		intakePiston = getDoubleSolenoid("intakePiston");
		// ...
	}

	@Override
	public void outputToShuffleboard() {
		try {
			layout.add(intake).withWidget(BuiltInWidgets.kSpeedController);
			layout.add(intakePiston);
		} catch (NullPointerException e) {
			var error = String.format("[!] COULD NOT OUTPUT SUBSYSTEM '%1$s':\n\t%2$s.", getName(),
					e.getLocalizedMessage());

			System.out.println(error);
			DriverStation.reportWarning(error, false);
		}
	}

	/**
	 * Sets the {@link Intake}'s main {@link #intake motor}'s speed value.
	 * 
	 * @param speed The speed at which to set the {@link #intake motor} to
	 */
	public void setMotorSpeed(double speed) {
		intake.set(speed);
	}

	/**
	 * Stops the {@link Intake}'s main {@link #intake motor} dead in its tracks.
	 */
	public void stopMotor() {
		intake.stopMotor();
	}

	/**
	 * Toggles the front {@link Intake} {@link #intakePiston solenoid}.
	 */
	public void toggleSolenoid() {
		intakePiston.toggle();
	}
}
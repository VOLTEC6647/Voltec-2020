package org.usfirst.frc6647.robot;

import org.usfirst.frc6647.subsystems.Chassis;
import org.usfirst.frc6647.subsystems.Intake;
import org.usfirst.lib6647.loops.LoopContainer;
import org.usfirst.lib6647.oi.JController;

import edu.wpi.first.wpilibj.GenericHID.Hand;

/**
 * A 'Container' class for the {@link Robot}, which contains all of the
 * {@link Robot}'s {@link Loop loops}, {@link SuperSubsystem subsystems}, and
 * {@link JController joysticks}.
 */
public class RobotContainer extends LoopContainer {
	/** The {@link Robot}'s main {@link Chassis} instance. */
	private final Chassis chassis;
	/** The {@link Robot}'s main {@link Intake} instance. */
	private final Intake intake;

	/**
	 * Constructor for the main 'Container' class for the {@link Robot}, which
	 * contains all of the {@link Robot}'s {@link Loop loops}, {@link SuperSubsystem
	 * subsystems}, and {@link JController joysticks}.
	 */
	public RobotContainer() {
		// Initialize Joysticks.
		initJoysticks();

		// Initialize every Subsystem.
		chassis = new Chassis();
		intake = new Intake();

		// Register each initialized Subsystem.
		registerSubsystems(chassis, intake);
	}

	/**
	 * Run any {@link JController} initialization here.
	 */
	private void initJoysticks() {
		// Create JController object.
		var driver1 = new JController(0);

		System.out.printf("Found: '%s'!\n", driver1.getName());

		if (driver1.getName().equals("Wireless Controller")) {
			driver1.setXY(Hand.kLeft, 0, 1);
			driver1.setXY(Hand.kRight, 2, 5);
		} else if (driver1.getName().equals("Generic   USB  Joystick")) {
			driver1.setXY(Hand.kLeft, 0, 1);
			driver1.setXY(Hand.kRight, 2, 4);
		} else if (driver1.getName().toLowerCase().contains("xbox")
				|| driver1.getName().equals("Controller (Gamepad F310)")) {
			driver1.setXY(Hand.kLeft, 0, 1);
			driver1.setXY(Hand.kLeft, 4, 5);
		}

		// Register each instantiated JController object in the joysticks HashMap.
		registerJoystick(driver1, "driver1");
	}
}
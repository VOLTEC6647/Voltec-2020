package org.usfirst.frc6647.robot;

import org.usfirst.frc6647.subsystems.Chassis;
import org.usfirst.frc6647.subsystems.Intake;
import org.usfirst.frc6647.subsystems.Shooter;
import org.usfirst.frc6647.subsystems.Turret;
import org.usfirst.lib6647.loops.Loop;
import org.usfirst.lib6647.loops.LoopContainer;
import org.usfirst.lib6647.oi.JController;
import org.usfirst.lib6647.subsystem.SuperSubsystem;

import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj2.command.Command;

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
	private final Shooter shooter;
	private final Turret turret;
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
		shooter = new Shooter();
		turret = new Turret();

		// Register each initialized Subsystem.
		registerSubsystems(chassis, intake);

		configureButtonBindings();
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

	/**
	 * Throw all {@link Command} initialization and {@link JController} binding into
	 * this method.
	 */
	private void configureButtonBindings() {
		var driver1 = getJoystick("driver1");

		// Chassis commands.
		Runnable prepareSong = () -> chassis.prepareSong(); // Prepare a song to play.
		Runnable toggleSong = () -> chassis.toggleSong(); // Play/pause current song.

		Runnable enableTurbo = () -> chassis.setLimiter(1); // Zu schnell!
		Runnable disableTurbo = () -> chassis.setLimiter(0.5); // Zu langsam...
		// ...

		// Intake commands.
		Runnable ballOut = () -> intake.setMotorVoltage(40); // Ball out.
		Runnable ballIn = () -> intake.setMotorVoltage(-40); // Ball in.
		Runnable stopIntake = () -> intake.stopMotor(); // Stop intake motor.
		// ...

		if (driver1.getName().equals("Wireless Controller")) { // PS4 controller.
			driver1.get("Options").whenPressed(prepareSong);
			driver1.get("L2").and(driver1.get("R2")).and(driver1.get("Share")).whenActive(toggleSong);
			driver1.get("L2").whenPressed(enableTurbo).whenReleased(disableTurbo);

			driver1.get("L1").whenPressed(ballOut, intake).whenReleased(stopIntake, intake);
			driver1.get("R1").whenPressed(ballIn, intake).whenReleased(stopIntake, intake);
		} else if (driver1.getName().equals("Generic   USB  Joystick")) { // A random generic controller I own.
			driver1.get("Start").whenPressed(prepareSong);
			driver1.get("LTrigger").and(driver1.get("RTrigger")).and(driver1.get("Select")).whenActive(toggleSong);
			driver1.get("LTrigger").whenPressed(enableTurbo).whenReleased(disableTurbo);

			driver1.get("LBumper").whenPressed(ballOut, intake).whenReleased(stopIntake, intake);
			driver1.get("RBumper").whenPressed(ballIn, intake).whenReleased(stopIntake, intake);
		} else if (driver1.getName().toLowerCase().contains("xbox")) { // Any XBOX controller.
			driver1.get("Start").whenPressed(prepareSong);
			driver1.get("LTrigger").and(driver1.get("RTrigger")).and(driver1.get("Back")).whenActive(toggleSong);
			driver1.get("LTrigger").whenPressed(enableTurbo).whenReleased(disableTurbo);

			driver1.get("LBumper").whenPressed(ballOut, intake).whenReleased(stopIntake, intake);
			driver1.get("RBumper").whenPressed(ballIn, intake).whenReleased(stopIntake, intake);
		}
	}
}
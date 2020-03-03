package org.usfirst.frc6647.robot;

import org.usfirst.frc6647.subsystems.Chassis;
import org.usfirst.frc6647.subsystems.Elevator;
import org.usfirst.frc6647.subsystems.Indexer;
import org.usfirst.frc6647.subsystems.Intake;
import org.usfirst.frc6647.subsystems.Shooter;
import org.usfirst.frc6647.subsystems.Turret;
import org.usfirst.lib6647.loops.Loop;
import org.usfirst.lib6647.loops.LoopContainer;
import org.usfirst.lib6647.oi.JController;
import org.usfirst.lib6647.subsystem.SuperSubsystem;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.geometry.Rotation2d;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.FunctionalCommand;
import edu.wpi.first.wpilibj2.command.StartEndCommand;

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
	/** The {@link Robot}'s main {@link Shooter} instance. */
	private final Shooter shooter;
	/** The {@link Robot}'s main {@link Turret} instance. */
	private final Turret turret;
	/** The {@link Robot}'s main {@link Indexer} instance. */
	private final Indexer indexer;
	/** The {@link Robot}'s main {@link Elevator} instance. */
	private final Elevator elevator;

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
		indexer = new Indexer();
		elevator = new Elevator();

		// Register each initialized Subsystem.
		registerSubsystems(chassis, intake, shooter, turret, indexer, elevator);

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
		StartEndCommand toggleTurbo = new StartEndCommand(chassis::toggleReduction, chassis::toggleReduction);
		// ...

		// Intake commands.
		Runnable ballStop = () -> { // Stop intake, indexer, and pulley motors.
			intake.stopMotor();
			indexer.stopIndexer();
			indexer.stopPulley();
		};

		StartEndCommand ballOut = new StartEndCommand(() -> { // Ball out.
			intake.setMotorVoltage(40);
			indexer.setIndexerVoltage(40, 40);
			indexer.setPulleyVoltage(40, 40);
		}, ballStop, intake, indexer);
		StartEndCommand ballIn = new StartEndCommand(() -> { // Ball in.
			intake.setMotorVoltage(-40);
			indexer.setIndexerVoltage(-40, -40);
			indexer.setPulleyVoltage(-40, -40);
		}, ballStop, intake, indexer);
		// ...

		// Turret commands.
		FunctionalCommand zeroTurret = new FunctionalCommand(() -> turret.reset(Rotation2d.fromDegrees(0)),
				() -> turret.setMotor(-0.4), interrupted -> turret.reset(Rotation2d.fromDegrees(0)),
				turret::getReverseLimitSwitch, turret);
		// ...

		try {
			driver1.get("Options", "Start").whenPressed(chassis::prepareSong);
			driver1.get("L2", "LTrigger").and(driver1.get("R2", "RTrigger")).and(driver1.get("Share", "Select"))
					.whenActive(chassis::toggleSong);
			driver1.get("L2", "LTrigger").whileHeld(toggleTurbo);

			driver1.get("L1", "LBumper").whileHeld(ballOut);
			driver1.get("R1", "RBumper").whileHeld(ballIn);

			driver1.get("dPadLeft").whenPressed(zeroTurret);
		} catch (NullPointerException e) {
			System.out.println(e.getLocalizedMessage());
			DriverStation.reportError(e.getLocalizedMessage(), false);
			System.exit(1);
		}
	}
}
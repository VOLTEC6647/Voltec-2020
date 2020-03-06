package org.usfirst.frc6647.robot;

import org.usfirst.frc6647.subsystems.Chassis;
import org.usfirst.frc6647.subsystems.Elevator;
import org.usfirst.frc6647.subsystems.Gyro;
import org.usfirst.frc6647.subsystems.Indexer;
import org.usfirst.frc6647.subsystems.Intake;
import org.usfirst.frc6647.subsystems.Shooter;
import org.usfirst.frc6647.subsystems.Vision;
import org.usfirst.lib6647.loops.Loop;
import org.usfirst.lib6647.loops.LoopContainer;
import org.usfirst.lib6647.oi.JController;
import org.usfirst.lib6647.subsystem.SuperSubsystem;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.controller.ProfiledPIDController;
import edu.wpi.first.wpilibj.trajectory.TrapezoidProfile;
import edu.wpi.first.wpilibj2.command.ProfiledPIDCommand;
import edu.wpi.first.wpilibj2.command.StartEndCommand;

/**
 * A 'Container' class for the {@link Robot}, which contains all of the
 * {@link Robot}'s {@link Loop loops}, {@link SuperSubsystem subsystems}, and
 * {@link JController joysticks}.
 */
public class RobotContainer extends LoopContainer {
	/** The {@link Robot}'s {@link Chassis} instance. */
	private Chassis chassis;
	/** The {@link Robot}'s {@link Gyro} instance. */
	private Gyro gyro;
	/** The {@link Robot}'s {@link Intake} instance. */
	private Intake intake;
	/** The {@link Robot}'s {@link Shooter} instance. */
	private Shooter shooter;
	/** The {@link Robot}'s {@link Indexer} instance. */
	private Indexer indexer;
	/** The {@link Robot}'s {@link Elevator} instance. */
	private Elevator elevator;
	/** The {@link Robot}'s {@link Vision} instance. */
	private Vision vision;

	@Override
	public void initSubsystems() {
		// Initialize every Subsystem.
		chassis = new Chassis();
		gyro = new Gyro();
		intake = new Intake();
		shooter = new Shooter();
		indexer = new Indexer();
		elevator = new Elevator();
		vision = new Vision();

		// Register each initialized Subsystem.
		registerSubsystems(chassis, gyro, intake, shooter, indexer, elevator);
	}

	@Override
	public void initJoysticks() {
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

	@Override
	public void configureButtonBindings() {
		var driver1 = getJoystick("driver1");

		// Chassis commands.
		var toggleTurbo = new StartEndCommand(chassis::toggleReduction, chassis::toggleReduction);
		// ...

		// Intake commands.
		Runnable ballStop = () -> { // Stop intake, indexer, and pulley motors.
			intake.stopMotor();
			indexer.stopIndexer();
			indexer.stopPulley();
		};

		// Intake commands.
		var intakeIn = new StartEndCommand(() -> intake.setMotorVoltage(-40), ballStop);
		var intakeOut = new StartEndCommand(() -> intake.setMotorVoltage(40), ballStop);
		var indexerIn = new StartEndCommand(() -> indexer.setIndexerVoltage(-40, -40), ballStop);
		var indexerOut = new StartEndCommand(() -> indexer.setIndexerVoltage(40, 40), ballStop);
		var pulleyIn = new StartEndCommand(() -> indexer.setPulleyVoltage(-40, -40), ballStop);
		var pulleyOut = new StartEndCommand(() -> indexer.setPulleyVoltage(40, 40), ballStop);

		var ballOut = new StartEndCommand(() -> { // Ball out.
			intake.setMotorVoltage(40);
			indexer.setIndexerVoltage(40, 40);
			indexer.setPulleyVoltage(40, 40);
		}, ballStop, intake, indexer);
		var ballIn = new StartEndCommand(() -> { // Ball in.
			intake.setMotorVoltage(-40);
			indexer.setIndexerVoltage(-40, -40);
			indexer.setPulleyVoltage(-40, -40);
		}, ballStop, intake, indexer);
		// ...

		// Vision commands.
		var aimChassis = new ProfiledPIDCommand(
				new ProfiledPIDController(Constants.Aim.kP, Constants.Aim.kI, Constants.Aim.kD,
						new TrapezoidProfile.Constraints(Constants.Aim.maxVelocity, Constants.Aim.maxAceleration)),
				vision::getHorizontalRotation, () -> 0, (output, setpoint) -> chassis.arcadeDrive(0, output), chassis,
				vision);
		// ...

		try {
			driver1.get("Options", "Start").whenPressed(chassis::prepareSong);
			driver1.get("L2", "LTrigger").and(driver1.get("R2", "RTrigger")).and(driver1.get("Share", "Select"))
					.whenActive(chassis::toggleSong);
			driver1.get("L2", "LTrigger").whileHeld(toggleTurbo);

			// driver1.get("L1", "LBumper").whileHeld(ballOut);
			// driver1.get("R1", "RBumper").whileHeld(ballIn);

			driver1.get("dPadDown").whileHeld(intakeOut);
			driver1.get("dPadRight").whileHeld(indexerOut);
			driver1.get("dPadUp").whileHeld(pulleyOut);

			driver1.get("X").whileHeld(intakeIn);
			driver1.get("Square").whileHeld(indexerIn);
			driver1.get("Triangle").whileHeld(pulleyIn);

			driver1.get("Touchpad").whileHeld(aimChassis);
		} catch (NullPointerException e) {
			System.out.println(e.getLocalizedMessage());
			DriverStation.reportError(e.getLocalizedMessage(), false);
			System.exit(1);
		}
	}
}
package org.usfirst.frc6647.robot;

import static org.usfirst.frc6647.robot.Constants.AutoConstants.kMaxAccelerationMetersPerSecondSquared;
import static org.usfirst.frc6647.robot.Constants.AutoConstants.kMaxSpeedMetersPerSecond;
import static org.usfirst.frc6647.robot.Constants.AutoConstants.kPDriveVel;
import static org.usfirst.frc6647.robot.Constants.AutoConstants.kRamseteB;
import static org.usfirst.frc6647.robot.Constants.AutoConstants.kRamseteZeta;
import static org.usfirst.frc6647.robot.Constants.AutoConstants.kaVoltSecondsSquaredPerMeter;
import static org.usfirst.frc6647.robot.Constants.AutoConstants.ksVolts;
import static org.usfirst.frc6647.robot.Constants.AutoConstants.kvVoltSecondsPerMeter;
import static org.usfirst.frc6647.robot.Constants.ShooterConstants.behindTrenchAngle;
import static org.usfirst.frc6647.robot.Constants.ShooterConstants.behindTrenchRPM;
import static org.usfirst.frc6647.robot.Constants.ShooterConstants.cursedAngle;
import static org.usfirst.frc6647.robot.Constants.ShooterConstants.cursedRPM;
import static org.usfirst.frc6647.robot.Constants.ShooterConstants.initiationLineAngle;
import static org.usfirst.frc6647.robot.Constants.ShooterConstants.initiationLineRPM;
import static org.usfirst.frc6647.robot.Constants.ShooterConstants.trenchAngle;
import static org.usfirst.frc6647.robot.Constants.ShooterConstants.trenchRPM;

import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

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
import edu.wpi.first.wpilibj.GenericHID.RumbleType;
import edu.wpi.first.wpilibj.controller.PIDController;
import edu.wpi.first.wpilibj.controller.RamseteController;
import edu.wpi.first.wpilibj.controller.SimpleMotorFeedforward;
import edu.wpi.first.wpilibj.geometry.Pose2d;
import edu.wpi.first.wpilibj.geometry.Rotation2d;
import edu.wpi.first.wpilibj.geometry.Translation2d;
import edu.wpi.first.wpilibj.trajectory.TrajectoryConfig;
import edu.wpi.first.wpilibj.trajectory.TrajectoryGenerator;
import edu.wpi.first.wpilibj.trajectory.constraint.DifferentialDriveVoltageConstraint;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.FunctionalCommand;
import edu.wpi.first.wpilibj2.command.RamseteCommand;
import edu.wpi.first.wpilibj2.command.StartEndCommand;

/**
 * A 'Container' class for the {@link Robot}, which contains all of the
 * {@link Robot}'s {@link Loop loops}, {@link SuperSubsystem subsystems}, and
 * {@link JController joysticks}.
 */
public class RobotContainer extends LoopContainer {
	/** The {@link Robot}'s {@link Chassis} instance. */
	private Chassis chassis;
	/** The {@link Robot}'s {@link Intake} instance. */
	private Intake intake;
	/** The {@link Robot}'s {@link Turret} instance. */
	private Turret turret;
	/** The {@link Robot}'s {@link Shooter} instance. */
	private Shooter shooter;
	/** The {@link Robot}'s {@link Indexer} instance. */
	private Indexer indexer;
	/** The {@link Robot}'s {@link Elevator} instance. */
	private Elevator elevator;

	@Override
	public void initSubsystems() {
		// Initialize every Subsystem.
		chassis = new Chassis();
		intake = new Intake();
		turret = new Turret();
		shooter = new Shooter();
		indexer = new Indexer();
		elevator = new Elevator();

		// Register each initialized Subsystem.
		registerSubsystems(chassis, intake, turret, shooter, indexer, elevator);
	}

	@Override
	public void initJoysticks() {
		// Create JController object.
		var driver1 = new JController(0);
		var driver2 = new JController(1);

		System.out.printf("Found: '%s'!\n", driver1.getName());

		if (driver1.getName().equals("Wireless Controller")) {
			driver1.setXY(Hand.kLeft, 0, 1);
			driver1.setXY(Hand.kRight, 2, 5);
		} else if (driver1.getName().equals("Sony Computer Entertainment Wireless Controller")
				|| driver1.getName().equals("DragonRise Inc.   Generic   USB  Joystick")) {
			driver1.setXY(Hand.kLeft, 0, 1);
			driver1.setXY(Hand.kRight, 3, 4);
		} else if (driver1.getName().equals("Logitech Extreme 3D")) {
			driver1.setXY(Hand.kLeft, 0, 1);
			driver1.setXY(Hand.kRight, 2, 3);
		} else if (driver1.getName().equals("Generic   USB  Joystick")) {
			driver1.setXY(Hand.kLeft, 0, 1);
			driver1.setXY(Hand.kRight, 2, 4);
		} else if (driver1.getName().toLowerCase().contains("xbox")
				|| driver1.getName().equals("Controller (Gamepad F310)")) {
			driver1.setXY(Hand.kLeft, 0, 1);
			driver1.setXY(Hand.kRight, 4, 5);
		}

		System.out.printf("Found: '%s'!\n", driver2.getName());

		if (driver2.getName().equals("Wireless Controller")) {
			driver2.setXY(Hand.kLeft, 0, 1);
			driver2.setXY(Hand.kRight, 2, 5);
		} else if (driver2.getName().equals("Sony Computer Entertainment Wireless Controller")
				|| driver2.getName().equals("DragonRise Inc.   Generic   USB  Joystick")) {
			driver2.setXY(Hand.kLeft, 0, 1);
			driver2.setXY(Hand.kRight, 3, 4);
		} else if (driver2.getName().equals("Logitech Extreme 3D")) {
			driver2.setXY(Hand.kLeft, 2, 1);
			driver2.setXY(Hand.kRight, 0, 3);
		} else if (driver2.getName().equals("Generic   USB  Joystick")) {
			driver2.setXY(Hand.kLeft, 0, 1);
			driver2.setXY(Hand.kRight, 2, 4);
		} else if (driver2.getName().toLowerCase().contains("xbox")
				|| driver2.getName().equals("Controller (Gamepad F310)")) {
			driver2.setXY(Hand.kLeft, 0, 1);
			driver2.setXY(Hand.kRight, 4, 5);
		}

		// Register each instantiated JController object in the joysticks HashMap.
		registerJoystick(driver1, "driver1");
		registerJoystick(driver2, "driver2");
	}

	@Override
	public void configureButtonBindings() {
		var driver1 = getJoystick("driver1");
		var driver2 = getJoystick("driver2");

		// Chassis commands.
		var toggleReduction = new StartEndCommand(chassis::toggleReduction, chassis::toggleReduction);
		var toggleHeading = new StartEndCommand(chassis::toggleHeading, chassis::toggleHeading);
		// ...

		// Intake commands.
		Runnable ballStop = () -> {
			intake.stopMotor();
			indexer.stopIndexer();
			indexer.stopPulley();
		};
		var ballIn = new StartEndCommand(() -> {
			intake.setMotorSpeed(-0.45);
			indexer.toggleAutoIndexing();
		}, indexer::toggleAutoIndexing);
		Runnable ballOut = () -> {
			intake.setMotorSpeed(0.45);
			indexer.setIndexerSpeed(-1, -1);
			indexer.setPulleySpeed(0.5, 0.5);
		};

		var toggleIntake = new StartEndCommand(intake::toggleSolenoid, intake::toggleSolenoid);
		// ...

		// Elevator commands.
		var climberUp = new StartEndCommand(() -> elevator.setElevatorSpeed(1), elevator::stopElevatorMotor);
		var climberDown = new StartEndCommand(() -> elevator.setElevatorSpeed(-1), elevator::stopElevatorMotor);
		// ...

		// Turret commands.
		var turretLeft = new StartEndCommand(() -> turret.setMotor(-0.2), turret::stopMotor);
		var turretRight = new StartEndCommand(() -> turret.setMotor(0.2), turret::stopMotor);
		var toggleAim = new StartEndCommand(turret::toggleAim, turret::toggleAim);
		// ...

		// Shooter commands.
		BooleanSupplier forever = () -> false; // Runs a command forever, or until it is interrupted.

		Runnable startFeeding = () -> { // My bot lane every game.
			driver2.setRumble(RumbleType.kLeftRumble, 1);
			driver2.setRumble(RumbleType.kRightRumble, 1);

			if (!shooter.onTarget())
				return;

			indexer.setIndexerSpeed(0.8, 0.8);
			indexer.setPulleySpeed(-0.5, -0.5);
		};
		Consumer<Boolean> stopFeeding = interrupted -> { // Wish this was possible in League.
			driver2.setRumble(RumbleType.kLeftRumble, 0);
			driver2.setRumble(RumbleType.kRightRumble, 0);

			indexer.stopIndexer();
			indexer.stopPulley();
			shooter.stopMotor();
		};

		var initiationLineShoot = new FunctionalCommand(() -> shooter.set(initiationLineRPM, initiationLineAngle),
				startFeeding, stopFeeding, forever, indexer, shooter);
		var trenchShoot = new FunctionalCommand(() -> shooter.set(trenchRPM, trenchAngle), startFeeding, stopFeeding,
				forever, indexer, shooter);
		var behindTrenchShoot = new FunctionalCommand(() -> shooter.set(behindTrenchRPM, behindTrenchAngle),
				startFeeding, stopFeeding, forever, indexer, shooter);
		var cursedShoot = new FunctionalCommand(() -> shooter.set(cursedRPM, cursedAngle), startFeeding, stopFeeding,
				forever, indexer, shooter);
		// ...

		try { // Driver 1 commands.
			driver1.get("Options", "Start", "Base12").whenPressed(chassis::prepareSong);
			driver1.get("Touchpad", "Select", "Back", "PS4Btn", "Base11").whenPressed(chassis::toggleSong);

			driver1.get("L2", "LTrigger", "Trigger").whileHeld(toggleReduction);
			driver1.get("R2", "RTrigger", "Thumb6").whileHeld(toggleHeading);

			// For debugging purposes.
			driver1.get("Circle", "B", "Btn2", "Base7").whileHeld(toggleIntake);
			driver1.get("Circle", "B", "Btn2", "Base7").whileHeld(ballIn).whenReleased(ballStop);
			driver1.get("Square", "X", "Btn4", "Base8").whileHeld(ballOut).whenReleased(ballStop);
			driver1.get("Square", "X", "Btn4", "Base8").whileHeld(toggleIntake);
			// ...
		} catch (NullPointerException e) {
			System.out.println(e.getLocalizedMessage());
			DriverStation.reportError(e.getLocalizedMessage(), false);
		}

		try { // Driver 2 commands.
			driver2.get("L2", "LTrigger").whileHeld(toggleIntake);
			driver2.get("L1", "LBumper").whileHeld(ballIn).whenReleased(ballStop);
			driver2.get("R2", "RTrigger").whileHeld(toggleAim);

			driver2.get("Cross", "A", "Btn3").whileHeld(initiationLineShoot);
			driver2.get("Circle", "B", "Btn2").whileHeld(trenchShoot);
			driver2.get("Square", "X", "Btn4").whileHeld(behindTrenchShoot);
			driver2.get("Triangle", "Y", "Btn1").whileHeld(cursedShoot);

			driver2.get("dPadUp").whileHeld(climberUp);
			driver2.get("dPadDown").whileHeld(climberDown);
			driver2.get("dPadLeft").whileHeld(turretLeft);
			driver2.get("dPadRight").whileHeld(turretRight);
		} catch (NullPointerException e) {
			System.out.println(e.getLocalizedMessage());
			DriverStation.reportError(e.getLocalizedMessage(), false);
		}
	}

	/**
	 * Passes the {@link Command autonomous command} to the {@link Robot} class, to
	 * schedule it.
	 * 
	 * @return The {@link Command} to run in autonomous
	 */
	public Command getAutonomousCommand() {
		// Create a voltage constraint to ensure we don't accelerate too fast.
		var autoVoltageConstraint = new DifferentialDriveVoltageConstraint(
				new SimpleMotorFeedforward(ksVolts, kvVoltSecondsPerMeter, kaVoltSecondsSquaredPerMeter),
				chassis.getKinematics(), 10);

		// Create config for trajectory.
		var config = new TrajectoryConfig(kMaxSpeedMetersPerSecond, kMaxAccelerationMetersPerSecondSquared)
				// Add kinematics to ensure max speed is actually obeyed.
				.setKinematics(chassis.getKinematics())
				// Apply the voltage constraint.
				.addConstraint(autoVoltageConstraint);

		// Useful for creating reverse paths.
		var reverseConfig = new TrajectoryConfig(kMaxSpeedMetersPerSecond, kMaxAccelerationMetersPerSecondSquared)
				// Add kinematics to ensure max speed is actually obeyed.
				.setKinematics(chassis.getKinematics())
				// Apply the voltage constraint.
				.addConstraint(autoVoltageConstraint)
				// Set the trajectory as reverse.
				.setReversed(true);

		var startX = 0.0;
		var startY = 0.0;

		// TODO: There is currently only one trajectory, more need to be added.

		// An example trajectory to follow. All units are in meters.
		var exampleTrajectory = TrajectoryGenerator.generateTrajectory(
				new Pose2d(startX, startY, new Rotation2d(Math.PI)), List.of(new Translation2d(-2, 1)),
				new Pose2d(-4, startY, Rotation2d.fromDegrees(0)), config);

		var ramseteCommand = new RamseteCommand(exampleTrajectory, chassis::getPose,
				new RamseteController(kRamseteB, kRamseteZeta),
				new SimpleMotorFeedforward(ksVolts, kvVoltSecondsPerMeter, kaVoltSecondsSquaredPerMeter),
				chassis.getKinematics(), chassis::getWheelSpeeds, new PIDController(kPDriveVel, 0, 0),
				new PIDController(kPDriveVel, 0, 0),
				// RamseteCommand passes volts to the callback.
				chassis::tankDriveVolts, chassis)
						// Lower intake by toggling intake piston.
						.alongWith(new StartEndCommand(intake::toggleSolenoid, intake::toggleSolenoid))
						// Begin auto indexing.
						.alongWith(new StartEndCommand(indexer::toggleAutoIndexing, indexer::toggleAutoIndexing))
						// Time out after 7 seconds.
						.withTimeout(7);

		// Shoot initial three balls.
		var initialShoot = new FunctionalCommand(() -> {
			shooter.set(initiationLineRPM, initiationLineAngle);

			turret.toggleAim();
		}, () -> {
			if (!shooter.onTarget())
				return;

			indexer.setPulleySpeed(-0.5, -0.5);
			indexer.setIndexerSpeed(0.8, 0.8);
		}, interrupted -> {
			indexer.stopIndexer();
			indexer.stopPulley();
			shooter.stopMotor();

			turret.toggleAim();
		}, () -> false).withTimeout(10);

		// Stop everything.
		Runnable stopAll = () -> {
			chassis.stopDrive();
			intake.stopMotor();
			indexer.stopIndexer();
			indexer.stopPulley();
		};

		// Need to test if chaining commands like this actually works.
		return initialShoot.andThen(ramseteCommand).andThen(stopAll);
	}
}
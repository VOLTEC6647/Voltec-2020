package org.usfirst.frc6647.subsystems;

import static org.usfirst.frc6647.robot.Constants.DriveConstants.collisionThresholdDeltaG;
import static org.usfirst.frc6647.robot.Constants.DriveConstants.encoderDistancePerPulse;
import static org.usfirst.frc6647.robot.Constants.DriveConstants.trackWidthMeters;

import java.io.File;
import java.util.List;
import java.util.Random;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.music.Orchestra;

import org.usfirst.frc6647.robot.Robot;
import org.usfirst.frc6647.robot.RobotContainer;
import org.usfirst.lib6647.loops.ILooper;
import org.usfirst.lib6647.loops.Loop;
import org.usfirst.lib6647.loops.LoopType;
import org.usfirst.lib6647.oi.JController;
import org.usfirst.lib6647.subsystem.SuperSubsystem;
import org.usfirst.lib6647.subsystem.hypercomponents.HyperAHRS;
import org.usfirst.lib6647.subsystem.hypercomponents.HyperDoubleSolenoid;
import org.usfirst.lib6647.subsystem.hypercomponents.HyperFalcon;
import org.usfirst.lib6647.subsystem.hypercomponents.HyperSolenoid;
import org.usfirst.lib6647.subsystem.supercomponents.SuperAHRS;
import org.usfirst.lib6647.subsystem.supercomponents.SuperDoubleSolenoid;
import org.usfirst.lib6647.subsystem.supercomponents.SuperFalcon;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Filesystem;
import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.GenericHID.RumbleType;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.geometry.Pose2d;
import edu.wpi.first.wpilibj.geometry.Rotation2d;
import edu.wpi.first.wpilibj.kinematics.DifferentialDriveKinematics;
import edu.wpi.first.wpilibj.kinematics.DifferentialDriveOdometry;
import edu.wpi.first.wpilibj.kinematics.DifferentialDriveWheelSpeeds;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj.util.Units;
import edu.wpi.first.wpilibj2.command.button.Trigger;

/**
 * The {@link Robot}'s {@link Chassis Chassis/Drive} {@link SuperSubsystem}
 * implementation, with arcade drive, {@link #collisionDetected collision
 * detection} and odometry support.
 */
public class Chassis extends SuperSubsystem implements SuperAHRS, SuperDoubleSolenoid, SuperFalcon {
	/** Driver1's {@link JController} instance. */
	private final JController joystick;

	/** {@link HyperFalcon HyperFalcons} used by this {@link Chassis subsystem}. */
	private final HyperFalcon frontLeft, frontRight, backLeft, backRight;
	/** {@link HyperSolenoid} used by this {@link Chassis subsystem}. */
	private final HyperDoubleSolenoid reduction;
	/** Whether the {@link Robot}'s 'heading' is inverted or not. */
	private boolean invertedHeading = false;

	/** {@link HyperAHRS} used by this {@link Chassis subsystem}. */
	private final HyperAHRS navX;
	/** Stores last detected world linear X and Y acceleration values. */
	private double lastWorldLinearAccelX = 0.0, lastWorldLinearAccelY = 0.0;
	/** Whether or not a collision was detected by the {@link #navX}. */
	private boolean collisionDetected = false;

	/** {@link Orchestra} instance, for playing MIDI (.chrp) files. */
	private final Orchestra orchestra;

	/**
	 * Tracks the {@link Robot}'s position and rotation, for path generation
	 * purposes.
	 */
	private final DifferentialDriveOdometry odometry;
	/**
	 * Converts a {@link Chassis} velocity to left and right
	 * {@link DifferentialDrive} speeds.
	 */
	private final DifferentialDriveKinematics kinematics;
	/** Holds the {@link Robot}'s current position and rotation. */
	private Pose2d pose;

	/**
	 * Should only need to create a single of instance of {@link Chassis this
	 * class}; inside the {@link RobotContainer}.
	 */
	public Chassis() {
		super("chassis");

		// All SuperComponents must be initialized like this. The 'robotMap' Object is
		// inherited from the SuperSubsystem class, while the second argument is simply
		// this Subsystem's name.
		initAHRS(robotMap, getName());
		initDoubleSolenoids(robotMap, getName());
		initFalcons(robotMap, getName());

		// Additional initialiation & configuration.
		joystick = Robot.getInstance().getContainer().getJoystick("driver1");

		Runnable setRumble = () -> { // Sets joystick rumble to 1.
			joystick.setRumble(RumbleType.kLeftRumble, 1);
			joystick.setRumble(RumbleType.kRightRumble, 1);
		};
		Runnable stopRumble = () -> { // Stops joystick rumble.
			joystick.setRumble(RumbleType.kLeftRumble, 0.0);
			joystick.setRumble(RumbleType.kRightRumble, 0.0);
		};

		var collision = new Trigger(this::didCollide); // Triggers when a collision is detected.
		collision.whenActive(setRumble, this).whenInactive(stopRumble, this);

		frontLeft = getFalcon("frontLeft");
		frontRight = getFalcon("frontRight");
		backLeft = getFalcon("backLeft");
		backRight = getFalcon("backRight");

		frontLeft.setEncoderDistancePerPulse(-encoderDistancePerPulse);
		frontRight.setEncoderDistancePerPulse(-encoderDistancePerPulse);
		backLeft.setEncoderDistancePerPulse(-encoderDistancePerPulse);
		backRight.setEncoderDistancePerPulse(-encoderDistancePerPulse);

		reduction = getDoubleSolenoid("reduction");
		navX = getAHRS("navX");

		odometry = new DifferentialDriveOdometry(Rotation2d.fromDegrees(-navX.getHeading()),
				new Pose2d(0, 0, new Rotation2d(Math.PI)));
		kinematics = new DifferentialDriveKinematics(trackWidthMeters);

		orchestra = new Orchestra(List.of(frontLeft, backLeft, frontRight, backRight));
		// ...
	}

	@Override
	public void outputToShuffleboard() {
		try {
			layout.add(frontLeft).withWidget(BuiltInWidgets.kSpeedController);
			layout.add(frontRight).withWidget(BuiltInWidgets.kSpeedController);
			layout.add(backLeft).withWidget(BuiltInWidgets.kSpeedController);
			layout.add(backRight).withWidget(BuiltInWidgets.kSpeedController);

			layout.add(reduction);

			layout.addBoolean("headingFlipped", this::getHeading).withWidget(BuiltInWidgets.kBooleanBox);
			layout.addBoolean("orchestraPlaying", orchestra::isPlaying).withWidget(BuiltInWidgets.kBooleanBox);

			layout.add(navX).withWidget(BuiltInWidgets.kGyro);
			layout.addNumber("gyroYaw", navX::getYaw);
			layout.addNumber("gyroHeading", navX::getHeading);
			layout.addBoolean("collisionDetected", this::didCollide).withWidget(BuiltInWidgets.kBooleanBox);
		} catch (NullPointerException e) {
			var error = String.format("[!] COULD NOT OUTPUT SUBSYSTEM '%1$s':\n\t%2$s.", getName(),
					e.getLocalizedMessage());

			System.out.println(error);
			DriverStation.reportWarning(error, false);
		}
	}

	/**
	 * Picks a random song from the 'deploy' directory, and loads it into the
	 * {@link #orchestra} instance.
	 */
	public void prepareSong() {
		var songs = new File(Filesystem.getDeployDirectory() + "/MIDI/").listFiles();
		var song = songs[new Random().nextInt(songs.length)].toString();

		System.out.println("Ready to play: '" + song + "'...");
		orchestra.loadMusic(song);
	}

	/**
	 * Plays/pauses currently loaded song.
	 */
	public void toggleSong() {
		if (!orchestra.isPlaying())
			orchestra.play();
		else
			orchestra.pause();
	}

	/**
	 * Toggles where the {@link Robot}'s 'front' is.
	 */
	public void toggleHeading() {
		invertedHeading = !invertedHeading;
	}

	/**
	 * Toggle {@link HyperFalcon} reductions, on both sides.
	 */
	public void toggleReduction() {
		reduction.toggle();
	}

	/**
	 * Use {@link HyperFalcon falcons} as an arcade drive.
	 * 
	 * @param forward  The drive's forward speed
	 * @param rotation The drive's rotation speed
	 */
	public void arcadeDrive(double forward, double rotation) {
		frontLeft.setWithRamp(forward * (invertedHeading ? -1 : 1), -rotation);
		backLeft.setWithRamp(forward * (invertedHeading ? -1 : 1), -rotation);
		frontRight.setWithRamp(forward * (invertedHeading ? -1 : 1), rotation);
		backRight.setWithRamp(forward * (invertedHeading ? -1 : 1), rotation);
	}

	/**
	 * Use {@link HyperFalcon falcons} as a tank drive, in
	 * {@link ControlMode#PercentOutput}.
	 * 
	 * @param left  The speed at which to set the left side of the {@link Chassis}
	 * @param right The speed at which to set the right side of the {@link Chassis}
	 */
	public void tankDrive(double left, double right) {
		frontLeft.setWithRamp(left * (invertedHeading ? -1 : 1));
		backLeft.setWithRamp(left * (invertedHeading ? -1 : 1));
		frontRight.setWithRamp(right * (invertedHeading ? -1 : 1));
		backRight.setWithRamp(right * (invertedHeading ? -1 : 1));
	}

	/**
	 * Use {@link HyperFalcon falcons} as a tank drive, in voltage.
	 * 
	 * @param leftVoltage  The voltage at which to set the left side of the
	 *                     {@link Chassis}
	 * @param rightVoltage The voltage at which to set the right side of the
	 *                     {@link Chassis}
	 */
	public void tankDriveVolts(double leftVoltage, double rightVoltage) {
		frontLeft.setVoltage(-leftVoltage);
		backLeft.setVoltage(-leftVoltage);
		frontRight.setVoltage(-rightVoltage);
		backRight.setVoltage(-rightVoltage);

		feedDrive();
	}

	/**
	 * Method to individually feed each of the {@link Chassis}' {@link HyperFalcon
	 * falcons}.
	 */
	public void feedDrive() {
		frontLeft.feed();
		frontRight.feed();
		backLeft.feed();
		backRight.feed();
	}

	/**
	 * Method to individually stop each of the {@link Chassis}' {@link HyperFalcon
	 * falcons}.
	 */
	public void stopDrive() {
		frontLeft.stopMotor();
		frontRight.stopMotor();
		backLeft.stopMotor();
		backRight.stopMotor();
	}

	/**
	 * Gets whether or not a {@link #collisionDetected collision was detected} by
	 * the {@link #navX}.
	 * 
	 * @return Whether or not a collision was detected
	 */
	public boolean didCollide() {
		return collisionDetected;
	}

	/**
	 * Gets whether or not the {@link Robot}'s 'front' is flipped.
	 * 
	 * @return The current state of the {@link Robot}'s {@link #inverted heading}
	 */
	public boolean getHeading() {
		return invertedHeading;
	}

	/**
	 * Gets the {@link Chassis}' current {@link #pose}.
	 * 
	 * @return The {@link Chassis}' current {@link Pose2d pose}
	 */
	public Pose2d getPose() {
		return pose;
	}

	/**
	 * Gets the {@link Chassis}' {@link #kinematics} instance, for path generation
	 * purposes.
	 * 
	 * @return The {@link Chassis}' {@link #kinematics} instance
	 */
	public DifferentialDriveKinematics getKinematics() {
		return kinematics;
	}

	/**
	 * Gets the {@link Chassis}' current wheel speeds, for path generation purposes.
	 * 
	 * @return The {@link Chassis}' {@link DifferentialDriveWheelSpeeds wheel
	 *         speeds}
	 */
	public DifferentialDriveWheelSpeeds getWheelSpeeds() {
		return new DifferentialDriveWheelSpeeds(frontLeft.getEncoderRate(), frontRight.getEncoderRate());
	}

	@Override
	public void registerLoops(ILooper looper) {
		looper.register(new Loop() { // Arcade drive loop.
			@Override
			public void onFirstStart(double timestamp) {
			}

			@Override
			public void onStart(double timestamp) {
				synchronized (Chassis.this) {
					System.out.println("Started arcade drive at: " + timestamp + "!");
				}
			}

			@Override
			public void onLoop(double timestamp) {
				if (orchestra.isPlaying()) // Prevents the robot from moving whilst playing a MIDI file.
					return;

				synchronized (Chassis.this) {
					arcadeDrive(joystick.getY(Hand.kLeft), joystick.getX(Hand.kRight));
					feedDrive();
				}
			}

			@Override
			public void onStop(double timestamp) {
				stopDrive();

				System.out.println("Stopped arcade drive at: " + timestamp + ".");
			}

			@Override
			public LoopType getType() {
				return LoopType.TELEOP;
			}
		}, new Loop() { // Gyro loop.
			@Override
			public void onFirstStart(double timestamp) {
				synchronized (Chassis.this) {
					navX.zeroYaw();
				}
			}

			@Override
			public void onStart(double timestamp) {
				synchronized (Chassis.this) {
					System.out.println("Started gyro at: " + timestamp + "!");
				}
			}

			@Override
			public void onLoop(double timestamp) {
				synchronized (Chassis.this) {
					collisionDetected = false;

					var currentWorldLinearAccelX = navX.getWorldLinearAccelX();
					var currentJerkX = currentWorldLinearAccelX - lastWorldLinearAccelX;
					lastWorldLinearAccelX = currentWorldLinearAccelX;

					var currentWorldLinearAccelY = navX.getWorldLinearAccelY();
					var currentJerkY = currentWorldLinearAccelY - lastWorldLinearAccelY;
					lastWorldLinearAccelY = currentWorldLinearAccelY;

					if ((Math.abs(currentJerkX) > collisionThresholdDeltaG)
							|| (Math.abs(currentJerkY) > collisionThresholdDeltaG))
						collisionDetected = true;
				}
			}

			@Override
			public void onStop(double timestamp) {
				System.out.println("Stopped gyro at: " + timestamp + ".");
			}

			@Override
			public LoopType getType() {
				return LoopType.ENABLED;
			}
		}, new Loop() { // Auto loop.
			private NetworkTable table;

			@Override
			public void onFirstStart(double timestamp) {
			}

			@Override
			public void onStart(double timestamp) {
				synchronized (Chassis.this) {
					table = NetworkTableInstance.getDefault().getTable("Live_Dashboard");

					System.out.println("Started auto drive at: " + timestamp + "!");
				}
			}

			@Override
			public void onLoop(double timestamp) {
				synchronized (Chassis.this) {
					pose = odometry.update(Rotation2d.fromDegrees(-navX.getHeading()), frontLeft.getEncoderRate(),
							frontRight.getEncoderRate());

					table.getEntry("robotX").setNumber(Units.metersToFeet(pose.getTranslation().getX()));
					table.getEntry("robotY").setNumber(Units.metersToFeet(pose.getTranslation().getY()));
					table.getEntry("robotHeading").setNumber(pose.getRotation().getRadians());
				}
			}

			@Override
			public void onStop(double timestamp) {
				stopDrive();

				System.out.println("Stopped auto drive at: " + timestamp + ".");
			}

			@Override
			public LoopType getType() {
				return LoopType.AUTO;
			}
		});
	}
}

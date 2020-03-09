package org.usfirst.frc6647.subsystems;

import java.io.File;
import java.util.List;
import java.util.Random;

import com.ctre.phoenix.music.Orchestra;

import org.usfirst.frc6647.robot.Robot;
import org.usfirst.frc6647.robot.RobotContainer;
import org.usfirst.lib6647.control.CheesyDrive;
import org.usfirst.lib6647.loops.ILooper;
import org.usfirst.lib6647.loops.Loop;
import org.usfirst.lib6647.loops.LoopType;
import org.usfirst.lib6647.oi.JController;
import org.usfirst.lib6647.subsystem.SuperSubsystem;
import org.usfirst.lib6647.subsystem.hypercomponents.HyperDoubleSolenoid;
import org.usfirst.lib6647.subsystem.hypercomponents.HyperFalcon;
import org.usfirst.lib6647.subsystem.hypercomponents.HyperSolenoid;
import org.usfirst.lib6647.subsystem.supercomponents.SuperCompressor;
import org.usfirst.lib6647.subsystem.supercomponents.SuperDoubleSolenoid;
import org.usfirst.lib6647.subsystem.supercomponents.SuperFalcon;

import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Filesystem;
import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;

/**
 * Simple {@link Chassis Chassis/Drive} {@link SuperSubsystem} implementation,
 * with arcade drive.
 */
public class Chassis extends SuperSubsystem implements SuperCompressor, SuperDoubleSolenoid, SuperFalcon {
	/** {@link JController} instance used by the {@link Robot}. */
	private JController joystick;
	/** {@link Compressor} instance used by the {@link Robot}. */
	private Compressor compressor;
	/** {@link HyperFalcon HyperFalcons} used by this {@link Chassis subsystem}. */
	private HyperFalcon frontLeft, frontRight, backLeft, backRight;
	/** {@link HyperSolenoid} used by this {@link Chassis subsystem}. */
	private HyperDoubleSolenoid reduction;

	/** Instance of a {@link CheesyDrive}, mostly for testing. */
	private CheesyDrive drive;
	/** Whether or not to use {@link CheesyDrive}. */
	private boolean useCheesy = false;

	/** {@link Orchestra} object instance, for playing MIDI (.chrp) files. */
	private Orchestra orchestra;

	/**
	 * Checks whether the {@link Robot}'s 'front' is at the front, or at the back.
	 */
	private boolean inverted = false;

	/**
	 * Should only need to create a single of instance of {@link Chassis this
	 * class}; inside the {@link RobotContainer}.
	 */
	public Chassis() {
		super("chassis");

		// All SuperComponents must be initialized like this. The 'robotMap' Object is
		// inherited from the SuperSubsystem class, while the second argument is simply
		// this Subsystem's name.
		initCompressors(robotMap, getName());
		initDoubleSolenoids(robotMap, getName());
		initFalcons(robotMap, getName());

		// Additional initialiation & configuration.
		joystick = Robot.getInstance().getContainer().getJoystick("driver1");

		compressor = getCompressor("compressor");

		frontLeft = getFalcon("frontLeft");
		frontRight = getFalcon("frontRight");
		backLeft = getFalcon("backLeft");
		backRight = getFalcon("backRight");

		reduction = getDoubleSolenoid("reduction");

		drive = new CheesyDrive();

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
			layout.addBoolean("cheesyDrive", this::getCheesy).withWidget(BuiltInWidgets.kBooleanBox);
			layout.addBoolean("compressorOn", compressor::enabled);
			layout.addBoolean("orchestraPlaying", orchestra::isPlaying).withWidget(BuiltInWidgets.kBooleanBox);
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
	 * Toggle {@link HyperFalcon} reductions, on both sides.
	 */
	public void toggleReduction() {
		reduction.toggle();
	}

	/**
	 * Gets whether or not the {@link Robot}'s 'front' is flipped.
	 * 
	 * @return The current state of the {@link Robot}'s {@link #inverted heading}
	 */
	public boolean getHeading() {
		return inverted;
	}

	/**
	 * Toggles where the {@link Robot}'s 'front' is.
	 */
	public void toggleHeading() {
		inverted = !inverted;
	}

	/**
	 * Method to get if the current Drive is set to {@link CheesyDrive}.
	 * 
	 * @return Whether or not this {@link Chassis} is using {@link CheesyDrive}.
	 */
	public boolean getCheesy() {
		return useCheesy;
	}

	/**
	 * Toggles between using {@link CheesyDrive}.
	 */
	public void toggleCheesy() {
		useCheesy = !useCheesy;
	}

	/**
	 * Use {@link HyperFalcon falcons} as an arcade drive.
	 * 
	 * @param forward  The drive's forward speed
	 * @param rotation The drive's rotation speed
	 */
	public void arcadeDrive(double forward, double rotation) {
		frontLeft.set(forward * (inverted ? -1 : 1), -rotation * (inverted ? -1 : 1));
		backLeft.set(forward * (inverted ? -1 : 1), -rotation * (inverted ? -1 : 1));

		frontRight.set(forward * (inverted ? -1 : 1), rotation * (inverted ? -1 : 1));
		backRight.set(forward * (inverted ? -1 : 1), rotation * (inverted ? -1 : 1));
	}

	/**
	 * Use {@link HyperFalcon falcons} as a tank drive.
	 * 
	 * @param left  The speed at which to set the left side of the {@link Chassis}
	 * @param right The speed at which to set the right side of the {@link Chassis}
	 */
	public void tankDrive(double left, double right) {
		frontLeft.setWithRamp(left * (inverted ? -1 : 1));
		backLeft.setWithRamp(left * (inverted ? -1 : 1));

		frontRight.set(right * (inverted ? -1 : 1));
		backRight.set(right * (inverted ? -1 : 1));
	}

	@Override
	public void registerLoops(ILooper looper) {
		looper.register(new Loop() { // Drive loop
			@Override
			public void onFirstStart(double timestamp) {
			}

			@Override
			public void onStart(double timestamp) {
				synchronized (Chassis.this) {
					compressor.start();
					System.out.println("Started arcade drive at: " + timestamp + "!");
				}
			}

			@Override
			public void onLoop(double timestamp) {
				if (orchestra.isPlaying()) // Prevents the robot from moving whilst playing a MIDI file.
					return;

				synchronized (Chassis.this) {
					if (useCheesy)
						drive.cheesyDrive(joystick.getY(Hand.kLeft), joystick.getX(Hand.kRight),
								joystick.get("L1", "LBumper").get(), reduction.getForward(), Chassis.this::tankDrive);
					else
						arcadeDrive(joystick.getY(Hand.kLeft), joystick.getX(Hand.kRight));
				}
			}

			@Override
			public void onStop(double timestamp) {
				compressor.stop();

				frontLeft.stopMotor();
				frontRight.stopMotor();

				backLeft.stopMotor();
				backRight.stopMotor();

				System.out.println("Stopped arcade drive at: " + timestamp + ".");
			}

			@Override
			public LoopType getType() {
				return LoopType.TELEOP;
			}
		});
	}
}

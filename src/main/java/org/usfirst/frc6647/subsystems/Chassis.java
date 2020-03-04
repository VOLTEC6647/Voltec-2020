package org.usfirst.frc6647.subsystems;

import java.io.File;
import java.util.List;
import java.util.Random;

import com.ctre.phoenix.music.Orchestra;

import org.usfirst.frc6647.robot.Robot;
import org.usfirst.frc6647.robot.RobotContainer;
import org.usfirst.lib6647.loops.ILooper;
import org.usfirst.lib6647.loops.Loop;
import org.usfirst.lib6647.loops.LoopType;
import org.usfirst.lib6647.oi.JController;
import org.usfirst.lib6647.subsystem.SuperSubsystem;
import org.usfirst.lib6647.subsystem.hypercomponents.HyperDoubleSolenoid;
import org.usfirst.lib6647.subsystem.hypercomponents.HyperFalcon;
import org.usfirst.lib6647.subsystem.hypercomponents.HyperSolenoid;
import org.usfirst.lib6647.subsystem.supercomponents.SuperDoubleSolenoid;
import org.usfirst.lib6647.subsystem.supercomponents.SuperFalcon;

import edu.wpi.first.wpilibj.Filesystem;
import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInLayouts;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardLayout;

/**
 * Simple {@link Chassis Chassis/Drive} {@link SuperSubsystem} implementation,
 * with arcade drive.
 */
public class Chassis extends SuperSubsystem implements SuperDoubleSolenoid, SuperFalcon {
	/** {@link JController} instance used by the Robot. */
	private JController joystick;
	/** {@link HyperFalcon HyperFalcons} used by this {@link SuperSubsystem}. */
	private HyperFalcon frontLeft, frontRight, backLeft, backRight;
	/** {@link HyperSolenoid HyperSolenoids} used by this {@link SuperSubsystem}. */
	private HyperDoubleSolenoid leftReduction, rightReduction;

	/** {@link Orchestra} object instance, for playing MIDI (.chrp) files. */
	private Orchestra orchestra;

	/**
	 * The {@link ShuffleboardLayout layout} to update in the {@link Shuffleboard}.
	 */
	private ShuffleboardLayout layout;

	/**
	 * Should only need to create a single of instance of {@link Chassis this
	 * class}; inside the {@link RobotContainer}.
	 */
	public Chassis() {
		super("chassis");

		// All SuperComponents must be initialized like this. The 'robotMap' Object is
		// inherited from the SuperSubsystem class, while the second argument is simply
		// this Subsystem's name.
		initDoubleSolenoids(robotMap, getName());
		initFalcons(robotMap, getName());

		// Additional initialiation & configuration.
		joystick = Robot.getInstance().getContainer().getJoystick("driver1");

		frontLeft = getFalcon("frontLeft");
		frontRight = getFalcon("frontRight");
		backLeft = getFalcon("backLeft");
		backRight = getFalcon("backRight");

		leftReduction = getDoubleSolenoid("leftReduction");
		rightReduction = getDoubleSolenoid("rightReduction");

		orchestra = new Orchestra(List.of(frontLeft, backLeft, frontRight, backRight));

		layout = Shuffleboard.getTab("Robot").getLayout("Chassis", BuiltInLayouts.kList);
	}

	@Override
	public void periodic() {
		// Debug data.
		layout.add("frontLeftMotor", frontLeft).withWidget(BuiltInWidgets.kSpeedController);
		layout.add("frontRightMotor", frontRight).withWidget(BuiltInWidgets.kSpeedController);
		layout.add("backLeftMotor", backLeft).withWidget(BuiltInWidgets.kSpeedController);
		layout.add("backRightMotor", backRight).withWidget(BuiltInWidgets.kSpeedController);
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
		leftReduction.toggle();
		rightReduction.toggle();
	}

	/**
	 * Use {@link HyperFalcon falcons} as an arcade drive.
	 * 
	 * @param forward  The drive's forward speed
	 * @param rotation The drive's rotation speed
	 */
	public void arcadeDrive(double forward, double rotation) {
		frontLeft.setWithRamp(forward, -rotation);
		backLeft.setWithRamp(forward, -rotation);

		frontRight.setWithRamp(forward, rotation);
		backRight.setWithRamp(forward, rotation);
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
					System.out.println("Started arcade drive at: " + timestamp + "!");
				}
			}

			@Override
			public void onLoop(double timestamp) {
				if (orchestra.isPlaying()) // Prevents the robot from moving whilst playing a MIDI file.
					return;

				synchronized (Chassis.this) {
					arcadeDrive(joystick.getY(Hand.kLeft), joystick.getX(Hand.kRight));
				}
			}

			@Override
			public void onStop(double timestamp) {
				arcadeDrive(0, 0);
				System.out.println("Stopped arcade drive at: " + timestamp + ".");
			}

			@Override
			public LoopType getType() {
				return LoopType.TELEOP;
			}
		});
	}
}

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
import org.usfirst.lib6647.subsystem.hypercomponents.HyperAHRS;
import org.usfirst.lib6647.subsystem.hypercomponents.HyperFalcon;
import org.usfirst.lib6647.subsystem.supercomponents.SuperAHRS;
import org.usfirst.lib6647.subsystem.supercomponents.SuperFalcon;

import edu.wpi.first.wpilibj.Filesystem;
import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * Simple Chassis/Drive {@link SuperSubsystem} implementation, with arcade
 * drive.
 */
public class Chassis extends SuperSubsystem implements SuperAHRS, SuperFalcon {
	/** {@link HyperFalcon HyperFalcons} used by this {@link SuperSubsystem}. */
	private HyperFalcon frontLeft, frontRight, backLeft, backRight;
	/** {@link JController} instance used by the Robot. */
	private JController joystick;
	/** {@link HyperAHRS} instance of the Robot's NavX. */
	private HyperAHRS navX;

	/** {@link Orchestra} object instance, for playing MIDI (.chrp) files. */
	private Orchestra orchestra;

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
		initFalcons(robotMap, getName());

		// Additional initialiation & configuration.
		frontLeft = getFalcon("frontLeft");
		frontRight = getFalcon("frontRight");
		backLeft = getFalcon("backLeft");
		backRight = getFalcon("backRight");

		joystick = Robot.getInstance().getContainer().getJoystick("driver1");
		navX = getAHRS("navX");

		orchestra = new Orchestra(List.of(frontLeft, backLeft, frontRight, backRight));
	}

	@Override
	public void periodic() {
		// Debug data.
		SmartDashboard.putNumber("heading", navX.getHeading());
		SmartDashboard.putNumber("yaw", navX.getHeading());
		SmartDashboard.putNumber("rate", navX.getRate());
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
	 * Sets the {@link HyperFalcon motors}' limiter value to the given amount.
	 * 
	 * @param limiter The value at which to set the limiter
	 */
	public void setLimiter(double limiter) {
		frontLeft.setLimiter(limiter);
		frontRight.setLimiter(limiter);
	}

	/**
	 * Use {@link HyperFalcon falcons} as an arcade drive.
	 * 
	 * @param forward  The drive's forward speed
	 * @param rotation The drive's rotation speed
	 */
	private void arcadeDrive(double forward, double rotation) {
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
				// Reset NavX only on first start; zero its yaw afterwards.
				synchronized (Chassis.this) {
					navX.reset();
				}
			}

			@Override
			public void onStart(double timestamp) {
				synchronized (Chassis.this) {
					navX.zeroYaw();
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
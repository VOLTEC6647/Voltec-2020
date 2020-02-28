package org.usfirst.frc6647.subsystems;

import java.io.File;
import java.util.List;
import java.util.Random;

import com.ctre.phoenix.music.Orchestra;

import org.usfirst.frc6647.robot.Robot;
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

	/** {@link Orchestra} object instance, for playing MIDI files. */
	private Orchestra orchestra;

	/** Stores last set limiter value. */
	private double lastLimiter = Double.NaN;

	/**
	 * A lambda of every {@link SuperSubsystem Subsystem} must be provided to the
	 * {@link Robot} via the {@link LooperRobot#registerSubsystems} method.
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

		configureButtonBindings();
	}

	/**
	 * Throw all Command initialization and {@link JController} binding for this
	 * {@link SuperSubsystem} into this method.
	 */
	private void configureButtonBindings() {
		orchestra = new Orchestra(List.of(getFalcon("frontLeft"), getFalcon("backLeft"), getFalcon("frontRight"),
				getFalcon("backRight")));

		var songs = new File(Filesystem.getDeployDirectory() + "/MIDI/").listFiles();

		Runnable prepareSong = () -> { // Prepare a song to play.
			var song = songs[new Random().nextInt(songs.length)].toString();
			System.out.println("Ready to play: '" + song + "'...");
			orchestra.loadMusic(song);
		};
		Runnable playPauseSong = () -> { // Play current song.
			if (!orchestra.isPlaying())
				orchestra.play();
			else
				orchestra.pause();
		};

		Runnable enableTurbo = () -> { // zu schnell!
			lastLimiter = frontLeft.getLimiter();
			frontLeft.setLimiter(1);
			frontRight.setLimiter(1);
		};
		Runnable disableTurbo = () -> { // zu langsam...
			frontLeft.setLimiter(lastLimiter);
			frontRight.setLimiter(lastLimiter);
		};

		if (joystick.getName().equals("Wireless Controller")) {
			joystick.get("Options").whenPressed(prepareSong);
			joystick.get("L2").and(joystick.get("R2")).and(joystick.get("Share")).whenActive(playPauseSong);

			joystick.get("L2").whenPressed(enableTurbo).whenReleased(disableTurbo);
		} else if (joystick.getName().equals("Generic   USB  Joystick")) {
			joystick.get("Start").whenPressed(prepareSong);
			joystick.get("LTrigger").and(joystick.get("RTrigger")).and(joystick.get("Select"))
					.whenActive(playPauseSong);

			joystick.get("LTrigger").whenPressed(enableTurbo).whenReleased(disableTurbo);
		} else if (joystick.getName().toLowerCase().contains("xbox")) {
			joystick.get("Start").whenPressed(prepareSong);
			joystick.get("LTrigger").and(joystick.get("RTrigger")).and(joystick.get("Back")).whenActive(playPauseSong);

			joystick.get("LTrigger").whenPressed(enableTurbo).whenReleased(disableTurbo);
		}
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
	public void periodic() {
	}

	@Override
	public void registerLoops(ILooper looper) {
		looper.register(new Loop() {
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
				// Debug data.
				SmartDashboard.putNumber("heading", navX.getHeading());
				SmartDashboard.putNumber("yaw", navX.getHeading());
				SmartDashboard.putNumber("rate", navX.getRate());

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
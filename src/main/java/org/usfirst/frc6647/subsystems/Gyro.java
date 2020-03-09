package org.usfirst.frc6647.subsystems;

import org.usfirst.frc6647.robot.Constants;
import org.usfirst.frc6647.robot.Robot;
import org.usfirst.frc6647.robot.RobotContainer;
import org.usfirst.lib6647.loops.ILooper;
import org.usfirst.lib6647.loops.Loop;
import org.usfirst.lib6647.loops.LoopType;
import org.usfirst.lib6647.oi.JController;
import org.usfirst.lib6647.subsystem.SuperSubsystem;
import org.usfirst.lib6647.subsystem.hypercomponents.HyperAHRS;
import org.usfirst.lib6647.subsystem.supercomponents.SuperAHRS;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.GenericHID.RumbleType;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj2.command.button.Trigger;

/**
 * {@link SuperSubsystem} implementation for our {@link #navX gyroscope}.
 */
public class Gyro extends SuperSubsystem implements SuperAHRS {
	/** {@link JController} instance used by the {@link Robot}. */
	private JController joystick;
	/** {@link HyperAHRS} instance used by this {@link Gyro subsystem}. */
	private HyperAHRS navX;

	/** Stores last detected world linear X and Y acceleration values. */
	private double lastWorldLinearAccelX = 0.0, lastWorldLinearAccelY = 0.0;
	/** Whether or not a collision was detected by the {@link #navX}. */
	private boolean collisionDetected = false;

	/**
	 * Should only need to create a single of instance of {@link Gyro this class};
	 * inside the {@link RobotContainer}.
	 */
	public Gyro() {
		super("gyro");

		// All SuperComponents must be initialized like this. The 'robotMap' Object is
		// inherited from the SuperSubsystem class, while the second argument is simply
		// this Subsystem's name.
		initAHRS(robotMap, getName());

		// Additional initialiation & configuration.
		joystick = Robot.getInstance().getContainer().getJoystick("driver1");
		navX = getAHRS("navX");

		Runnable setRumble = () -> { // Sets joystick rumble to 0.5.
			joystick.setRumble(RumbleType.kLeftRumble, 0.5);
			joystick.setRumble(RumbleType.kRightRumble, 0.5);
		};
		Runnable stopRumble = () -> { // Stops joystick rumble.
			joystick.setRumble(RumbleType.kLeftRumble, 0.0);
			joystick.setRumble(RumbleType.kRightRumble, 0.0);
		};

		var collision = new Trigger(this::didCollide); // Triggers when a collision is detected.
		collision.whenActive(setRumble, this).whenInactive(stopRumble, this);
		// ...
	}

	@Override
	public void outputToShuffleboard() {
		try {
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
	 * Gets whether or not a collision was detected by the {@link #navX}.
	 * 
	 * @return Whether or not a collision was detected
	 */
	public boolean didCollide() {
		return collisionDetected;
	}

	@Override
	public void registerLoops(ILooper looper) {
		looper.register(new Loop() {
			@Override
			public void onFirstStart(double timestamp) {
				// Reset NavX only on first start; zero its yaw afterwards.
				synchronized (Gyro.this) {
					navX.reset();
				}
			}

			@Override
			public void onStart(double timestamp) {
				synchronized (Gyro.this) {
					navX.zeroYaw();
					System.out.println("Started gyro at: " + timestamp + "!");
				}
			}

			@Override
			public void onLoop(double timestamp) {
				collisionDetected = false;

				var currentWorldLinearAccelX = navX.getWorldLinearAccelX();
				var currentJerkX = currentWorldLinearAccelX - lastWorldLinearAccelX;
				lastWorldLinearAccelX = currentWorldLinearAccelX;

				var currentWorldLinearAccelY = navX.getWorldLinearAccelY();
				var currentJerkY = currentWorldLinearAccelY - lastWorldLinearAccelY;
				lastWorldLinearAccelY = currentWorldLinearAccelY;

				if ((Math.abs(currentJerkX) > Constants.GyroConstants.collisionThresholdDeltaG)
						|| (Math.abs(currentJerkY) > Constants.GyroConstants.collisionThresholdDeltaG))
					collisionDetected = true;
			}

			@Override
			public void onStop(double timestamp) {
				System.out.println("Stopped gyro at: " + timestamp + ".");
			}

			@Override
			public LoopType getType() {
				return LoopType.ENABLED;
			}
		});
	}
}
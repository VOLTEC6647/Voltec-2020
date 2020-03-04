package org.usfirst.frc6647.subsystems;

import org.usfirst.frc6647.robot.Constants;
import org.usfirst.frc6647.robot.Robot;
import org.usfirst.lib6647.loops.ILooper;
import org.usfirst.lib6647.loops.Loop;
import org.usfirst.lib6647.loops.LoopType;
import org.usfirst.lib6647.oi.JController;
import org.usfirst.lib6647.subsystem.SuperSubsystem;
import org.usfirst.lib6647.subsystem.hypercomponents.HyperAHRS;
import org.usfirst.lib6647.subsystem.supercomponents.SuperAHRS;

import edu.wpi.first.wpilibj.GenericHID.RumbleType;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInLayouts;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardLayout;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.button.Trigger;

/**
 * {@link SuperSubsystem} implementation for our {@link #navX Gyro}.
 */
public class Gyro extends SuperSubsystem implements SuperAHRS {
	/** {@link HyperAHRS} instance of the Robot's NavX. */
	private HyperAHRS navX;
	/** {@link JController} instance used by the Robot. */
	private JController joystick;

	/** Stores last detected world linear X and Y acceleration values. */
	private double lastWorldLinearAccelX = 0.0, lastWorldLinearAccelY = 0.0;
	/** Whether or not a collision was detected by the {@link #navX}. */
	private boolean collisionDetected = false;

	/**
	 * The {@link ShuffleboardLayout layout} to update in the {@link Shuffleboard}.
	 */
	private ShuffleboardLayout layout;

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
		navX = getAHRS("navX");
		joystick = Robot.getInstance().getContainer().getJoystick("driver1");

		layout = Shuffleboard.getTab("Robot").getLayout("Gyro", BuiltInLayouts.kList);

		Runnable setRumble = () -> { // Sets joystick rumble to 0.5.
			joystick.setRumble(RumbleType.kLeftRumble, 0.5);
			joystick.setRumble(RumbleType.kRightRumble, 0.5);
		};
		Runnable stopRumble = () -> { // Stops joystick rumble.
			joystick.setRumble(RumbleType.kLeftRumble, 0.0);
			joystick.setRumble(RumbleType.kRightRumble, 0.0);
		};

		Trigger collision = new Trigger(this::get); // Triggers when a collision is detected.
		collision.whenActive(new InstantCommand(setRumble, this).withTimeout(1.5).andThen(stopRumble, this));
	}

	@Override
	public void periodic() {
		// Debug data.
		layout.add("navX", navX).withWidget(BuiltInWidgets.kGyro);
		layout.add("navXYaw", navX.getYaw());
		layout.add("navXHeading", navX.getHeading());
		layout.add("collisionDetected", get()).withWidget(BuiltInWidgets.kBooleanBox);
	}

	/**
	 * Gets whether or not a collision was detected by the {@link #navX}.
	 * 
	 * @return Whether or not a collision was detected
	 */
	public boolean get() {
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
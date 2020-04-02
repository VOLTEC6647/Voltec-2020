package org.usfirst.frc6647.subsystems;

import com.revrobotics.ControlType;

import org.usfirst.frc6647.robot.Constants;
import org.usfirst.frc6647.robot.Robot;
import org.usfirst.frc6647.robot.RobotContainer;
import org.usfirst.lib6647.loops.ILooper;
import org.usfirst.lib6647.loops.Loop;
import org.usfirst.lib6647.loops.LoopType;
import org.usfirst.lib6647.subsystem.SuperSubsystem;
import org.usfirst.lib6647.subsystem.hypercomponents.HyperSparkMax;
import org.usfirst.lib6647.subsystem.supercomponents.SuperSparkMax;
import org.usfirst.lib6647.vision.LimelightCamera;
import org.usfirst.lib6647.vision.LimelightData.Data;

import edu.wpi.first.wpilibj.geometry.Rotation2d;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;

/**
 * {@link SuperSubsystem} implementation of a {@link Turret}, using a Limelight
 * camera to auto-aim.
 */
public class Turret extends SuperSubsystem implements SuperSparkMax {
	/** {@link HyperSparkMax} instance used by the {@link Turret}. */
	private HyperSparkMax turret;

	/** Stores current {@link #setpoint position goal}. */
	private double setpoint;
	/** Whether or not the {@link Turret} is currently aiming. */
	private boolean aiming = false;

	/** The {@link Robot}'s frontal {@link LimelightCamera} instance. */
	private LimelightCamera limelight;

	/**
	 * Should only need to create a single of instance of {@link Turret this class};
	 * inside the {@link RobotContainer}.
	 */
	public Turret() {
		super("turret");

		// All SuperComponents must be initialized like this. The 'robotMap' Object is
		// inherited from the SuperSubsystem class, while the second argument is simply
		// this Subsystem's name.
		initSparks(robotMap, getName());

		// Additional initialiation & configuration.
		turret = getSpark("turret");

		limelight = new LimelightCamera("limelight");
		// ...
	}

	@Override
	public void outputToShuffleboard() {
		layout.addNumber("error", this::getError);

		layout.addNumber("angle", () -> getAngle().getDegrees());
		layout.addString("angleFull", () -> getAngle().toString());

		layout.addNumber("setpoint", this::getSetpoint);

		layout.addBoolean("isOnline", limelight::isConnected).withWidget(BuiltInWidgets.kBooleanBox);
		layout.addBoolean("targetFound", limelight::isTargetFound).withWidget(BuiltInWidgets.kBooleanBox);
		layout.addNumber("horizontalRotation", this::getHorizontalRotation);
	}

	/**
	 * Set the {@link Turret}'s {@link #turret motor} to the given speed.
	 * 
	 * @param speed The speed at which to set the {@link Turret}'s {@link #turret
	 *              motor}
	 */
	public void setMotor(double speed) {
		setpoint = (getAngle().getRadians()
				* (Constants.TurretConstants.ticksPerRotation * Constants.TurretConstants.reduction)) / 360;
		turret.getPIDController().setReference(speed, ControlType.kDutyCycle);
	}

	/**
	 * Sets the {@link #turretPID} to the specified angle, also updates the
	 * {@link #setpoint} variable.
	 * 
	 * @param angle The angle at which to set the {@link #turretPID}'s setpoint
	 */
	public void setDesiredAngle(Rotation2d angle) {
		setpoint = (angle.getRadians()
				* (Constants.TurretConstants.ticksPerRotation * Constants.TurretConstants.reduction)) / 360;
		turret.getPIDController().setReference(setpoint, ControlType.kPosition);
	}

	/**
	 * Stops the {@link Turret}'s {@link #turret motor} dead in its tracks.
	 */
	public void stopMotor() {
		turret.stopMotor();
	}

	/**
	 * Toggles whether or not to start aiming the {@link #turret}.
	 */
	public void toggleAim() {
		aiming = !aiming;
	}

	/**
	 * Reset {@link #turretEncoder} position to the given angle.
	 * 
	 * @param angle The angle at which to reset the {@link #turretEncoder}
	 */
	public void reset(Rotation2d angle) {
		turret.getEncoder().setPosition((angle.getRadians()
				* (Constants.TurretConstants.ticksPerRotation * Constants.TurretConstants.reduction)) / 360);
	}

	/**
	 * Get the {@link #turret}'s current {@link Rotation2d angle}.
	 * 
	 * @return The current {@link Rotation2d angle}
	 */
	public Rotation2d getAngle() {
		return new Rotation2d(turret.getEncoder().getPosition() * 360
				/ (Constants.TurretConstants.ticksPerRotation * Constants.TurretConstants.reduction));
	}

	/**
	 * Gets the {@link Turret}'s current {@link #setpoint}.
	 * 
	 * @return The {@link Turret}'s current {@link #setpoint} value
	 */
	public double getSetpoint() {
		return Math.toDegrees(
				(setpoint * 360) / (Constants.TurretConstants.ticksPerRotation * Constants.TurretConstants.reduction));
	}

	/**
	 * Gets the difference between current angle, and the {@link #setpoint}.
	 * 
	 * @return The {@link Turret}'s error
	 */
	public double getError() {
		return getAngle().getDegrees() - getSetpoint();
	}

	/**
	 * Gets the {@link Robot}'s horizontal rotation to the target, in degrees.
	 * 
	 * @return The target's horizontal rotation in relation to the {@link Robot}
	 */
	public double getHorizontalRotation() {
		return limelight.getData(Data.HORIZONTAL_OFFSET);
	}

	@Override
	public void registerLoops(ILooper looper) {
		looper.register(new Loop() { // Auto-aim loop.
			@Override
			public void onFirstStart(double timestamp) {
				reset(new Rotation2d());
			}

			@Override
			public void onStart(double timestamp) {
				synchronized (Turret.this) {
					System.out.println("Started Turret Auto-aim at: " + timestamp + "!");
				}
			}

			@Override
			public void onLoop(double timestamp) {
				if (!aiming) // Checks whether or not the Turret should be auto-aiming
					return;

				synchronized (Turret.this) {
					if (!limelight.isTargetFound()) // Checks if a target is found by the Limelight
						return;

					turret.getPIDController().setReference(turret.getEncoder().getPosition() + getHorizontalRotation(),
							ControlType.kPosition);
				}
			}

			@Override
			public void onStop(double timestamp) {
				System.out.println("Stopped Turret Auto-aim at: " + timestamp + ".");
			}

			@Override
			public LoopType getType() {
				return LoopType.ENABLED;
			}
		});
	}
}
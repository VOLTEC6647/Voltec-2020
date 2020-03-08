package org.usfirst.frc6647.subsystems;

import com.revrobotics.ControlType;

import org.usfirst.frc6647.robot.Constants;
import org.usfirst.frc6647.robot.RobotContainer;
import org.usfirst.lib6647.loops.ILooper;
import org.usfirst.lib6647.loops.Loop;
import org.usfirst.lib6647.loops.LoopType;
import org.usfirst.lib6647.subsystem.SuperSubsystem;
import org.usfirst.lib6647.subsystem.hypercomponents.HyperSparkMax;
import org.usfirst.lib6647.subsystem.supercomponents.SuperDigitalInput;
import org.usfirst.lib6647.subsystem.supercomponents.SuperSparkMax;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.geometry.Rotation2d;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;

/**
 * {@link SuperSubsystem} implementation of a {@link Turret}, using a Limelight
 * camera to auto-aim.
 */
public class Turret extends SuperSubsystem implements SuperDigitalInput, SuperSparkMax {
	/** {@link HyperSparkMax} instance used by the {@link Turret}. */
	private HyperSparkMax turret;

	/** This {@link Turret}'s {@link DigitalInput limit switches}. */
	private DigitalInput limitReverse, limitForward;

	/** Stores current {@link #setpoint position goal}. */
	private double setpoint;
	/** Whether or not the {@link Turret} is currently aiming. */
	private boolean aiming = false;

	/**
	 * Should only need to create a single of instance of {@link Turret this class};
	 * inside the {@link RobotContainer}.
	 */
	public Turret() {
		super("turret");

		// All SuperComponents must be initialized like this. The 'robotMap' Object is
		// inherited from the SuperSubsystem class, while the second argument is simply
		// this Subsystem's name.
		initDigitalInputs(robotMap, getName());
		initSparks(robotMap, getName());

		// Additional initialiation & configuration.
		turret = getSpark("turret");

		limitReverse = getDigitalInput("limitReverse");
		limitForward = getDigitalInput("limitForward");
		// ...
	}

	@Override
	public void outputToShuffleboard() {
		layout.addNumber("error", this::getError);

		layout.addNumber("angle", () -> getAngle().getDegrees());
		layout.addString("angleFull", () -> getAngle().toString());

		layout.addNumber("setpoint", this::getSetpoint);

		layout.addBoolean("forwardLimit", this::getForwardLimitSwitch).withWidget(BuiltInWidgets.kBooleanBox);
		layout.addBoolean("reverseLimit", this::getReverseLimitSwitch).withWidget(BuiltInWidgets.kBooleanBox);
		layout.addBoolean("onTarget", this::onTarget).withWidget(BuiltInWidgets.kBooleanBox);
	}

	/**
	 * Set the {@link Turret}'s {@link #turret motor} to the given speed.
	 * 
	 * @param speed The speed at which to set the {@link Turret}'s {@link #turret
	 *              motor}
	 */
	public void setMotor(double speed) {
		turret.set(speed);
	}

	/**
	 * Stops the {@link Turret}'s {@link #turret motor} dead in its tracks.
	 */
	public void stopMotor() {
		turret.stopMotor();
	}

	/**
	 * Sets the {@link #turretPID} to the specified angle, also updates the
	 * {@link #setpoint} variable.
	 * 
	 * @param angle The angle at which to set the {@link #turretPID}'s setpoint
	 */
	public void setDesiredAngle(Rotation2d angle) {
		setpoint = angle.getRadians() / (2 * Math.PI * Constants.TurretConstants.rotationsPerTick);
		turret.getPIDController().setReference(setpoint, ControlType.kPosition);
	}

	/**
	 * Reset {@link #turretEncoder} position to the given angle.
	 * 
	 * @param angle The angle at which to reset the {@link #turretEncoder}
	 */
	public void reset(Rotation2d angle) {
		turret.getEncoder()
				.setPosition(angle.getRadians() / (2 * Math.PI * Constants.TurretConstants.rotationsPerTick));
	}

	/**
	 * Get the {@link #turret}'s current {@link Rotation2d angle}.
	 * 
	 * @return The current {@link Rotation2d angle}
	 */
	public Rotation2d getAngle() {
		return new Rotation2d(
				Constants.TurretConstants.rotationsPerTick * turret.getEncoder().getPosition() * 2 * Math.PI);
	}

	/**
	 * Gets the value of the {@link Turret}'s {@link #limitForward forward limit
	 * switch}.
	 * 
	 * @return The value of the {@link #limitForward forward limit switch}
	 */
	public boolean getForwardLimitSwitch() {
		return limitForward.get();
	}

	/**
	 * Gets the value of the {@link Turret}'s {@link #limitReverse reverse limit
	 * switch}.
	 * 
	 * @return The value of the {@link #limitReverse reverse limit switch}
	 */
	public boolean getReverseLimitSwitch() {
		return limitReverse.get();
	}

	/**
	 * Gets the {@link Turret}'s current {@link #setpoint}.
	 * 
	 * @return The {@link Turret}'s current {@link #setpoint} value
	 */
	public double getSetpoint() {
		return setpoint;
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
	 * Checks if the {@link Turret}'s {@link #turret motor}'s current position is
	 * within tolerance.
	 * 
	 * @return Whether or not the {@link Turret}'s {@link #turret motor}'s current
	 *         position is within tolerance
	 */
	public boolean onTarget() {
		return Math.abs(getError()) < Constants.TurretConstants.tolerance;
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
					// TODO: Get Vision subsystem to affect this.

					turret.getPIDController().setReference(0.0, ControlType.kPosition);
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
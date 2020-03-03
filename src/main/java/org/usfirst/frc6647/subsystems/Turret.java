package org.usfirst.frc6647.subsystems;

import com.revrobotics.CANEncoder;
import com.revrobotics.CANPIDController;
import com.revrobotics.CANSparkMax;
import com.revrobotics.ControlType;

import org.usfirst.frc6647.robot.Constants;
import org.usfirst.frc6647.robot.RobotContainer;
import org.usfirst.lib6647.loops.ILooper;
import org.usfirst.lib6647.loops.Loop;
import org.usfirst.lib6647.loops.LoopType;
import org.usfirst.lib6647.subsystem.SuperSubsystem;
import org.usfirst.lib6647.subsystem.supercomponents.SuperDigitalInput;
import org.usfirst.lib6647.subsystem.supercomponents.SuperSparkMax;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.geometry.Rotation2d;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInLayouts;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardLayout;

/**
 * {@link SuperSubsystem} implementation of a {@link Turret}, using a Limelight
 * camera to auto-aim.
 */
public class Turret extends SuperSubsystem implements SuperDigitalInput, SuperSparkMax {
	/** {@link CANSparkMax} instance of the {@link Turret} */
	private CANSparkMax turret;
	/**
	 * {@link CANPIDController} instance of the {@link Turret}'s {@link #turret
	 * motor}.
	 */
	private CANPIDController turretPID;
	/**
	 * {@link CANEncoder} instance of the {@link Turret}'s {@link #turret motor}.
	 */
	private CANEncoder turretEncoder;

	/** This {@link Turret}'s {@link DigitalInput limit switches}. */
	private DigitalInput limitReverse, limitForward;

	/**
	 * The {@link ShuffleboardLayout layout} to update in the {@link Shuffleboard}.
	 */
	private ShuffleboardLayout layout;

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
		turretPID = getSparkPID("turret");
		turretEncoder = getSparkEncoder("turret");

		limitReverse = getDigitalInput("limitReverse");
		limitForward = getDigitalInput("limitForward");

		layout = Shuffleboard.getTab("Robot").getLayout("Turret", BuiltInLayouts.kList);
	}

	@Override
	public void periodic() {
		// Debug data.
		layout.add("turret_error", getError());
		layout.add("turret_angle", getAngle().getDegrees()).withWidget(BuiltInWidgets.kGyro);
		layout.add("turret_setpoint", getSetpoint());
		layout.add("turret_fwd_limit", getForwardLimitSwitch()).withWidget(BuiltInWidgets.kBooleanBox);
		layout.add("turret_rev_limit", getReverseLimitSwitch()).withWidget(BuiltInWidgets.kBooleanBox);
		layout.add("turret_on_target", onTarget()).withWidget(BuiltInWidgets.kBooleanBox);
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
		turretPID.setReference(setpoint, ControlType.kPosition);
	}

	/**
	 * Reset {@link #turretEncoder} position to the given angle.
	 * 
	 * @param angle The angle at which to reset the {@link #turretEncoder}
	 */
	public void reset(Rotation2d angle) {
		turretEncoder.setPosition(angle.getRadians() / (2 * Math.PI * Constants.TurretConstants.rotationsPerTick));
	}

	/**
	 * Get the {@link #turret}'s current {@link Rotation2d angle}.
	 * 
	 * @return The current {@link Rotation2d angle}
	 */
	public Rotation2d getAngle() {
		return new Rotation2d(Constants.TurretConstants.rotationsPerTick * turretEncoder.getPosition() * 2 * Math.PI);
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
	public void zeroSensors() {
		reset(new Rotation2d());
	}

	@Override
	public void registerLoops(ILooper looper) {
		looper.register(new Loop() { // Auto-aim loop.
			private NetworkTable limelight;
			private NetworkTableEntry ty;

			@Override
			public void onFirstStart(double timestamp) {
			}

			@Override
			public void onStart(double timestamp) {
				synchronized (Turret.this) {
					limelight = NetworkTableInstance.getDefault().getTable("limelight");
					ty = limelight.getEntry("ty");

					System.out.println("Started Turret Auto-aim at: " + timestamp + "!");
				}
			}

			@Override
			public void onLoop(double timestamp) {
				if (!aiming) // Checks whether or not the Turret should be auto-aiming
					return;

				synchronized (Turret.this) {
					turretPID.setReference(ty.getDouble(turretEncoder.getPosition()), ControlType.kPosition);
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

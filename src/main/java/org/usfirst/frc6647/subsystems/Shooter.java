package org.usfirst.frc6647.subsystems;

import java.util.function.Function;

import com.revrobotics.CANEncoder;
import com.revrobotics.CANPIDController;
import com.revrobotics.ControlType;

import org.usfirst.frc6647.robot.Constants;
import org.usfirst.frc6647.robot.RobotContainer;
import org.usfirst.lib6647.subsystem.SuperSubsystem;
import org.usfirst.lib6647.subsystem.hypercomponents.HyperSolenoid;
import org.usfirst.lib6647.subsystem.supercomponents.SuperServo;
import org.usfirst.lib6647.subsystem.supercomponents.SuperSolenoid;
import org.usfirst.lib6647.subsystem.supercomponents.SuperSparkMax;

import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.Servo;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInLayouts;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardLayout;

/**
 * {@link SuperSubsystem} implementation of a {@link Shooter}.
 */
public class Shooter extends SuperSubsystem implements SuperServo, SuperSolenoid, SuperSparkMax {
	/** {@link Servo} that controls the {@link Shooter}'s {@link #hood}. */
	private Servo hood;
	/**
	 * {@link HyperSolenoid} to stop the {@link Shooter}'s {@link CANSparkMax
	 * motor}.
	 */
	private HyperSolenoid stop;

	/**
	 * {@link CANPIDController} instance of the {@link Shooter}'s {@link CANSparkMax
	 * motor}.
	 */
	private CANPIDController shooterPID;
	/**
	 * {@link CANEncoder} instance of the {@link Shooter}'s {@link CANSparkMax
	 * motor}.
	 */
	private CANEncoder shooterEncoder;

	/**
	 * The {@link ShuffleboardLayout layout} to update in the {@link Shuffleboard}.
	 */
	private ShuffleboardLayout layout;

	/** Formula to calculate distance from target. */
	private Function<Double, Double> formula = ty -> ((3.8883 * Math.pow(10, 6)) * Math.pow((ty + 31.2852), -4.9682)
			+ 113.358) / (ty + 31.2852) - 0.978398;

	/** Stores current {@link #setpoint speed goal}. */
	private double setpoint;

	/**
	 * Should only need to create a single of instance of {@link Shooter this
	 * class}; inside the {@link RobotContainer}.
	 */
	public Shooter() {
		super("shooter");

		// All SuperComponents must be initialized like this. The 'robotMap' Object is
		// inherited from the SuperSubsystem class, while the second argument is simply
		// this Subsystem's name.
		initServos(robotMap, getName());
		initSolenoids(robotMap, getName());
		initSparks(robotMap, getName());

		// Additional initialiation & configuration.
		hood = getServo("hood");
		stop = getSolenoid("stop");

		shooterPID = getSparkPID("shooter");
		shooterEncoder = getSparkEncoder("shooter");

		layout = Shuffleboard.getTab("Robot").getLayout("Shooter", BuiltInLayouts.kList);
	}

	@Override
	public void periodic() {
		// Debug data.
		layout.add("shooter_RPM", shooterEncoder.getVelocity()).withWidget(BuiltInWidgets.kGraph);
		layout.add("hood_angle", hood.getAngle());
		layout.add("hood_brake", stop.get()).withWidget(BuiltInWidgets.kBooleanBox);
		layout.add("shooter_on_target", onTarget()).withWidget(BuiltInWidgets.kBooleanBox);
	}

	/**
	 * Sets the {@link Shooter}'s {@link CANSparkMax motor} to the {@link #calculate
	 * calculated} speed, and updates both the setpoint {@link #layout layout} and
	 * {@link #setpoint variable}.
	 */
	public void setMotor() {
		setpoint = calculate();
		layout.add("setpoint", setpoint);

		shooterPID.setReference(setpoint, ControlType.kVelocity);
	}

	/**
	 * Toggles the {@link Shooter}'s {@link #stop brake} {@link HyperSolenoid}, to
	 * stop the {@link Shooter}'s {@link CANSparkMax motor}.
	 */
	public void toggleHoodBrake() {
		stop.toggle();
	}

	/**
	 * Checks if the {@link Shooter}'s {@link CANSparkMax motor} is running at the
	 * target speed.
	 * 
	 * @return Whether or not the {@link Shooter}'s {@link CANSparkMax motor} has
	 *         reached the target speed.
	 */
	public boolean onTarget() {
		return Math.abs(getError()) < Constants.ShooterConstants.tolerance;
	}

	/**
	 * Gets the error between the current {@link Shooter}'s {@link CANSparkMax
	 * motor} and the {@link #setpoint goal speed}.
	 * 
	 * @return The {@link #shooterPID}'s error
	 */
	public double getError() {
		return shooterEncoder.getVelocity() - setpoint;
	}

	/**
	 * Calculates the necessary speed for the {@link Shooter}'s {@link CANSparkMax
	 * motor} depending on the distance from the target.
	 * 
	 * @return The calculated speed value
	 */
	private double calculate() {
		var ty = NetworkTableInstance.getDefault().getTable("limelight").getEntry("ty").getDouble(0);
		layout.add("ty", ty);

		var distance = formula.apply(ty);
		layout.add("target_distance", distance);

		var speed = 0.0;

		// TODO Actually modify the speed value.
		if (distance < 3) {

		} else if (distance < 6) {

		} else if (distance < 9) {

		} else {

		}

		return speed;
	}
}
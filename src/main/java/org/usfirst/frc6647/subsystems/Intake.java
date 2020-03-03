package org.usfirst.frc6647.subsystems;

import com.revrobotics.CANPIDController;
import com.revrobotics.CANSparkMax;
import com.revrobotics.ControlType;

import org.usfirst.frc6647.robot.RobotContainer;
import org.usfirst.lib6647.subsystem.SuperSubsystem;
import org.usfirst.lib6647.subsystem.hypercomponents.HyperDoubleSolenoid;
import org.usfirst.lib6647.subsystem.supercomponents.SuperDoubleSolenoid;
import org.usfirst.lib6647.subsystem.supercomponents.SuperSparkMax;

import edu.wpi.first.wpilibj.shuffleboard.BuiltInLayouts;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardLayout;

/**
 * Ball {@link Intake} {@link SuperSubsystem} implementation.
 */
public class Intake extends SuperSubsystem implements SuperDoubleSolenoid, SuperSparkMax {
	/** Main {@link CANSparkMax} used by this {@link Intake}. */
	private CANSparkMax intake;
	/** {@link CANPIDController} instance of the main {@link #intake motor}. */
	private CANPIDController intakePID;
	/** {@link HyperDoubleSolenoid} used by this {@link SuperSubsystem}. */
	private HyperDoubleSolenoid intakePiston;

	/**
	 * The {@link ShuffleboardLayout layout} to update in the {@link Shuffleboard}.
	 */
	private ShuffleboardLayout layout;

	/**
	 * Should only need to create a single of instance of {@link Intake this class};
	 * inside the {@link RobotContainer}.
	 */
	public Intake() {
		super("intake");

		// All SuperComponents must be initialized like this. The 'robotMap' Object is
		// inherited from the SuperSubsystem class, while the second argument is simply
		// this Subsystem's name.
		initDoubleSolenoids(robotMap, getName());
		initSparks(robotMap, getName());

		// Additional initialiation & configuration.
		intake = getSpark("intake");
		intakePID = getSparkPID("intake");

		intakePiston = getDoubleSolenoid("intakePiston");

		layout = Shuffleboard.getTab("Robot").getLayout("Indexer", BuiltInLayouts.kList);
	}

	@Override
	public void periodic() {
		layout.add("intakeMotor", intake).withWidget(BuiltInWidgets.kSpeedController);
		layout.add("intakeVoltage", intake.getOutputCurrent()).withWidget(BuiltInWidgets.kGraph);
	}

	/**
	 * Set the {@link Intake}'s main {@link #intake motor}'s {@link #intakePID PID
	 * Controller} to a specific voltage, in {@link ControlType#kCurrent}.
	 * 
	 * @param voltage The voltage at which to set the {@link #intake motor}'s
	 *                {@link #intakePID PID Controller} to
	 */
	public void setMotorVoltage(double voltage) {
		intakePID.setReference(voltage, ControlType.kCurrent);
	}

	/**
	 * Stops the {@link Intake}'s main {@link #intake motor} dead in its tracks.
	 */
	public void stopMotor() {
		intake.stopMotor();
	}

	/**
	 * Toggles the front {@link Intake} {@link #intakePiston solenoid}.
	 */
	public void toggleSolenoid() {
		intakePiston.toggle();
	}
}
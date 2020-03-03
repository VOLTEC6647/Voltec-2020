package org.usfirst.frc6647.subsystems;

import com.revrobotics.CANPIDController;
import com.revrobotics.CANSparkMax;
import com.revrobotics.ControlType;

import org.usfirst.lib6647.subsystem.SuperSubsystem;
import org.usfirst.lib6647.subsystem.supercomponents.SuperSparkMax;

import edu.wpi.first.wpilibj.shuffleboard.BuiltInLayouts;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardLayout;

public class Elevator extends SuperSubsystem implements SuperSparkMax {
	/** {@link CANSparkMax} instances used by the {@link Elevator}. */
	private CANSparkMax elevator, elevatorWheel;
	/**
	 * {@link CANPIDController} instance of the {@link Indexer}'s {@link #elevator
	 * main motor}.
	 */
	private CANPIDController elevatorPID;

	/**
	 * The {@link ShuffleboardLayout layout} to update in the {@link Shuffleboard}.
	 */
	private ShuffleboardLayout layout;

	/**
	 * Should only need to create a single of instance of {@link Elevator this
	 * class}; inside the {@link RobotContainer}.
	 */
	public Elevator() {
		super("elevator");

		// All SuperComponents must be initialized like this. The 'robotMap' Object is
		// inherited from the SuperSubsystem class, while the second argument is simply
		// this Subsystem's name.
		initSparks(robotMap, getName());

		// Additional initialiation & configuration.
		elevator = getSpark("elevator");
		elevatorPID = getSparkPID("elevator");
		elevatorWheel = getSpark("elevatorWheel");

		layout = Shuffleboard.getTab("Robot").getLayout("Elevator", BuiltInLayouts.kList);
	}

	@Override
	public void periodic() {
		// Debug data.
		layout.add("elevatorCurrent", elevator.getOutputCurrent()).withWidget(BuiltInWidgets.kGraph);
		layout.add("elevatorWheel", elevatorWheel).withWidget(BuiltInWidgets.kSpeedController);
	}

	/**
	 * Method to set the {@link #elevator}'s voltage value.
	 * 
	 * @param voltage The voltage at which to set the {@link #elevator motor}
	 */
	public void setElevatorVoltage(double voltage) {
		elevatorPID.setReference(voltage, ControlType.kCurrent);
	}

	/**
	 * Method to set the {@link #elevatorWheel}'s speed value.
	 * 
	 * @param speed The speed at which to set the {@link #elevatorWheel motor}
	 */
	public void setWheelSpeed(double speed) {
		elevatorWheel.set(speed);
	}
}
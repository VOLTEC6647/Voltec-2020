package org.usfirst.frc6647.subsystems;

import com.revrobotics.ControlType;

import org.usfirst.frc6647.robot.RobotContainer;
import org.usfirst.lib6647.subsystem.SuperSubsystem;
import org.usfirst.lib6647.subsystem.hypercomponents.HyperSparkMax;
import org.usfirst.lib6647.subsystem.supercomponents.SuperSparkMax;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;

/**
 * {@link SuperSubsystem} implementation for our {@link Elevator}.
 */
public class Elevator extends SuperSubsystem implements SuperSparkMax {
	/** {@link HyperSparkMax} instances used by this {@link Elevator subsystem}. */
	private HyperSparkMax elevator, elevatorWheel;

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
		elevatorWheel = getSpark("elevatorWheel");
		// ...

		outputToShuffleboard();
	}

	@Override
	protected void outputToShuffleboard() {
		try {
			layout.addNumber("elevatorCurrent", elevator::getOutputCurrent).withWidget(BuiltInWidgets.kGraph);
			// layout.add(elevator).withWidget(BuiltInWidgets.kSpeedController);
			// layout.add(elevatorWheel).withWidget(BuiltInWidgets.kSpeedController);
		} catch (NullPointerException e) {
			var error = String.format("[!] COULD NOT OUTPUT SUBSYSTEM '%1$s':\n\t%2$s.", getName(),
					e.getLocalizedMessage());

			System.out.println(error);
			DriverStation.reportWarning(error, false);
		}
	}

	/**
	 * Method to set the {@link #elevator}'s voltage value.
	 * 
	 * @param voltage The voltage at which to set the {@link #elevator motor}
	 */
	public void setElevatorVoltage(double voltage) {
		elevator.getPIDController().setReference(voltage, ControlType.kCurrent);
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
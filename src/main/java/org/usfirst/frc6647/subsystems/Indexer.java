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

/**
 * {@link SuperSubsystem} implementation of an {@link Indexer} mechanism, which
 * feeds balls into our {@link Turret} and {@link Shooter}
 */
public class Indexer extends SuperSubsystem implements SuperSparkMax {
	/** {@link CANSparkMax} instances used by the {@link Indexer}. */
	private CANSparkMax indexerLeft, indexerRight, pulleyFront, pulleyBack;
	/**
	 * {@link CANPIDController} instances of the {@link Indexer}'s
	 * {@link CANSparkMax motors}.
	 */
	private CANPIDController indexerLeftPID, indexerRightPID, pulleyFrontPID, pulleyBackPID;

	/**
	 * The {@link ShuffleboardLayout layout} to update in the {@link Shuffleboard}.
	 */
	private ShuffleboardLayout layout;

	/**
	 * Should only need to create a single of instance of {@link Indexer this
	 * class}; inside the {@link RobotContainer}.
	 */
	public Indexer() {
		super("indexer");

		// All SuperComponents must be initialized like this. The 'robotMap' Object is
		// inherited from the SuperSubsystem class, while the second argument is simply
		// this Subsystem's name.
		initSparks(robotMap, getName());

		// Additional initialiation & configuration.
		indexerLeft = getSpark("indexerLeft");
		indexerLeftPID = getSparkPID("indexerLeft");
		indexerRight = getSpark("indexerRight");
		indexerRightPID = getSparkPID("indexerRight");

		pulleyFront = getSpark("pulleyFront");
		pulleyFrontPID = getSparkPID("pulleyFront");
		pulleyBack = getSpark("pulleyBack");
		pulleyBackPID = getSparkPID("pulleyBack");

		layout = Shuffleboard.getTab("Robot").getLayout("Indexer", BuiltInLayouts.kList);
	}

	@Override
	public void periodic() {
		layout.add("indexerLeftCurrent", indexerLeft.getOutputCurrent()).withWidget(BuiltInWidgets.kGraph);
		layout.add("indexerRightCurrent", indexerRight.getOutputCurrent()).withWidget(BuiltInWidgets.kGraph);
		layout.add("pulleyFrontCurrent", pulleyFront.getOutputCurrent()).withWidget(BuiltInWidgets.kGraph);
		layout.add("pulleyBackCurrent", pulleyBack.getOutputCurrent()).withWidget(BuiltInWidgets.kGraph);
	}

	/**
	 * Method to set voltage of both {@link Indexer} motors.
	 * 
	 * @param leftVoltage  The voltage at which to set the {@link #indexerLeft left
	 *                     motor}
	 * @param rightVoltage The voltage at which to set the {@link #indexerRight
	 *                     right motor}
	 */
	public void setIndexerVoltage(double leftVoltage, double rightVoltage) {
		indexerLeftPID.setReference(leftVoltage, ControlType.kCurrent);
		indexerRightPID.setReference(rightVoltage, ControlType.kCurrent);
	}

	/**
	 * Method to set voltage of both {@link Indexer pulley} motors.
	 * 
	 * @param frontVoltage The voltage at which to set the {@link #pulleyFront front
	 *                     motor}
	 * @param backVoltage  The voltage at which to set the {@link #pulleyBack back
	 *                     motor}
	 */
	public void setPulleyVoltage(double frontVoltage, double backVoltage) {
		pulleyFrontPID.setReference(frontVoltage, ControlType.kCurrent);
		pulleyBackPID.setReference(backVoltage, ControlType.kCurrent);
	}
}
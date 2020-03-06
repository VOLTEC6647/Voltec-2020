package org.usfirst.frc6647.subsystems;

import com.revrobotics.ControlType;
import com.revrobotics.EncoderType;

import org.usfirst.frc6647.robot.RobotContainer;
import org.usfirst.lib6647.subsystem.SuperSubsystem;
import org.usfirst.lib6647.subsystem.hypercomponents.HyperSparkMax;
import org.usfirst.lib6647.subsystem.supercomponents.SuperSparkMax;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;

/**
 * {@link SuperSubsystem} implementation of an {@link Indexer} mechanism, which
 * feeds balls into our {@link Turret} and {@link Shooter}
 */
public class Indexer extends SuperSubsystem implements SuperSparkMax {
	/** {@link HyperSparkMax} instances used by this {@link Indexer subsystem}. */
	private HyperSparkMax indexerLeft, indexerRight, pulleyFront, pulleyBack;

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
		indexerRight = getSpark("indexerRight");

		pulleyFront = getSpark("pulleyFront");
		pulleyBack = getSpark("pulleyBack");
		// ...

		outputToShuffleboard();
		indexerLeft.getEncoder(EncoderType.kNoSensor, 0);
		indexerRight.getEncoder(EncoderType.kNoSensor, 0);
	}

	@Override
	protected void outputToShuffleboard() {
		try {
			layout.addNumber("indexerLeftCurrent", indexerLeft::getOutputCurrent).withWidget(BuiltInWidgets.kGraph);
			layout.addNumber("indexerRightCurrent", indexerRight::getOutputCurrent).withWidget(BuiltInWidgets.kGraph);
			layout.addNumber("pulleyFrontCurrent", pulleyFront::getOutputCurrent).withWidget(BuiltInWidgets.kGraph);
			layout.addNumber("pulleyBackCurrent", pulleyBack::getOutputCurrent).withWidget(BuiltInWidgets.kGraph);
		} catch (NullPointerException e) {
			var error = String.format("[!] COULD NOT OUTPUT SUBSYSTEM '%1$s':\n\t%2$s.", getName(),
					e.getLocalizedMessage());

			System.out.println(error);
			DriverStation.reportWarning(error, false);
		}
	}

	/**
	 * Method to set voltage of both {@link Indexer} motors.
	 * 
	 * @param leftCurrent  The voltage at which to set the {@link #indexerLeft left
	 *                     motor}
	 * @param rightCurrent The voltage at which to set the {@link #indexerRight
	 *                     right motor}
	 */
	public void setIndexerCurrent(double leftCurrent, double rightCurrent) {
		indexerLeft.getPIDController().setReference(leftCurrent, ControlType.kCurrent);
		indexerRight.getPIDController().setReference(rightCurrent, ControlType.kCurrent);

	}
	
	public void setIndexer(double leftSpeed, double rightSpeed) {
		indexerLeft.set(leftSpeed);
		indexerRight.set(rightSpeed);
	}

	/**
	 * Stops the {@link Indexer}'s {@link #indexerLeft left} and
	 * {@link #indexerRight right} motors dead in their tracks.
	 */
	public void stopIndexer() {
		indexerLeft.stopMotor();
		indexerRight.stopMotor();
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
		pulleyFront.getPIDController().setReference(frontVoltage, ControlType.kCurrent);
		pulleyBack.getPIDController().setReference(backVoltage, ControlType.kCurrent);
	}

	public void setPulley(double frontVoltage, double backVoltage){
		pulleyFront.set(frontVoltage);
		pulleyBack.set(backVoltage);
	}

	/**
	 * Stops the {@link Indexer}'s {@link #pulleyFront front} and {@link #pulleyBack
	 * back} pulley motors dead in their tracks.
	 */
	public void stopPulley() {
		pulleyFront.stopMotor();
		pulleyBack.stopMotor();
	}
}
package org.usfirst.frc6647.subsystems;

import com.revrobotics.ControlType;

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
	}

	@Override
	public void outputToShuffleboard() {
		try {
			layout.add(indexerLeft).withWidget(BuiltInWidgets.kSpeedController);
			layout.add(indexerRight).withWidget(BuiltInWidgets.kSpeedController);

			layout.add(pulleyFront).withWidget(BuiltInWidgets.kSpeedController);
			layout.add(pulleyBack).withWidget(BuiltInWidgets.kSpeedController);
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

	/**
	 * Method to set the speed of both {@link Indexer} motors.
	 * 
	 * @param leftSpeed  The speed at which to set the {@link #indexerLeft left
	 *                   motor}
	 * @param rightSpeed The speed at which to set the {@link #indexerRight right
	 *                   motor}
	 */
	public void setIndexerSpeed(double leftSpeed, double rightSpeed) {
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

	/**
	 * Method to set speed of both {@link Indexer pulley} motors.
	 * 
	 * @param frontSpeed The speed at which to set the {@link #pulleyFront front
	 *                   motor}
	 * @param backSpeed  The speed at which to set the {@link #pulleyBack back
	 *                   motor}
	 */
	public void setPulleySpeed(double frontSpeed, double backSpeed) {
		pulleyFront.set(frontSpeed);
		pulleyBack.set(backSpeed);
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
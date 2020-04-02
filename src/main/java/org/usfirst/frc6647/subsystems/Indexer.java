package org.usfirst.frc6647.subsystems;

import java.util.Map;

import com.revrobotics.ControlType;

import org.usfirst.frc6647.robot.RobotContainer;
import org.usfirst.lib6647.loops.ILooper;
import org.usfirst.lib6647.loops.Loop;
import org.usfirst.lib6647.loops.LoopType;
import org.usfirst.lib6647.subsystem.SuperSubsystem;
import org.usfirst.lib6647.subsystem.hypercomponents.HyperSparkMax;
import org.usfirst.lib6647.subsystem.supercomponents.SuperSparkMax;

import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.AnalogTrigger;
import edu.wpi.first.wpilibj.Counter;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;

/**
 * {@link SuperSubsystem} implementation of an {@link Indexer} mechanism, which
 * feeds balls into our {@link Turret} and {@link Shooter}
 */
public class Indexer extends SuperSubsystem implements SuperSparkMax {
	/** {@link HyperSparkMax} instances used by this {@link Indexer subsystem}. */
	private HyperSparkMax indexerLeft, indexerRight, pulleyFront, pulleyBack;

	/** Whether or not auto-indexing is enabled. */
	private boolean autoIndexing = false;

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

		// TODO: Set this via JSON.
		pulleyFront.getEncoder().setPosition(0);
		pulleyBack.getEncoder().setPosition(0);
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
	 * Method to toggle {@link #autoIndexing} value.
	 */
	public void toggleAutoIndexing() {
		autoIndexing = !autoIndexing;
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
	 * Method to set position of both {@link Indexer pulley} motors.
	 * 
	 * @param position The position at which to set both motors
	 */
	public void setPulleyPosition(double position) {
		pulleyFront.getPIDController().setReference(position, ControlType.kPosition);
		pulleyBack.getPIDController().setReference(position, ControlType.kPosition);
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

	@Override
	public void registerLoops(ILooper looper) {
		looper.register(new Loop() { // Auto indexer loop.
			private AnalogTrigger irTrigger;
			private Counter ballCounter;

			@Override
			public void onFirstStart(double timestamp) {
				synchronized (Indexer.this) {
					var ir = new AnalogInput(0);
					ir.setAverageBits(4);

					irTrigger = new AnalogTrigger(ir);
					irTrigger.setLimitsRaw(200, 1800);

					ballCounter = new Counter(irTrigger);
					ballCounter.setUpSourceEdge(true, false);
					ballCounter.setUpDownCounterMode();

					layout.add(ballCounter).withWidget(BuiltInWidgets.kNumberBar)
							.withProperties(Map.of("Min", 0, "Max", 4, "Center", 2));
				}
			}

			@Override
			public void onStart(double timestamp) {
				synchronized (Indexer.this) {
					System.out.println("Auto-indexer started at: " + timestamp + "!");
				}
			}

			@Override
			public void onLoop(double timestamp) {
				if (!autoIndexing) {
					if (ballCounter.get() != 0)
						ballCounter.reset(); // Might change if it proves to be inconvenient.
					return;
				}

				synchronized (Indexer.this) {
					var position = pulleyFront.getEncoder().getPosition();

					if (irTrigger.getTriggerState() && ballCounter.get() < 4 && ballCounter.get() > 0) {
						setIndexerSpeed(1, 1);
						setPulleyPosition(ballCounter.get() == 1 ? position - 25 : position - 15);
					} else if (irTrigger.getTriggerState() && ballCounter.get() >= 4)
						stopIndexer();
					else
						setIndexerSpeed(1, 1);
				}
			}

			@Override
			public void onStop(double timestamp) {
				System.out.println("Auto-indexer stopped at: " + timestamp + ".");
			}

			@Override
			public LoopType getType() {
				return LoopType.ENABLED;
			}
		});
	}
}
package org.usfirst.frc6647.subsystems;

import org.usfirst.lib6647.subsystem.SuperSubsystem;
import org.usfirst.lib6647.vision.LimelightCamera;
import org.usfirst.lib6647.vision.LimelightControlModes.LEDMode;
import org.usfirst.lib6647.vision.LimelightData.Data;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;

public class Vision extends SuperSubsystem {

	// TODO: Comment this properly.

	private LimelightCamera limelight;

	public Vision() {
		super("vision");

		limelight = new LimelightCamera("limelight");
	}

	@Override
	public void outputToShuffleboard() {
		try {
			layout.addBoolean("isOnline", limelight::isConnected).withWidget(BuiltInWidgets.kBooleanBox);
			layout.addBoolean("targetFound", limelight::isTargetFound).withWidget(BuiltInWidgets.kBooleanBox);
		} catch (NullPointerException e) {
			var error = String.format("[!] COULD NOT OUTPUT SUBSYSTEM '%1$s':\n\t%2$s.", getName(),
					e.getLocalizedMessage());

			System.out.println(error);
			DriverStation.reportWarning(error, false);
		}
	}

	public void setLED(LEDMode mode) {
		limelight.setLEDMode(mode);
	}

	public double getHorizontalRotation() {
		return limelight.getData(Data.HORIZONTAL_OFFSET);
	}

	public double getTargetArea() {
		return limelight.getData(Data.TARGET_AREA);
	}

	public void setLimelightPipeline(int pipeline) {
		limelight.setPipeline(pipeline);
	}
}
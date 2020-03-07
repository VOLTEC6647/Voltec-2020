/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package org.usfirst.frc6647.robot;

/**
 * The Constants class provides a convenient place for teams to hold robot-wide
 * numerical or boolean constants. This class should not be used for any other
 * purpose. All constants should be declared globally (i.e. public static). Do
 * not put anything functional in this class.
 *
 * <p>
 * It is advised to statically import this class (or one of its inner classes)
 * wherever the constants are needed, to reduce verbosity.
 */
public final class Constants {
	public class DriveConstants {
		// TODO: Set these accordingly.
		public static final double trackWidthMeters = 0.6647246510483854;

		private static final double wheelDiameterMeters = 0.1524;
		private static final int encoderCPR = 2048;
		public static final double encoderDistancePerPulse = (wheelDiameterMeters * Math.PI) / (double) encoderCPR;

		public static final double ksVolts = 0.836;
		public static final double kvVoltSecondsPerMeter = 3.16;
		public static final double kaVoltSecondsSquaredPerMeter = 0.416;

		public static final double kPDriveVel = 47.7;
	}

	public class AutoConstants {

	}
	
	public class GyroConstants {
		public static final double collisionThresholdDeltaG = 0.25f;
	}

	public class ShooterConstants {
		public static final float tolerance = 30.0f;

		// TODO: Set these accordingly.
		public static final double initiationLineRPM = 0;
		public static final double trenchRPM = 0;
		public static final double behindTrenchRPM = 0;
		public static final double cursedRPM = 0;
	}

	public class Aim {
		public static final double kP = 0;
		public static final double kI = 0;
		public static final double kD = 0;
		public static final int maxVelocity = 360;
		public static final int maxAceleration = 360;
	}
}

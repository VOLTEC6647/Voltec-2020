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
		private static final double encoderCPR = 14933.19;
		public static final double encoderDistancePerPulse = (wheelDiameterMeters * Math.PI) / encoderCPR;

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

	public class TurretConstants {
		public static final double reduction = 702;
		public static final double ticksPerRotation = 42;
		public static final float tolerance = 0.01f;
	}

	public class ShooterConstants {
		public static final float tolerance = 30.0f;

		public static final double initiationLineRPM = 2100;
		public static final double trenchRPM = 2750;
		public static final double behindTrenchRPM = 3555;
		public static final double cursedRPM = 1800;
		
		public static final double initiationLineAngle = 50;
		public static final double trenchAngle = 59;
		public static final double behindTrenchAngle = 57;
		public static final double cursedAngle = 20;
	}

	public class Aim {
		public static final double kP = 0;
		public static final double kI = 0;
		public static final double kD = 0;
		public static final int maxVelocity = 360;
		public static final int maxAceleration = 360;
	}
}

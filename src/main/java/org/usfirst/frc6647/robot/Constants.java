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
	public class GyroConstants {
		public static final double collisionThresholdDeltaG = 0.5f;
	}

	public class ShooterConstants {
		public static final float tolerance = 30.0f;
	}

	public class TurretConstants {
		public static final double rotationsPerTick = 1;
		public static final float tolerance = 0.01f;
	}

	public class Aim{
		public static final double kP = 0;
		public static final double kI = 0;
		public static final double kD = 0;
		public static final int maxVelocity = 360;
		public static final int maxAceleration = 360;


	}
}

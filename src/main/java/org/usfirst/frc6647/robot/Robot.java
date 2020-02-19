/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package org.usfirst.frc6647.robot;

import org.usfirst.frc6647.subsystems.Chassis;
import org.usfirst.lib6647.loops.LooperRobot;
import org.usfirst.lib6647.oi.JController;

import edu.wpi.first.wpilibj.GenericHID.Hand;

public class Robot extends LooperRobot {
	/** Static {@link Robot} instance. */
	private static Robot instance = null;

	/**
	 * Method for getting currently running {@link Robot} instance.
	 * 
	 * @return The current {@link Robot} instance
	 */
	public synchronized static Robot getInstance() {
		return instance;
	}

	/**
	 * Constructor for this implementation of {@link LooperRobot}, should only need
	 * to be created once, by the {@link Main} class.
	 */
	protected Robot() {
		super();

		if (instance == null) // Might not be necessary, but just in case.
			instance = this;

		initJoysticks();
		registerSubsystems(Chassis::new);
	}

	/**
	 * Run any {@link JController} initialization here.
	 */
	private void initJoysticks() {
		// Create JController object.
		var driver1 = new JController(0);

		System.out.printf("Found: '%s'!\n", driver1.getName());

		if (driver1.getName().equals("Wireless Controller")) {
			driver1.setXY(Hand.kLeft, 0, 1);
			driver1.setXY(Hand.kRight, 2, 5);
		} else if (driver1.getName().equals("Generic   USB  Joystick")) {
			driver1.setXY(Hand.kLeft, 0, 1);
			driver1.setXY(Hand.kRight, 2, 4);
		}

		// Register each instantiated JController object in the joysticks HashMap.
		registerJoystick(driver1, "driver1");
	}
}

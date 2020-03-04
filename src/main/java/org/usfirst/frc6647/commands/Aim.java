/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package org.usfirst.frc6647.commands;

import org.usfirst.frc6647.robot.Constants;
import org.usfirst.frc6647.robot.Robot;
import org.usfirst.frc6647.robot.RobotContainer;
import org.usfirst.frc6647.subsystems.Chassis;

import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.controller.ProfiledPIDController;
import edu.wpi.first.wpilibj.trajectory.TrapezoidProfile;
import edu.wpi.first.wpilibj2.command.ProfiledPIDCommand;

// NOTE:  Consider using this command inline, rather than writing a subclass.  For more
// information, see:
// https://docs.wpilib.org/en/latest/docs/software/commandbased/convenience-features.html
public class Aim extends ProfiledPIDCommand {
  /**
   * Creates a new Aim.
   */

  boolean hasTarget = false;

  public Aim() {
    super(
        // The ProfiledPIDController used by the command
        new ProfiledPIDController(
            // The PID gains
            Constants.Aim.kP, Constants.Aim.kI, Constants.Aim.kD,
            // The motion profile constraints
            new TrapezoidProfile.Constraints(Constants.Aim.maxVelocity, Constants.Aim.maxAceleration)),
        // This should return the measurement
        () -> NetworkTableInstance.getDefault().getTable("limelight").getEntry("ty").getDouble(0),
        // This should return the goal (can also be a constant)
        () -> 0,
        // This uses the output
        (output, setpoint) -> Robot.getInstance().getContainer().getChassis().arcadeDrive(0, output),
        // Use the output (and setpoint, if desired) here
        Robot.getInstance().getContainer().getChassis());
    // Use addRequirements() here to declare subsystem dependencies.
    // Configure additional PID options by calling `getController` here.
    getController().setTolerance(0.3);
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return false;
  }
}

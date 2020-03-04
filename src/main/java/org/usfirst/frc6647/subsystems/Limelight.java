/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

//Based on Spectrum3847 Code https://github.com/Spectrum3847/Ultraviolet-2020/blob/master/src/main/java/frc/robot/subsystems/VisionLL.java
package org.usfirst.frc6647.subsystems;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import org.usfirst.lib6647.vision.LimeLight;
import org.usfirst.lib6647.vision.LimeLightControlModes;
import org.usfirst.lib6647.vision.LimeLightControlModes.LedMode;
import org.usfirst.frc6647.robot.Robot;
import org.usfirst.frc6647.robot.RobotContainer;

public class Limelight extends SubsystemBase {

    public final LimeLight limelight;
    private boolean LEDState;

    /**
     * Creates a new VisionLL.
     */
    public Limelight() {
        limelight = new LimeLight();
    }

    @Override
    public void periodic() {
        // This method will be called once per scheduler run
        //If disabled and LED-Toggle is false, than leave lights off, else they should be on
        //if(!SmartDashboard.getBoolean("Limelight-LED Toggle", false) && !(RobotContainer.driverController.aButton.get() && (Robot.s_robot_state == RobotState.TELEOP))){
        /*if(Robot.s_robot_state == RobotState.DISABLED && !SmartDashboard.getBoolean("Limelight-LED Toggle", false) && !DriverStation.getInstance().isFMSAttached()){
        if (LEDState == true) {
            limeLightLEDOff();
            LEDState = false;
        }
        } else {
        if (LEDState == false) {
            limeLightLEDOn();
            LEDState = true;
        }
        } */
    }

    public void limeLightLEDOff(){
        limelight.setLEDMode(LedMode.kforceOff);
    }

    public void limeLightLEDOn(){
        limelight.setLEDMode(LedMode.kforceOn);
    }

    public void setLimeLightLED(boolean b){
        if (b){
            limeLightLEDOn();
            LEDState = true;
        } else{
            limeLightLEDOff();
            LEDState = false;
        }
    }

    public double getLLDegToTarget(){
        return limelight.getdegRotationToTarget();
    }

    public boolean getLLIsTargetFound(){
        return limelight.getIsTargetFound();
    }

    public double getLLTargetArea(){
        return limelight.getTargetArea();
    }

    public boolean getLimelightHasValidTarget(){
        return limelight.getIsTargetFound();
    }

    public void setLimeLightPipeline(int i) {
    setLimeLightPipeline(i);
    }
}

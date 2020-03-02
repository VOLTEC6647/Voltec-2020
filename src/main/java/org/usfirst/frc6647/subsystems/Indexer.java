/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package org.usfirst.frc6647.subsystems;

import com.revrobotics.CANEncoder;
import com.revrobotics.CANPIDController;
import com.revrobotics.CANSparkMax;
import com.revrobotics.ControlType;

import org.usfirst.lib6647.subsystem.SuperSubsystem;
import org.usfirst.lib6647.subsystem.supercomponents.SuperSparkMax;

import edu.wpi.first.wpilibj.shuffleboard.BuiltInLayouts;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardLayout;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;

public class Indexer extends SuperSubsystem implements SuperSparkMax {
  /**
   * Creates a new Indexer.
   */
  private CANSparkMax indexerL, indexerR, pulleyL, pulleyR;
  private CANPIDController indexerLPID,indexerRPID, pulleyLPID, pulleyRPID;
  private CANEncoder lEncoder, rEncoder;
  private ShuffleboardTab tab = Shuffleboard.getTab("Robot");
	private ShuffleboardLayout layout;
  
  public Indexer() {
    super("indexer");
    initSparks(robotMap, getName());
    indexerL = getSpark("indexerL");
    indexerR = getSpark("indexerR");

    indexerLPID = getSparkPID("indexerL");
    indexerRPID = getSparkPID("indexerR");

    pulleyL = getSpark("pulleyL");
    pulleyR = getSpark("pulleyR");

    pulleyLPID = getSparkPID("pulleyL");
    pulleyRPID = getSparkPID("pulleyR");

    lEncoder = getSparkEncoder("pulleyL");
    rEncoder = getSparkEncoder("pulleyR");

    layout = tab.getLayout("indexer", BuiltInLayouts.kList);
  }

  @Override
  public void periodic() {
    // This method will be called once per scheduler run
    outputToSmartDashboard();
  }

  public void setIndexerCurrent(int Lcurrent, int Rcurrent){
    indexerLPID.setReference(Lcurrent, ControlType.kCurrent);
    indexerRPID.setReference(Rcurrent, ControlType.kCurrent);
  }

  public void setPulley(int speed){
    pulleyL.set(speed);
    pulleyR.set(speed);
  }

  public void outputToSmartDashboard() {
		layout.add("indexer_Left_Current", indexerL.getOutputCurrent());
    layout.add("indexer_Right_Current", indexerR.getOutputCurrent());
    layout.add("pulley_Left_Velocity", lEncoder.getVelocity());
    layout.add("pulley_Left_Velocity", rEncoder.getVelocity());
	}
}

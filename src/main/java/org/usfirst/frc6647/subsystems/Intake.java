package org.usfirst.frc6647.subsystems;

import org.usfirst.frc6647.robot.Robot;

import org.usfirst.lib6647.oi.JController;
import org.usfirst.lib6647.subsystem.SuperSubsystem;
import org.usfirst.lib6647.subsystem.hypercomponents.HyperTalon;
import org.usfirst.lib6647.subsystem.hypercomponents.HyperVictor;
import org.usfirst.lib6647.subsystem.supercomponents.SuperVictor;

/**
 * Ball intake mechanism {@link SuperSubsystem} implementation.
 * 
 */

public class Intake extends SuperSubsystem implements SuperVictor {

    private HyperVictor intakeMotor;
    private JController joystick;

    public Intake(String name) {
        super("intake");
        initVictors(robotMap, getName());

        intakeMotor = getVictor("intakeMotor");

        joystick = Robot.getInstance().getJoystick("driver1");

        configureButtonBindings();
    }

    private void configureButtonBindings() {
        joystick.get("X").whenPressed(() -> {
            setIntake(9);
        });
    }

    /**
     * Use {@link HyperTalon talons} as intake.
     * 
     * @param voltage To turn intake counterclockwise speed
     */
    private void setIntake(double voltage) {
        intakeMotor.setVoltage(voltage);
    }

}
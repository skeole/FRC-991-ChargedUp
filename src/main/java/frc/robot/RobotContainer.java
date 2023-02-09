// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

//import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.XboxController;
import frc.robot.autonomous.Taxi;
import frc.robot.commands.CheezyDrive;
import frc.robot.commands.ShiftGears;
import frc.robot.subsystems.DoubleArm;
import frc.robot.subsystems.DriveTrain;
import frc.robot.subsystems.TeleopDoubleArm;

import static frc.robot.Constants.*;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.button.JoystickButton;

public class RobotContainer {
    private final Joystick operator = new Joystick(1);

    private final int horizAxis = XboxController.Axis.kLeftX.value;
    private final int vertAxis = XboxController.Axis.kLeftY.value;

    private final DriveTrain driveTrainSubsystem = new DriveTrain();
    
    private final Taxi taxiAuto = new Taxi(driveTrainSubsystem);

    private final Joystick leftJoystick = new Joystick(kLeftJoystickId);
    private final Joystick rightJoystick = new Joystick(kRightJoystickId);

    private final DoubleArm doubleArm = new DoubleArm();

    public RobotContainer() {
        doubleArm.setDefaultCommand(
            new TeleopDoubleArm(
                doubleArm, 
                () -> operator.getRawAxis(horizAxis),
                () -> -operator.getRawAxis(vertAxis)
            )
        );

        configureButtonBindings();
    }

    private void configureButtonBindings() {
        new JoystickButton(leftJoystick, Joystick.ButtonType.kTrigger.value)
            .or(new JoystickButton(rightJoystick, Joystick.ButtonType.kTrigger.value))
            .whenActive(new ShiftGears(driveTrainSubsystem));

        driveTrainSubsystem.setDefaultCommand(
            new CheezyDrive(driveTrainSubsystem, leftJoystick::getY, rightJoystick::getY));
    }

    public Command getAutonomousCommand() {
        return taxiAuto;
    }
}
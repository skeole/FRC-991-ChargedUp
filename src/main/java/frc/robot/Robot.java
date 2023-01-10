// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.XboxController;
//import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
//import edu.wpi.first.wpilibj2.command.Command;
//import edu.wpi.first.wpilibj2.command.CommandScheduler;
import frc.robot.subsystems.SwerveDriveTrain;

import static frc.robot.Constants.*;

public class Robot extends TimedRobot {
  //private Command m_autonomousCommand;
  //private RobotContainer m_robotContainer;

  private SwerveDriveTrain swerve;
  private XboxController driver;

  @Override
  public void robotInit() {
    //m_robotContainer = new RobotContainer();
    swerve = new SwerveDriveTrain();
  }

  @Override
  public void robotPeriodic() { //autonomous command OR all non-driving teleOp Commands
    //CommandScheduler.getInstance().run();
  }

  @Override
  public void autonomousInit() {
    //m_autonomousCommand = m_robotContainer.getAutonomousCommand();

    //if (m_autonomousCommand != null) {
    //  m_autonomousCommand.schedule();
    //}
    swerve.turnClockwise(45);
    pause(0.5);
    swerve.turnToDegree(-90);
    pause(0.5);
    swerve.turnToDegree(180);
    pause(0.5);
    swerve.strafe(new double[] {1, 0}, 0.3, 5);
    pause(0.5);
    swerve.strafe(new double[] {-1, 1}, 0.3, 5);
    pause(0.5);
  }

  @Override
  public void teleopInit() {
    driver = new XboxController(0);
;    //if (m_autonomousCommand != null) {
    //  m_autonomousCommand.cancel();
    //}
  }

  @Override
  public void teleopPeriodic() { //driving code
    swerve.drive(driver.getLeftX(), 0 - driver.getLeftY(), driver.getRightX(), 0 - driver.getRightY(), 1 - 0.5 * driver.getRightTriggerAxis(), driver.getAButton());
  }
}

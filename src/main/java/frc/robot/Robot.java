// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.XboxController;
//import edu.wpi.first.wpilibj2.command.Command;
//import edu.wpi.first.wpilibj2.command.CommandScheduler;
import frc.robot.subsystems.SwerveDriveTrain;

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

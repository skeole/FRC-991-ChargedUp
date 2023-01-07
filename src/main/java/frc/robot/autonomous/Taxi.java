package frc.robot.autonomous;

import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitCommand;
import frc.robot.commands.CheezyDrive;
import frc.robot.subsystems.DriveTrain;

public class Taxi extends SequentialCommandGroup {
    public Taxi(DriveTrain driveTrain) {
        addCommands(
            // drive forward with speed of 0.5 and turn of 0; interrupt after 5 seconds
            new CheezyDrive(driveTrain, () -> 0.5, () -> 0).withTimeout(5),
            // stop the motors
            new CheezyDrive(driveTrain, () -> 0, () -> 0),
            // wait half a second; convenience method provided by WPILib
            new WaitCommand(0.5)
        );
    }
}
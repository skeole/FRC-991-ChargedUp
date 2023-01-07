package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.DriveTrain;

public class DriveDistance extends CommandBase {
    private final DriveTrain _driveTrain;
    private final double _distance;

    public DriveDistance(DriveTrain driveTrain, double distance) {
        _driveTrain = driveTrain;
        _distance = distance;
        addRequirements(driveTrain);
    }

    @Override
    public void initialize() {
        _driveTrain.setPosition(_distance);
    }

    @Override
    public boolean isFinished() {
        return _driveTrain.isClosedLoopAtTarget();
    }
}

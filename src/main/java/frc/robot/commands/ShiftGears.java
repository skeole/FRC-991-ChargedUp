package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.DriveTrain;

public class ShiftGears extends CommandBase {
    private final DriveTrain _driveTrain;

    public ShiftGears(DriveTrain driveTrain) {
        _driveTrain = driveTrain;
        addRequirements(_driveTrain);
    }

    @Override
    public void initialize() {
        _driveTrain.toggleShifter();
    }

    @Override
    public boolean isFinished() {
        return true;
    }
}

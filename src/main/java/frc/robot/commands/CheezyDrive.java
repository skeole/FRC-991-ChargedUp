package frc.robot.commands;

import java.util.function.DoubleSupplier;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.DriveTrain;

public class CheezyDrive extends CommandBase {
    private final DriveTrain _driveTrain;
    private final DoubleSupplier _forward;
    private final DoubleSupplier _rotation;

    public CheezyDrive(DriveTrain driveTrain, DoubleSupplier forward, DoubleSupplier rotation) {
        _driveTrain = driveTrain;
        _forward = forward;
        _rotation = rotation;
        addRequirements(_driveTrain);
    }

    @Override
    public void execute() {
        double turnPower = _rotation.getAsDouble() * Math.abs(_forward.getAsDouble());
        _driveTrain.set(_forward.getAsDouble() + turnPower, _forward.getAsDouble() - turnPower);
    }

    @Override
    public boolean isFinished() {
        return false;
    }    
}

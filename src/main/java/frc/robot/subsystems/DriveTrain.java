package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.InvertType;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.PneumaticsModuleType;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

import static frc.robot.Constants.*;

public class DriveTrain extends SubsystemBase {
    private final TalonSRX LeftPrimary = new TalonSRX(kDriveLeftId);
    private final TalonSRX LeftFollower = new TalonSRX(kDriveLeftFollowerId);
    private final TalonSRX RightPrimary = new TalonSRX(kDriveRightId);
    private final TalonSRX RightFollower = new TalonSRX(kDriveRightFollowerId);
    private final Solenoid LeftShifter = new Solenoid(kPneumaticsControlModuleId, PneumaticsModuleType.CTREPCM, kDriveLeftSolenoidId);
    private final Solenoid RightShifter = new Solenoid(kPneumaticsControlModuleId, PneumaticsModuleType.CTREPCM, kDriveRightSolenoidId);

    public DriveTrain() {
        RightPrimary.setInverted(InvertType.InvertMotorOutput);
        LeftFollower.set(ControlMode.Follower, 10);
        RightFollower.set(ControlMode.Follower, 12);
    }

    public void set(double speed) {
        set(speed, speed);
    }

    public void set(double left, double right) {
        LeftPrimary.set(ControlMode.PercentOutput, left);
        RightPrimary.set(ControlMode.PercentOutput, right);
    }

    public void setPosition(double position) {
        LeftPrimary.set(ControlMode.Position, position);
        RightPrimary.set(ControlMode.Position, position);
    }

    public boolean isClosedLoopAtTarget() {
        boolean leftStatus = Math.abs(LeftPrimary.getClosedLoopError()) < 10;
        boolean rightStatus = Math.abs(RightPrimary.getClosedLoopError()) < 10;
        return leftStatus && rightStatus;
    }

    public void toggleShifter() {
        LeftShifter.toggle();
        RightShifter.toggle();
    }
}
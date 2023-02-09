package frc.robot.subsystems;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;

public class Limelight {
    
    NetworkTable table = NetworkTableInstance.getDefault().getTable("limelight");
    NetworkTableEntry tx = table.getEntry("tx");
    NetworkTableEntry ty = table.getEntry("ty");
    NetworkTableEntry ta = table.getEntry("ta");

    public double[] getValues(){
        double x = tx.getDouble(0.0);
        double y = ty.getDouble(0.0);
        double area = ta.getDouble(0.0);
        return new double[] {x, y, area};
    }

    public void showTelemetry(){
        double[] vals = getValues();
        SmartDashboard.putNumber("LimelightX", vals[0]);
        SmartDashboard.putNumber("LimelightY", vals[1]);
        SmartDashboard.putNumber("LimelightArea", vals[2]);
    }
}
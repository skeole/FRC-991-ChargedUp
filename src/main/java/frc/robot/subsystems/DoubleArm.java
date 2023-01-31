package frc.robot.subsystems;

import com.revrobotics.CANSparkMax;
// go to WPILib Command Palette -> Manage Vendor Libraries -> Install new libraries (online) -> URL is https://software-metadata.revrobotics.com/REVLib-2023.json
// run ./gradlew build in terminal to run gradle build
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import static frc.robot.Constants.*;

public class DoubleArm {
    
    private CANSparkMax first_pivot;
    private CANSparkMax second_pivot;

    private Encoder first_pivot_encoder;
    private Encoder second_pivot_encoder;

    private double first_pivot_target;
    private double second_pivot_target;
    
    public DoubleArm() {
        first_pivot = new CANSparkMax(f_pivot, MotorType.kBrushless); // NEO Motors are brushless
        second_pivot = new CANSparkMax(s_pivot, MotorType.kBrushless);

        first_pivot_encoder = new Encoder(0, 1); // idk what channelA and B are
        second_pivot_encoder = new Encoder(0, 1);

        first_pivot_encoder.setReverseDirection(false); // we want it so the encoder increases when the arm goes counterclockwise
        second_pivot_encoder.setReverseDirection(false);

        first_pivot_encoder.setDistancePerPulse(Math.PI / 4986.0); // 2048 * 4 ticks per revolution
        second_pivot_encoder.setDistancePerPulse(Math.PI / 4986.0); // adjust this there's a gear ratio
            // 2pi of an angle per every 8192 ticks

        first_pivot_target = first_pivot_encoder.getDistance();
        second_pivot_target = second_pivot_encoder.getDistance();
    }

    public void resetEncoders() { // do this at the beginning just to normalize it
        first_pivot_encoder.reset();
        second_pivot_encoder.reset();

        first_pivot_target = first_pivot_encoder.getDistance(); // should be 0
        second_pivot_target = second_pivot_encoder.getDistance();
    }

    public double[] getEncoderValues() {
        return new double[] {
            first_pivot_encoder.getDistance(),
            first_pivot_encoder.getDistance() - first_encoder_zero,
            second_pivot_encoder.getDistance(),
            second_pivot_encoder.getDistance() - second_encoder_zero,
        };
    }

    private double[] getArmAngles() {
        return new double[] {
            first_pivot_encoder.getDistance() - first_encoder_zero, 
            second_pivot_encoder.getDistance() - second_encoder_zero
        };
    }

    private double getFirstPivotTorque() {
        double[] armAngles = getArmAngles();
        double torque = first_arm_length * Math.cos(armAngles[0]) * (first_arm_center * first_arm_weight + second_arm_weight) + 
                        second_arm_length * Math.cos(armAngles[1]) * second_arm_center * second_arm_weight;

        return 0 - torque / 12.0; // pound-feet, positive means motor has to supply torque counterclockwise
                                
    } // currently, I'm assuming that the claw is rigid with the second arm. If it's not, I will have to account for that as well

    private double getSecondPivotTorque() {
        double torque = second_arm_length * Math.cos((getArmAngles())[1]) * second_arm_center * second_arm_weight;
        return 0 - torque / 12.0;
    }

    public void goToPosition(double x, double y) {
        double[] temp = convertPositionToAngles(x, y);
        first_pivot_target = temp[0];
        second_pivot_target = temp[1];
        tick();
    }

    private double speed(double error_radians) {
        return 2 * error_radians; // our target speed is 2 * angular error
                                  // as a ratio; if magnitude > 1 -> it gets clipped
    }

    private void tick() {
        double[] current_angles = getArmAngles();
        
        // Velocity PID
        first_pivot.setVoltage(Math.min(12, Math.max(-12, 
            first_pivot.getBusVoltage() + // whatever our current voltage is

            3 * (Math.min(1, Math.max(-1, speed(first_pivot_target - current_angles[0]))) * target_angular_speed * Math.PI / 180.0 - first_pivot_encoder.getRate()))
            // if we're going slower/faster than we want, then correct that
            // target angular speed is decreased if we are closer than half a radian
            // we will have to tune this
        ));

        second_pivot.setVoltage(Math.min(12, Math.max(-12, 
            second_pivot.getBusVoltage() + 
            3 * (Math.min(1, Math.max(-1, speed(second_pivot_target - current_angles[1]))) * target_angular_speed * Math.PI / 180.0 - second_pivot_encoder.getRate()))
        ));

        if (show_data) {
            SmartDashboard.putNumberArray("Torque Data", new double[] {
                getFirstPivotTorque(),
                first_pivot.getBusVoltage(),
                getSecondPivotTorque(),
                second_pivot.getBusVoltage()
            });
        }
    }

    private double[] convertPositionToAngles(double x, double y) { // position is with respect to the first pivot, not the ground

        // don't let x be less than 0
        // plan: if y < 0, then make it so the arm is concave up
        // if y > 0, make it so the arm is concave down

        // assume the minimum for the radius is around 4 / 3 * theoretical minimum and maximum is 9 / 10 * theoretical maximum
        // for now, assume that x > 0 at all times

        if (x <= 0.25) {
            x = 0.25;
        }
        double radius = Math.sqrt(x * x + y * y); // we don't have to worry about divide by zero errors
        if (radius < 4.0 / 3.0 * (first_arm_length - second_arm_length)) {
            x *= 4.0 * (first_arm_length - second_arm_length) / 3.0 / radius;
            y *= 4.0 * (first_arm_length - second_arm_length) / 3.0 / radius;
            radius = 4.0 * (first_arm_length - second_arm_length) / 3.0;
        }
        if (radius > 9.0 / 10.0 * (first_arm_length + second_arm_length)) {
            x *= 9.0 * (first_arm_length + second_arm_length) / 10.0 / radius;
            y *= 9.0 * (first_arm_length + second_arm_length) / 10.0 / radius;
            radius = 9.0 * (first_arm_length + second_arm_length) / 10.0;
        }
        // normalize all of them so they are within the radius

        double angle = Math.atan(y / x); // neither target angle 1 nor target angle 2
                                         // atan is between -90 and 90 so we're good :)
        
        // set target 1 and target 2

        double first_angle = Math.acos((radius * radius + first_arm_length * first_arm_length - second_arm_length * second_arm_length) / (2.0 * first_arm_length * radius)); // not the target angles, just something that's useful
        double second_angle = Math.acos((radius * radius + second_arm_length * second_arm_length - first_arm_length * first_arm_length) / (2.0 * second_arm_length * radius));
                // angle between first arm and radial vector and between second arm and radial vector

        return new double[] {
            angle + (angle < 0 ? 0 - first_angle : first_angle), // replace 0 with whatever our switching angle should be
            angle + (angle < 0 ? second_angle : 0 - second_angle)
            // absolute angles, not target angles
        };
    }
}

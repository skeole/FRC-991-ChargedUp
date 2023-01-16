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

    private final double first_pivot_initial;
    private final double second_pivot_initial;

    private double first_pivot_target;
    private double second_pivot_target;

    private double time;
    
    public DoubleArm() {
        first_pivot = new CANSparkMax(f_pivot, MotorType.kBrushless); // NEO Motors are brushless
        second_pivot = new CANSparkMax(s_pivot, MotorType.kBrushless);

        first_pivot_encoder = new Encoder(0, 1); // idk what channelA and B are
        second_pivot_encoder = new Encoder(0, 1);

        first_pivot_encoder.setReverseDirection(false);
        second_pivot_encoder.setReverseDirection(false);

        first_pivot_encoder.setDistancePerPulse(Math.PI / 4986.0); // 2048 * 4 ticks per revolution
        second_pivot_encoder.setDistancePerPulse(Math.PI / 4986.0);

        first_pivot_initial = first_pivot_encoder.getDistance() - first_arm_initial_angle * Math.PI / 180.0;
        // angle = distance() - first pivot initial + init angle
        second_pivot_initial = second_pivot_encoder.getDistance() - second_arm_initial_angle * Math.PI / 180.0;

        first_pivot_target = first_arm_initial_angle * Math.PI / 180.0;
        second_pivot_target = first_pivot_target + second_arm_initial_angle * Math.PI / 180.0;

        time = System.nanoTime() / 1000000000.0;
    }

    private double[] getArmAngles() {
        return new double[] {
            first_pivot_encoder.getDistance() - first_pivot_initial, 
            first_pivot_encoder.getDistance() - first_pivot_initial + second_pivot_encoder.getDistance() - second_pivot_initial
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

    private double torqueToVoltage(double torque) {
        // if we want to rotate a motor counterclockwise, torqueToVoltage(.getTorque() + 0.1)
        // if we want to rotate clockwise, .getTorque() - 0.1 instead
        // Torque is directly proportional to current
        // Power = Current * Voltage

        // I'm pretty sure that voltage is proportional to torque SQUARED

        // we can also test this empirically: 
            // set the motors up with PID to not move then do this
        // first_pivot.getBusVoltage();
        return 0; // very sad
    }

    // what we could do is add to SmartDashboard our torque and voltage at all times
    // put them down in a graph and do a regression

    private void goToPosition(double x, double y) {

    }

    private void tick() {
        double[] current_angles = getArmAngles();

        if (Math.abs(first_pivot_target - current_angles[0]) < 0.2) { // if we're within 11.5 degrees, do a normal PID
            first_pivot.setVoltage(Math.min(12, Math.max(-12, 
                40 * (first_pivot_target - current_angles[0]) // up to 8V
            )));
        } else { // if we're farther away, do a velocity PID
            first_pivot.setVoltage(Math.min(12, Math.max(-12, 
                first_pivot.getBusVoltage() + // whatever our current voltage is

                3 * (Math.min(1, Math.max(-1, 2 * (first_pivot_target - current_angles[0]))) * target_angular_speed * Math.PI / 180.0 - first_pivot_encoder.getRate()))
                // if we're going slower/faster than we want, then correct that
                // target angular speed is decreased if we are closer than half a radian
                // we will have to tune this
            ));
        }

        if (Math.abs(second_pivot_target - current_angles[1]) < 0.2) {
            second_pivot.setVoltage(Math.min(12, Math.max(-12, 
                40 * (second_pivot_target - current_angles[1])
            )));
        } else {
            second_pivot.setVoltage(Math.min(12, Math.max(-12, 
                second_pivot.getBusVoltage() + 
                3 * (Math.min(1, Math.max(-1, 2 * (second_pivot_target - current_angles[1]))) * target_angular_speed * Math.PI / 180.0 - second_pivot_encoder.getRate()))
            ));
        }

        if (show_data) {
            SmartDashboard.putNumberArray("Torque Data", new double[] {
                getFirstPivotTorque(),
                first_pivot.getBusVoltage(),
                getSecondPivotTorque(),
                second_pivot.getBusVoltage()
            }); // try to find a relationship between torque and voltage
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

        return new double[] {
            angle + (angle < 0 ? 0 - first_angle : first_angle), 
            angle + (angle < 0 ? second_angle : 0 - second_angle)
        };
    }
}

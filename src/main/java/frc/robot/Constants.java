// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import java.lang.Math;

public final class Constants {

    // general

    public static final boolean show_data = false; // should only be true if use_pigeon2
    public static final boolean use_ADIS16448 = false;
    public static final boolean use_pigeon2 = false; // only one of these should be true
    public static final int imu_port = 0; // only if using pigeon

    public static final double joystick_deadzone = 0.2; 
            // if the joysticks have a magnitude less than this then they register as not being activated

    // drivetrain

    public static final double ticks_per_revolution = 2048.0 * 150.0 / 7.0; // 2048 ticks per revolution with a 150 / 7 = 21.43 gear ratio for MK4i Swerve Module

    public static final double max_error = 25; // wheels have to point within this many degrees of the target before they start spinning

    public static final int rf_driving = 3, rf_direction = 2, lf_driving = 4, lf_direction = 5, 
                            rb_driving = 0, rb_direction = 1, lb_driving = 6, lb_direction = 7; // port numbers
    
    public static final boolean absolute_directing = false; // can only be true if use_ADIS16448 or use_pigeon2 is true
    public static final double directing_sensitivity = 4.0; // turning factor is this * angular error

    public static final double directing_motor_sensitivity = 4.0; // power of directing motors is this * angular error

    // double arm

    public static final double arm_sensitivity = 5; // inches per second for arm

    public static final double min_x = 10, min_y = -45; 
            // inches
    
    public static final double clipping_one = 1.1, clipping_two = 0.9; // first should be > 1, second should be < 1

    public static final double first_arm_length = 32, second_arm_length = 20; // inches

    public static final int first_motor_ID = 0, second_motor_ID = 0; // IDs

    public static final double first_encoder_zero = 1239, second_encoder_zero = -1247; // can be negative
                // encoder reading when both arms are straight ahead; can be negative

    public static final double switching_angle = -90; // always concave down

    public static final double first_motor_max_power = 1.0, second_motor_max_power = 1.0;

    public static final double first_motor_min_error = 1.0, second_motor_min_error = 1.0;

    public static final double first_motor_max_error = 15.0, second_motor_max_error = 15.0;

    public static final double k_exponent = 1.5; // 1.0 for a normal PID

    public static final int first_encoder_channel_A = 0, first_encoder_channel_B = 0, second_encoder_channel_A = 0, second_encoder_channel_B = 0;

    public static final boolean invert_first_motor = false, invert_second_motor = false, invert_first_encoder = false, invert_second_encoder = false;

    // static functions

    public static double normalizeAngle(double angle) {
        while (angle > Math.PI) { angle -= 2.0 * Math.PI; }
        while (angle < 0 - Math.PI) { angle += 2.0 * Math.PI; }

        if (Math.abs(angle) > Math.PI * 0.999999) angle = Math.PI * 0.999999; // if we're at -pi, reset to positive pi

        return angle;
    }

    public static double vectorToAngle(double[] vector) {
        if (vector[0] == 0) vector[0] = 0.001;
        if (vector[1] == 0) vector[1] = 0.001;
        
        return Math.atan(vector[0] / vector[1]) + Math.PI * (vector[1] > 0 ? 0 : 1) * (vector[0] > 0 ? 1 : -1);
    }

    public static double[] angleToVector(double angle) {
        return new double[] {Math.sin(angle), Math.cos(angle)};
    }

    public static void pause(double seconds) {
        double finalTime = System.nanoTime() + seconds * 1000000000L;
        while (System.nanoTime() < finalTime) {
            // idle
        }
    }

    // constants for example code

    // converted constants, if applicable

    public static final double ticks_per_radian = ticks_per_revolution / (2.0 * Math.PI);
    
    public static final int kDriveLeftId = 10;
    public static final int kDriveLeftFollowerId = 11;
    public static final int kDriveRightId = 12;
    public static final int kDriveRightFollowerId = 13;

    public static final int kPneumaticsControlModuleId = 20;
    public static final int kDriveLeftSolenoidId = 0;
    public static final int kDriveRightSolenoidId = 1;

    public static final int kLeftJoystickId = 0;
    public static final int kRightJoystickId = 1;
    public static final int kOperatorGamepadId = 2;
}
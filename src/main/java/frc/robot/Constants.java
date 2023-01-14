// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import java.lang.Math;

public final class Constants {

    // general

    public static final boolean show_data = true;
    public static final boolean use_pigeon2 = false;
        // if false -> use ADIS16448 instead

    // drivetrain

    public static final double ticks_per_radian = 2048.0 * 12.8 / (2.0 * Math.PI); // 2048 ticks per revolution with a 12.8 L1 gear ratio

    public static final int rf_driving = 0, rf_direction = 0, lf_driving = 0, lf_direction = 0, 
                            rb_driving = 0, rb_direction = 0, lb_driving = 0, lb_direction = 0; // port number
    
    public static final boolean absolute_directing = false;

    // double arm

    public static final double first_pivot_height = 44.875, first_arm_length = 32, second_arm_length = 20; // inches

    public static final int f_pivot = 0, s_pivot = 0; // port number

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
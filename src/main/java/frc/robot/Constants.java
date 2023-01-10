// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import java.lang.Math;

public final class Constants {
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

    public static final double ticks_per_radian = 2048.0 / (2.0 * Math.PI); //2048 ticks per revolution

    public static final int rf_driving = 0, rf_direction = 0, lf_driving = 0, lf_direction = 0, 
                            rb_driving = 0, rb_direction = 0, lb_driving = 0, lb_direction = 0; //ports
    
    public static final boolean absolute_directing = false;

    public static double normalizeAngle(double angle) {
        while (angle > Math.PI) { angle -= 2.0 * Math.PI; }
        while (angle < 0 - Math.PI) { angle += 2.0 * Math.PI; }

        if (Math.abs(angle) > Math.PI * 0.999999) angle = Math.PI * 0.999999; //if we're at -pi, reset to positive pi

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
            //idle
        }
    }
}

package frc.robot.subsystems;

import static frc.robot.Constants.*;

import edu.wpi.first.wpilibj.ADIS16448_IMU;
//import com.ctre.phoenix.sensors.Pigeon2;

public class Odometry {

    private final double starting_time = System.nanoTime() / 1000000000.0;
    private double[] previous_three_times = {0, 0, 0};

    private double[] previous_three_x_positions = {0, 0, 0}; // we need 4 points to do cubic simpson, but we can generate most recent
    private double[] previous_three_y_positions = {0, 0, 0}; // we need 4 points to do cubic simpson, but we can generate most recent

    private double[] previous_three_x_velocities = {0, 0, 0};
    private double[] previous_three_y_velocities = {0, 0, 0};

    private double[] previous_three_x_accelerations = {0, 0, 0};
    private double[] previous_three_y_accelerations = {0, 0, 0};

    private double angle;
    private final double starting_angle;
    private ADIS16448_IMU gyro;

    /* private Pigeon2 imu;
    private double[] quaternion = new double[4]; */

    public Odometry() {
        gyro = new ADIS16448_IMU();
            //don't calibrate or reset because we do that in SwerveDriveTrain, and we define this after our SwerveDriveTrain
        starting_angle = gyro.getAngle() * Math.PI / 180.0;

        /* imu = new Pigeon2(0);
        imu.get6dQuaternion(quaternion);
            //don't reset because we do that in SwerveDriveTrain, and we define this after our SwerveDriveTrain
        starting_yaw = imu.getYaw(); //.getRoll(), .getPitch() or .getYaw() */
        angle = 0;
    }

    public double[] getPosition() {
        return new double[] {
            previous_three_x_positions[2], 
            previous_three_y_positions[2], 
            angle
        };
        /* return new double[] {
            quaternion[0], quaternion[1], quaternion[2], quaternion[3], angle
        } */
    }

    public void update() {
        double[] XYAccel = getXYAccel(); // first is X accel, second is Y accel
        //angle also updates

        double new_time = System.nanoTime() / 1000000000.0 - starting_time;

        //first, solve for velocity
        
        double new_x_velocity = previous_three_x_velocities[0] + cubic_area(new double[][] {
            {previous_three_x_accelerations[0], previous_three_times[0]}, 
            {previous_three_x_accelerations[1], previous_three_times[1]}, 
            {previous_three_x_accelerations[2], previous_three_times[2]}, 
            {XYAccel[0], new_time}
        });

        double new_y_velocity = previous_three_y_velocities[0] + cubic_area(new double[][] {
            {previous_three_y_accelerations[0], previous_three_times[0]}, 
            {previous_three_y_accelerations[1], previous_three_times[1]}, 
            {previous_three_y_accelerations[2], previous_three_times[2]}, 
            {XYAccel[1], new_time}
        });

        //now, we can solve for position
        double new_x_position = previous_three_x_positions[0] + cubic_area(new double[][] {
            {previous_three_x_velocities[0], previous_three_times[0]}, 
            {previous_three_x_velocities[1], previous_three_times[1]}, 
            {previous_three_x_velocities[2], previous_three_times[2]}, 
            {new_x_velocity, new_time}
        });

        double new_y_position = previous_three_y_positions[0] + cubic_area(new double[][] {
            {previous_three_y_velocities[0], previous_three_times[0]}, 
            {previous_three_y_velocities[1], previous_three_times[1]}, 
            {previous_three_y_velocities[2], previous_three_times[2]}, 
            {new_y_velocity, new_time}
        });

        //now, we can update previous time, acceleration, velocity and position
        previous_three_times[0] = previous_three_times[1];
        previous_three_times[1] = previous_three_times[2];
        previous_three_times[2] = new_time;

        previous_three_x_accelerations[0] = previous_three_x_accelerations[1];
        previous_three_x_accelerations[1] = previous_three_x_accelerations[2];
        previous_three_x_accelerations[2] = XYAccel[0];

        previous_three_y_accelerations[0] = previous_three_y_accelerations[1];
        previous_three_y_accelerations[1] = previous_three_y_accelerations[2];
        previous_three_y_accelerations[2] = XYAccel[1];

        previous_three_x_velocities[0] = previous_three_x_velocities[1];
        previous_three_x_velocities[1] = previous_three_x_velocities[2];
        previous_three_x_velocities[2] = new_x_velocity;

        previous_three_y_velocities[0] = previous_three_y_velocities[1];
        previous_three_y_velocities[1] = previous_three_y_velocities[2];
        previous_three_y_velocities[2] = new_y_velocity;

        previous_three_x_positions[0] = previous_three_x_positions[1];
        previous_three_x_positions[1] = previous_three_x_positions[2];
        previous_three_x_positions[2] = new_x_position;

        previous_three_y_positions[0] = previous_three_y_positions[1];
        previous_three_y_positions[1] = previous_three_y_positions[2];
        previous_three_y_positions[2] = new_y_position;

        /* angle = angle();
        imu.get6dQuaternion(quaternion); */
    }

    private double angle() {
        return normalizeAngle(starting_angle - gyro.getAngle() * Math.PI / 180.0);
            // .getAngle(), .getGyroAngleX(), .getGyroAngleY(), .getGyroAngleZ();
        //return normalizeAngle(starting_yaw - imu.getYaw() * Math.PI / 180.0);
    }

    private double[] getXYAccel() { //I assume x and y acceleration are with respect to the accelerometer, not absolute
            //"X" : Forward
            //"Y" : Sideways (right is positive)
        angle = angle();
        return new double[] {
            gyro.getAccelX() * Math.sin(angle) + gyro.getAccelY() * Math.cos(angle), 
            gyro.getAccelX() * Math.cos(angle) - gyro.getAccelY() * Math.sin(angle)
        };
    }

    /* 
    function:
    (a,b,c,m,n,x,y)->(m-n)*(12*a*b*c-6*(a*b+b*c+c*a)*(m+n)+4*(a+b+c)*(m*m+m*n+n*n)-3*(m*m*m+m*m*n+m*n*n+n*n*n))*y/(12*(x-a)*(x-b)*(x-c));
    (a,b,c,m,n,x,y)->(12*a*b*c*(m-n)-6*(a*b+b*c+c*a)*(m*m-n*n)+4*(a+b+c)*(m*m*m-n*n*n)-3*(m*m*m*m-n*n*n*n))*y/(12*(x-a)*(x-b)*(x-c));

    expanded function:
    (a, b, c, m, n, x, y) -> (m - n) * (12 * a * b * c - 6 * (a * b + b * c + c * a) * (m + n) + 4 * (a + b + c) * (m * m + m * n + n * n) - 3 * (m * m * m + m * m * n + m * n * n + n * n * n)) * y / (12 * (x - a) * (x - b) * (x - c));
    (a, b, c, m, n, x, y) -> (12 * a * b * c * (m - n) - 6 * (a * b + b * c + c * a) * (m * m - n * n) + 4 * (a + b + c) * (m * m * m - n * n * n) - 3 * (m * m * m * m - n * n * n * n)) * y / (12 * (x - a) * (x - b) * (x - c));
     */
    
    private static double cubic_area(double a, double b, double c, double x, double y, double m, double n) {
        return (12*a*b*c*(m-n)-6*(a*b+b*c+c*a)*(m*m-n*n)+4*(a+b+c)*(m*m*m-n*n*n)-3*(m*m*m*m-n*n*n*n))*y/(12*(x-a)*(x-b)*(x-c));
        //return (m-n)*(12*a*b*c-6*(a*b+b*c+c*a)*(m+n)+4*(a+b+c)*(m*m+m*n+n*n)-3*(m*m*m+m*m*n+m*n*n+n*n*n))*y/(12*(x-a)*(x-b)*(x-c));
    }

    private static double cubic_area(double[][] points) {
        //points: {{x, y}, {x, y}, {x, y}, {x, y}}
        return cubic_area(points[1][0], points[2][0], points[3][0], points[0][0], points[0][1], points[0][0], points[3][0]) + cubic_area(points[0][0], points[2][0], points[3][0], points[1][0], points[1][1], points[0][0], points[3][0]) + cubic_area(points[0][0], points[1][0], points[3][0], points[2][0], points[2][1], points[0][0], points[3][0]) + cubic_area(points[0][0], points[1][0], points[2][0], points[3][0], points[3][1], points[0][0], points[3][0]);
    }

    /* Quadratic instead of cubic - surprisingly, I don't think its less accurate than cubic
    private static double quadratic_area(double a, double b, double x, double y, double m, double n) {
        return (6 * a * b * (n - m) - 3 * (a + b) * (n * n - m * m) + 2 * (n * n * n - m * m * m)) * y / (6 * (x - a) * (x - b));
    }

    private static double quadratic_area(double[][] points) {
        //points: {{x, y}, {x, y}, {x, y}}
        return  quadratic_area(points[1][0], points[2][0], points[0][0], points[0][1], points[0][0], points[2][0]) + 
                quadratic_area(points[0][0], points[2][0], points[1][0], points[1][1], points[0][0], points[2][0]) + 
                quadratic_area(points[0][0], points[1][0], points[2][0], points[2][1], points[0][0], points[2][0]);
    } */
}

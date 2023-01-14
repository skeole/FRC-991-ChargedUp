package frc.robot.subsystems;

import com.revrobotics.CANSparkMax;
// go to WPILib Command Palette -> Manage Vendor Libraries -> Install new libraries (online) -> URL is https://software-metadata.revrobotics.com/REVLib-2023.json
// run ./gradlew build in terminal to run gradle build
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import static frc.robot.Constants.*;

public class DoubleArm {
    
    private CANSparkMax first_pivot;
    private CANSparkMax second_pivot;
    
    public DoubleArm() {
        first_pivot = new CANSparkMax(f_pivot, MotorType.kBrushed);
        second_pivot = new CANSparkMax(f_pivot, MotorType.kBrushed);
    }

}

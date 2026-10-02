package org.firstinspires.ftc.teamcode.Helpers;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;


public class Turret {

    public Turret(HardwareMap hardwareMap) {

        DcMotor turretMotor = hardwareMap.get(DcMotor.class, "turretMotor");
        turretMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        turretMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        turretMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        // Initialize turret and shooter hardware here
    }


    public void autoAim(double[] robotPosition) { // X, Y, heading

        // Calculate and move turret here
    }


    public boolean shoot() {

        // Shooting sequence here

        // Return true when the shot is finished
        return true;
    }
}
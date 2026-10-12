package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Helpers.Odometry;

@TeleOp (name = "TestOdo")
public class TestOdo extends LinearOpMode {

    @Override
    public void runOpMode(){
        telemetry.addData("Status: ", "Init");
        telemetry.update();

        while (opModeIsActive()){
            telemetry.addData("X: ", Odometry.getX());
            telemetry.addData("Y: ", Odometry.getY());
            telemetry.addData("T: ", Odometry.getHeading());
            telemetry.update();
        }
    }
}

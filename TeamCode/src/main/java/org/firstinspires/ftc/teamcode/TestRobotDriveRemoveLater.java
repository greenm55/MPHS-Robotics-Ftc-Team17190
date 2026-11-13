package org.firstinspires.ftc.teamcode;


import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.Helpers.RobotDrive;

@Autonomous (name = "Test Flower Arm")
public class TestRobotDriveRemoveLater extends LinearOpMode {

    @Override
    public void runOpMode(){
        Servo flowerIntake = hardwareMap.get(Servo.class, ("flowerIntake"));
        DcMotor intake = hardwareMap.get(DcMotor.class, "intake");

        double low=.5;
        double high=1;
        double mode = 0;

        waitForStart();

        while (opModeIsActive()){
            double med = (low + high)/2;
            if (gamepad1.a){
                mode = 1;
            }else if (gamepad1.b){
                mode = 0;
            } else if (gamepad1.x) {
                mode = -1;
            }

            if (mode == 1){
                flowerIntake.setPosition(high);
                if (gamepad1.dpadUpWasPressed()){
                    high += .01;
                } else if (gamepad1.dpadDownWasPressed()) {
                    high -= .01;
                }
            } else if (mode == 0){
                flowerIntake.setPosition(low);
                if (gamepad1.dpadUpWasPressed()){
                    low += .01;
                } else if (gamepad1.dpadDownWasPressed()) {
                    low -= .01;
                }
            } else{
                flowerIntake.setPosition(med);
            }

            if (gamepad1.right_bumper){
                intake.setPower(1);
            } else if (gamepad1.left_bumper) {
                intake.setPower(-1);
            } else {
                intake.setPower(0);
            }

            telemetry.addData("high: ", high);
            telemetry.addData("low: ", low);
            telemetry.update();

        }

    }

}

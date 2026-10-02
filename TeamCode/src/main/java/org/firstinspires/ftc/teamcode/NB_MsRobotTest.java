package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

@TeleOp(name= "NB_MsRobotTest", group="LinearOpMode" )
public class NB_MsRobotTest extends LinearOpMode {


    @Override
    public void runOpMode() {

        //matching to names in configuration
        DcMotor FL = hardwareMap.get(DcMotor.class, "front.left");
        DcMotor FR = hardwareMap.get(DcMotor.class, "front.right");
        DcMotor BL = hardwareMap.get(DcMotor.class, "back.left");
        DcMotor BR = hardwareMap.get(DcMotor.class, "back.right");
        CRServo sr1 = hardwareMap.get(CRServo.class, "servo1");
        CRServo sr2 = hardwareMap.get(CRServo.class, "servo2");
        CRServo sr3 = hardwareMap.get(CRServo.class, "servo3");
        CRServo sr4 = hardwareMap.get(CRServo.class, "servo4");


        //Direction settings
        FL.setDirection(DcMotorSimple.Direction.FORWARD);
        FR.setDirection(DcMotorSimple.Direction.FORWARD);
        BR.setDirection(DcMotorSimple.Direction.FORWARD);
        BL.setDirection(DcMotorSimple.Direction.FORWARD);

        waitForStart();

        while (opModeIsActive()) {
            //motor controls
            if (gamepad1.left_stick_y != 0) {
                BL.setPower(-gamepad1.left_stick_y);
            }
            if (gamepad1.right_stick_y != 0) {
                BR.setPower(-gamepad1.right_stick_y);
            }
            if (gamepad1.left_trigger != 0) {
                FL.setPower(gamepad1.left_trigger);
            }
            if (gamepad1.right_trigger != 0) {
                FR.setPower(gamepad1.right_trigger);
            }
            //servo controls
            if (gamepad1.a) {
                sr1.setPower(1);
            }
            if (gamepad1.b) {
                sr2.setPower(1);
            }
            if (gamepad1.x) {
                sr3.setPower(1);
            }
            if (gamepad1.y) {
                sr4.setPower(1);
            }
        }
    }
}


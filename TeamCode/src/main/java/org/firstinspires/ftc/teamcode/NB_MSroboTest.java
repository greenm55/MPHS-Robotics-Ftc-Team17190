package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp(name= "NB_MSroboTest", group="LinearOpMode" )
public class NB_MSroboTest extends LinearOpMode {
    private DcMotor FL;
    private DcMotor FR;
    private DcMotor BL;
    private DcMotor BR;
    private CRServo sr1;
    private CRServo sr2;
    private CRServo sr3;
    private CRServo sr4;


    @Override
    public void runOpMode() {

        //matching to names in configuration
        FL = hardwareMap.get(DcMotor.class, "front.left");
        FR = hardwareMap.get(DcMotor.class, "front.right");
        BL = hardwareMap.get(DcMotor.class, "back.left");
        BR = hardwareMap.get(DcMotor.class, "back.right");
        sr1 = hardwareMap.get(CRServo.class, "servo1");
        sr2 = hardwareMap.get(CRServo.class, "servo2");
        sr3 = hardwareMap.get(CRServo.class, "servo3");
        sr4 = hardwareMap.get(CRServo.class, "servo4");


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


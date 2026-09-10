package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

@TeleOp(name = "Basic Mecanum TeleOp", group = "Linear OpMode")
public class BasicMecanum extends LinearOpMode {

    @Override
    public void runOpMode() {
        DcMotor frontLeftDrive = hardwareMap.get(DcMotor.class, "front_left_drive");
        DcMotor frontRightDrive = hardwareMap.get(DcMotor.class, "front_right_drive");
        DcMotor backLeftDrive = hardwareMap.get(DcMotor.class, "back_left_drive");
        DcMotor backRightDrive = hardwareMap.get(DcMotor.class, "back_right_drive");

        float[] wheelPower = {0,0,0,0}; // 0:FR, 1:FL, 2:BR, 3:BL
        float[] leftXY;
        float[] rightXY;
        float[] finalWheelPower;
        float max;

        frontLeftDrive.setDirection(DcMotor.Direction.REVERSE);
        frontRightDrive.setDirection(DcMotor.Direction.FORWARD);
        backLeftDrive.setDirection(DcMotor.Direction.REVERSE);
        backRightDrive.setDirection(DcMotor.Direction.FORWARD);

        telemetry.addData("Status", "Initialized");
        telemetry.update();
        waitForStart();

        while (opModeIsActive()) {
            // Note: Pushing up is -1
            leftXY = new float[]{-gamepad1.left_stick_y, gamepad1.left_stick_x};
            rightXY = new float[]{-gamepad1.right_stick_y, gamepad1.right_stick_x};

            wheelPower[0] = leftXY[0] - leftXY[1] - rightXY[1]; // Left y - Left x - Right x
            wheelPower[1] = leftXY[0] + leftXY[1] + rightXY[1]; // Left y + Left x + Right x
            wheelPower[2] = leftXY[0] + leftXY[1] - rightXY[1]; // Left y + Left x - Right x
            wheelPower[3] = leftXY[0] - leftXY[1] + rightXY[1]; // Left y - Left x + Right x

            max = Math.max(Math.max(Math.abs(wheelPower[0]), Math.abs(wheelPower[1])), Math.max(Math.abs(wheelPower[2]), Math.abs(wheelPower[3])));

            if (max != 0) {
                finalWheelPower = new float[]{(wheelPower[0] / max), (wheelPower[1] / max), (wheelPower[2] / max), (wheelPower[3] / max)};
            }
            else{
                finalWheelPower = new float[]{0,0,0,0};
            }

            frontRightDrive.setPower(finalWheelPower[0]);
            frontLeftDrive.setPower(finalWheelPower[1]);
            backRightDrive.setPower(finalWheelPower[2]);
            backLeftDrive.setPower(finalWheelPower[3]);

            telemetry.update();
        }
    }
}
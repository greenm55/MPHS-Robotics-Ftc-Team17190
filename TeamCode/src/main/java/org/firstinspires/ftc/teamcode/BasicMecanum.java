package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

@TeleOp(name = "Basic Mecanum TeleOp")
public class BasicMecanum extends LinearOpMode {

    @Override
    public void runOpMode() {
        DcMotor frontLeftDrive = hardwareMap.get(DcMotor.class, "front_left_drive");
        DcMotor frontRightDrive = hardwareMap.get(DcMotor.class, "front_right_drive");
        DcMotor backLeftDrive = hardwareMap.get(DcMotor.class, "back_left_drive");
        DcMotor backRightDrive = hardwareMap.get(DcMotor.class, "back_right_drive");

        double[] wheelPower = {0,0,0,0}; // 0:FR, 1:FL, 2:BR, 3:BL
        double[] leftXY;
        double[] rightXY;

        frontLeftDrive.setDirection(DcMotor.Direction.REVERSE);
        frontRightDrive.setDirection(DcMotor.Direction.FORWARD);
        backLeftDrive.setDirection(DcMotor.Direction.REVERSE);
        backRightDrive.setDirection(DcMotor.Direction.FORWARD);

        telemetry.addData("Status", "Initialized");
        telemetry.update();
        waitForStart();

        while (opModeIsActive()) {
            // Note: Pushing up is -1
            leftXY = new double[]{gamepad1.left_stick_y, gamepad1.left_stick_x};
            rightXY = new double[]{gamepad1.right_stick_y, gamepad1.right_stick_x};

            wheelPower[0] = leftXY[0] + leftXY[1] - rightXY[1]; // Left x + Left y - Right x
            wheelPower[1] = leftXY[0] - leftXY[1] + rightXY[1]; // Left x - Left y + Right x
            wheelPower[2] = leftXY[0] - leftXY[1] - rightXY[1]; // Left x - Left y - Right x
            wheelPower[3] = leftXY[0] + leftXY[1] + rightXY[1]; // Left x + Left y + Right x

            frontRightDrive.setPower(wheelPower[0]);
            frontLeftDrive.setPower(wheelPower[1]);
            backRightDrive.setPower(wheelPower[2]);
            backLeftDrive.setPower(wheelPower[3]);

            telemetry.addLine("Controller inputs");
            //noinspection SpellCheckingInspection
            telemetry.addData("LeftY:", leftXY[0]);
            telemetry.addData("LeftX:", leftXY[1]);
            //noinspection SpellCheckingInspection
            telemetry.addData("RightY:", rightXY[1]);
            telemetry.addData("RightX:", rightXY[0]);

            telemetry.addData("FR Power: ", wheelPower[0]);
            telemetry.addData("FL Power: ", wheelPower[1]);
            telemetry.addData("BR Power: ", wheelPower[2]);
            telemetry.addData("BL Power: ", wheelPower[3]);

            telemetry.update();
        }
    }
}
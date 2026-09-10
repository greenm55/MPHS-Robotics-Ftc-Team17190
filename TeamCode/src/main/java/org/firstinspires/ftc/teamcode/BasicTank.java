package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

@TeleOp(name = "Basic Tank TeleOp", group = "Linear OpMode")
public class BasicTank extends LinearOpMode {

    @Override
    public void runOpMode() {
        DcMotor frontLeftDrive = hardwareMap.get(DcMotor.class, "front_left_drive");
        DcMotor frontRightDrive = hardwareMap.get(DcMotor.class, "front_right_drive");
        DcMotor backLeftDrive = hardwareMap.get(DcMotor.class, "back_left_drive");
        DcMotor backRightDrive = hardwareMap.get(DcMotor.class, "back_right_drive");

        float[] wheelPower = {0,0,0,0}; // 0:FR, 1:FL, 2:BR, 3:BL
        float[] leftXY;
        float[] rightXY;
        float[] finalWheelPower = {0,0,0,0};
        float max;

        frontLeftDrive.setDirection(DcMotor.Direction.REVERSE);
        frontRightDrive.setDirection(DcMotor.Direction.FORWARD);
        backLeftDrive.setDirection(DcMotor.Direction.REVERSE);
        backRightDrive.setDirection(DcMotor.Direction.FORWARD);

        telemetry.addData("Status", "Initialized");
        telemetry.update();
        waitForStart();

        while (opModeIsActive()) {
            leftXY = new float[]{-gamepad1.left_stick_y /* Note: Pushing up is -1*/, gamepad1.left_stick_x};
            rightXY = new float[]{-gamepad1.right_stick_y /* Note: Pushing up is -1*/, gamepad1.right_stick_x};

            wheelPower[0/* Front right */] = rightXY[0] - rightXY[1];
            wheelPower[2/* Back right */] = rightXY[0] + rightXY[1];

            wheelPower[1/* Front left */] = leftXY[0] + leftXY[1];
            wheelPower[3/* Back left */] = leftXY[0] - leftXY[1];

            max = Math.max(Math.abs(wheelPower[0]), Math.abs(wheelPower[1]));
            max = Math.max(Math.abs(max),Math.abs(wheelPower[2]));
            max = Math.max(Math.abs(max),Math.abs(wheelPower[3]));

            if (max > 0) {
                finalWheelPower = new float[]{(wheelPower[0] / max), (wheelPower[1] / max), (wheelPower[2] / max), (wheelPower[3] / max)};
            }
            else{
                finalWheelPower = new float[]{0,0,0,0};
            }

            frontRightDrive.setPower(finalWheelPower[0]);
            frontLeftDrive.setPower(finalWheelPower[1]);
            backRightDrive.setPower(finalWheelPower[2]);
            backLeftDrive.setPower(finalWheelPower[3]);


        }
    }
}
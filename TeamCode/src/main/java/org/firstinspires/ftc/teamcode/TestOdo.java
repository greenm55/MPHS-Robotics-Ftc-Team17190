package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.Helpers.Odometry;


@TeleOp (name = "TestOdo")
public class TestOdo extends LinearOpMode {
    @Override
    public void runOpMode(){

        DcMotor frontLeftDrive = hardwareMap.get(DcMotor.class, "front_left_drive");
        DcMotor frontRightDrive = hardwareMap.get(DcMotor.class, "front_right_drive");
        DcMotor backRightDrive = hardwareMap.get(DcMotor.class, "back_right_drive");

        frontLeftDrive.setDirection(DcMotor.Direction.REVERSE);
        frontRightDrive.setDirection(DcMotor.Direction.FORWARD);
        backRightDrive.setDirection(DcMotor.Direction.FORWARD);

        frontLeftDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        frontRightDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        backRightDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        frontLeftDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        frontRightDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        backRightDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        double[] robotPosition;

        telemetry.addData("Status: ", "Init");
        telemetry.update();
        waitForStart();

        while (opModeIsActive()){

            robotPosition = Odometry.evalPos(
                    frontRightDrive.getCurrentPosition(),
                    frontLeftDrive.getCurrentPosition(),
                    backRightDrive.getCurrentPosition()
            );
            telemetry.addData("X: ", robotPosition[0]);
            telemetry.addData("Y: ", robotPosition[1]);
            telemetry.addData("T: ", robotPosition[2]);

            telemetry.addData("Fr: ", frontRightDrive.getCurrentPosition());
            telemetry.addData("Fl: ", frontLeftDrive.getCurrentPosition());
            telemetry.addData("Br: ", backRightDrive.getCurrentPosition());
            telemetry.update();
        }
    }
}

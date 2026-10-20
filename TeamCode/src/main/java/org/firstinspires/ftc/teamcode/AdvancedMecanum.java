package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.Helpers.Odometry;
import org.firstinspires.ftc.teamcode.Helpers.Turret;

@TeleOp(name = "Basic Mecanum TeleOp")
public class AdvancedMecanum extends LinearOpMode {

    @Override
    public void runOpMode() {
        DcMotor frontLeftDrive = hardwareMap.get(DcMotor.class, "front_left_drive");
        DcMotor frontRightDrive = hardwareMap.get(DcMotor.class, "front_right_drive");
        DcMotor backLeftDrive = hardwareMap.get(DcMotor.class, "back_left_drive");
        DcMotor backRightDrive = hardwareMap.get(DcMotor.class, "back_right_drive");

        Odometry odometry = new Odometry(hardwareMap);

        Turret turret = new Turret(hardwareMap);

        float[] wheelPower = {0,0,0,0}; // 0:FR, 1:FL, 2:BR, 3:BL
        float[] leftXY;
        float[] rightXY;

        double heading;
        double[] position;

        int fr = frontRightDrive.getCurrentPosition();   // right pod
        int fl = frontLeftDrive.getCurrentPosition();    // left pod
        int br = backRightDrive.getCurrentPosition();    // perpendicular pod

        double distPerTick = (Math.PI * Odometry.wheelDiameter) / Odometry.tickPerRev;

        frontLeftDrive.setDirection(DcMotor.Direction.REVERSE);
        frontRightDrive.setDirection(DcMotor.Direction.FORWARD);
        backLeftDrive.setDirection(DcMotor.Direction.REVERSE);
        backRightDrive.setDirection(DcMotor.Direction.FORWARD);

        frontLeftDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        frontRightDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        backRightDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        frontLeftDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        frontRightDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        backRightDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        sleep(50); // let the encoder reset settle before Odometry takes its baseline

        odometry.reset();

        telemetry.addData("Status", "Initialized");
        telemetry.update();
        waitForStart();

        while (opModeIsActive()) {
            odometry.evalPos();
            heading = Odometry.getHeading();
            // Note: Pushing up is -1
            leftXY = new float[]{-gamepad1.left_stick_y, gamepad1.left_stick_x};
            rightXY = new float[]{-gamepad1.right_stick_y, gamepad1.right_stick_x};

            float fieldX = (float) (leftXY[1] * Math.cos(heading) - leftXY[0] * Math.sin(heading));
            float fieldY = (float) (leftXY[1] * Math.sin(heading) + leftXY[0] * Math.cos(heading));

            wheelPower[0] = fieldY - fieldX - rightXY[1]; // Left y - Left x - Right x
            wheelPower[1] = fieldY + fieldX + rightXY[1]; // Left y + Left x + Right x
            wheelPower[2] = fieldY + fieldX - rightXY[1]; // Left y + Left x - Right x
            wheelPower[3] = fieldY - fieldX + rightXY[1]; // Left y - Left x + Right x

            frontRightDrive.setPower(wheelPower[0]);
            frontLeftDrive.setPower(wheelPower[1]);
            backRightDrive.setPower(wheelPower[2]);
            backLeftDrive.setPower(wheelPower[3]);

            position = Odometry.getRobotPosition();

            turret.autoAim(position);

            if(gamepad1.right_trigger_pressed){
                while(!turret.shoot()){
                    odometry.evalPos();
                    position = Odometry.getRobotPosition();
                    turret.autoAim(position);
                }
            }

            double rightMm = fr * distPerTick;
            double leftMm = -fl * distPerTick;
            double perpMm = br * distPerTick;
            double checkHeading = Odometry.getHeading();

            telemetry.addLine("--- Odometry output ---");
            telemetry.addData("X (mm)", "%.1f", position[0]);
            telemetry.addData("Y (mm)", "%.1f", position[1]);
            telemetry.addData("Heading (deg)", "%.2f", Math.toDegrees(position[2]));

            telemetry.addLine("--- Raw ticks ---");
            telemetry.addData("Fr (right pod)", frontRightDrive.getCurrentPosition());
            telemetry.addData("Fl (left pod)", frontLeftDrive.getCurrentPosition());
            telemetry.addData("Br (perp pod)", backRightDrive.getCurrentPosition());

            telemetry.addLine("--- Distance since zero (mm) ---");
            telemetry.addData("Right", "%.1f", rightMm);
            telemetry.addData("Left", "%.1f", leftMm);
            telemetry.addData("Perp", "%.1f", perpMm);
            telemetry.addData("R - L (positive = turning CCW)", "%.1f", rightMm - leftMm);
            telemetry.addData("(R + L) / 2 (forward)", "%.1f", (rightMm + leftMm) / 2.0);

            telemetry.addLine("--- Sanity check ---");
            telemetry.addData("Heading from raw ticks (deg)", "%.2f", Math.toDegrees(checkHeading));
            telemetry.addData("Mismatch (deg)", "%.2f", Math.toDegrees(position[2] - checkHeading));

            telemetry.addLine("--- Controller input ---");
            telemetry.addData("Left stick Y", leftXY[0]);
            telemetry.addData("Left stick X", leftXY[1]);
            telemetry.addData("Right stick Y", rightXY[0]);
            telemetry.addData("Right stick Y", rightXY[1]);

            telemetry.update();
        }
    }
}
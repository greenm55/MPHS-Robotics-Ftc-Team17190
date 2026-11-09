package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.Helpers.Intake;
import org.firstinspires.ftc.teamcode.Helpers.Odometry;

@TeleOp(name = "Advanced Mecanum TeleOp")
public class AdvancedMecanum extends LinearOpMode {

    @Override
    public void runOpMode() {
        DcMotor frontLeftDrive = hardwareMap.get(DcMotor.class, "front_left_drive");
        DcMotor frontRightDrive = hardwareMap.get(DcMotor.class, "front_right_drive");
        DcMotor backLeftDrive = hardwareMap.get(DcMotor.class, "back_left_drive");
        DcMotor backRightDrive = hardwareMap.get(DcMotor.class, "back_right_drive");

        Odometry odometry = new Odometry(hardwareMap);
        Intake in = new Intake(hardwareMap);
       // Turret turret = new Turret(this.telemetry, hardwareMap);

        double[] wheelPower = {0,0,0,0}; // 0:FR, 1:FL, 2:BR, 3:BL
        double[] leftXY;
        double[] rightXY;

        double heading;
        double[] position;

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
            heading = odometry.getHeading();

            // Note: Pushing up is -1
            leftXY = new double[]{-gamepad1.left_stick_y, -gamepad1.left_stick_x};
            rightXY = new double[]{-gamepad1.right_stick_y, gamepad1.right_stick_x};

            double Xp = leftXY[0] * Math.cos(heading) + leftXY[1] * Math.sin(heading);
            double Yp = leftXY[1] * Math.cos(heading) - leftXY[0] * Math.sin(heading);

            wheelPower[0] = Yp - Xp - rightXY[1];
            wheelPower[1] = Yp + Xp + rightXY[1];
            wheelPower[2] = Yp + Xp - rightXY[1];
            wheelPower[3] = Yp - Xp + rightXY[1];
            // 0:FR, 1:FL, 2:BR, 3:BL

            double intakePower = gamepad1.right_trigger-gamepad1.left_trigger;

            in.runIntake(intakePower);

            frontRightDrive.setPower(wheelPower[0]);
            frontLeftDrive.setPower(wheelPower[1]);
            backRightDrive.setPower(wheelPower[2]);
            backLeftDrive.setPower(wheelPower[3]);

            position = odometry.getRobotPosition();

            // turret.autoAim();

            /*if(gamepad1.right_trigger_pressed){
                while(!turret.shoot()){
                    odometry.evalPos();
                    position = Odometry.getRobotPosition();
                    turret.autoAim();
                }
            }*/
            double checkHeading = odometry.getHeading();

            telemetry.addLine("--- Odometry output ---");
            telemetry.addData("X (mm)", "%.1f", position[0]);
            telemetry.addData("Y (mm)", "%.1f", position[1]);
            telemetry.addData("Heading (deg)", "%.2f", Math.toDegrees(position[2]));

            telemetry.addLine("--- Raw ticks ---");
            telemetry.addData("Fr (right pod)", frontRightDrive.getCurrentPosition());
            telemetry.addData("Fl (left pod)", frontLeftDrive.getCurrentPosition());
            telemetry.addData("Br (perp pod)", backRightDrive.getCurrentPosition());

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
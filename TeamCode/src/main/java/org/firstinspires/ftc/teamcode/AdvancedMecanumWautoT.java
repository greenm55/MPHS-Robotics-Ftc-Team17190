package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.Helpers.Flywheel_PID;
import org.firstinspires.ftc.teamcode.Helpers.Intake;
import org.firstinspires.ftc.teamcode.Helpers.Odometry;
import org.firstinspires.ftc.teamcode.Helpers.TurretController;

@TeleOp(name = "Advanced Mecanum TeleOp")
public class AdvancedMecanumWautoT extends LinearOpMode {

    @Override
    public void runOpMode() {
        DcMotor frontLeftDrive = hardwareMap.get(DcMotor.class, "front_left_drive");
        DcMotor frontRightDrive = hardwareMap.get(DcMotor.class, "front_right_drive");
        DcMotor backLeftDrive = hardwareMap.get(DcMotor.class, "back_left_drive");
        DcMotor backRightDrive = hardwareMap.get(DcMotor.class, "back_right_drive");
        DcMotor flywheel = hardwareMap.get(DcMotor.class, "flywheel");
        Servo flowerIntake = hardwareMap.get(Servo.class, "flowerIntake");

        Odometry odometry = new Odometry(hardwareMap, new double[]{0, 1271, 0});
        Intake in = new Intake(hardwareMap);
        Flywheel_PID fly = new Flywheel_PID(flywheel);
        TurretController turret = new TurretController(hardwareMap);
        // Turret turret = new Turret(this.telemetry, hardwareMap);

        double[] wheelPower = {0,0,0,0}; // 0:FR, 1:FL, 2:BR, 3:BL
        double[] left1XY;
        double[] right1XY;
        double[] left2XY;
        double[] right2XY;

        double heading;
        double[] position;
        int targetRPM = 3500;

        boolean upPrev = false;
        boolean downPrev = false;

        double turretTarget = 0;

        double[] target1Pos = {1500,1300};
        double[] target2Pos = {1500,2300};

        double high = 1;
        double low = .48;

        int target = 0;

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
            position = odometry.evalPos();
            heading = odometry.getHeading();

            if (position[1] > 2200){
                target = 2;
            } else {
                target = 1;
            }


            // Note: Pushing up is -1
            left1XY = new double[]{-gamepad1.left_stick_y, -gamepad1.left_stick_x};
            right1XY = new double[]{-gamepad1.right_stick_y, gamepad1.right_stick_x};

            left2XY = new double[]{-gamepad2.left_stick_x, -gamepad2.left_stick_y};
            right2XY = new double[]{gamepad2.right_stick_x, -gamepad2.right_stick_y};


            double Xp = left1XY[0] * Math.cos(heading) + left1XY[1] * Math.sin(heading);
            double Yp = left1XY[1] * Math.cos(heading) - left1XY[0] * Math.sin(heading);

            wheelPower[0] = Yp - Xp - right1XY[1];
            wheelPower[1] = Yp + Xp + right1XY[1];
            wheelPower[2] = Yp + Xp - right1XY[1];
            wheelPower[3] = Yp - Xp + right1XY[1];
            // 0:FR, 1:FL, 2:BR, 3:BL


            in.loadBall(gamepad2.right_trigger-gamepad2.left_trigger);
            in.runIntake(-left2XY[1]);

            turretTarget += right2XY[0] * 0.1;

            if (target == 1) {
                turret.trackPoint(target1Pos[0], target1Pos[1], position[0], position[1]);
                turret.update(heading);
            } else{
                turret.trackPoint(target2Pos[0], target2Pos[1], position[0], position[1]);
                turret.update(heading);
            }

            if (gamepad2.dpad_up && !upPrev) targetRPM += 50;
            if (gamepad2.dpad_down && !downPrev) targetRPM -= 50;
            upPrev = gamepad2.dpad_up;
            downPrev = gamepad2.dpad_down;

            if (gamepad2.b) {
                flywheel.setPower(fly.update(targetRPM));
            } else {
                fly.runRaw(0);
            }

            if(gamepad2.yWasPressed()){
                flowerIntake.setPosition(high);
            } else if (gamepad2.aWasPressed()){
                flowerIntake.setPosition(low);
            }

            frontRightDrive.setPower(wheelPower[0]);
            frontLeftDrive.setPower(wheelPower[1]);
            backRightDrive.setPower(wheelPower[2]);
            backLeftDrive.setPower(wheelPower[3]);

            position = odometry.getRobotPosition();

            telemetry.addLine("--- Turret Check ---");
            telemetry.addData("RPM", fly.getRpm());
            telemetry.addData("Target RPM", targetRPM);
            telemetry.addData("Error", targetRPM - fly.getRpm());
            telemetry.addData("target:", turretTarget);

            telemetry.update();
        }
    }
}
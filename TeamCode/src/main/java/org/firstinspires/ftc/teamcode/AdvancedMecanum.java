package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.Helpers.Flywheel_PID;
import org.firstinspires.ftc.teamcode.Helpers.Intake;
import org.firstinspires.ftc.teamcode.Helpers.Odometry;
import org.firstinspires.ftc.teamcode.Helpers.TurretController;

@TeleOp(name = "Advanced Mecanum TeleOp")
public class AdvancedMecanum extends LinearOpMode {

    @Override
    public void runOpMode() {
        DcMotor frontLeftDrive = hardwareMap.get(DcMotor.class, "front_left_drive");
        DcMotor frontRightDrive = hardwareMap.get(DcMotor.class, "front_right_drive");
        DcMotor backLeftDrive = hardwareMap.get(DcMotor.class, "back_left_drive");
        DcMotor backRightDrive = hardwareMap.get(DcMotor.class, "back_right_drive");
        DcMotor flywheel = hardwareMap.get(DcMotor.class, "flywheel");

        Odometry odometry = new Odometry(hardwareMap);
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
        int targetRPM = 1000;

        boolean upPrev = false;
        boolean downPrev = false;

        double turretTarget = 0;

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

            turret.setFieldAngle(turretTarget);
            turret.update(heading);

            if (gamepad2.dpad_up && !upPrev) targetRPM += 50;
            if (gamepad2.dpad_down && !downPrev) targetRPM -= 50;
            upPrev = gamepad2.dpad_up;
            downPrev = gamepad2.dpad_down;

            if (gamepad2.b) {
                flywheel.setPower(fly.update(targetRPM));
            } else {
                fly.runRaw(0);
            }

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

            telemetry.addLine("--- Turret Check ---");
            telemetry.addData("RPM", fly.getRpm());
            telemetry.addData("Target RPM", targetRPM);
            telemetry.addData("Error", targetRPM - fly.getRpm());
            telemetry.addData("target:", turretTarget);

            telemetry.update();
        }
    }
}
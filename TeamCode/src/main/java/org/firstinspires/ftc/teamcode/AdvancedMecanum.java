package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.Helpers.Flywheel_PID;
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
        DcMotor flywheel = hardwareMap.get(DcMotor.class, "flywheel");

        Odometry odometry = new Odometry(hardwareMap);
        Intake in = new Intake(hardwareMap);
        Flywheel_PID fly = new Flywheel_PID(flywheel);
       // Turret turret = new Turret(this.telemetry, hardwareMap);

        double[] wheelPower = {0,0,0,0}; // 0:FR, 1:FL, 2:BR, 3:BL
        double[] leftXY;
        double[] rightXY;

        double heading;
        double[] position;
        int targetRPM = 1000;

        boolean upPrev = false;
        boolean downPrev = false;

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


            in.loadBall(gamepad2.right_trigger-gamepad2.left_trigger);
            in.runIntake(gamepad2.right_stick_y);

            if (gamepad2.dpad_up && !upPrev) targetRPM += 250;
            if (gamepad2.dpad_down && !downPrev) targetRPM -= 250;
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

            telemetry.update();
        }
    }
}
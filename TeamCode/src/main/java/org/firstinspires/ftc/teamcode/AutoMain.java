package org.firstinspires.ftc.teamcode;


import android.util.Log;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Helpers.Flywheel_PID;
import org.firstinspires.ftc.teamcode.Helpers.Intake;
import org.firstinspires.ftc.teamcode.Helpers.RobotDrive;
import org.firstinspires.ftc.teamcode.Helpers.Turret;
import org.firstinspires.ftc.teamcode.Helpers.TurretController;
import org.firstinspires.ftc.teamcode.Helpers.Turret_PID;

@TeleOp(name = "Blue Auto")
public class AutoMain extends OpMode {

    private final ElapsedTime runtime = new ElapsedTime();
    double[][][] targetList = {
            { // rout #1
                    {205,1425,0,.8,0},
                    {1257,74,0,.9,0},
                    {95,963,(Math.PI/2),.9,0},
                    {321,2679,Math.PI,.8,0},
                    {961,3025,Math.PI,.8,0},
                    {915,3300,Math.PI,.9,0},
                    {979,3107,Math.PI,.8,0},
                    {979,3107,0,.8,0},
                    {651,3085,0,.9,0}, //
                    {979,3107,0,.8,0},
                    {979,3107,Math.PI,.8,0},
                    {915,3300,Math.PI,.9,0},
                    {979,3107,Math.PI,.8,0},
                    {-179,2640,0,.9,0}
            },
            { //rout #2
                    {0,0,0,0,0},
                    {0,0,0,0,0}
            }
    };
    double[][] startPos = {
            {0,1271,0}
    };
    /*
    Basic 1&2 flower: 0,1
    Solo 1&2 flower: 2,3
    Duo 1&2 flower start: 4,5
    Duo 1&2 shoot wait: 6,7
    Don't move: 8
     */
    double[] motorPower;
    int routNumber = 0;
    int initStage = 0;
    long waitTime = 0;
    DcMotor frontLeftDrive;
    DcMotor frontRightDrive;
    DcMotor backLeftDrive;
    DcMotor backRightDrive;
    DcMotor turretM;
    DcMotor flywheel;
    RobotDrive roboD;
    Turret_PID turretPid;
    Intake in;
    TurretController turret;
    Flywheel_PID fly;
    Servo flowerIntake;
    boolean waitForStart = false;
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

    @Override
    public void init() {

        frontLeftDrive = hardwareMap.get(DcMotor.class, "front_left_drive");
        frontRightDrive = hardwareMap.get(DcMotor.class, "front_right_drive");
        backLeftDrive = hardwareMap.get(DcMotor.class, "back_left_drive");
        backRightDrive = hardwareMap.get(DcMotor.class, "back_right_drive");
        turretM = hardwareMap.get(DcMotor.class, "turret");
        flywheel = hardwareMap.get(DcMotor.class, "flywheel");
        flowerIntake = hardwareMap.get(Servo.class, "flowerIntake");


        frontLeftDrive.setDirection(DcMotor.Direction.REVERSE);
        frontRightDrive.setDirection(DcMotor.Direction.FORWARD);
        backLeftDrive.setDirection(DcMotor.Direction.REVERSE);
        backRightDrive.setDirection(DcMotor.Direction.FORWARD);

        turretM.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        roboD= new RobotDrive(hardwareMap, targetList[0], startPos[0]);
        turretPid = new Turret_PID();
        turret = new TurretController(hardwareMap);
        in = new Intake(hardwareMap);
        fly = new Flywheel_PID(flywheel);

    }
    @Override
    public void init_loop(){
        turretPid.updateTurret(turretM.getCurrentPosition(), 10);
    }

    @Override
    public void loop(){
        while (runtime.seconds() > 28) {

            motorPower = roboD.moveRobot();
            frontRightDrive.setPower(motorPower[0]);
            frontLeftDrive.setPower(motorPower[1]);
            backRightDrive.setPower(motorPower[2]);
            backLeftDrive.setPower(motorPower[3]);

        }
        while (!waitForStart){
            if (!gamepad1.atRest() || !gamepad1.atRest()){
                waitForStart = false;
            }
        }

        while(runtime.seconds() > 30){

            position = roboD.getRobotPosition();
            heading = position[2];

            if (position[1] > 2200){
                turretTarget = 2;
            } else {
                turretTarget = 1;
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

            if (turretTarget == 1) {
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

            position = roboD.getRobotPosition();

            telemetry.addLine("--- Turret Check ---");
            telemetry.addData("RPM", fly.getRpm());
            telemetry.addData("Target RPM", targetRPM);
            telemetry.addData("Error", targetRPM - fly.getRpm());
            telemetry.addData("target:", turretTarget);

            telemetry.update();


        }
    }
    @Override
    public void stop(){
        frontRightDrive.setPower(0);
        frontLeftDrive.setPower(0);
        backRightDrive.setPower(0);
        backLeftDrive.setPower(0);
        System.exit(1);
    }
}


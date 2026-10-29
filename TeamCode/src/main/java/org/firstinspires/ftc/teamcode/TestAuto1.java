package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.Helpers.RobotDrive;

@Autonomous(name = "Test Auto 1")
public class TestAuto1 extends LinearOpMode{

    final double[][] targetList = { // targetPoint[target number][X,Y,T, tolerance (0-1), [0-3](nothing, intake while moving, shoot, intake flower)]
            {0,1000,0,.8,0},
            {0,1000,Math.PI,.9,0},
            {-700,500,Math.PI/2,.5,0},
            {0,0,0,.95,0}
    }; // 5 per point req
    double[] motorPower;

        @Override
        public void runOpMode(){
            DcMotor frontLeftDrive = hardwareMap.get(DcMotor.class, "front_left_drive");
            DcMotor frontRightDrive = hardwareMap.get(DcMotor.class, "front_right_drive");
            DcMotor backLeftDrive = hardwareMap.get(DcMotor.class, "back_left_drive");
            DcMotor backRightDrive = hardwareMap.get(DcMotor.class, "back_right_drive");

            frontLeftDrive.setDirection(DcMotor.Direction.REVERSE);
            frontRightDrive.setDirection(DcMotor.Direction.FORWARD);
            backLeftDrive.setDirection(DcMotor.Direction.REVERSE);
            backRightDrive.setDirection(DcMotor.Direction.FORWARD);

            RobotDrive roboD = new RobotDrive(hardwareMap, targetList);

            waitForStart();
            while (opModeIsActive()){

                    motorPower = roboD.moveRobot();
                    frontRightDrive.setPower(motorPower[0]);
                    frontLeftDrive.setPower(motorPower[1]);
                    backRightDrive.setPower(motorPower[2]);
                    backLeftDrive.setPower(motorPower[3]);

            }

    }
}

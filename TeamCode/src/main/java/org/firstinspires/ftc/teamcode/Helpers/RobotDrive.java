package org.firstinspires.ftc.teamcode.Helpers;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;


public class RobotDrive {

    private final DcMotor frontLeftDrive;
    private final DcMotor frontRightDrive;
    private final DcMotor backLeftDrive;
    private final DcMotor backRightDrive;


    private double[] robotPosition = {0, 0, 0};


    public RobotDrive(HardwareMap hardwareMap) {

        frontLeftDrive = hardwareMap.get(DcMotor.class, "front_left_drive");
        frontRightDrive = hardwareMap.get(DcMotor.class, "front_right_drive");
        backLeftDrive = hardwareMap.get(DcMotor.class, "back_left_drive");
        backRightDrive = hardwareMap.get(DcMotor.class, "back_right_drive");

        frontLeftDrive.setDirection(DcMotor.Direction.REVERSE);
        frontRightDrive.setDirection(DcMotor.Direction.FORWARD);
        backLeftDrive.setDirection(DcMotor.Direction.REVERSE);
        backRightDrive.setDirection(DcMotor.Direction.FORWARD);

        frontLeftDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        frontRightDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        backLeftDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        backRightDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        frontLeftDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        frontRightDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        backLeftDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        backRightDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }


    public void update() {

        robotPosition = Odometry.evalPos(
                frontRightDrive.getCurrentPosition(),
                frontLeftDrive.getCurrentPosition(),
                backRightDrive.getCurrentPosition()
        );
    }

    public double[] getRobotPosition() {
        update();
        return robotPosition;
    }

    public void goToPoint(double[] targetPoint) {
        double[] wheelPower = targeter(targetPoint);

        double maxPower = Math.max(
                Math.max(
                        Math.abs(wheelPower[0]),
                        Math.abs(wheelPower[1])
                ),
                Math.max(
                        Math.abs(wheelPower[2]),
                        Math.abs(wheelPower[3])
                )
        );

        if (maxPower != 0) {
            frontRightDrive.setPower(wheelPower[0] / maxPower);
            frontLeftDrive.setPower(wheelPower[1] / maxPower);
            backRightDrive.setPower(wheelPower[2] / maxPower);
            backLeftDrive.setPower(wheelPower[3] / maxPower);

        } else {
            stop();
        }
    }

    public boolean atPoint(double[] targetPoint) {

        update();

        double errorX = targetPoint[0] - robotPosition[0];
        double errorY = targetPoint[1] - robotPosition[1];
        double errorT = angleWrap(targetPoint[2] - robotPosition[2]);

        double XYError = Math.sqrt(errorX * errorX + errorY * errorY);

        return XYError <= targetPoint[3] && Math.abs(errorT) <= targetPoint[4];
    }
    private double[] targeter(double[] targetPoint) {

        double errorX = targetPoint[0] - robotPosition[0];
        double errorY = targetPoint[1] - robotPosition[1];
        double errorT = angleWrap(targetPoint[2] - robotPosition[2]);

        double robotErrorX = errorX * Math.cos(robotPosition[2]) + errorY * Math.sin(robotPosition[2]);
        double robotErrorY = -errorX * Math.sin(robotPosition[2]) + errorY * Math.cos(robotPosition[2]);

        double XYError = Math.sqrt(robotErrorX * robotErrorX + robotErrorY * robotErrorY);

        double targetRobotX = targetPoint[0] * Math.cos(targetPoint[2]) + targetPoint[1] * Math.sin(targetPoint[2]);
        double targetRobotY = targetPoint[0] * Math.sin(targetPoint[2]) + targetPoint[1] * Math.cos(targetPoint[2]);

        double XYDist = Math.sqrt(targetRobotX * targetRobotX + targetRobotY * targetRobotY);

        double percentRemaining;

        if (XYDist == 0) {
            percentRemaining = 0;
        } else {
            percentRemaining = XYError / XYDist;
        }

        double powerScale;

        if (percentRemaining <= 0.25) {
            powerScale = 3 * percentRemaining + 0.25;
        } else if (percentRemaining <= 0.75) {
            powerScale = 1;

        } else {
            powerScale = -3 * percentRemaining + 3.25;
        }

        double frontRightPower = (robotErrorY - robotErrorX - errorT) * powerScale;
        double frontLeftPower = (robotErrorY + robotErrorX + errorT) * powerScale;
        double backRightPower = (robotErrorY + robotErrorX - errorT) * powerScale;
        double backLeftPower = (robotErrorY - robotErrorX + errorT) * powerScale;

        return new double[]{frontRightPower, frontLeftPower, backRightPower, backLeftPower};
    }

    public void stop() {
        frontRightDrive.setPower(0);
        frontLeftDrive.setPower(0);
        backRightDrive.setPower(0);
        backLeftDrive.setPower(0);
    }

    private double angleWrap(double angle) {

        while (angle > Math.PI) {
            angle -= 2 * Math.PI;
        }

        while (angle < -Math.PI) {
            angle += 2 * Math.PI;
        }

        return angle;
    }
}
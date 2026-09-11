package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;


@Autonomous(name = "Basic Mecanum TeleOp")
public class BasicMecanumWOdom extends LinearOpMode {


    //=======================
    //Target Points
    public static final double[][] targetPoints = {
            {100, 100, Math.PI, 50, 5}, // X, Y, Heading, XY Tolerance, Heading Tolerance
            {-100, 100, Math.PI / 2, 10, 1},
            {0, 0, 0, 1, 1} // Min tolerance 1
    };
    //Target Points
    //=======================

    public static double prevLeft = 0;
    public static double prevRight = 0;
    public static double prevPerpendicular = 0;

    public static double[] robotPosition = {0, 0, 0}; // (X,Y,T)

    @Override
    public void runOpMode() {
        DcMotor frontLeftDrive = hardwareMap.get(DcMotor.class, "front_left_drive");
        DcMotor frontRightDrive = hardwareMap.get(DcMotor.class, "front_right_drive");
        DcMotor backLeftDrive = hardwareMap.get(DcMotor.class, "back_left_drive");
        DcMotor backRightDrive = hardwareMap.get(DcMotor.class, "back_right_drive");

        frontLeftDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        frontRightDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        backLeftDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        backRightDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        frontLeftDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        frontRightDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        backLeftDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        backRightDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        for (double[] targetPoint : targetPoints) {
            if (targetPoint[3] <= 0) {
                telemetry.addData("Fatal Tolerance Error: Negative or Zero Tolerance", "Tolerance at point <=0 ");
                telemetry.addData("Tolerance:", targetPoint[3]);
                telemetry.update();
                requestOpModeStop();
                return;
            } else if (targetPoint[4] <= 0) {
                telemetry.addData("Fatal Tolerance Error: Negative or Zero Tolerance", "Tolerance at Heading <=0 ");
                telemetry.addData("Tolerance:", targetPoint[4]);
                telemetry.update();
                requestOpModeStop();
                return;
            }

        }


        frontLeftDrive.setDirection(DcMotor.Direction.REVERSE);
        frontRightDrive.setDirection(DcMotor.Direction.FORWARD);
        backLeftDrive.setDirection(DcMotor.Direction.REVERSE);
        backRightDrive.setDirection(DcMotor.Direction.FORWARD);

        telemetry.addData("Status", "Initialized");
        telemetry.update();
        waitForStart();
        int targetNum = 0;

        while (opModeIsActive()) {

            double[] finalWheelPower; // 0:FR, 1:FL, 2:BR, 3:BL
            double maxPower;

           robotPosition = Odometry.evalPos(frontRightDrive.getCurrentPosition(), frontLeftDrive.getCurrentPosition(), backRightDrive.getCurrentPosition());

            /*
            ====================================================

            Don't use the mecanum wheels plug dead wheals into encoder ports for these wheels

            ** BACK WHEEL IS THE PERPENDICULAR ENCODER **

            ====================================================

             */

            if (atPoint(targetNum)) {
                targetNum++;

                if (targetNum >= targetPoints.length) {
                    requestOpModeStop();
                    return;
                }
            }

            double[] wheelPower = targeter(targetNum);

            maxPower = Math.max(Math.max(Math.abs(wheelPower[0]), Math.abs(wheelPower[1])), Math.max(Math.abs(wheelPower[2]), Math.abs(wheelPower[3])));

            if (maxPower != 0) {
                finalWheelPower = new double[]{(wheelPower[0] / maxPower), (wheelPower[1] / maxPower), (wheelPower[2] / maxPower), (wheelPower[3] / maxPower)};
            } else {
                finalWheelPower = new double[]{0, 0, 0, 0};
            }

            frontRightDrive.setPower(finalWheelPower[0]);
            frontLeftDrive.setPower(finalWheelPower[1]);
            backRightDrive.setPower(finalWheelPower[2]);
            backLeftDrive.setPower(finalWheelPower[3]);

            telemetry.addData("X:", robotPosition[0]);
            telemetry.addData("Y:", robotPosition[1]);
            telemetry.addData("Theta:", robotPosition[2]);
            telemetry.update();
        }
    }

    public static double[] targeter(int targetNum) {

        double errorX = targetPoints[targetNum][0] - robotPosition[0];
        double errorY = targetPoints[targetNum][1] - robotPosition[1];
        double errorT = angleWrap(targetPoints[targetNum][2] - robotPosition[2]);

        double robotErrorX = errorX * Math.cos(robotPosition[2]) + errorY * Math.sin(robotPosition[2]);
        double robotErrorY = -errorX * Math.sin(robotPosition[2]) + errorY * Math.cos(robotPosition[2]);

        double XYError = Math.sqrt((robotErrorX * robotErrorX) + (robotErrorY * robotErrorY));

        double targetRobotX = targetPoints[targetNum][0] * Math.cos(targetPoints[targetNum][2]) + targetPoints[targetNum][1] * Math.sin(targetPoints[targetNum][2]);
        double targetRobotY = targetPoints[targetNum][0] * Math.sin(targetPoints[targetNum][2]) + targetPoints[targetNum][1] * Math.cos(targetPoints[targetNum][2]);

        double XYDist = Math.sqrt((targetRobotX * targetRobotX) + (targetRobotY * targetRobotY));

        double precentRemaining = XYError / XYDist;

        double powerScale;

        if (precentRemaining <= 0.25) {
            powerScale = 3 * precentRemaining + 0.25;
        } else if (precentRemaining <= 0.75) {
            powerScale = 1;
        } else {
            powerScale = -3 * precentRemaining + 3.25;
        }

        double frontRightPower = (robotErrorY - robotErrorX - errorT) * powerScale;
        double frontLeftPower = (robotErrorY + robotErrorX + errorT) * powerScale;
        double backRightPower = (robotErrorY + robotErrorX - errorT) * powerScale;
        double backLeftPower = (robotErrorY - robotErrorX + errorT) * powerScale;
        return new double[]{frontRightPower, frontLeftPower, backRightPower, backLeftPower};
    }

    static double angleWrap(double angle) {
        while (angle > Math.PI) {
            angle -= 2 * Math.PI;
        }

        while (angle < -Math.PI) {
            angle += 2 * Math.PI;
        }

        return angle;
    }

    static boolean atPoint(int targetNum) {

        double errorX = targetPoints[targetNum][0] - robotPosition[0];
        double errorY = targetPoints[targetNum][1] - robotPosition[1];
        double errorT = angleWrap(targetPoints[targetNum][2] - robotPosition[2]);

        double XYError = Math.sqrt(errorX * errorX + errorY * errorY);

        return XYError <= targetPoints[targetNum][3] && Math.abs(errorT) <= targetPoints[targetNum][4];
    }


}

package org.firstinspires.ftc.teamcode.Helpers;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
public class Odometry {

    //=======================
    //ODOMETRY CONSTANTS

    public static double wheelDiameter = 32.0; // mm
    public static double tickPerRev = 2000.0;
    public static double trackWidth = 138.6;   // mm
    public static double perpendicularOffset = 228.10;   // mm

    //ODOMETRY CONSTANTS
    //=======================

    public final DcMotor frontLeftDrive;
    public final DcMotor frontRightDrive;
    public final DcMotor backLeftDrive;
    public final DcMotor backRightDrive;

    static double prevRight = 0;
    static double prevLeft = 0;
    static double prevPerpendicular = 0;

    public static double robotT = 0;
    public static double fieldX = 0;
    public static double fieldY = 0;

    public static double[] robotPosition = {0,0,0};

    public Odometry(HardwareMap hardwareMap){

        frontLeftDrive = hardwareMap.get(DcMotor.class, "front_left_drive");
        frontRightDrive = hardwareMap.get(DcMotor.class, "front_right_drive");
        backLeftDrive = hardwareMap.get(DcMotor.class, "back_left_drive");
        backRightDrive = hardwareMap.get(DcMotor.class, "back_right_drive");

    }

    // Front right and Left are Right and right encoder, and back right it the perpendicular

    public static double[] evalPos(double frontRightEncoder,
                                   double frontLeftEncoder,
                                   double backRightEncoder) {

        double distPerTick = (Math.PI * wheelDiameter) / tickPerRev;
        double deltaLeft, deltaRight, deltaPerpendicular;
        double deltaX, deltaY, deltaT;
        double curLeft, curRight, curPerpendicular;
        double fieldDeltaX, fieldDeltaY;
        double averageT;

        // Undo REVERSE on left side
        frontLeftEncoder *= -1;

        // Step 1 — Read encoders
        curRight = frontRightEncoder;
        curLeft = frontLeftEncoder;
        curPerpendicular = backRightEncoder;

        // Step 2 — Calculate change in encoders
        deltaRight = (curRight - prevRight) * distPerTick;
        deltaLeft = (curLeft - prevLeft) * distPerTick;
        deltaPerpendicular = (curPerpendicular - prevPerpendicular) * distPerTick;

        //Step 3 — Calculate movement
        deltaT = (deltaRight - deltaLeft) / trackWidth;
        deltaY = (deltaRight + deltaLeft) / 2;
        deltaX = deltaPerpendicular - perpendicularOffset * deltaT;

        //Step 4 - Calculate average heading
        averageT = robotT + (deltaT / 2);

        //Step 5 - Convert robot movement to field movement
        fieldDeltaX = (deltaX * (Math.cos(averageT)) - deltaY * (Math.sin(averageT)));
        fieldDeltaY = (deltaX * (Math.sin(averageT)) + deltaY * (Math.cos(averageT)));

        //Step 6 - Update position
        fieldX += fieldDeltaX;
        fieldY += fieldDeltaY;
        robotT += deltaT;

        //Step 7 - Update coordinates
        robotPosition[0] = fieldX;
        robotPosition[1] = fieldY;
        robotPosition[2] = robotT;

        //Step 8 - Update previous position
        prevRight = curRight;
        prevLeft = curLeft;
        prevPerpendicular = curPerpendicular;

        return robotPosition;
    }
    public void reset() {

        prevRight = prevLeft = prevPerpendicular = 0;
        robotT = fieldX = fieldY = 0;
        robotPosition = new double[]{0, 0, 0};
        robotPosition = evalPos(
                frontRightDrive.getCurrentPosition(),
                frontLeftDrive.getCurrentPosition(),
                backRightDrive.getCurrentPosition()
        );
    }

    public static double getX(){
        return robotPosition[0];
    }

    public static double getY(){
        return robotPosition[1];
    }

    public static double getHeading(){
        return robotPosition[2];
    }

    public static double[] getRobotPosition(){
        return new double[]{getX(), getY(), getHeading()};
    }
}

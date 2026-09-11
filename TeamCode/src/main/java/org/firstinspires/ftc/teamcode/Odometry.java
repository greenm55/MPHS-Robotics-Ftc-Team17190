package org.firstinspires.ftc.teamcode;

public class Odometry {

    //=======================
    //ODOMETRY CONSTANTS

    public static double wheelDiameter = 48.0; // mm
    public static double tickPerRev = 537.7;
    public static final double trackWidth = 350.0;   // mm
    public static final double PerpendicularOffset = 120.0;   // mm

    //ODOMETRY CONSTANTS
    //=======================

    static double prevRight = 0;
    static double prevLeft = 0;
    static double prevPerpendicular = 0;

    public static double robotT = 0;
    public static double fieldX = 0;
    public static double fieldY = 0;

    public static double[] robotPosition = {0,0,0};

    public static double[] evalPos(double frontRightEncoder, double frontLeftEncoder, double backRightEncoder) {

        double distPerTick = (Math.PI * wheelDiameter) / tickPerRev;
        double deltaLeft, deltaRight, deltaPerpendicular;
        double deltaX, deltaY, deltaT;
        double curLeft, curRight, curPerpendicular;
        double fieldDeltaX, fieldDeltaY;
        double averageT;


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
        deltaX = deltaPerpendicular - PerpendicularOffset * deltaT;

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

    public static double getX(){
        return robotPosition[0];
    }

    public static double getY(){
        return robotPosition[1];
    }

    public static double getHeading(){
        return robotPosition[2];
    }
}

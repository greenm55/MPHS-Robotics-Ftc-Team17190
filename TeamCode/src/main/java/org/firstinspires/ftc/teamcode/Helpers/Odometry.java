package org.firstinspires.ftc.teamcode.Helpers;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
public class Odometry {

    //=======================
    //ODOMETRY CONSTANTS

    public static double wheelDiameter = 32.0; // mm
    public static double tickPerRev = 2000.0;
    public static double trackWidth = 144;   // mm
    public static double perpendicularOffset = 102;   // mm

    //ODOMETRY CONSTANTS
    //=======================


    static double prevRight = 0;
    static double prevLeft = 0;
    static double prevPerpendicular = 0;
    public final DcMotor frontLeftDrive, frontRightDrive,backRightDrive;
    public static double robotT = 0;
    public static double fieldX = 0;
    public static double fieldY = 0;

    public static double[] robotPositionF = {0,0,0};

    public Odometry(HardwareMap hardwareMap){

        frontLeftDrive = hardwareMap.get(DcMotor.class, "front_left_drive");
        frontRightDrive = hardwareMap.get(DcMotor.class, "front_right_drive");
        backRightDrive = hardwareMap.get(DcMotor.class, "back_right_drive");
    }

    public double[] evalPos() {

        // Front right and Left are Right and right encoder, and back right it the perpendicular

        double frontLeftEncoder = frontLeftDrive.getCurrentPosition();
        double frontRightEncoder = frontRightDrive.getCurrentPosition();
        double perpendicularEncoder = backRightDrive.getCurrentPosition();

        double distPerTick = (Math.PI * wheelDiameter) / tickPerRev;
        double deltaLeft, deltaRight, deltaPerpendicular;
        double deltaX, deltaY, deltaT;
        double curLeft, curRight, curPerpendicular;
        double fieldDeltaX, fieldDeltaY;
        double averageT;

        // Undo REVERSE
        frontLeftEncoder *= -1;
        perpendicularEncoder *= -1;

        // Step 1 — Read encoders
        curRight = frontRightEncoder;
        curLeft = frontLeftEncoder;
        curPerpendicular = perpendicularEncoder;

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
        robotPositionF[0] = fieldX;
        robotPositionF[1] = fieldY;
        robotPositionF[2] = robotT;

        //Step 8 - Update previous position
        prevRight = curRight;
        prevLeft = curLeft;
        prevPerpendicular = curPerpendicular;

        return robotPositionF;
    }
    public void reset() {

        frontLeftDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        frontRightDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        backRightDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        frontLeftDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        frontRightDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        backRightDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        prevRight = prevLeft = prevPerpendicular = 0;
        robotT = fieldX = fieldY = 0;
        robotPositionF = new double[]{0, 0, 0};
        robotPositionF = evalPos();
    }

    public static double getX(){
        return robotPositionF[0];
    }

    public static double getY(){
        return robotPositionF[1];
    }

    public static double getHeading(){
        return robotPositionF[2];
    }

    public static double[] getRobotPosition(){
        return new double[]{getX(), getY(), getHeading()};
    }
}

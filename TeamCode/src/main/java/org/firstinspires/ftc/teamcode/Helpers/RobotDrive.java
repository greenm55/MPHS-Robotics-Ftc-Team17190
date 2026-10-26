package org.firstinspires.ftc.teamcode.Helpers;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class RobotDrive {

    private static final double MIN_POS_TOL  = Math.PI * Odometry.wheelDiameter / Odometry.tickPerRev; // mm
    private static final double MIN_HEAD_TOL = MIN_POS_TOL / Odometry.trackWidth;                       // rad

    // your choice: how sloppy is precision = 0?
    private static final double MAX_POS_TOL  = 300;  // mm, placeholder
    private static final double MAX_HEAD_TOL = 0.5;  // rad, placeholder


    private double[] robotPosition = new double [3];
    double[][] targetPoints;
    private boolean stopped = false;
    int targetNum = 0;

    private final Odometry odo;
    private final Intake intake;
    PID pid = new PID();


    public RobotDrive(Telemetry telemetry, HardwareMap hardwareMap, double[][] tP) {

        DcMotor frontLeftDrive = hardwareMap.get(DcMotor.class, "front_left_drive");
        DcMotor frontRightDrive = hardwareMap.get(DcMotor.class, "front_right_drive");
        DcMotor backLeftDrive = hardwareMap.get(DcMotor.class, "back_left_drive");
        DcMotor backRightDrive = hardwareMap.get(DcMotor.class, "back_right_drive");

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

        this.targetPoints = tP; // targetPoint[target number][X,Y,T, tolerance (0-1), [0-3](nothing, intake while moving, shoot, intake flower)]
        odo = new Odometry(hardwareMap);
        intake = new Intake(3,0,hardwareMap);
        odo.reset();
    }

    public boolean atPoint(int tarNum) {
        double precision = targetPoints[tarNum][3];
        double[] minTol = {MIN_POS_TOL, MIN_POS_TOL, MIN_HEAD_TOL};
        double[] maxTol = {MAX_POS_TOL, MAX_POS_TOL, MAX_HEAD_TOL};

        for (int i = 0; i < 3; i++) {
            double err = targetPoints[tarNum][i] - robotPosition[i];
            if (i == 2) {                       // wrap heading like PID does
                while (err >  Math.PI) err -= 2 * Math.PI;
                while (err < -Math.PI) err += 2 * Math.PI;
            }
            double allowed = minTol[i] + (1 - precision) * (maxTol[i] - minTol[i]);
            if (Math.abs(err) > allowed) return false;
        }
        return true;
    }
    public double[] moveRobot() {
        robotPosition = odo.evalPos();
        int startNum = targetNum;

        while (targetNum < targetPoints.length) {
            if (!stopped && !atPoint(targetNum)) break;     // haven't reached this point yet

            if (targetPoints[targetNum][4] > 1) {           // hold point
                stopped = true;// latch: keep polling until done
                if (targetPoints[targetNum][4] == 2) { // shoot
                    if (true) break;                  // still stopped, stay on this point; replace true with !turret.shoot()
                    stopped = false;// all balls launched
                }
                if (targetPoints[targetNum][4] == 3){ // take flower
                    if (!intake.flowerIntake())break;
                    stopped = false;
                }
            }
            targetNum++;
        }
        if (targetNum != startNum) pid.resetTime();

        if (targetNum >= targetPoints.length) return pid.updateRobot(robotPosition, new double[]{0, 0, 0});

        return pid.updateRobot(robotPosition, targetPoints[targetNum]);
    }
}
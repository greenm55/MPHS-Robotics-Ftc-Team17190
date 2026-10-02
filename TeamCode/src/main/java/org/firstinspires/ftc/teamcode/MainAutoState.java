package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;


@Autonomous(name = "Auto State Machine")
public class MainAutoState extends LinearOpMode {

    //==================================================
    // Target Points
    //
    // {X, Y, Heading, XY Tolerance, Heading Tolerance, Shoot}
    //
    // Shoot:
    // 0 = Do not shoot
    // 1 = Shoot
    //==================================================

    public static final double[][] targetPoints = {
            {100, 100, Math.PI, 50, 5, 0},
            {-100, 100, Math.PI / 2, 10, 1, 1},
            {0, 0, 0, 1, 1, 0}
    };
    //==================================================
    // States
    //==================================================
    enum State {
        MOVING,
        SHOOTING,
        FINISHED
    }
    State state = State.MOVING;
    int targetNum = 0;
    @Override
    public void runOpMode() {
        RobotDrive robotDrive = new RobotDrive(hardwareMap);
        Turret turret = new Turret(hardwareMap);
        //==================================================
        // Initialization
        //==================================================

        telemetry.addData("Status", "Initialized");
        telemetry.update();

        waitForStart();

        //==================================================
        // Autonomous Loop
        //==================================================

        while (opModeIsActive()) {

            // Update drive/odometry
            robotDrive.update();

            switch (state) {

                //==========================================
                // MOVING
                //==========================================

                case MOVING:

                    // Make sure there are targets remaining
                    if (targetNum >= targetPoints.length) {
                        state = State.FINISHED;
                        break;
                    }

                    // Tell RobotDrive to move toward target
                    robotDrive.goToPoint(targetPoints[targetNum]);

                    // Check whether we reached the target
                    if (robotDrive.atPoint(targetPoints[targetNum])) {

                        // Stop the robot
                        robotDrive.stop();

                        // Check whether this target requires shooting
                        if (targetPoints[targetNum][5] == 1) {
                            state = State.SHOOTING;
                        } else {
                            targetNum++;
                        }
                    }

                    break;

                //==========================================
                // SHOOTING
                //==========================================

                case SHOOTING:

                    // RobotDrive stays stopped
                    robotDrive.stop();

                    // Tell turret to auto-aim
                    turret.autoAim(robotDrive.getRobotPosition());

                    // Tell turret to shoot
                    if (turret.shoot()) {
                        targetNum++;

                        if (targetNum >= targetPoints.length) {
                            state = State.FINISHED;
                        } else {
                            state = State.MOVING;
                        }
                    }
                    break;


                //==========================================
                // FINISHED
                //==========================================

                case FINISHED:
                    robotDrive.stop();
                    break;
            }

            //==================================================
            // Telemetry
            //==================================================

            telemetry.addData("State", state);
            telemetry.addData("Target", targetNum + " / " + targetPoints.length);

            double[] position = robotDrive.getRobotPosition();

            telemetry.addData("X", position[0]);
            telemetry.addData("Y", position[1]);
            telemetry.addData("Theta", position[2]);

            telemetry.update();
        }
    }
}
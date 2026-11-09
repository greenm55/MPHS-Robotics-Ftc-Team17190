package org.firstinspires.ftc.teamcode;


import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.Helpers.RobotDrive;

@Autonomous
public class TestRobotDriveRemoveLater extends LinearOpMode {
    double[][] targetList = {
            {0,600,0,.95,0},
            {-600,600,0,.95,0},
            {-300,300, Math.PI/2,.95,0},
            {-600,0,Math.PI,.95,0},
            {0,0,0,.95,0},
            {0,600,0,.95,0},
            {-600,600,0,.95,0},
            {-300,300, Math.PI/2,.95,0},
            {-600,0,Math.PI,.95,0},
            {0,0,0,.95,0},
            {0,600,0,.95,0},
            {-600,600,0,.95,0},
            {-300,300, Math.PI/2,.95,0},
            {-600,0,Math.PI,.95,0},
            {0,0,0,.95,0},
            {0,600,0,.95,0},
            {-600,600,0,.95,0},
            {-300,300, Math.PI/2,.95,0},
            {-600,0,Math.PI,.95,0},
            {0,0,0,.95,0}
    };
    double[][] startPos = {
            {0,0,0}
    };
    /*
    Basic 1&2 flower: 0,1
    Solo 1&2 flower: 2,3
    Duo 1&2 flower start: 4,5
    Duo 1&2 shoot wait: 6,7
    Don't move: 8
     */
    double[] motorPower;
    long waitTime = 0;

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

        /* while(opModeInInit() && initStage != 2){
            telemetry.addData(" Basic 1&2 flower: 0,1\n" +
                    "    Solo 1&2 flower: 2,3\n" +
                    "    Duo 1&2 flower start: 4,5\n" +
                    "    Duo 1&2 shoot wait: 6,7\n" +
                    "    Don't move: 8", "");
            telemetry.update();

            while (initStage == 0){
                telemetry.addData(" Basic 1&2 flower: 0,1\n" +
                        "    Solo 1&2 flower: 2,3\n" +
                        "    Duo 1&2 flower start: 4,5\n" +
                        "    Duo 1&2 shoot wait: 6,7\n" +
                        "    Don't move: 8", "");

                if(gamepad1.y){
                    initStage = 2;
                    break;
                } else if (gamepad1.x){
                    initStage = 1;
                }

                if (gamepad1.dpad_up){
                    routNumber++;
                    routNumber = wrap(routNumber);
                }
                else if (gamepad1.dpad_down){
                    routNumber--;
                    routNumber = wrap(routNumber);
                }
                telemetry.addData("rout number: ", routNumber);
                telemetry.update();
            }
            if (initStage == 2) break;
            telemetry.update();
            while (initStage == 1){
                if(gamepad1.y || gamepad1.x){
                    initStage = 2;
                    break;
                }
                if (gamepad1.dpad_up){
                    waitTime += 250;
                } else if (gamepad1.dpad_down) {
                    waitTime -= 250;
                    if (waitTime < 0) waitTime = 0;
                }
                telemetry.addData("wait time: ", waitTime);
            }
        }

         */

        RobotDrive roboD = new RobotDrive(hardwareMap, targetList, startPos[0]);

        waitForStart();
        sleep(waitTime);
        while (opModeIsActive()){

            motorPower = roboD.moveRobot();
            frontRightDrive.setPower(motorPower[0]);
            frontLeftDrive.setPower(motorPower[1]);
            backRightDrive.setPower(motorPower[2]);
            backLeftDrive.setPower(motorPower[3]);

        }

    }

}

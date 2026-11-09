package org.firstinspires.ftc.teamcode;


import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.Helpers.RobotDrive;

@Autonomous (name = "Blue Auto")
public class AutoMain extends LinearOpMode {
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

        RobotDrive roboD = new RobotDrive(hardwareMap, targetList[0], startPos[0]);

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

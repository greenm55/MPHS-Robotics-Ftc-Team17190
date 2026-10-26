package org.firstinspires.ftc.teamcode.Helpers;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class Intake {
    int ballMax;
    int ballCount;
    int fakeCycle;
    DcMotor frontLeftDrive; //change to intake
    public Intake(int max, int count, HardwareMap hardwareMap){
        frontLeftDrive = hardwareMap.get(DcMotor.class, "front_left_drive");
        this.ballMax = max;
        this.ballCount = count;
    }

    public boolean flowerIntake(){
        fakeCycle++;
        ballCount = checkBalls();
        runIntake();
        return ballMax-ballCount > 0; //
    }
    public int checkBalls(){
        ballCount = (int) (Math.random()*5);
        return ballCount;
    }
    public void runIntake(){
        //frontLeftDrive.setPower(1);
        ballCount = checkBalls();
        if (ballCount > ballMax){
            //frontLeftDrive.setPower(-1);
        } else if (ballCount == ballMax) {
            //frontLeftDrive.setPower(0);
        }
    }
}

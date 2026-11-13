package org.firstinspires.ftc.teamcode.Helpers;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class Intake {
    int ballMax;
    int ballCount;
    int fakeCycle;
    DcMotor intake; //change to intake
    public Intake(int max, int count, HardwareMap hardwareMap){
        intake = hardwareMap.get(DcMotor.class, "intake");
        this.ballMax = max;
        this.ballCount = count;
    }
    public Intake(HardwareMap hardwareMap){
        intake = hardwareMap.get(DcMotor.class, "intake");
        this.ballMax = 4;
        this.ballCount = 0;
    }

    public boolean flowerIntake(){
        fakeCycle++;
        ballCount = checkBalls();
        runIntake(1);
        return ballMax-ballCount > 0; //
    }
    public int checkBalls(){
        ballCount = (int) (Math.random()*5);
        return ballCount;
    }
    public void runIntake(double p){
        intake.setPower(p);
        }
    }

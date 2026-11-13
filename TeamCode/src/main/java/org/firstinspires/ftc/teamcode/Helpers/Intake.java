package org.firstinspires.ftc.teamcode.Helpers;

import static com.qualcomm.robotcore.hardware.DcMotorSimple.Direction.REVERSE;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

public class Intake {
    int ballMax;
    int ballCount;
    int fakeCycle;
    boolean loaderRunning = false;
    ElapsedTime time = new ElapsedTime();
    DcMotor intake;
    CRServo loader;

    public Intake(int max, int count, HardwareMap hardwareMap) {
        intake = hardwareMap.get(DcMotor.class, "intake");
        loader = hardwareMap.get(CRServo.class, "loader");
        loader.setDirection(REVERSE);
        this.ballMax = max;
        this.ballCount = count;
    }

    public Intake(HardwareMap hardwareMap) {
        intake = hardwareMap.get(DcMotor.class, "intake");
        loader = hardwareMap.get(CRServo.class, "loaderServo");
        loader.setDirection(REVERSE);
        this.ballMax = 4;
        this.ballCount = 0;
    }

    public boolean flowerIntake() {
        fakeCycle++;
        ballCount = checkBalls();
        runIntake(1);
        return ballMax - ballCount > 0; //
    }

    public int checkBalls() {
        ballCount = (int) (Math.random() * 5);
        return ballCount;
    }

    public void runIntake(double p) {
        intake.setPower(p);

    }
    public void loadBall(double p){
        loader.setPower(p);
    }
}
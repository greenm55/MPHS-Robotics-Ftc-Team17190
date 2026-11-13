package org.firstinspires.ftc.teamcode.Helpers;

import com.qualcomm.robotcore.util.Range;

public class Turret_PID {
    double kP, kD;
    double dTerm = 0;
    double motorPower;
    long lastTime = System.nanoTime();
    double prevTurPos = 0;


    public Turret_PID() {
        this.kP = 0.0075;
        this.kD = 0.00005;
    }

    public double updateTurret(double pos, double target) { // just in ticks for now
        double error = target - pos;
        long now = System.nanoTime();
        double dt = (now - lastTime) / 1e9;

        /*
        while (error >  Math.PI) error -= 2 * Math.PI;
        while (error < -Math.PI) error += 2 * Math.PI;
        */

        motorPower = kP * error;

        double turretVel = (pos - prevTurPos) / dt;

        dTerm = -(kD * turretVel);

        motorPower += (dTerm);

        lastTime = now;
        prevTurPos = pos;
        return Range.clip(motorPower, -1,1);
    }

    public double updateHood(double pos, double target) { //potentially unused
        return 0.0;
    }

    public double updateFlyWheel(double pos, double targetRPM) {
        long now = System.nanoTime();
        lastTime = now;


        return 0.0;
    }
}

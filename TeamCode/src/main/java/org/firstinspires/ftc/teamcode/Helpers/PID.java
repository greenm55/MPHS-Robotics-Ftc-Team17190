package org.firstinspires.ftc.teamcode.Helpers;
public class PID {

    double kP, kI, kD, kPT, kDT, kIT;  // P I and D terms

    double[] motorPower = new double[4]; // motor power list
    long lastTime = System.nanoTime();

    double[] lastCmd = new double[3];
    double[] lastPos = new double[3];
    boolean hasLast = false;
    public double[] dTerm = new double[3];
    double[] integral = new double[3];   // field X, field Y, heading
    double maxI = 0.15;
    double maxIT = 0.1;


    public PID(double P, double I, double D, double PT, double IT, double DT) {
        // set gains
        this.kP = P;
        this.kI = I;
        this.kD = D;
        this.kPT = PT;
        this.kIT = IT;
        this.kDT = DT;

    }
    public PID(){
        this.kP = 0.005;
        this.kI = 0.0075;
        this.kD = 0.002;
        this.kPT = 1;
        this.kIT = 0.5;
        this.kDT = 0.07;
    }


    public double[] updateRobot(double[] roboPos, double[] target){
        // [] 0:X 1:Y 2:T
        double errorX = target[0]- roboPos[0];
        double errorY = target[1] - roboPos[1];
        double errorT = target[2] - roboPos[2];

        while (errorT >  Math.PI) errorT -= 2 * Math.PI;
        while (errorT < -Math.PI) errorT += 2 * Math.PI;
        // wrap T to pi, -pi

        double cos = Math.cos(roboPos[2]);
        double sin = Math.sin(roboPos[2]);
        double robotX =  errorX * cos + errorY * sin;
        double robotY = -errorX * sin + errorY * cos;
        // make X and Y robot centric

        double[] scaledPower = {robotX * kP, robotY * kP, errorT * kPT};

        // in updateRobot:
        long now = System.nanoTime();
        double dt = (now - lastTime) / 1e9;
        lastTime = now;

        if (hasLast && dt > 0) {
            double dT = roboPos[2] - lastPos[2];  // change in heading
            while (dT >  Math.PI) dT -= 2 * Math.PI;
            while (dT < -Math.PI) dT += 2 * Math.PI;
            // wrap heading

            double vx = (roboPos[0] - lastPos[0]) / dt; // x vel
            double vy = (roboPos[1] - lastPos[1]) / dt; // y vel
            double vT = dT / dt; // t vel

            double velRobotX =  vx * cos + vy * sin;
            double velRobotY = -vx * sin + vy * cos;

            dTerm[0] = -kD  * velRobotX;
            dTerm[1] = -kD  * velRobotY;
            dTerm[2] = -kDT * vT;

            integral[0] += errorX * dt;
            integral[1] += errorY * dt;
            integral[2] += errorT * dt;

            if (kI != 0){
                double lim = maxI / kI;
                integral[0] = Math.max(-lim, Math.min(lim, integral[0]));
                integral[1] = Math.max(-lim, Math.min(lim, integral[1]));
            }
            if (kIT != 0){
                double lim = maxIT / kIT;
                integral[2] = Math.max(-lim, Math.min(lim, integral[2]));
            }

        }
        lastPos[0] = roboPos[0];
        lastPos[1] = roboPos[1];
        lastPos[2] = roboPos[2];
        hasLast = true;

        double intX =  integral[0] * cos + integral[1] * sin;
        double intY = -integral[0] * sin + integral[1] * cos;

        double[] iTerm = {kI * intX, kI * intY, kIT * integral[2]};

        for (int i = 0; i < 3; i++) scaledPower[i] += (dTerm[i] + iTerm[i]);

        motorPower[0] = -(scaledPower[0] - scaledPower[1] - scaledPower[2]); //FR
        motorPower[1] = -(-scaledPower[0] - scaledPower[1] + scaledPower[2]); //FL
        motorPower[2] = -(-scaledPower[0] - scaledPower[1] - scaledPower[2]); //BR
        motorPower[3] = -(scaledPower[0] - scaledPower[1] + scaledPower[2]); //BL


        double max = 1.0;
        double maxPower = 1.0;
        for (double p : motorPower) max = Math.max(max, (Math.abs(p)));
        for (int i = 0; i < 4; i++){
            motorPower[i] /= max;
            motorPower[i] *= maxPower;
        }

        return motorPower;
    }
    public void resetTime() {
        lastTime = System.nanoTime();
        lastCmd[0] = lastCmd[1] = lastCmd[2] = 0;
        hasLast = false;
        for (int i=0; i<=2;){
            integral[i] = 0;
            i++;
        }
    }
}

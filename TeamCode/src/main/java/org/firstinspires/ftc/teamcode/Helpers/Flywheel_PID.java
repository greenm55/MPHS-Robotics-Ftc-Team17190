package org.firstinspires.ftc.teamcode.Helpers; // ASSUMPTION: match the package line at the top of your PID.java

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

/**
 * Flywheel speed controller: power = kF*target + kP*error + I.
 * Measures RPM from getCurrentPosition(), so it does NOT use getVelocity().
 * Motor must be in RUN_WITHOUT_ENCODER. Call update(targetRpm) once per loop.
 */
public class Flywheel_PID {
    // ---- Gains (units: motor power per RPM) ----
    public double kF = 1.0 / 5400.0; // REPLACE with 1 / (max RPM measured at full power) from the tune OpMode
    public double kP = 0.00015; // 0.0003, 0.00015
    public double kI = 0.0002;
    public double maxI = 0.15;       // most power the I term is allowed to add or remove

    public static final double MAX_RPM = 6000.0;
    private static final double WINDOW_S = 0.05; // measure speed over at least 50 ms (less jitter)
    private static final double ALPHA = 0.5;     // smoothing: 1 = none, smaller = smoother but slower

    private final DcMotor motor;
    private final double ticksPerRev; // encoder ticks per FLYWHEEL revolution
    private final ElapsedTime timer = new ElapsedTime();

    private double lastTime = 0;
    private int lastPos;
    private double rpm = 0;
    private boolean haveRpm = false;
    private double integral = 0;
    private double power = 0;
    private double target = 0;

    public Flywheel_PID(DcMotor motor) {
        this.motor = motor;
        this.ticksPerRev = 28;
        this.lastPos = motor.getCurrentPosition();
        timer.reset();
    }

    /** Takes a new speed sample if >= WINDOW_S has passed. Returns the sample's dt, or -1 if none. */
    private double sample() {
        double now = timer.seconds();
        double dt = now - lastTime;
        if (dt < WINDOW_S) return -1;

        int pos = motor.getCurrentPosition();
        // abs(): only speed matters, so the encoder's sign convention can't break the controller
        double raw = Math.abs((pos - lastPos) / dt) * 60.0 / ticksPerRev;
        rpm = haveRpm ? rpm + ALPHA * (raw - rpm) : raw;
        haveRpm = true;

        lastPos = pos;
        lastTime = now;
        return dt;
    }

    /** Run the controller. Returns the motor power that was applied (0..1). */
    public double update(double targetRpm) {
        target = Range.clip(targetRpm, 0, MAX_RPM);
        double dt = sample();

        if (target <= 0) {
            integral = 0;
            power = 0;
        } else if (dt > 0) {
            double error = target - rpm;
            integral = Range.clip(integral + kI * error * dt, -maxI, maxI);
            power = Range.clip(kF * target + kP * error + integral, 0, 1);
        } else if (power == 0) {
            // spinning up from rest before the first sample: start from feedforward
            power = Range.clip(kF * target, 0, 1);
        }

        motor.setPower(power);
        return power;
    }

    /** Bypass the controller (used for calibration). Still measures RPM. */
    public void runRaw(double rawPower) {
        sample();
        integral = 0;
        target = 0;
        power = 0;
        motor.setPower(rawPower);
    }

    public double getRpm() { return rpm; }
    public double getTarget() { return target; }

    /** True when a target is set and measured RPM is within tolRpm of it. */
    public boolean atSpeed(double tolRpm) {
        return target > 0 && Math.abs(target - rpm) <= tolRpm;
    }
}
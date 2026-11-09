package org.firstinspires.ftc.teamcode.Helpers;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

/**
 * Holds the turret at a field-relative angle while the robot turns.
 * <p>
 * Conventions (all angles in radians):
 *  - robotHeading and every field angle are counterclockwise-positive.
 *  - Field angle 0 points along +X, so atan2(dy, dx) is a valid field angle.
 *  - Encoder angle 0 is the turret's starting limit; it reads ZERO_OFFSET
 *    when the turret faces the robot's front.
 * <p>
 * Call once per loop:  turret.update(robotHeading);
 */
public class TurretController {

    // Verified hardware values
    static final double TICKS_PER_MOTOR_REV = 537.7;
    static final double GEAR_RATIO = 2.75;               // motor revs per turret rev
    static final double TICKS_PER_RAD = TICKS_PER_MOTOR_REV * GEAR_RATIO / (2 * Math.PI);
    static final double ZERO_OFFSET = 5 * Math.PI / 9;   // encoder angle when facing robot front
    static final double RANGE = 5.624;                   // usable travel from encoder 0, rad
    static final double RANGE_TICKS = RANGE * TICKS_PER_RAD;
    private final Turret_PID pid = new Turret_PID();
    DcMotor turret;

    private double desiredFieldAngle = 0;
    private double targetTicks = 0;

    /** Assumes the turret is at its starting limit when this is constructed. */
    public TurretController(HardwareMap hardwareMap) {
        turret = hardwareMap.get(DcMotor.class, "turret");

        turret.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        turret.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }

    // ---- Choosing what to aim at ----

    /** Aim at a fixed direction on the field. */
    public void setFieldAngle(double fieldAngle) {
        desiredFieldAngle = wrap(fieldAngle);
    }

    /** Freeze whatever direction the turret is pointing at right now. */
    public void holdCurrentFieldAngle(double robotHeading) {
        desiredFieldAngle = getFieldAngle(robotHeading);
    }

    /** Aim at a point on the field from the robot's current position. */
    public void trackPoint(double targetX, double targetY, double robotX, double robotY) {
        desiredFieldAngle = Math.atan2(-(targetX - robotX), targetY - robotY);
    }

    // ---- Main loop ----

    public void update(double robotHeading) {
        double robotFrame = wrap(desiredFieldAngle - robotHeading);
        double v = wrap(robotFrame + ZERO_OFFSET);
        if (v < 0) v += 2 * Math.PI;                     // now [0, 2PI)

        if (v > RANGE) {                                 // dead zone: park at the nearer limit
            v = (v - RANGE < 2 * Math.PI - v) ? RANGE : 0;
        }
        targetTicks = v * TICKS_PER_RAD;

        double pos = turret.getCurrentPosition();
        double power = pid.updateTurret(pos, targetTicks);

        // Never push further into a limit
        if (pos <= 0 && power < 0) power = 0;
        if (pos >= RANGE_TICKS && power > 0) power = 0;

        turret.setPower(power);
    }

    public void stop() {
        turret.setPower(0);
    }

    // ---- Readouts ----

    /** Turret angle relative to the robot's front (CCW positive). */
    public double getRobotFrameAngle() {
        return turret.getCurrentPosition() / TICKS_PER_RAD - ZERO_OFFSET;
    }

    /** Direction the turret is pointing on the field. */
    public double getFieldAngle(double robotHeading) {
        return wrap(getRobotFrameAngle() + robotHeading);
    }

    public double getTargetTicks() {
        return targetTicks;
    }

    /** True when the turret is within tolRad of its commanded position. */
    public boolean isAimed(double tolRad) {
        return Math.abs(targetTicks - turret.getCurrentPosition()) / TICKS_PER_RAD < tolRad;
    }

    private static double wrap(double a) {
        while (a > Math.PI) a -= 2 * Math.PI;
        while (a <= -Math.PI) a += 2 * Math.PI;
        return a;
    }
}
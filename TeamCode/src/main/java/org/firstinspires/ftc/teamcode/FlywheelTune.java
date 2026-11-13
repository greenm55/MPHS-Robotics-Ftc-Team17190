package org.firstinspires.ftc.teamcode; // ASSUMPTION: match the package of your other OpModes

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.teamcode.Helpers.Flywheel_PID; // ASSUMPTION: same package as FlywheelPID.java


/**
 * Controls (gamepad1):
 *   A (hold)  = full power, raw. Use this to find the max RPM -> kF = 1 / maxRPM
 *   B (hold)  = run the PID at the target RPM
 *   dpad up / down = target +/- 250 RPM
 *   neither A nor B = motor off
 */
@TeleOp(name = "FlywheelTune", group = "Test")
public class FlywheelTune extends LinearOpMode {

    // ASSUMPTIONS to verify:
    private static final String MOTOR_NAME = "flywheel"; // name in your robot configuration
    private static final double TICKS_PER_REV = 28.0;    // ticks per FLYWHEEL rev (28 = bare motor shaft)

    @Override
    public void runOpMode() {
        DcMotor motor = hardwareMap.get(DcMotor.class, MOTOR_NAME);
        motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        // If the wheel spins the wrong way, uncomment:
        // motor.setDirection(DcMotor.Direction.REVERSE);

        Flywheel_PID fw = new Flywheel_PID(motor);

        double target = 3000;
        boolean upPrev = false, downPrev = false;

        waitForStart();

        while (opModeIsActive()) {
            if (gamepad1.dpad_up && !upPrev) target += 250;
            if (gamepad1.dpad_down && !downPrev) target -= 250;
            upPrev = gamepad1.dpad_up;
            downPrev = gamepad1.dpad_down;
            target = Range.clip(target, 0, Flywheel_PID.MAX_RPM);

            double power;
            if (gamepad1.a) {
                fw.runRaw(1.0);
                power = 1.0;
            } else if (gamepad1.b) {
                power = fw.update(target);
            } else {
                fw.runRaw(0);
                power = 0;
            }

            telemetry.addData("Encoder ticks", motor.getCurrentPosition());
            telemetry.addData("RPM", "%.0f", fw.getRpm());
            telemetry.addData("Target RPM", "%.0f", target);
            telemetry.addData("Error", "%.0f", target - fw.getRpm());
            telemetry.addData("Power", "%.3f", power);
            telemetry.addData("kF / kP / kI", "%.6f / %.6f / %.6f", fw.kF, fw.kP, fw.kI);
            telemetry.update();
        }

        motor.setPower(0);
    }
}
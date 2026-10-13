package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.Helpers.Odometry;

@TeleOp(name = "TestOdo")
public class TestOdo extends LinearOpMode {

    private DcMotor frontLeftDrive;
    private DcMotor frontRightDrive;
    private DcMotor backRightDrive;

    @Override
    public void runOpMode() {

        frontLeftDrive = hardwareMap.get(DcMotor.class, "front_left_drive");
        frontRightDrive = hardwareMap.get(DcMotor.class, "front_right_drive");
        backRightDrive = hardwareMap.get(DcMotor.class, "back_right_drive");

        frontLeftDrive.setDirection(DcMotor.Direction.REVERSE);
        frontRightDrive.setDirection(DcMotor.Direction.FORWARD);
        backRightDrive.setDirection(DcMotor.Direction.FORWARD);

        resetAll();

        telemetry.addData("Status", "Init - press A while running to zero everything");
        telemetry.update();
        waitForStart();

        // Zero again at start so the moment you press Start is the origin
        resetAll();

        double distPerTick = (Math.PI * Odometry.wheelDiameter) / Odometry.tickPerRev;

        while (opModeIsActive()) {

            // Press A to zero encoders and odometry without restarting the OpMode
            if (gamepad1.a) {
                resetAll();
            }

            // Read each encoder exactly once per loop so every number below
            // comes from the same snapshot
            int fr = frontRightDrive.getCurrentPosition();   // right pod
            int fl = frontLeftDrive.getCurrentPosition();    // left pod
            int br = backRightDrive.getCurrentPosition();    // perpendicular pod

            double[] pos = Odometry.evalPos(fr, fl, br);

            // Independent check: heading computed straight from the raw ticks,
            // not through Odometry's running state
            double rightMm = fr * distPerTick;
            double leftMm = fl * distPerTick;
            double perpMm = br * distPerTick;
            double checkHeading = (rightMm - leftMm) / Odometry.trackWidth;

            telemetry.addLine("--- Raw ticks ---");
            telemetry.addData("Fr (right pod)", fr);
            telemetry.addData("Fl (left pod)", fl);
            telemetry.addData("Br (perp pod)", br);

            telemetry.addLine("--- Distance since zero (mm) ---");
            telemetry.addData("Right", "%.1f", rightMm);
            telemetry.addData("Left", "%.1f", leftMm);
            telemetry.addData("Perp", "%.1f", perpMm);
            telemetry.addData("R - L (positive = turning CCW)", "%.1f", rightMm - leftMm);
            telemetry.addData("(R + L) / 2 (forward)", "%.1f", (rightMm + leftMm) / 2.0);

            telemetry.addLine("--- Odometry output ---");
            telemetry.addData("X (mm)", "%.1f", pos[0]);
            telemetry.addData("Y (mm)", "%.1f", pos[1]);
            telemetry.addData("Heading (deg)", "%.2f", Math.toDegrees(pos[2]));

            telemetry.addLine("--- Sanity check ---");
            telemetry.addData("Heading from raw ticks (deg)", "%.2f", Math.toDegrees(checkHeading));
            telemetry.addData("Mismatch (deg)", "%.2f", Math.toDegrees(pos[2] - checkHeading));

            telemetry.update();
        }
    }

    private void resetAll() {
        frontLeftDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        frontRightDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        backRightDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        frontLeftDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        frontRightDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        backRightDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        sleep(50); // let the encoder reset settle before Odometry takes its baseline

        Odometry.reset();
    }
}
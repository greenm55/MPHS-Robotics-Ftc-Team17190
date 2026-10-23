package org.firstinspires.ftc.teamcode;

import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.telemetry;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.Helpers.Odometry;
import org.firstinspires.ftc.teamcode.Helpers.PID;

@Autonomous(name = "Test PID Remove Later")
public class TestPIDRemoveLater extends LinearOpMode {

    @Override
    public void runOpMode() throws InterruptedException {
        DcMotor frontLeftDrive = hardwareMap.get(DcMotor.class, "front_left_drive");
        DcMotor frontRightDrive = hardwareMap.get(DcMotor.class, "front_right_drive");
        DcMotor backLeftDrive = hardwareMap.get(DcMotor.class, "back_left_drive");
        DcMotor backRightDrive = hardwareMap.get(DcMotor.class, "back_right_drive");

        frontLeftDrive.setDirection(DcMotor.Direction.REVERSE);
        frontRightDrive.setDirection(DcMotor.Direction.FORWARD);
        backLeftDrive.setDirection(DcMotor.Direction.REVERSE);
        backRightDrive.setDirection(DcMotor.Direction.FORWARD);
        // target == 0,0,0
        // eg hold
        Odometry odo = new Odometry(hardwareMap);
        PID pid = new PID(.001,0,0.0002);

        double[] target = {0,0,0};
        double[] roboPos;
        double[] motorPower;
        odo.reset();
        roboPos= odo.evalPos();
        telemetry.addData("Robot Position X :", roboPos[0]);
        telemetry.addData("Robot Position Y :", roboPos[1]);
        telemetry.addData("Robot Position T :", roboPos[2]);
        telemetry.addData("Status", "Initialized");
        telemetry.update();
        waitForStart();
        pid.resetTime();
        while(opModeIsActive()){

            roboPos = odo.evalPos();
            motorPower = pid.updateRobot(roboPos, target);

            frontRightDrive.setPower(-motorPower[0]);
            frontLeftDrive.setPower(-motorPower[1]);
            backRightDrive.setPower(-motorPower[2]);
            backLeftDrive.setPower(-motorPower[3]);

            telemetry.addData("Robot Position X :", roboPos[0]);
            telemetry.addData("Robot Position Y :", roboPos[1]);
            telemetry.addData("Robot Position T :", roboPos[2]);

            telemetry.addData("motor power FR:", motorPower[0]);
            telemetry.addData("motor power FL:", motorPower[1]);
            telemetry.addData("motor power BR:", motorPower[2]);
            telemetry.addData("motor power BL:", motorPower[3]);

            telemetry.addData("D0:",pid.dTerm[0]);
            telemetry.addData("D1:",pid.dTerm[1]);
            telemetry.addData("D2:",pid.dTerm[2]);

            telemetry.update();
        }
    }
}

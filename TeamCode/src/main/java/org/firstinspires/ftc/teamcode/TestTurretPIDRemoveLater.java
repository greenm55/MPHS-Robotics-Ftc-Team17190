package org.firstinspires.ftc.teamcode;


import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.Helpers.Odometry;
import org.firstinspires.ftc.teamcode.Helpers.TurretController;

@Autonomous(name = "Test Turret PID Remove Later")
public class TestTurretPIDRemoveLater extends LinearOpMode {

    @Override
    public void runOpMode() throws InterruptedException {
        DcMotor turret = hardwareMap.get(DcMotor.class, "turret");

        turret.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        turret.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);


        TurretController turretController = new TurretController(hardwareMap);
        Odometry odo = new Odometry(hardwareMap);


        odo.reset();
        waitForStart();

        while(opModeIsActive()){

            double pos = turret.getCurrentPosition();
            double[] robotPos = odo.evalPos();
            double heading = robotPos[2];

            turretController.trackPoint(100,0,robotPos[0],robotPos[1]);
            turretController.update(heading);

            telemetry.addData("Position", pos);
            telemetry.addData("x", robotPos[0]);
            telemetry.addData("y", robotPos[1]);
            telemetry.addData("heading (Rad)", heading);
            telemetry.addData("heading (deg)", Math.toDegrees(heading));

            telemetry.update();
        }
    }
}

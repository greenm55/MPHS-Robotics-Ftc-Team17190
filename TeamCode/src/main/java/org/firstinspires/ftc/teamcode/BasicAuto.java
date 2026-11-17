package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.Helpers.Odometry;
import org.firstinspires.ftc.teamcode.Helpers.RobotDrive;
import org.firstinspires.ftc.teamcode.Helpers.TurretController;
import org.firstinspires.ftc.teamcode.Helpers.Turret_PID;

@Autonomous (name = "Basic Auto")
public class BasicAuto extends LinearOpMode {


    @Override
    public void runOpMode(){
        DcMotor frontLeftDrive = hardwareMap.get(DcMotor.class, "front_left_drive");
        DcMotor frontRightDrive = hardwareMap.get(DcMotor.class, "front_right_drive");
        DcMotor backLeftDrive = hardwareMap.get(DcMotor.class, "back_left_drive");
        DcMotor backRightDrive = hardwareMap.get(DcMotor.class, "back_right_drive");
        DcMotor turret = hardwareMap.get(DcMotor.class, "turret");
        DcMotor flywheel = hardwareMap.get(DcMotor.class, "flywheel");
        CRServo loader = hardwareMap.get(CRServo.class, "loaderServo");


        frontLeftDrive.setDirection(DcMotor.Direction.REVERSE);
        frontRightDrive.setDirection(DcMotor.Direction.FORWARD);
        backLeftDrive.setDirection(DcMotor.Direction.REVERSE);
        backRightDrive.setDirection(DcMotor.Direction.FORWARD);

        Odometry odo = new Odometry(hardwareMap);
        TurretController tc = new TurretController(hardwareMap);
        Turret_PID tPID = new Turret_PID();

        waitForStart();

        while(opModeIsActive()) {
            double pos = turret.getCurrentPosition();
            turret.setPower(tPID.updateTurret(pos, 10));
        }
    }

}

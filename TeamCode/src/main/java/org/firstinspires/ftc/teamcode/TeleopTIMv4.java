package org.firstinspires.ftc.teamcode;
//these are our imports; they help the code understand certain terms

import static com.qualcomm.robotcore.hardware.DcMotor.ZeroPowerBehavior.BRAKE;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.hardware.dfrobot.HuskyLens;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DistanceSensor;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Servo;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;



//these names must match the name of the code
@TeleOp(name = "TeleopTIMv4")

public class TeleopTIMv4 extends LinearOpMode {
    //this is where I declare all of my motors/servos
    private DcMotor FL;
    private DcMotor FR;
    private DcMotor BL;
    private DcMotor BR;
    private DcMotor in;
    private DcMotor ix;
    private DcMotorEx shootl;
    private DcMotorEx shootr;
    private Servo serv;
    private DistanceSensor distance;
    private HuskyLens huskyLens;

    @Override
    public void runOpMode() {
        // x is the status of the intake
        // y is for the indexer
        // z is for the flywheels
        //a is for the drivetrain (reversible)
        //the autos help us automatically shoot
        int x;
        int y;
        int z;
        int a;
        int f;
        int auto;
        int auto1;
        int auto2;
        int auto3;
        int auto4;
        int auto5;
        int aut;
        double distance1;

//this is where I map the motors to the ones in the configuration
        FL = hardwareMap.get(DcMotor.class, "Front Left");
        FR = hardwareMap.get(DcMotor.class, "Front Right");
        BL = hardwareMap.get(DcMotor.class, "Back Left");
        BR = hardwareMap.get(DcMotor.class, "Back Right");
        in = hardwareMap.get(DcMotor.class, "intake");
        ix = hardwareMap.get(DcMotor.class, "indexer");
        shootl = hardwareMap.get(DcMotorEx.class, "shooter left");
        shootr = hardwareMap.get(DcMotorEx.class, "shooter right");
        serv = hardwareMap.get(Servo.class, "servo");
        distance = hardwareMap.get(DistanceSensor.class, "distance");
        huskyLens = hardwareMap.get(HuskyLens.class, "HuskyLens");


        huskyLens.selectAlgorithm(HuskyLens.Algorithm.TAG_RECOGNITION);
        telemetry.update();

        ElapsedTime timer = new ElapsedTime();

        waitForStart();
//here is initialization
        FL.setDirection(DcMotor.Direction.REVERSE);
        BL.setDirection(DcMotor.Direction.REVERSE);
        BR.setDirection(DcMotor.Direction.REVERSE);
        ix.setDirection(DcMotor.Direction.REVERSE);
        x = 0;
        y = 0;
        z = 0;
        a = 0;
        f = 0;
        auto = 0;
        auto1 = 0;
        auto2 = 0;
        auto3 = 0;
        auto4 = 0;
        auto5 = 0;
        aut = 0;




        if (opModeIsActive()) {
            while (opModeIsActive()){



                //hold these buttons to use the intake
                if (gamepad2.dpad_right) {
                    in.setPower(-0.75);
                }else{
                    in.setPower(0);
                }
                if (gamepad2.dpad_left) {
                    in.setPower(0.75);
                }else{
                    in.setPower(0);
                }
//here I use variables so you only have to press a button to run it.
//if (gamepad2.rightBumperWasPressed()) {
                //        x = 1;
                //    }
                //  if (x == 1) {
                //  serv.setPower(0.5);
//        } else if (x == 0) {
                //        serv.setPower(0);
                //    }
                //  if (gamepad2.leftBumperWasPressed()) {
                //  ix.setPower(0);
                //x = 0;
                //   }
                //if(gamepad2.)
                if (gamepad2.right_bumper) {
                    serv.setPosition(0);
                }
                if (gamepad2.left_bumper) {
                    serv.setPosition(1);
                }
                if (gamepad2.rightStickButtonWasPressed() &&  y ==0) {
                    y += 1;
                }
                if (y == 1) {
                    in.setPower(-0.75);
                }
                else if (y == 0) {
                    in.setPower(0);
                }
                if (gamepad2.rightStickButtonWasPressed() &&  y >=1) {
                    y = 0;
                }
                if (gamepad2.leftStickButtonWasPressed() &&  y >=1) {
                    y=0;
                }
                if (gamepad2.dpad_up) {
                    ix.setPower(0.5);
                }else{
                    ix.setPower(0);
                }
                if (gamepad2.dpad_down) {
                    ix.setPower(-0.5);
                }else{
                    ix.setPower(0);
                }

                if (gamepad2.right_trigger  !=0) {
                    ((DcMotorEx) shootl).setVelocity(505);
                    ((DcMotorEx) shootr).setVelocity(505);
                }else{
                    shootl.setPower(0);
                    shootr.setPower(0);
                }
                if (gamepad2.y) {
                    distance1 = (distance.getDistance(DistanceUnit.CM));
                    telemetry.addData("dist", distance1);
                    telemetry.update();
                    if (distance1 <= 100){
                        ((DcMotorEx) shootl).setVelocity(365);
                        ((DcMotorEx) shootr).setVelocity(365);
                    }else if (distance1 >= 121 && distance1 <= 150) {
                        ((DcMotorEx) shootl).setVelocity(2 * (distance.getDistance(DistanceUnit.CM)));
                        ((DcMotorEx) shootr).setVelocity(2 * (distance.getDistance(DistanceUnit.CM)));
                    }else if (distance1 >= 151 && distance1 <= 210) {
                        ((DcMotorEx) shootl).setVelocity(2.1 * (distance.getDistance(DistanceUnit.CM)));
                        ((DcMotorEx) shootr).setVelocity(2.1 * (distance.getDistance(DistanceUnit.CM)));
                    }else if (distance1 >= 101 && distance1 <= 120) {
                        ((DcMotorEx) shootl).setVelocity(3.6 * (distance.getDistance(DistanceUnit.CM)));
                        ((DcMotorEx) shootr).setVelocity(3.6 * (distance.getDistance(DistanceUnit.CM)));
                    }else if (distance1 >= 210) {
                        ((DcMotorEx) shootl).setVelocity(500);
                        ((DcMotorEx) shootr).setVelocity(500);
                    }
                    telemetry.update();

                }else{
                    shootl.setPower(0);
                    shootr.setPower(0);
                }
                if (gamepad2.right_stick_y !=0) {
                    ix.setPower(-gamepad2.right_stick_y);
                }else{
                    ix.setPower(0);
                }
                if (gamepad2.left_trigger !=0) {
                    shootl.setZeroPowerBehavior(BRAKE);
                    shootr.setZeroPowerBehavior(BRAKE);
                    shootl.setPower(0);
                    shootr.setPower(0);
                }else{
                    shootl.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.FLOAT);
                    shootr.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.FLOAT);

                }
//if (gamepad2.backWasPressed()){
                //  auto = 0;
                //auto1 = 0;
//    auto2 = 1;
                //  auto3 = 0;
                //auto4 = 0;
                //auto5 = 0;
//}
                if (gamepad1.backWasPressed()){
                    auto = 0;
                    auto1 = 0;
                    auto2 = 1;
                    auto3 = 0;
                    auto4 = 0;
                    auto5 = 0;
                }

//here is where we get into automatic shooting
                if (gamepad2.bWasPressed()) { //start
                    auto = 1;
                }
                if ((gamepad2.backWasPressed())){ //command if we have two balls.
                    auto3 = 1;
                    auto = 1;

                }
                if (gamepad2.xWasPressed()){ //command if we have two balls.
                    auto4 = 1;
                    auto = 1;
                }
                if (gamepad2.aWasPressed()){ //command if we have two balls.
                    auto5 = 1;
                    auto = 1;
                }



                if (auto == 1 && auto4 == 0 && auto5 == 0) { //locking the wheels, setting motor velocity
                    ((DcMotorEx) shootl).setVelocity(485);
                    ((DcMotorEx) shootr).setVelocity(465);
                    sleep(1700);
                    auto1 = 1;
                }
                if (auto == 1 && auto4 == 1 && auto5 == 0) { //locking the wheels, setting motor velocity
                    ((DcMotorEx) shootl).setVelocity(335);
                    ((DcMotorEx) shootr).setVelocity(335);
                    sleep(1600);
                    auto1 = 1;
                }
                if (auto == 1 && auto4 == 0 && auto5 == 1) { //locking the wheels, setting motor velocity
                    ((DcMotorEx) shootl).setVelocity(2550);
                    ((DcMotorEx) shootr).setVelocity(3250);
                    sleep(3000);
//    if ((shootr.getVelocity() >= 1500) && (shootl.getVelocity() >= 1500)) { // Fix: Added ()
                    auto1 = 1;
//    }
                }
// Added parentheses to .getVelocity()
                if ((auto == 1) && (auto1 == 1) && (auto3 == 0)) {
                    ix.setPower(0.75);
                    sleep(1000);        // Warning: This pauses the whole program
                    ix.setPower(0);

                    // Reset state variables
                    auto = 0;
                    auto1 = 0;
                    auto2 = 0;
                    auto4 = 0;
                    auto5 = 0;
                    serv.setPosition(1);


                }

                if (auto == 1 && auto1 == 1 && auto3 == 1) { //shooting two balls
                    ix.setPower(0.75);
                    sleep(300);
                    ix.setPower(0);
                    sleep(350);
                    ix.setPower(0.75);
                    sleep(500);
                    ix.setPower(0);
                    auto = 0;     //reset autos
                    auto1 = 0;
                    auto3 = 0;
                    auto2 = 0;
                    auto4 = 0;
                    auto5 = 0;
                    serv.setPosition(1);

                }
                if (auto2 == 1) { //braking the flywheels for two seconds after.

                    shootl.setZeroPowerBehavior(BRAKE);
                    shootr.setZeroPowerBehavior(BRAKE);
                    shootl.setPower(0);
                    shootr.setPower(0);
                    sleep(1000);
                    auto2 = 0;
                }else{
                    shootl.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.FLOAT);
                    shootr.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.FLOAT);
                }
                if (gamepad1.dpad_down) {
                    BL.setZeroPowerBehavior(BRAKE);
                    BR.setZeroPowerBehavior(BRAKE);
                    BL.setPower(0);
                    BR.setPower(0);
                    FR.setZeroPowerBehavior(BRAKE);
                    FL.setZeroPowerBehavior(BRAKE);
                    FR.setPower(0);
                    FL.setPower(0);

                }else{
                    BL.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
                    BR.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
                    FL.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
                    FR.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
                }
                //This is the start of wheelbase controls.
                if (gamepad1.a) {
                    //       if (timer.seconds() >= 0.3) {
                    //             timer.reset();

                    // FIX: Use an Array [] instead of a List
                    HuskyLens.Block[] blocks = huskyLens.blocks();

                    // Use .length for arrays instead of .size()
                    telemetry.addData("Total Blocks Found", blocks.length);

                    for (HuskyLens.Block block : blocks) {
                        // --- VERSION WITH SPECIFIC VARIABLES ---
                        int tagId = block.id;        // The ID assigned by HuskyLens
                        int posX = block.x;          // Horizontal center (0-320)
                        int posY = block.y;          // Vertical center (0-240)
                        int tagWidth = block.width;  // Width of the bounding box
                        int tagHeight = block.height;// Height of the bounding box

                        // Use these variables in your logic (e.g., driving or centering)
                        telemetry.addLine("--- Tag Data ---");
                        telemetry.addData("ID", tagId);
                        telemetry.addData("Center Pos", "X:%d, Y:%d", posX, posY);
                        telemetry.addData("Dimensions", "%dw x %dh", tagWidth, tagHeight);
                        if (posX >210){
                            BL.setPower(0.3);
                            BR.setPower(-0.3);
                            FL.setPower(0.3);
                            FR.setPower(-0.3);
                        }
                        if (posX <190){
                            BL.setPower(-0.3);
                            BR.setPower(0.3);
                            FL.setPower(-0.3);
                            FR.setPower(0.3);

                        }
                        if (posX >=190 && posX <=210){
                            // aut = 0.5(tagWidth + tagHeight);
                            if (aut >= 35){
                                shootl.setVelocity(500);
                                shootr.setVelocity(500);

                            }
                        }
                    }

                    telemetry.update();
                    // }
                }
                if (gamepad1.bWasPressed()) {
                    a = 0;
                }
                if (gamepad1.rightBumperWasPressed()) {
                    a = 0;
                }
                if (gamepad1.leftBumperWasPressed()) {
                    a = 1;
                }

                if (a == 0)   { //first drivetrain
                    if (gamepad1.right_stick_y !=0) {
                        BL.setPower(-gamepad1.right_stick_y);
                        BR.setPower(-gamepad1.right_stick_y);
                        FR.setPower(-gamepad1.right_stick_y);
                        FL.setPower(-gamepad1.right_stick_y);
                    }else{
                        BL.setPower(0);
                        BR.setPower(0);
                        FR.setPower(0);
                        FL.setPower(0);
                    }
//this makes the robot strafe left/right with the left stick left/right
                    if (gamepad1.left_stick_x !=0) {
                        BL.setPower(-gamepad1.left_stick_x);
                        BR.setPower(gamepad1.left_stick_x);
                        FR.setPower(-gamepad1.left_stick_x);
                        FL.setPower(gamepad1.left_stick_x);
                    }else{
                        BL.setPower(0);
                        BR.setPower(0);
                        FR.setPower(0);
                        FL.setPower(0);
                    }
//these triggers make the robot spin/turn (i.e. right trigger spins it clockwise)
                    if (gamepad1.left_trigger !=0) {
                        BL.setPower(-gamepad1.left_trigger);
                        BR.setPower(gamepad1.left_trigger);
                        FR.setPower(gamepad1.left_trigger);
                        FL.setPower(-gamepad1.left_trigger);
                    }else{
                        BL.setPower(0);
                        BR.setPower(0);
                        FR.setPower(0);
                        FL.setPower(0);
                    }
                    if (gamepad1.right_trigger !=0) {
                        BL.setPower(gamepad1.right_trigger);
                        BR.setPower(-gamepad1.right_trigger);
                        FR.setPower(-gamepad1.right_trigger);
                        FL.setPower(gamepad1.right_trigger);
                    }else{
                        BL.setPower(0);
                        BR.setPower(0);
                        FR.setPower(0);
                        FL.setPower(0);
                    }
                }
                if (a == 1)   { //second drivetrain
                    if (gamepad1.right_stick_y !=0) {
                        BL.setPower(gamepad1.right_stick_y);
                        BR.setPower(gamepad1.right_stick_y);
                        FR.setPower(gamepad1.right_stick_y);
                        FL.setPower(gamepad1.right_stick_y);
                    }else{
                        BL.setPower(0);
                        BR.setPower(0);
                        FR.setPower(0);
                        FL.setPower(0);
                    }
//this makes the robot strafe left/right with the left stick left/right
                    if (gamepad1.left_stick_x !=0) {
                        BL.setPower(gamepad1.left_stick_x);
                        BR.setPower(-gamepad1.left_stick_x);
                        FR.setPower(gamepad1.left_stick_x);
                        FL.setPower(-gamepad1.left_stick_x);
                    }else{
                        BL.setPower(0);
                        BR.setPower(0);
                        FR.setPower(0);
                        FL.setPower(0);
                    }
//these triggers make the robot spin/turn (i.e. right trigger spins it clockwise)
                    if (gamepad1.left_trigger !=0) {
                        BL.setPower(-gamepad1.left_trigger);
                        BR.setPower(gamepad1.left_trigger);
                        FR.setPower(gamepad1.left_trigger);
                        FL.setPower(-gamepad1.left_trigger);
                    }else{
                        BL.setPower(0);
                        BR.setPower(0);
                        FR.setPower(0);
                        FL.setPower(0);
                    }
                    if (gamepad1.right_trigger !=0) {
                        BL.setPower(gamepad1.right_trigger);
                        BR.setPower(-gamepad1.right_trigger);
                        FR.setPower(-gamepad1.right_trigger);
                        FL.setPower(gamepad1.right_trigger);
                    }else{
                        BL.setPower(0);
                        BR.setPower(0);
                        FR.setPower(0);
                        FL.setPower(0);
                    }
                }


            }
        }
    }
}









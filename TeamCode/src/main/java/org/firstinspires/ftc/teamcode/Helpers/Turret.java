package org.firstinspires.ftc.teamcode.Helpers;


import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.hardwareMap;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.hardware.dfrobot.HuskyLens;

import org.firstinspires.ftc.robotcore.external.Telemetry;


public class Turret {

    private final Telemetry telemetry;
    public enum TargetState {
        LEFT_UP,
        RIGHT_UP,
        UNKNOWN
    }
    HuskyLens huskyLens;

    public Turret(Telemetry telemetry, HardwareMap hardwareMap) {
        this.telemetry = telemetry;
        huskyLens = hardwareMap.get(HuskyLens.class, "HuskyLens");
        huskyLens.selectAlgorithm(HuskyLens.Algorithm.TAG_RECOGNITION);
        // Initialize turret and shooter hardware here
        telemetry.update();
    }
    private int getOriginalTagId(int huskyId) {
        if (huskyId >= 1 && huskyId <= 8) {
            return huskyId + 29;
        }
        return -1; // Unknown ID
    }
    private TargetState getTagSide(int originalTagId) {
        switch (originalTagId) {
            case 30:
            case 31:
            case 32:
            case 33:
                return TargetState.LEFT_UP;

            case 34:
            case 35:
            case 36:
            case 37:
                return TargetState.RIGHT_UP;

            default:
                return TargetState.UNKNOWN;
        }
    }

    public void autoAim() {
        Odometry odo = new Odometry(hardwareMap);

        double [] robotPosition = odo.evalPos();
        // Calculate and move turret here
        double thf= (robotPosition[2]);
        double xf= robotPosition[0];
        double yf= robotPosition[1];
        double yt;
        double yt1=1341.12 ;
        double yt2= 2316.48;
        if (yf>1828.8){
            yt=yt2;
        }else{
            yt=yt1;
        }
        double xt= 1463.04;
        double xr=xt-xf;
        double yr=yt-yf;
        double alpha = Math.atan2(yr,xr);
        double beta = Math.hypot(yr,xr); //Math.pow((Math.pow(yr,2)+Math.pow(xr,2)),0.5);
        double thr=alpha-thf;
        thr = Math.atan2(Math.sin(thr), Math.cos(thr));
        double thrDeg= Math.toDegrees(thr); //note: this is relative to the robots theta, not the field!!
        int leftUp = 0;
        int rightUp = 0;

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
            int originalTagId = getOriginalTagId(tagId); // my code
            TargetState side = getTagSide(originalTagId);

            telemetry.addData("HuskyLens ID", tagId);
            telemetry.addData("Original AprilTag", originalTagId);
            telemetry.addData("Tag Side", side);
            if (side ==  TargetState.LEFT_UP){
                leftUp +=1;
            } else if (side == TargetState.RIGHT_UP) {
                rightUp+=1;
            }

            // Use these variables in your logic (e.g., driving or centering)
            telemetry.addLine("--- Tag Data ---");
            telemetry.addData("ID", tagId);
            telemetry.addData("Center Pos", "X:%d, Y:%d", posX, posY);
            telemetry.addData("Dimensions", "%dw x %dh", tagWidth, tagHeight);
        }
        if (leftUp >0 && rightUp >0){
            telemetry.addLine("BOTH SIDES SEEN!");
        }
    }


    public boolean shoot() {

        // Shooting sequence here

        // Return true when the shot is finished
        return true;
    }
}
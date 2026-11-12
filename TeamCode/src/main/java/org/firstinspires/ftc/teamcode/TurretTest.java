package org.firstinspires.ftc.teamcode;


//import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.hardwareMap;

import com.qualcomm.hardware.dfrobot.HuskyLens;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.Helpers.Odometry;


public class TurretTest {
    int side=0;
    private final Telemetry telemetry;
    public enum TargetState {
        sideUp,
        sideDown,
        UNKNOWN
    }
    public enum TagSeen{
        bothSides,
        oneSide,
        none
    }
    HuskyLens huskyLens;
    Odometry odo;
    double[] target = null;
    boolean blueAlliance = false;   // default red

    public static class TagReport {
        public final boolean red, blue, tiltA, tiltB;

        public TagReport(boolean red, boolean blue, boolean tiltA, boolean tiltB) {
            this.red = red; this.blue = blue; this.tiltA = tiltA; this.tiltB = tiltB;
        }

        public TagSeen alliances() { return summarize(red, blue); }
        public TagSeen tiltSides() { return summarize(tiltA, tiltB); }

        public boolean any()       { return red || blue; }
        public boolean onlyRed()   { return red && !blue; }
        public boolean onlyBlue()  { return blue && !red; }
        public boolean onlyTiltA() { return tiltA && !tiltB; }
        public boolean onlyTiltB() { return tiltB && !tiltA; }

        private static TagSeen summarize(boolean a, boolean b) {
            if (a && b) return TagSeen.bothSides;
            if (a || b) return TagSeen.oneSide;
            return TagSeen.none;
        }
    }
    public TurretTest(Telemetry telemetry, HardwareMap hardwareMap) {
        this.telemetry = telemetry;
        huskyLens = hardwareMap.get(HuskyLens.class, "HuskyLens");
        odo = new Odometry(hardwareMap);
        // Initialize turret and shooter hardware here
        telemetry.update();
    }

    public TurretTest(Telemetry telemetry, HardwareMap hardwareMap, double[] targ) {
        this(telemetry, hardwareMap);
        this.target = targ;
        odo = new Odometry(hardwareMap);
        huskyLens.selectAlgorithm(HuskyLens.Algorithm.OBJECT_CLASSIFICATION);
    }

    public void setBlueAlliance(boolean blue) {
        this.blueAlliance = blue;
    }
    private int getOriginalTagId(int huskyId) {
        if (huskyId >= 1 && huskyId <= 8) {
            return huskyId + 29;
        }
        return -1; // Unknown ID
    }
    private TargetState getSideUpClass(){

        HuskyLens.Block[] blocks = huskyLens.blocks();
        telemetry.addData("Detected Block Count", blocks.length);
        side = 0;   // reset

        for (int i = 0; i < blocks.length; i++) {
            HuskyLens.Block block = blocks[i];

            // Print the classification parameters to telemetry
            telemetry.addLine(String.format("\nBlock [%d/%d]", i + 1, blocks.length));

            // id corresponds to the learned item number (ID1, ID2, etc.)
            telemetry.addData(" Learned ID", block.id);
            telemetry.addData(" Center X", block.x);
            telemetry.addData(" Center Y", block.y);
            telemetry.addData(" Width", block.width);
            telemetry.addData(" Height", block.height);
            side= block.id;
        }
        switch (side){
            case 1:
                return TargetState.sideUp;
            case 2:
                return TargetState.sideDown;
            default:
                return TargetState.UNKNOWN;
        }

    }
    public TagReport getTags() {
        boolean red = false, blue = false, tiltA = false, tiltB = false;

        for (HuskyLens.Block block : huskyLens.blocks()) {
            int id = block.id;
            if (id < 1 || id > 16) continue;          // ignore unknown tags

            int group = (id - 1) / 4;                 // 0=red left, 1=red right, 2=blue left, 3=blue right (red audience view)
            boolean isRedTag    = group < 2;
            boolean leftFromRed = group % 2 == 0;
            boolean isA = blueAlliance ? !leftFromRed : leftFromRed;   // tiltA = left from your alliance's view

            if (isRedTag) red = true; else blue = true;
            if (isA) tiltA = true; else tiltB = true;
        }
        return new TagReport(red, blue, tiltA, tiltB);
    }



    public void autoAim() {

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
        //double thrDeg= Math.toDegrees(thr); //note: this is relative to the robots theta, not the field!!



        TargetState state = getSideUpClass();
    }
    public double[] angleData(double[] angData ) { //given data double[] in order: encoder turret theta, encoder of hood. encoder of flywheel.
        double enTh = angData[0];
        double encWh= angData[2];
        double encHd=angData[1];
        //math here
        double[] finalData= {0,0,0};
        finalData[0]=0.0;//final (turret?) Theta in radians
        finalData[1]=0.0;//final hood angle in radians
        finalData[2]=0.0;//final expected velocity of flywheel

        return finalData; //data is returned in order: turret (theta?) angle in radians, hood angle in radians, expected velocity of flywheel.
    }
    public boolean[] cameraData(){
        boolean[] finalData = {false,false};//data is as follows: sideUp, sideDown
        //set booleans to data
        TargetState side=getSideUpClass();
        if (side == TargetState.sideUp){
            finalData[0]= true;
            finalData[1]= false;

        } else if (side== TargetState.sideDown) {
            finalData[1]=true;
            finalData[0]= false;

        }
        return finalData;

    }
    public void setHuskyLensObject(){
        huskyLens.selectAlgorithm(HuskyLens.Algorithm.OBJECT_CLASSIFICATION);
    }
    public void setHuskyLensTags(){
        huskyLens.selectAlgorithm(HuskyLens.Algorithm.TAG_RECOGNITION);

    }
    public boolean cameraDataSimple(){
        boolean finalData = false;//automatically false for side down
        //set booleans to data
        TargetState side=getSideUpClass();
        if (side == TargetState.sideUp){
           finalData=true;

        } else if (side== TargetState.sideDown) {
            finalData= false;

        }
        telemetry.addData("Side: ",side);
        return finalData;

    }


    public boolean shoot() {

        // Shooting sequence here

        // Return true when the shot is finished
        return true;
    }
}
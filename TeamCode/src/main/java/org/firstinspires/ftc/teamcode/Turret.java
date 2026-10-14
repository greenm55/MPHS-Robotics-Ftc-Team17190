package org.firstinspires.ftc.teamcode;

import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.telemetry;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.hardware.dfrobot.HuskyLens;


public class Turret {
    HuskyLens huskyLens;

    public Turret(HardwareMap hardwareMap) {
        huskyLens = hardwareMap.get(HuskyLens.class, "HuskyLens");
        huskyLens.selectAlgorithm(HuskyLens.Algorithm.TAG_RECOGNITION);
        telemetry.update();
        // Initialize turret and shooter hardware here
    }


    public void autoAim(double[] robotPosition) {
        // Calculate and move turret here
        double rotat= Math.toDegrees(robotPosition[2]);

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
        }
    }


    public boolean shoot() {

        // Shooting sequence here

        // Return true when the shot is finished
        return true;
    }
}
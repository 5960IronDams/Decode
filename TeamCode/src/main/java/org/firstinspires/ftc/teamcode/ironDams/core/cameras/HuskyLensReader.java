package org.firstinspires.ftc.teamcode.ironDams.core.cameras;

import com.qualcomm.hardware.dfrobot.HuskyLens;
import com.qualcomm.robotcore.hardware.HardwareMap;
import org.firstinspires.ftc.teamcode.ironDams.Config;

public class HuskyLensReader {
    private final HuskyLens HUSKY_LENS;

    public HuskyLensReader(HardwareMap hardwareMap) {
        HUSKY_LENS = hardwareMap.get(HuskyLens.class, Config.Hardware.Cameras.HUSKY_ID);
        HUSKY_LENS.selectAlgorithm(HuskyLens.Algorithm.TAG_RECOGNITION);
    }

    public boolean isInitialized() {
        return HUSKY_LENS.knock();
    }

    public HuskyLens.Block[] read() {
        return HUSKY_LENS.blocks();
    }

    public int getFirstId() {
        HuskyLens.Block[] blocks = read();
        if (blocks.length > 0) return blocks[0].id;

        return -1;
    }
}
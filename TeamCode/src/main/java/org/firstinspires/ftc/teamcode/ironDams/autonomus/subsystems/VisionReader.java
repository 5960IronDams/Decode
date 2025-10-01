package org.firstinspires.ftc.teamcode.ironDams.autonomus.subsystems;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;
import com.qualcomm.hardware.dfrobot.HuskyLens;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class VisionReader {

    private final HuskyLens _huskyLens;

    public VisionReader(HardwareMap hardwareMap) {
        _huskyLens = hardwareMap.get(HuskyLens.class, "huskylens");
        _huskyLens.selectAlgorithm(HuskyLens.Algorithm.TAG_RECOGNITION);
    }

    public boolean isInitialized() {
        return _huskyLens.knock();
    }

    public HuskyLens.Block[] read() {
        return _huskyLens.blocks();
    }
}

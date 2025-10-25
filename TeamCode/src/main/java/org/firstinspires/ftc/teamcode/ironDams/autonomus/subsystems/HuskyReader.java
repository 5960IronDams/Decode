package org.firstinspires.ftc.teamcode.ironDams.autonomus.subsystems;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;
import com.qualcomm.hardware.dfrobot.HuskyLens;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.decode.Constants;
import org.firstinspires.ftc.teamcode.decode.core.GreenBallPosition;

import java.util.function.BooleanSupplier;

public class HuskyReader {

    private final HuskyLens HUSKY_LENS;
    private final GreenBallPosition GREEN_BALL_POSITION;

    public HuskyReader(HardwareMap hardwareMap, GreenBallPosition greenBallPosition) {
        HUSKY_LENS = hardwareMap.get(HuskyLens.class, "huskylens");
        HUSKY_LENS.selectAlgorithm(HuskyLens.Algorithm.TAG_RECOGNITION);

        GREEN_BALL_POSITION = greenBallPosition;
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

    public Action readTag(BooleanSupplier driveComplete) {
        return new Action() {
            private boolean initialized = false;

            @Override
            public boolean run(@NonNull TelemetryPacket packet) {
                if (!initialized) {
                    initialized = true;
                }

                int huskyId = getFirstId();

                switch (huskyId) {
                    case 1:
                        GREEN_BALL_POSITION.setTargetIndex(2);
                        packet.put("Husky Target Index", GREEN_BALL_POSITION.getTargetIndex());
                        return false;
                    case 2:
                        GREEN_BALL_POSITION.setTargetIndex(0);
                        packet.put("Husky Target Index", GREEN_BALL_POSITION.getTargetIndex());
                        return false;
                    case 3:
                        GREEN_BALL_POSITION.setTargetIndex(1);
                        packet.put("Husky Target Index", GREEN_BALL_POSITION.getTargetIndex());
                        return false;
                    default: packet.put("Husky Target Index", GREEN_BALL_POSITION.getTargetIndex());
                }

                if (GREEN_BALL_POSITION.getTargetIndex() != -1) {
                    packet.put("Status Husky Read Tag", "Finished");
                    return false;
                }
                else {
                    if (driveComplete.getAsBoolean()) packet.put("Status Husky Read Tag", "Finished");
                    else packet.put("Status Husky Read Tag", "Running");
                    return !driveComplete.getAsBoolean();
                }
            }
        };
    }
}

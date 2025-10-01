package org.firstinspires.ftc.teamcode.decode.core;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;
import com.qualcomm.hardware.dfrobot.HuskyLens;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.ironDams.autonomus.subsystems.VisionReader;

public class BallVision {
    private final VisionReader _visionReader;
    private final boolean _continuous;

    public BallVision(HardwareMap hardwareMap, boolean continuous) {
        _visionReader = new VisionReader(hardwareMap);
        _continuous = continuous;
    }

    public Action readTagAction() {
        return new Action() {
            private boolean initialized = false;

            @Override
            public boolean run(@NonNull TelemetryPacket packet) {
                if (!initialized) {
                    initialized = _visionReader.isInitialized();
                }

                HuskyLens.Block[] blocks = _visionReader.read();

                boolean detected = blocks != null && blocks.length > 0;

                String blockDetection = "";
                if (detected) {
                    for (int i = 0; i < blocks.length; i++) {
                        blockDetection += blocks[i].id + ", ";
                        /*
                         * Here inside the FOR loop, you could save or evaluate specific info for the currently recognized Bounding Box:
                         * - blocks[i].width and blocks[i].height   (size of box, in pixels)
                         * - blocks[i].left and blocks[i].top       (edges of box)
                         * - blocks[i].x and blocks[i].y            (center location)
                         * - blocks[i].id                           (Color ID)
                         *
                         * These values have Java type int (integer).
                         */
                    }
                }

                packet.put("blocks", blockDetection);

                return !detected || _continuous;
            }
        };
    }
}

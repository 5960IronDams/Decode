package org.firstinspires.ftc.teamcode.ironDams.autonomus.subsystems;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;
import com.qualcomm.hardware.dfrobot.HuskyLens;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;

import java.util.List;

public class BallVisionReader {
    private final HardwareMap _hardwareMap;
    private final Telemetry _telemetry;
    private final boolean _continuous;

    private final HuskyLens _huskyLens;

    public BallVisionReader(LinearOpMode opMode, boolean continuous) {
        _continuous = continuous;
        _hardwareMap = opMode.hardwareMap;
        _telemetry = opMode.telemetry;

        _huskyLens = _hardwareMap.get(HuskyLens.class, "huskylens");
        _huskyLens.selectAlgorithm(HuskyLens.Algorithm.TAG_RECOGNITION);
    }

    public boolean isInitialized() {
        return _huskyLens.knock();
    }

    public HuskyLens.Block[] read() {
        return _huskyLens.blocks();
    }

    public Action readTagAction() {
        return new Action() {
            private boolean initialized = false;

            @Override
            public boolean run(@NonNull TelemetryPacket packet) {
                if (!initialized) {
                    initialized = isInitialized();
                }

                HuskyLens.Block[] blocks = read();

                boolean detected = blocks != null && blocks.length > 0;

                if (detected) {
                    for (int i = 0; i < blocks.length; i++) {
                        _telemetry.addData("Block_" + i, blocks[i].id);
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

                packet.put("blocks", blocks);

                return !detected || _continuous;
            }
        };
    }
}

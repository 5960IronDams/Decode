package org.firstinspires.ftc.teamcode.ironDams.Test;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.decode.core.ColorVision;

public class ColorVisionTest {

    private final ColorVision COLOR_VISION;

    public ColorVisionTest(LinearOpMode opMode) {
        COLOR_VISION = new ColorVision(opMode);
    }

    public Action runTest() {
        return new Action() {
            private boolean initialized = false;

            @Override
            public boolean run(@NonNull TelemetryPacket packet) {
                if (!initialized) {
                    initialized = true;
                }

                COLOR_VISION.update();

                packet.put("CV Red", COLOR_VISION.getRed());
                packet.put("CV Green", COLOR_VISION.getGreen());
                packet.put("CV Blue", COLOR_VISION.getBlue());
                packet.put("CV HasStateChange", COLOR_VISION.hasStateChange());
                packet.put("CV HasBall", COLOR_VISION.hasBall());
                packet.put("CV Pattern", COLOR_VISION.getColorCode());

                return true;
            }
        };
    }
}

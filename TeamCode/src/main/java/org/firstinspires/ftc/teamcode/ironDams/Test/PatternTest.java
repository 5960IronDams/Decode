package org.firstinspires.ftc.teamcode.ironDams.Test;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.decode.Pattern;

public class PatternTest {
    private final Pattern PATTERN;
    private final LinearOpMode OP_MODE;

    public PatternTest(LinearOpMode opMode) {
        OP_MODE = opMode;
        PATTERN = new Pattern(opMode);
    }

    public Action runTest() {
        return new Action() {
            private boolean initialized = false;

            @Override
            public boolean run(@NonNull TelemetryPacket packet) {
                if (!initialized) {
                    initialized = true;
                }

                packet.put("Pattern Target", PATTERN.setTargetPattern().getTarget());

                return true;
            }
        };
    }
}

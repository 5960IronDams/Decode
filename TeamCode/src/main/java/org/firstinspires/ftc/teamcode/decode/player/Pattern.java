package org.firstinspires.ftc.teamcode.decode.player;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.Gamepad;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.decode.SharedData;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BooleanSupplier;

public class Pattern {
    private final Telemetry TELEMETRY;
    private final Gamepad GAMEPAD2;
    private final SharedData DATA;

    public Pattern(LinearOpMode opMode, SharedData data) {
        TELEMETRY = opMode.telemetry;
        GAMEPAD2 = opMode.gamepad2;
        DATA = data;
    }

    public int changePattern() {
        if (GAMEPAD2.a) {
            DATA.setGreenBallTargetIndex(1);
            DATA.setHasPatternChanged(true);
        } else if (GAMEPAD2.b) {
            DATA.setGreenBallTargetIndex(2);
            DATA.setHasPatternChanged(true);
        } else if (GAMEPAD2.x) {
            DATA.setGreenBallTargetIndex(0);
            DATA.setHasPatternChanged(true);
        }

        int index = DATA.getGreenBallTargetIndex();
        DATA.setTargetPattern(index == 0 ? "GPP" : index == 1 ? "PGP" : index == 2 ? "PPG": "");

        return index;
    }

    public Action changeAction() {
        return new Action() {
            private boolean initialized = false;

            @Override
            public boolean run(@NonNull TelemetryPacket packet) {
                if (!initialized) {
                    initialized = true;
                }

                int index = changePattern();
                packet.put("Pattern", index == 0 ? "GPP" : index == 1 ? "PGP" : index == 2 ? "PPG": "");
                packet.put("Pattern 2A", GAMEPAD2.a);
                packet.put("Pattern 2B", GAMEPAD2.b);
                packet.put("Pattern 2X", GAMEPAD2.x);

                return true;
            }
        };
    }
}

package org.firstinspires.ftc.teamcode.decode.teleOp;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.Gamepad;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.decode.core.GreenBallPosition;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BooleanSupplier;

public class PlayerPattern {
    private final Telemetry TELEMETRY;
    private final Gamepad GAME_PAD_2;
    private final GreenBallPosition GREEN_BALL_POSITION;

    private final AtomicBoolean hasPatternChanged = new AtomicBoolean(false);

    public void setHasPatternChanged(boolean patternChanged) {
        hasPatternChanged.set(patternChanged);
    }

    public BooleanSupplier getHasPatternChanged() {
        return hasPatternChanged::get;
    }

    public PlayerPattern(LinearOpMode opMode, GreenBallPosition greenBallPosition) {
        TELEMETRY = opMode.telemetry;
        GAME_PAD_2 = opMode.gamepad2;
        GREEN_BALL_POSITION = greenBallPosition;
    }


    private int changePattern() {
        if (GAME_PAD_2.a) {
            GREEN_BALL_POSITION.setTargetIndex(1);
            setHasPatternChanged(true);
        } else if (GAME_PAD_2.b) {
            GREEN_BALL_POSITION.setTargetIndex(2);
            setHasPatternChanged(true);
        } else if (GAME_PAD_2.x) {
            GREEN_BALL_POSITION.setTargetIndex(0);
            setHasPatternChanged(true);
        }

        int index = GREEN_BALL_POSITION.getTargetIndex();

        TELEMETRY.addData("Pattern", index == 0 ? "GPP" : index == 1 ? "PGP" : index == 2 ? "PPG": "");

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
                packet.put("Pattern 2A", GAME_PAD_2.a);
                packet.put("Pattern 2B", GAME_PAD_2.b);
                packet.put("Pattern 2X", GAME_PAD_2.x);

                return true;
            }
        };
    }
}
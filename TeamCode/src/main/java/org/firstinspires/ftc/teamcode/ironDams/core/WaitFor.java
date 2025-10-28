package org.firstinspires.ftc.teamcode.ironDams.core;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;
import com.qualcomm.robotcore.util.ElapsedTime;

public class WaitFor {
    private final ElapsedTime TIMER = new ElapsedTime();
    private final long MILLIS;

    public WaitFor(long millis) {
        MILLIS = millis;
    }

    public boolean allowExec() {
        if (TIMER.milliseconds() >= MILLIS) {
            TIMER.reset();
            return true;
        }

        return false;
    }

    public void reset() {
        TIMER.reset();
    }

    public Action testAction() {
        return new Action() {
            private boolean initialized = false;

            @Override
            public boolean run(@NonNull TelemetryPacket packet) {
                if (!initialized) {
                    initialized = true;
                }

                packet.put("Timer", TIMER.milliseconds());
                packet.put("Delay", MILLIS);

                if (allowExec()) {
                    packet.put("Wait For", true);
                } else {
                    packet.put("Wait For", false);
                }

                return true;
            }
        };
    }
}
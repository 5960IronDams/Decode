package org.firstinspires.ftc.teamcode.ironDams.core;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

public class WaitFor {
    private final ElapsedTime _elapsedTime = new ElapsedTime();
    private final long _waitMs;

    private final LinearOpMode _opMode;

    public WaitFor(long waitMs) {
        _waitMs = waitMs;
        _opMode = null;
    }

    public WaitFor(long waitMs, LinearOpMode opMode) {
        _waitMs = waitMs;
        _opMode = opMode;
    }

    public boolean allowExec() {
        if (_elapsedTime.milliseconds() >= _waitMs) {
            _elapsedTime.reset();
            return true;
        }

        return false;
    }

    public void reset() {
        _elapsedTime.reset();
    }

    public Action testAction() {

        return new Action() {
            private boolean initialized = false;

            @Override
            public boolean run(@NonNull TelemetryPacket packet) {
                if (!initialized) {
                    initialized = true;
                }

                packet.put("Timer", _elapsedTime.milliseconds());
                if (_opMode != null) {
                    if (allowExec()) {
                        packet.put("Wait For", true);
                    } else {
                        packet.put("Wait For", false);
                    }
                }

                return true;
            }
        };
    }
 }

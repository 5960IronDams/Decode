package org.firstinspires.ftc.teamcode.decode.core;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class Spindexer {

    private final CRServo _spindexer;

    public Spindexer(HardwareMap hardwareMap) {
        _spindexer = hardwareMap.get(CRServo.class, "spindex");
        closeLauncher();
    }

    public void openLauncher() {
        double openPos = 1.0;
    }

    public void closeLauncher() {
        double closedPos = 0.0;
    }

    public void setPower() {
        setPower(1.0);
    }

    public void setPower(double power) {
        _spindexer.setPower(power);
    }

    public void stop() {
        _spindexer.setPower(0);
    }

    public Action manageSpindexer() {
        return new Action() {
            private boolean initialized = false;

            @Override
            public boolean run(@NonNull TelemetryPacket packet) {
                if (!initialized) {
                    initialized = true;
                }

                setPower(0.10);

                return true;
            }
        };
    }
}

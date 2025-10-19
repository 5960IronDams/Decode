package org.firstinspires.ftc.teamcode.ironDams.Test;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.ironDams.core.WaitFor;

public class WaitForTest {

    private final WaitFor WAIT_FOR;
    private final long DELAY = 3000;

    private int _counter;

    public WaitForTest(LinearOpMode opMode) {
        WAIT_FOR = new WaitFor(DELAY, opMode);
    }

    public Action runTest() {
        return new Action() {
            private boolean initialized = false;

            @Override
            public boolean run(@NonNull TelemetryPacket packet) {
                if (!initialized) {
                    initialized = true;
                }

                if (WAIT_FOR.allowExec()) {
                    _counter++;
                }

                packet.put("Wait Delay (ms)", DELAY);
                packet.put("Wait Counter", _counter);

                return true;
            }
        };
    }
}

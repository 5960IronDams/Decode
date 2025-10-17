package org.firstinspires.ftc.teamcode.ironDams.core;

import com.qualcomm.robotcore.util.ElapsedTime;

public class WaitFor {
    private final ElapsedTime _elapsedTime = new ElapsedTime();
    private final long _waitMs;

    public WaitFor(long waitMs) {
        _waitMs = waitMs;
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
 }

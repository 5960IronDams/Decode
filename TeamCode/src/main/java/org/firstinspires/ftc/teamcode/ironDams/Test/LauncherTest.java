package org.firstinspires.ftc.teamcode.ironDams.Test;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.sun.tools.javac.code.Attribute;

import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;
import org.firstinspires.ftc.teamcode.decode.Constants;
import org.firstinspires.ftc.teamcode.decode.core.Launcher;
import org.firstinspires.ftc.teamcode.ironDams.core.WaitFor;

public class LauncherTest {
    private final LinearOpMode _opMode;
    private final Launcher _launcher;

    private boolean _startTracking;

    private final WaitFor _sleep;

    private double _leftCurrent;
    private double _minLeftCurrent;
    private double _maxLeftCurrent;

    private double _rightCurrent;
    private double _minRightCurrent;
    private double _maxRightCurrent;

    public LauncherTest(LinearOpMode opMode) {
        _opMode = opMode;
        _sleep = new WaitFor(Constants.WAIT_DURATION_MS, opMode);
        _launcher = new Launcher(opMode);
    }

    public Action runTest() {
        return new Action() {
            private boolean initialized = false;

            @Override
            public boolean run(@NonNull TelemetryPacket packet) {
                if (!initialized) {
                    initialized = true;
                }

                _leftCurrent = _launcher.getLeftCurrent(CurrentUnit.MILLIAMPS);
                _rightCurrent = _launcher.getRightCurrent(CurrentUnit.MILLIAMPS);

                if (_opMode.gamepad2.dpad_down && _sleep.allowExec()) {
                    if (_launcher.getPower() == 0) {
                        _launcher.open().setPower();
                    } else {
                        _launcher.close().stop();
                    }

                    if (_leftCurrent > 550 && _rightCurrent > 700) _startTracking = true;

                    if (_startTracking) {
                        if (_leftCurrent < _minLeftCurrent || _minLeftCurrent == 0)
                            _minLeftCurrent = _leftCurrent;
                        if (_rightCurrent < _minRightCurrent || _minRightCurrent == 0)
                            _minRightCurrent = _rightCurrent;
                        if (_leftCurrent > _maxLeftCurrent) _maxLeftCurrent = _leftCurrent;
                        if (_rightCurrent > _maxRightCurrent) _maxRightCurrent = _rightCurrent;
                    }

                    _opMode.telemetry.addData("Launcher (2a)", _launcher.getPower() > 0 ? "Running" : "Stop");
                }

                packet.put("Launcher RC", _rightCurrent);
                packet.put("Launcher LC", _leftCurrent);
                packet.put("Launcher RCMax", _maxRightCurrent);
                packet.put("Launcher LCMax", _maxLeftCurrent);
                packet.put("Launcher RCMin", _minRightCurrent);
                packet.put("Launcher LCMin", _minLeftCurrent);
                packet.put("Launcher In Range", _launcher.isInRange());

                return true;
            }
        };
    }
}

package org.firstinspires.ftc.teamcode.ironDams.Test;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;
import org.firstinspires.ftc.teamcode.decode.Constants;
import org.firstinspires.ftc.teamcode.decode.core.Shooter;
import org.firstinspires.ftc.teamcode.ironDams.core.WaitFor;

public class ShooterTest {
    private final Shooter SHOOTER;
    private final WaitFor USER_DELAY = new WaitFor(Constants.WAIT_DURATION_MS);
    private final WaitFor TRACKING_DELAY = new WaitFor(5000);
    private final LinearOpMode OP_MODE;


    private double _maxTrueLeft = 0;
    private double _maxTrueRight = 0;
    private double _maxLeftCurrent = 0;
    private double _maxRightCurrent = 0;
    private double _minLeftCurrent = 10000;
    private double _minRightCurrent = 10000;

    public ShooterTest(LinearOpMode opMode) {
        OP_MODE = opMode;
        SHOOTER = new Shooter(opMode);
    }

    public Action runTest() {
        return new Action() {
            private boolean initialized = false;

            @Override
            public boolean run(@NonNull TelemetryPacket packet) {
                if (!initialized) {
                    initialized = true;
                }

                if (OP_MODE.gamepad2.dpad_down && USER_DELAY.allowExec()) {
                    if (SHOOTER.getPower() == 0) {
                        SHOOTER.open().setPower(Constants.Shooter.MAX_POWER);
                        TRACKING_DELAY.reset();
                    } else {
                        SHOOTER.close().stop();
                        _maxLeftCurrent = 0;
                        _maxRightCurrent = 0;
                        _minLeftCurrent = 10000;
                        _minRightCurrent = 10000;
                    }
                } else if (OP_MODE.gamepad2.dpad_left && USER_DELAY.allowExec()) {
                    SHOOTER.setPower(SHOOTER.getPower() + 0.01);

                } else if (OP_MODE.gamepad2.dpad_right && USER_DELAY.allowExec()) {
                    SHOOTER.setPower(SHOOTER.getPower() - 0.01);

                }

                double leftCurrent = SHOOTER.getLeftCurrent(CurrentUnit.MILLIAMPS);
                double rightCurrent = SHOOTER.getRightCurrent(CurrentUnit.MILLIAMPS);

                if (SHOOTER.getPower() > 0 && TRACKING_DELAY.allowExec()) {
                    if (leftCurrent > _maxLeftCurrent) _maxLeftCurrent = leftCurrent;
                    if (rightCurrent > _maxRightCurrent) _maxRightCurrent = rightCurrent;
                    if (leftCurrent < _minLeftCurrent) _minLeftCurrent = leftCurrent;
                    if (rightCurrent < _minRightCurrent) _minRightCurrent = rightCurrent;
                }

                if (leftCurrent > _maxTrueLeft) _maxTrueLeft = leftCurrent;
                if (rightCurrent > _maxTrueRight) _maxTrueRight = rightCurrent;

                packet.put("Power", SHOOTER.getPower());
                packet.put("Act LC", leftCurrent);
                packet.put("Act RC", rightCurrent);
                packet.put("Max LC", _maxLeftCurrent);
                packet.put("Min LC", _minLeftCurrent);
                packet.put("Max RC", _maxRightCurrent);
                packet.put("Min RC", _minRightCurrent);

                packet.put("Max TR", _maxTrueRight);
                packet.put("Max TL", _maxTrueLeft);

                packet.put("Is In Range", SHOOTER.isInRange());

                return true;
            }
        };
    }
}

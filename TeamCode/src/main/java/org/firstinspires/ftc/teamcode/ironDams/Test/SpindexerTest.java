package org.firstinspires.ftc.teamcode.ironDams.Test;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.decode.Constants;
import org.firstinspires.ftc.teamcode.decode.Pattern;
import org.firstinspires.ftc.teamcode.decode.core.ColorVision;
import org.firstinspires.ftc.teamcode.decode.core.Intake;
import org.firstinspires.ftc.teamcode.decode.core.Launcher;
import org.firstinspires.ftc.teamcode.decode.core.Spindexer;
import org.firstinspires.ftc.teamcode.ironDams.core.WaitFor;

public class SpindexerTest {

    private final LinearOpMode OP_MODE;
    private final WaitFor USER_DELAY;

    private final Spindexer SPINDEXER;

    private int _positionIndex = 0;
    private final double POS_OFFSET = 0.001;

    public SpindexerTest(LinearOpMode opMode) {
        OP_MODE = opMode;
        USER_DELAY = new WaitFor(Constants.WAIT_DURATION_MS, opMode);
        SPINDEXER = new Spindexer(opMode,
                new Intake(opMode),
                new ColorVision(opMode),
                new Launcher(opMode),
                new Pattern(opMode));
    }

    public Action runTest() {
        return new Action() {
            private boolean initialized = false;

            @Override
            public boolean run(@NonNull TelemetryPacket packet) {
                if (!initialized) {
                    initialized = true;
                }

                if (OP_MODE.gamepad1.x && USER_DELAY.allowExec()) {
                    if (_positionIndex < Constants.Spindexer.Positions.length - 1) _positionIndex++;
                    else _positionIndex = 0;
                    SPINDEXER.changePosition(Constants.Spindexer.Positions[_positionIndex]);
                }
                else if (OP_MODE.gamepad1.b && USER_DELAY.allowExec()) {
                    if (_positionIndex == 0) _positionIndex = Constants.Spindexer.Positions.length - 1;
                    else _positionIndex--;
                    SPINDEXER.changePosition(Constants.Spindexer.Positions[_positionIndex]);
                }
                else if (OP_MODE.gamepad1.y && USER_DELAY.allowExec()) {
                    SPINDEXER.changePosition(SPINDEXER.getPosition() + POS_OFFSET);
                } else if (OP_MODE.gamepad1.a && USER_DELAY.allowExec()) {
                    SPINDEXER.changePosition(SPINDEXER.getPosition() - POS_OFFSET);
                }

                packet.put("Spindex Pos Index", _positionIndex);
                packet.put("Spndex Position Count", Constants.Spindexer.Positions.length);
                packet.put("Spindex Pos", SPINDEXER.getPosition());

                return true;
            }
        };
    }
}

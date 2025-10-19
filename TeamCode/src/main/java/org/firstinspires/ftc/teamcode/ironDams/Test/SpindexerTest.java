package org.firstinspires.ftc.teamcode.ironDams.Test;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.decode.Constants;
import org.firstinspires.ftc.teamcode.decode.core.Pattern;
import org.firstinspires.ftc.teamcode.decode.core.ColorVision;
import org.firstinspires.ftc.teamcode.decode.core.Intake;
import org.firstinspires.ftc.teamcode.decode.core.Shooter;
import org.firstinspires.ftc.teamcode.decode.core.Spindexer;
import org.firstinspires.ftc.teamcode.ironDams.core.WaitFor;

public class SpindexerTest {

    private final LinearOpMode OP_MODE;
    private final WaitFor USER_BTN_DELAY;

    private final Spindexer SPINDEXER;
    private final Intake INTAKE;
    private final ColorVision COLOR_VISION;
    private final Shooter SHOOTER;
    private final Pattern PATTERN;

    private int _positionIndex = 0;
    private final double POS_OFFSET = 0.001;

    /**
     * 0: Position, 1: Intake
     */
    private int _testMode = 0;

    public SpindexerTest(LinearOpMode opMode) {
        OP_MODE = opMode;
        USER_BTN_DELAY = new WaitFor(Constants.WAIT_DURATION_MS, opMode);
        INTAKE = new Intake(opMode);
        COLOR_VISION = new ColorVision(opMode);
        SHOOTER = new Shooter(opMode);
        PATTERN = new Pattern(opMode);
        SPINDEXER = new Spindexer(opMode,
                INTAKE,
                COLOR_VISION,
                SHOOTER,
                PATTERN);
    }

    public Action runTest() {
        return new Action() {
            private boolean initialized = false;

            @Override
            public boolean run(@NonNull TelemetryPacket packet) {
                if (!initialized) {
                    initialized = true;
                }

                if (OP_MODE.gamepad1.dpad_down && USER_BTN_DELAY.allowExec()) {
                    if (_testMode > 0) _testMode--;
                } else if (OP_MODE.gamepad1.dpad_up && USER_BTN_DELAY.allowExec()) {
                    _testMode++;
                }

                packet.put("Test Mode", _testMode);

                if (_testMode == 0) {
                    if (OP_MODE.gamepad1.x && USER_BTN_DELAY.allowExec()) {
                        if (_positionIndex < Constants.Spindexer.Positions.length - 1)
                            _positionIndex++;
                        else _positionIndex = 0;
                        SPINDEXER.changePosition(Constants.Spindexer.Positions[_positionIndex]);
                    } else if (OP_MODE.gamepad1.b && USER_BTN_DELAY.allowExec()) {
                        if (_positionIndex == 0)
                            _positionIndex = Constants.Spindexer.Positions.length - 1;
                        else _positionIndex--;
                        SPINDEXER.changePosition(Constants.Spindexer.Positions[_positionIndex]);
                    } else if (OP_MODE.gamepad1.y && USER_BTN_DELAY.allowExec()) {
                        SPINDEXER.changePosition(SPINDEXER.getPosition() + POS_OFFSET);
                    } else if (OP_MODE.gamepad1.a && USER_BTN_DELAY.allowExec()) {
                        SPINDEXER.changePosition(SPINDEXER.getPosition() - POS_OFFSET);
                    }

                    packet.put("Spindex Pos Index", _positionIndex);
                    packet.put("Spindex Position Count", Constants.Spindexer.Positions.length);
                    packet.put("Spindex Pos", SPINDEXER.getPosition());
                } else if (_testMode == 1) {
                    SPINDEXER
                            .runIntakeMode(true)
                            .runSortMode(true)
                            .runShootMode();

                    int actualPos = PATTERN.getGreenActualPos();
                    int targetPos = PATTERN.getGreenTargetPos();

                    packet.put("Spindex Actual Pos", actualPos);
                    packet.put("Spindex Actual Pattern", PATTERN.getActual());
                    packet.put("Spindex Actual Pattern[]", PATTERN.getActualPattern()[0] + ", " + PATTERN.getActualPattern()[1] + ", " + PATTERN.getActualPattern()[2]);
                    packet.put("Spindex Target Pos", targetPos);
                    packet.put("Spindex Target Pattern", PATTERN.setTargetPattern().getTarget());

                    packet.put("Spindex Mode", SPINDEXER.getMode());


                    packet.put("CV B", COLOR_VISION.getBlue());
                    packet.put("CV G", COLOR_VISION.getGreen());
                    packet.put("CV R", COLOR_VISION.getRed());

                    if (actualPos != -1 && targetPos != -1) {
                        int distance = actualPos - targetPos;
                        packet.put("Spindex Distance", distance);
                        packet.put("Spindex Pos Movement", distance * 2);
                    }

                } else {
                    COLOR_VISION.update();
                    SPINDEXER.updatePattern(0);
                    packet.put("Spindex Actual Pattern", PATTERN.getActual());
                }

                return true;
            }
        };
    }
}

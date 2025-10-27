package org.firstinspires.ftc.teamcode.decode.teleOp;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.decode.Constants;
import org.firstinspires.ftc.teamcode.decode.core.GreenBallPosition;
import org.firstinspires.ftc.teamcode.ironDams.core.WaitFor;

import java.util.function.BooleanSupplier;

public class PlayerSpindexer {

    private final LinearOpMode OP_MODE;
    private final Gamepad GAME_PAD_2;
    private final Servo SERVO;

    private final GreenBallPosition GREEN_BALL_POSITION;
    private final WaitFor INDEX_MOVE_DELAY = new WaitFor(1500);
    private final WaitFor SHOOT_MOVE_DELAY = new WaitFor(1500);
//    private final WaitFor PATTERN_SORT_DELAY = new WaitFor(500);

    private Constants.Spindexer.Mode _mode = Constants.Spindexer.Mode.INDEX;

    public PlayerSpindexer(LinearOpMode opMode, GreenBallPosition greenBallPosition) {
        OP_MODE = opMode;
        GAME_PAD_2 = opMode.gamepad2;
        SERVO = opMode.hardwareMap.get(Servo.class, Constants.Spindexer.SPINDEXER_ID);
        GREEN_BALL_POSITION = greenBallPosition;

        SERVO.setPosition(Constants.Spindexer.Positions[0]);
    }

    public Action indexBalls() {
        return new Action() {
            private boolean initialized = false;

            @Override
            public boolean run(@NonNull TelemetryPacket packet) {
                if (!initialized) {
                    initialized = true;
                }

                if (_mode == Constants.Spindexer.Mode.INDEX && !GREEN_BALL_POSITION.isLoaded() && GREEN_BALL_POSITION.getMoveSpindexer()) {
                    packet.put("Spindexer Status", "Indexing");
                    SERVO.setPosition(Constants.Spindexer.Positions[GREEN_BALL_POSITION.getSpindexerCurrentPos()]);

                    if (INDEX_MOVE_DELAY.allowExec()) {
                        GREEN_BALL_POSITION.setMoveSpindexer(false);
                    }
                }
                else {
                    if (GREEN_BALL_POSITION.isLoaded()) _mode = Constants.Spindexer.Mode.SORT;
                    INDEX_MOVE_DELAY.reset();
                }

                packet.put("Spindexer Actual Pattern", String.join(", ", GREEN_BALL_POSITION.getActualPattern()));
                packet.put("Spindexer Actual Index", GREEN_BALL_POSITION.getActualIndex());
                packet.put("Spindexer Detected", GREEN_BALL_POSITION.getColorDetected());
                packet.put("Spindexer IsSpinning", GREEN_BALL_POSITION.getMoveSpindexer());
                packet.put("Color IsLoaded", GREEN_BALL_POSITION.isLoaded());
                packet.put("Spindexer Mode", _mode);
                return true;
            }
        };
    }

    public Action sortBalls(BooleanSupplier hasPatternChanged) {
        return new Action() {
            private boolean initialized = false;

            @Override
            public boolean run(@NonNull TelemetryPacket packet) {
                if (!initialized) {
                    initialized = true;
                }

                if (_mode != Constants.Spindexer.Mode.SORT || !GREEN_BALL_POSITION.isLoaded() ||
                    GREEN_BALL_POSITION.getTargetIndex() == -1 ||
                    GREEN_BALL_POSITION.getActualIndex() == -1) {
                    packet.put("Spindexer Status", "Waiting");
                    packet.put("Spindexer Mode", _mode);
                    return true;
                } else if (GREEN_BALL_POSITION.getTargetIndex() == GREEN_BALL_POSITION.getActualIndex()) {
                    packet.put("Spindexer Status", "Waiting");
                    packet.put("Spindexer Mode", _mode);
                    return true;
                }

                if (hasPatternChanged.getAsBoolean()) {
                    packet.put("Spindexer Status", "Sorting");
                    int distance = GREEN_BALL_POSITION.getActualIndex() - GREEN_BALL_POSITION.getTargetIndex();
                    GREEN_BALL_POSITION.setSpindexerCurrentPos(GREEN_BALL_POSITION.getSpindexerCurrentPos() + distance * 2);
                    SERVO.setPosition(Constants.Spindexer.Positions[GREEN_BALL_POSITION.getSpindexerCurrentPos()]);
                    GREEN_BALL_POSITION.setActualIndex(GREEN_BALL_POSITION.getTargetIndex());
                }

                packet.put("Spindexer Mode", _mode);

                return true;
            }
        };
    }

    public Action shootBalls(BooleanSupplier shootComplete) {
        return new Action() {
            private boolean initialized = false;

            @Override
            public boolean run(@NonNull TelemetryPacket packet) {
                if (!initialized) {
                    initialized = true;
                }

                /*
                 * The controls are also listened to in PlayerShooter
                 */
                if (GAME_PAD_2.dpad_down) {
                    _mode = Constants.Spindexer.Mode.SHOOT;
                } else if (GAME_PAD_2.dpad_left) {
                    _mode = GREEN_BALL_POSITION.isLoaded() ? Constants.Spindexer.Mode.SORT : Constants.Spindexer.Mode.INDEX;
                }

                if (_mode != Constants.Spindexer.Mode.SHOOT || !GREEN_BALL_POSITION.getMoveSpindexer()) {
                    SHOOT_MOVE_DELAY.reset();
                    packet.put("Spindexer Status", "Waiting");
                } else {
                    packet.put("Spindexer Status", "Shooting");
                    SERVO.setPosition(Constants.Spindexer.Positions[GREEN_BALL_POSITION.getSpindexerCurrentPos()]);

                    if (SHOOT_MOVE_DELAY.allowExec()) {
                        GREEN_BALL_POSITION.setMoveSpindexer(false);
                    }
                }

                packet.put("Spindexer CP", GREEN_BALL_POSITION.getSpindexerCurrentPos());
                packet.put("Spindexer DP", GREEN_BALL_POSITION.getSpindexerDetectionPos());
                packet.put("Spindexer Mode", _mode);
                return true;
            }
        };
    }
}

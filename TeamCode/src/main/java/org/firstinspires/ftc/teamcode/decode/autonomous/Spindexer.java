package org.firstinspires.ftc.teamcode.decode.autonomous;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.decode.Constants;
import org.firstinspires.ftc.teamcode.decode.core.GreenBallPosition;
import org.firstinspires.ftc.teamcode.ironDams.core.WaitFor;

import java.util.function.BooleanSupplier;

public class Spindexer {

    private final LinearOpMode OP_MODE;
    private final Servo SERVO;

    private final GreenBallPosition GREEN_BALL_POSITION;
    private final WaitFor MOVE_DELAY = new WaitFor(1500);
    private final WaitFor PATTERN_SORT_DELAY = new WaitFor(500);

    private Constants.Spindexer.Mode _mode = Constants.Spindexer.Mode.INDEX;

    public Spindexer(LinearOpMode opMode, GreenBallPosition greenBallPosition) {
        OP_MODE = opMode;
        SERVO = opMode.hardwareMap.get(Servo.class, Constants.Spindexer.SPINDEXER_ID);
        GREEN_BALL_POSITION = greenBallPosition;

        SERVO.setPosition(Constants.Spindexer.Positions[0]);
    }

    public Action indexBalls(BooleanSupplier driveComplete) {
        return new Action() {
            private boolean initialized = false;

            @Override
            public boolean run(@NonNull TelemetryPacket packet) {
                if (!initialized) {
                    initialized = true;
                }

                packet.put("Spindexer Actual Pattern", String.join(", ", GREEN_BALL_POSITION.getActualPattern()));
                packet.put("Spindexer Actual Index", GREEN_BALL_POSITION.getActualIndex());
                packet.put("Spindexer Detected", GREEN_BALL_POSITION.getColorDetected());
                packet.put("Spindexer IsSpinning", GREEN_BALL_POSITION.getMoveSpindexer());
                packet.put("Color IsLoaded", GREEN_BALL_POSITION.isLoaded());

                if (!GREEN_BALL_POSITION.getMoveSpindexer()) {
                    MOVE_DELAY.reset();
                    if (GREEN_BALL_POSITION.isLoaded()) {
                        packet.put("Status Spindex Index Balls", "Finished");
                        SERVO.setPosition(Constants.Spindexer.Positions[0]);
                        return false;
                    } else {
                        if (driveComplete.getAsBoolean()) packet.put("Status Spindex Sort Balls", "Finished");
                        else packet.put("Status Spindex Index Balls", "Running");
                        return !driveComplete.getAsBoolean();
                    }
                }

                SERVO.setPosition(Constants.Spindexer.Positions[GREEN_BALL_POSITION.getSpindexerCurrentPos()]);

                if (MOVE_DELAY.allowExec()) {
                    GREEN_BALL_POSITION.setMoveSpindexer(false);
                }

                if (GREEN_BALL_POSITION.isLoaded()) {
                    packet.put("Status Spindex Index Balls", "Finished");
                    SERVO.setPosition(Constants.Spindexer.Positions[0]);
                    return false;
                }
                else {
                    if (driveComplete.getAsBoolean()) packet.put("Status Spindex Index Balls", "Finished");
                    else packet.put("Status Spindex Index Balls", "Running");
                    return !driveComplete.getAsBoolean();
                }
            }
        };
    }

    public Action sortBalls(BooleanSupplier driveComplete) {
        return new Action() {
            private boolean initialized = false;

            @Override
            public boolean run(@NonNull TelemetryPacket packet) {
                if (!initialized) {
                    initialized = true;
                }

                if (!GREEN_BALL_POSITION.isLoaded() ||
                    GREEN_BALL_POSITION.getTargetIndex() == -1 ||
                    GREEN_BALL_POSITION.getActualIndex() == -1) {
                    if (driveComplete.getAsBoolean()) packet.put("Status Spindex Sort Balls", "Finished");
                    else packet.put("Status Spindex Sort Balls", "Running");
                    return !driveComplete.getAsBoolean();
                } else if (GREEN_BALL_POSITION.getTargetIndex() == GREEN_BALL_POSITION.getActualIndex()) {
                    packet.put("Status Spindex Sort Balls", "Finished");
                    return false;
                }

                int distance = GREEN_BALL_POSITION.getActualIndex() - GREEN_BALL_POSITION.getTargetIndex();
                GREEN_BALL_POSITION.setSpindexerCurrentPos(GREEN_BALL_POSITION.getSpindexerCurrentPos() + distance * 2);
                SERVO.setPosition(Constants.Spindexer.Positions[GREEN_BALL_POSITION.getSpindexerCurrentPos()]);
                GREEN_BALL_POSITION.setActualIndex(GREEN_BALL_POSITION.getTargetIndex());

                if (driveComplete.getAsBoolean()) packet.put("Status Spindex Sort Balls", "Finished");
                else packet.put("Status Spindex Sort Balls", "Running");
                return !driveComplete.getAsBoolean();
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

                if (!GREEN_BALL_POSITION.getMoveSpindexer()) {
                    MOVE_DELAY.reset();
                    if (shootComplete.getAsBoolean()) packet.put("Status Spindex Shoot Balls", "Finished");
                    else packet.put("Status Spindex Shoot Balls", "Running");
                    return !shootComplete.getAsBoolean();
                }

                SERVO.setPosition(Constants.Spindexer.Positions[GREEN_BALL_POSITION.getSpindexerCurrentPos()]);

                if (MOVE_DELAY.allowExec()) {
                    GREEN_BALL_POSITION.setMoveSpindexer(false);
                }

                if (shootComplete.getAsBoolean()) packet.put("Status Spindex Shoot Balls", "Finished");
                else packet.put("Status Spindex Shoot Balls", "Running");
                return !shootComplete.getAsBoolean();
            }
        };
    }
}

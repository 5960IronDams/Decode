package org.firstinspires.ftc.teamcode.decode.core;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.decode.Constants;
import org.firstinspires.ftc.teamcode.decode.SharedData;
import org.firstinspires.ftc.teamcode.ironDams.Config;
import org.firstinspires.ftc.teamcode.ironDams.core.WaitFor;

import java.util.function.BooleanSupplier;
import java.util.function.IntSupplier;

public class Spindexer {
    private final SharedData DATA;
    private final Servo SERVO;
    private final WaitFor MOVE_DELAY = new WaitFor(1500);
    private final WaitFor PATTERN_SORT_DELAY = new WaitFor(500);

    public Spindexer(LinearOpMode opMode, SharedData data) {
        DATA = data;
        SERVO = opMode.hardwareMap.get(Servo.class, Config.Hardware.Servos.Spindexer.SPINDEXER_ID);
        SERVO.setPosition(Constants.Spindexer.Positions[0]);
    }

    public double getPos() {
        return SERVO.getPosition();
    }

    public void setPos(double pos) {
        SERVO.setPosition(pos);
    }

    public void sort() {
        if (DATA.getGreenBallTargetIndex() != -1 &&
                DATA.getGreenBallActualIndex() != -1) {
            int distance = DATA.getGreenBallActualIndex() - DATA.getGreenBallTargetIndex();
            DATA.setSpindexerCurrentIndex(DATA.getSpindexerCurrentIndex() + distance * 2);
            SERVO.setPosition(Constants.Spindexer.Positions[DATA.getSpindexerCurrentIndex()]);
            DATA.setGreenBallActualIndex(DATA.getGreenBallTargetIndex());
        }
    }

    public void shoot() {
        int distance = DATA.getSpindexerCurrentIndex() % 2 == 0 ? 1 : 2;
        DATA.setSpindexerCurrentIndex(DATA.getSpindexerCurrentIndex() + distance);
        SERVO.setPosition(Constants.Spindexer.Positions[DATA.getSpindexerCurrentIndex()]);
        DATA.setShotCount(DATA.getShotCount() + 1);
    }

    public Action indexAction() {
        return new Action() {
            private boolean initialized = false;

            @Override
            public boolean run(@NonNull TelemetryPacket packet) {
                if (!initialized) {
                    initialized = true;
                }

                if (DATA.isSpindexerLoaded()) {
                    DATA.setSpindexerState(Constants.Spindexer.Mode.SORT.ordinal());
                    return false;
                }
                else {
                    if (DATA.getMoveSpindexer()) {
                        DATA.setMoveSpindexer(false);
                        DATA.setSpindexerDetectionIndex(DATA.getSpindexerCurrentIndex());
                        DATA.setSpindexerCurrentIndex(DATA.getSpindexerCurrentIndex() + 2);
                        SERVO.setPosition(Constants.Spindexer.Positions[DATA.getSpindexerCurrentIndex()]);
                    } else {
                        MOVE_DELAY.reset();
                    }

                    packet.put("Spindexer Is Moving", DATA.getMoveSpindexer());
                    packet.put("Spindexer Current Index", DATA.getSpindexerCurrentIndex());
                    packet.put("Spindexer Detection Index", DATA.getSpindexerDetectionIndex());
                }

                return true;
            }
        };
    }

    public Action autoIndexAction(BooleanSupplier driveComplete) {
        return new Action() {
            private boolean initialized = false;

            @Override
            public boolean run(@NonNull TelemetryPacket packet) {
                if (!initialized) {
                    initialized = true;
                }

                if (!DATA.getMoveSpindexer()) {
                    MOVE_DELAY.reset();
                    if (DATA.isSpindexerLoaded()) {
                        packet.put("Status Spindex Indexing", "Finished");
                        SERVO.setPosition(Constants.Spindexer.Positions[0]);
                        return false;
                    } else {
                        if (driveComplete.getAsBoolean()) packet.put("Status Spindex Indexing", "Finished");
                        else packet.put("Status Spindex Indexing", "Running");
                        return !driveComplete.getAsBoolean();
                    }
                }

                SERVO.setPosition(Constants.Spindexer.Positions[DATA.getSpindexerCurrentIndex()]);

                if (MOVE_DELAY.allowExec()) {
                    DATA.setMoveSpindexer(false);
                }

                if (DATA.isSpindexerLoaded()) {
                    packet.put("Status Spindex Indexing", "Finished");
                    SERVO.setPosition(Constants.Spindexer.Positions[0]);
                    return false;
                }
                else {
                    if (driveComplete.getAsBoolean()) packet.put("Status Spindex Indexing", "Finished");
                    else packet.put("Status Spindex Indexing", "Running");
                    return !driveComplete.getAsBoolean();
                }
            }
        };
    }

    public Action playerIndexAction(IntSupplier stateSupplier) {
        return new Action() {
            private boolean initialized = false;

            @Override
            public boolean run(@NonNull TelemetryPacket packet) {
                if (!initialized) {
                    initialized = true;
                }

                if (DATA.isSpindexerLoaded()) {
                    DATA.setSpindexerState(Constants.Spindexer.Mode.SORT.ordinal());
                }
                else if (stateSupplier.getAsInt() == Constants.Spindexer.Mode.INDEX.ordinal())
                {
                    if (DATA.getMoveSpindexer()) {
                        DATA.setMoveSpindexer(false);
                        DATA.setSpindexerDetectionIndex(DATA.getSpindexerCurrentIndex());
                        DATA.setSpindexerCurrentIndex(DATA.getSpindexerCurrentIndex() + 2);
                        SERVO.setPosition(Constants.Spindexer.Positions[DATA.getSpindexerCurrentIndex()]);
                    } else {
                        MOVE_DELAY.reset();
                    }
                }

                packet.put("Spindexer Is Moving", DATA.getMoveSpindexer());
                packet.put("Spindexer Current Index", DATA.getSpindexerCurrentIndex());
                packet.put("Spindexer Detection Index", DATA.getSpindexerDetectionIndex());

                return true;
            }
        };
    }

    public Action autoSortAction(BooleanSupplier driveComplete) {
        return new Action() {
            private boolean initialized = false;

            @Override
            public boolean run(@NonNull TelemetryPacket packet) {
                if (!initialized) {
                    initialized = true;
                }

                if (!DATA.isSpindexerLoaded() ||
                        DATA.getGreenBallTargetIndex() == -1 ||
                        DATA.getGreenBallActualIndex() == -1) {
                    if (driveComplete.getAsBoolean()) packet.put("Status Spindex Sort Balls", "Finished");
                    else packet.put("Status Spindex Sort Balls", "Running");
                    return !driveComplete.getAsBoolean();
                } else if (DATA.getGreenBallTargetIndex() == DATA.getGreenBallActualIndex()) {
                    packet.put("Status Spindex Sort Balls", "Finished");
                    return false;
                }

                int distance = DATA.getGreenBallActualIndex() - DATA.getGreenBallTargetIndex();
                DATA.setSpindexerCurrentIndex(DATA.getSpindexerCurrentIndex() + distance * 2);
                SERVO.setPosition(Constants.Spindexer.Positions[DATA.getSpindexerCurrentIndex()]);
                DATA.setGreenBallActualIndex(DATA.getGreenBallTargetIndex());

                if (driveComplete.getAsBoolean()) packet.put("Status Spindex Sort Balls", "Finished");
                else packet.put("Status Spindex Sort Balls", "Running");
                return !driveComplete.getAsBoolean();
            }
        };
    }

    public Action playerSortAction(BooleanSupplier hasPatternChanged, BooleanSupplier readyToSort, IntSupplier stateSupplier) {
        return new Action() {
            private boolean initialized = false;

            @Override
            public boolean run(@NonNull TelemetryPacket packet) {
                if (!initialized) {
                    initialized = true;
                }

                if (stateSupplier.getAsInt() == Constants.Spindexer.Mode.SORT.ordinal()) {

                    if (DATA.getGreenBallTargetIndex() != -1 &&
                            DATA.getGreenBallActualIndex() != -1) {
                        packet.put("Spindexer Status", "Sorting");
                        int distance = DATA.getGreenBallActualIndex() - DATA.getGreenBallTargetIndex();
                        DATA.setSpindexerCurrentIndex(DATA.getSpindexerCurrentIndex() + distance * 2);
                        SERVO.setPosition(Constants.Spindexer.Positions[DATA.getSpindexerCurrentIndex()]);
                        DATA.setGreenBallActualIndex(DATA.getGreenBallTargetIndex());
                    }

                    DATA.setSpindexerState(Constants.Spindexer.Mode.PRE_SHOOT.ordinal());
                }

                packet.put("Spindexer Mode", stateSupplier.getAsInt());

                return true;
            }
        };
    }

    public Action playerShootAction(IntSupplier stateSupplier) {
        return new Action() {
            private boolean initialized = false;

            @Override
            public boolean run(@NonNull TelemetryPacket packet) {
                if (!initialized) {
                    initialized = true;
                }

                if (stateSupplier.getAsInt() == Constants.Spindexer.Mode.SHOOT.ordinal())
                {
                    if (DATA.getShotCount() < 3 && MOVE_DELAY.allowExec()) {
                        int distance = DATA.getSpindexerCurrentIndex() % 2 == 0 ? 1 : 2;
                        DATA.setSpindexerCurrentIndex(DATA.getSpindexerCurrentIndex() + distance);
                        SERVO.setPosition(Constants.Spindexer.Positions[DATA.getSpindexerCurrentIndex()]);
                        DATA.setShotCount(DATA.getShotCount() + 1);
                    }
                    else if (DATA.getShotCount() == 3 && MOVE_DELAY.allowExec()) {
                        DATA.setSpindexerCurrentIndex(0);
                        SERVO.setPosition(Constants.Spindexer.Positions[DATA.getSpindexerCurrentIndex()]);
                        DATA.setSpindexerState(Constants.Spindexer.Mode.INDEX.ordinal());
                        DATA.setShotCount(0);
                    }
                }

                packet.put("Spindexer Mode", stateSupplier.getAsInt());
                packet.put("Spindexer Shot Count", DATA.getShotCount());
                packet.put("Spindexer Current Index", DATA.getSpindexerCurrentIndex());
                packet.put("Spindexer Detection Index", DATA.getSpindexerDetectionIndex());

                return true;
            }
        };
    }
}

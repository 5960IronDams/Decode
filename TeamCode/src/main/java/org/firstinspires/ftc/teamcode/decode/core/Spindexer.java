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

public class Spindexer {
    private final SharedData DATA;
    private final Servo SERVO;
    private final WaitFor MOVE_DELAY = new WaitFor(1500);
    private final WaitFor INDEX_DELAY = new WaitFor(500);

    private int _currentIndex = 0;
    private int _detectionIndex = -1;

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

    public void moveDistance(int distance) {
        _detectionIndex = _currentIndex;
        _currentIndex += distance;
        double pos = Constants.Spindexer.Positions[_currentIndex];

        SERVO.setPosition(pos);
    }

    public void moveIndex(int index) {
        _detectionIndex = _currentIndex;
        _currentIndex = index;
        SERVO.setPosition(Constants.Spindexer.Positions[index]);
    }

    public void sort() {
        if (DATA.getGreenBallTargetIndex() != -1 &&
                DATA.getGreenBallActualIndex() != -1) {
            int distance = DATA.getGreenBallActualIndex() - DATA.getGreenBallTargetIndex();

            _detectionIndex = _currentIndex;
            _currentIndex = _currentIndex + distance * 2;
            SERVO.setPosition(Constants.Spindexer.Positions[_currentIndex]);
            DATA.setGreenBallActualIndex(DATA.getGreenBallTargetIndex());
        }
    }

    public void shoot() {
        int distance = _currentIndex % 2 == 0 ? 1 : 2;
        _currentIndex = _currentIndex + distance;
        SERVO.setPosition(Constants.Spindexer.Positions[_currentIndex]);
        DATA.setShotCount(DATA.getShotCount() + 1);
    }

    public Action indexAction(BooleanSupplier driveComplete) {
        return new Action() {
            private boolean initialized = false;

            @Override
            public boolean run(@NonNull TelemetryPacket packet) {
                if (!initialized) {
                    initialized = true;
                }

                if (DATA.getMoveSpindexer()) {
                    DATA.setMoveSpindexer(false);
                    int currentIndex = _currentIndex;
                    _currentIndex += 2;
                    SERVO.setPosition(Constants.Spindexer.Positions[_currentIndex]);
                    if (INDEX_DELAY.allowExec()) {
                        _detectionIndex = currentIndex;
                    }
                } else {
                    INDEX_DELAY.reset();
                }

                if (DATA.isSpindexerLoaded()) {
                    packet.put("Status Spindex Indexing", "Finished");
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

    public Action moveDistAction(int distance) {
        return new Action() {
            private boolean initialized = false;

            @Override
            public boolean run(@NonNull TelemetryPacket packet) {
                if (!initialized) {
                    initialized = true;
                }

                packet.put("Color Allow Detection", DATA.getArtifactDetection());

                if (!DATA.getArtifactDetection())
                {
                    return false;
                }
                packet.put("Spin Move Current Index", _currentIndex);
                packet.put("Spin Move Requested Dist", distance);
                packet.put("Spin Move Current Pos", SERVO.getPosition());


                moveDistance(distance);

                packet.put("Spin Move New Index", _currentIndex);
                packet.put("Spin Move New Pos", SERVO.getPosition());

                return false;
            }
        };
    }

    public Action moveIndexAction(int index) {
        return new Action() {
            private boolean initialized = false;

            @Override
            public boolean run(@NonNull TelemetryPacket telemetryPacket) {
                if (!initialized) {
                    initialized = true;
                }

                moveIndex(index);

                return false;
            }
        };
    }

    public Action sortAction(BooleanSupplier hasPatternChanged) {
        return new Action() {
            private boolean initialized = false;

            @Override
            public boolean run(@NonNull TelemetryPacket packet) {
                if (!initialized) {
                    initialized = true;
                }

                sort();

                return false;
            }
        };
    }
}

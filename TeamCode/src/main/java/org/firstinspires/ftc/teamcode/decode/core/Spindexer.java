package org.firstinspires.ftc.teamcode.decode.core;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.decode.SharedData;
import org.firstinspires.ftc.teamcode.irondams.core.Logger;
import org.firstinspires.ftc.teamcode.irondams.core.WaitFor;

import java.util.function.BooleanSupplier;

public class Spindexer {
    private final Servo SERVO;
    private final Logger LOG;

    private final WaitFor SORT_TIMEOUT = new WaitFor(500);

    public Spindexer(@NonNull LinearOpMode opMode, Logger log) {
        LOG = log;

        SERVO = opMode.hardwareMap.get(Servo.class, "spindex");
        SERVO.setPosition(SharedData.Spindexer.POSITIONS[0]);

        SharedData.Spindexer.currentIndex = 0;
        SharedData.Spindexer.targetPos = SharedData.Spindexer.POSITIONS[0];
        SharedData.Spindexer.currentPos = SharedData.Spindexer.POSITIONS[0];
    }

    public void setPos(double pos) {
        SERVO.setPosition(pos);
    }

    public double getPos() {
        return SERVO.getPosition();
    }

    public boolean moveSpindexer(double millis) {
        if (SharedData.Spindexer.targetPos != SharedData.Spindexer.currentPos) {
            SERVO.setPosition(SharedData.Spindexer.targetPos);
            LOG.writeToMemory(millis, "spindexer - move startingPos", SharedData.Spindexer.currentPos);
            LOG.writeToMemory(millis, "spindexer - move targetPos", SharedData.Spindexer.targetPos);
            LOG.writeToMemory(millis, "spindexer - move actualPos", SERVO.getPosition());
            LOG.flushToDisc();
        }

        double TOLERANCE = 0.01;

        if (SharedData.Spindexer.targetPos > SERVO.getPosition() - TOLERANCE
            && SharedData.Spindexer.targetPos < SERVO.getPosition() + TOLERANCE) {
            SharedData.Spindexer.currentPos = SharedData.Spindexer.targetPos;
            return true;
        }

        return false;
    }

    public void sortBalls(double millis) {
        if (SharedData.BallDetection.areAllDetected() && SharedData.Pattern.actualIndex > -1 && SharedData.Pattern.targetIndex > -1)
        {
            int distance = SharedData.Pattern.actualIndex - SharedData.Pattern.targetIndex;
            if (distance != 0) {
                SharedData.Spindexer.currentIndex = SharedData.Spindexer.currentIndex + distance * 2;
                SharedData.Spindexer.targetPos = SharedData.Spindexer.POSITIONS[SharedData.Spindexer.currentIndex];

                SharedData.Pattern.actualIndex = SharedData.Pattern.targetIndex;
                SharedData.Pattern.actual = new String[] { "P", "P", "P" };
                SharedData.Pattern.actual[SharedData.Pattern.actualIndex] = "G";

                LOG.writeToMemory(millis, "spindexer - sort targetindex", SharedData.Pattern.targetIndex);
                LOG.writeToMemory(millis, "spindexer - sort distance", distance);
                LOG.flushToDisc();
            }
        }
    }

    public Action moveSpindexerAction(BooleanSupplier driveComplete, double millis) {
        return new Action() {
            private boolean initialized = false;

            @Override
            public boolean run(@NonNull TelemetryPacket packet) {
                if (!initialized) {
                    initialized = true;
                }

                boolean moveCompleted = moveSpindexer(millis);

                if (driveComplete.getAsBoolean()) {
                    LOG.writeToMemory(millis, "spindexer - move Ended", "Drive Complete");
                    LOG.flushToDisc();
                    return false;
                }
                else if (moveCompleted) {
                    LOG.writeToMemory(millis, "spindexer - move Ended", "Move Complete");
                    LOG.flushToDisc();
                    return false;
                }
                else {
                    return true;
                }
            }
        };
    }

    public Action resetSortTimeoutAction(BooleanSupplier driveComplete, double millis) {
        return new Action() {
            private boolean initialized = false;

            @Override
            public boolean run(@NonNull TelemetryPacket packet) {
                if (!initialized) {
                    initialized = true;
                }

                SORT_TIMEOUT.reset();

                return false;
            }
        };
    }

    public Action sortAction(BooleanSupplier driveComplete, double millis) {
        return new Action() {
            private boolean initialized = false;

            @Override
            public boolean run(@NonNull TelemetryPacket packet) {
                if (!initialized) {
                    initialized = true;
                }

                sortBalls(millis);

                if (SORT_TIMEOUT.allowExec()) return false;
                else if (driveComplete.getAsBoolean()) return false;
                else return !moveSpindexer(millis);
            }
        };
    }

    public Action resetToZeroAction(BooleanSupplier driveComplete, double millis) {
        return new Action() {
            private boolean initialized = false;

            @Override
            public boolean run(@NonNull TelemetryPacket packet) {
                if (!initialized) {
                    initialized = true;
                }

                SharedData.Spindexer.currentIndex = 0;
                SharedData.Spindexer.targetPos = SharedData.Spindexer.POSITIONS[0];

                if (driveComplete.getAsBoolean()) return false;
                else return !moveSpindexer(millis);
            }
        };
    }
}

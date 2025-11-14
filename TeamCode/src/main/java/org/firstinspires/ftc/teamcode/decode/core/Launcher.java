package org.firstinspires.ftc.teamcode.decode.core;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;
import org.firstinspires.ftc.teamcode.decode.SharedData;
import org.firstinspires.ftc.teamcode.irondams.core.Logger;
import org.firstinspires.ftc.teamcode.irondams.core.WaitFor;

public class Launcher {
    private final Servo LAUNCHER_SERVO;
    private final Servo EEERRR_SERVO;
    private final DcMotorEx LEFT;
    private final DcMotorEx RIGHT;

    private final Logger LOG;

    private double _targetVelocity = 1900;
    private final double DEFAULT_VELOCITY = 1900;

    private final double OPEN_POS = 0.37;
    private final double CLOSE_POS = 0;

    private final double INTAKE_POS = 0.3259;
    private final double OUTTAKE_POS = 0;

    private final int MAX_SHOT_COUNT = 3;

    public boolean isWaitingForCurrentSpike = false;

    private final WaitFor SHOOT_ACTION_TIMEOUT = new WaitFor(500);
    private final WaitFor SHOT_COMPLETE_TIMEOUT = new WaitFor(1200);

    private double _velocityTolerance;
    private double _maxCurrent = 0;

    public Launcher(LinearOpMode opMode, Logger log, double velocityTolerance) {
        LAUNCHER_SERVO = opMode.hardwareMap.get(Servo.class, "launcher");
        LAUNCHER_SERVO.setDirection(Servo.Direction.REVERSE);
        EEERRR_SERVO = opMode.hardwareMap.get(Servo.class, "eeerrr");

        _velocityTolerance = velocityTolerance;

        LOG = log;


        LEFT = opMode.hardwareMap.get(DcMotorEx.class, "leftOut");
        RIGHT = opMode.hardwareMap.get(DcMotorEx.class, "rightOut");

        RIGHT.setDirection(DcMotorEx.Direction.REVERSE);

        LEFT.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);
        RIGHT.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);

        LEFT.setVelocityPIDFCoefficients(1.41237, 0.141237, 0, 14.1237);
        LEFT.setPositionPIDFCoefficients(5.0);
        RIGHT.setVelocityPIDFCoefficients(1.42465, 0.142465, 0, 14.2465);
        RIGHT.setPositionPIDFCoefficients(5.0);

        LEFT.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.FLOAT);
        RIGHT.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.FLOAT);

        LAUNCHER_SERVO.setPosition(CLOSE_POS);

        SharedData.Launcher.shotCount = 0;
        SharedData.Launcher.isActive = false;
    }

    public Launcher intakePos() {
        EEERRR_SERVO.setPosition(INTAKE_POS);
        return this;
    }

    public Launcher outtakePos() {
        EEERRR_SERVO.setPosition(OUTTAKE_POS);
        return this;
    }

    public Launcher open() {
        LAUNCHER_SERVO.setPosition(OPEN_POS);
        return this;
    }

    public Launcher close() {
        LAUNCHER_SERVO.setPosition(CLOSE_POS);
        return this;
    }

    public void resetShotActionTimeOut() {
        SHOOT_ACTION_TIMEOUT.reset();
    }

    public boolean runLauncher(double millis) {
        if (SharedData.Launcher.isActive)
        {
            if (SharedData.Launcher.shotCount < MAX_SHOT_COUNT) {
                if (((LEFT.getVelocity() >= _targetVelocity - _velocityTolerance && RIGHT.getVelocity() >= _targetVelocity - _velocityTolerance) || SHOOT_ACTION_TIMEOUT.allowExec()) && !isWaitingForCurrentSpike) {
                    SHOT_COMPLETE_TIMEOUT.reset();
                    isWaitingForCurrentSpike = true;
                    SharedData.Spindexer.currentIndex += SharedData.Spindexer.currentIndex % 2 == 0 ? 1 : 2;
                    SharedData.Spindexer.targetPos = SharedData.Spindexer.POSITIONS[SharedData.Spindexer.currentIndex];
                } else if (isWaitingForCurrentSpike) {
                    if (LEFT.getCurrent(CurrentUnit.MILLIAMPS) > 2000 || SHOT_COMPLETE_TIMEOUT.allowExec()) {
                        SharedData.Launcher.shotCount += 1;
                        isWaitingForCurrentSpike = false;
                        SHOOT_ACTION_TIMEOUT.reset();
                    }
                }
            } else {
                SharedData.Launcher.isActive = false;
                SharedData.Launcher.shotCount = 0;
                SharedData.Spindexer.currentIndex = 0;
                SharedData.Spindexer.targetPos = SharedData.Spindexer.POSITIONS[0];

                SharedData.Pattern.actual = new String[] { "", "", "" };
                SharedData.Pattern.actualIndex = -1;

                SharedData.BallDetection.detectionCount = 0;

                setVelocity(0);
                intakePos();
            }

            LOG.writeToMemory(millis, "launcher - shot count", SharedData.Launcher.shotCount);
            LOG.writeToMemory(millis, "launcher - target velocity", _targetVelocity);
            LOG.writeToMemory(millis, "launcher - current spike", isWaitingForCurrentSpike);
            LOG.writeToMemory(millis, "launcher - left velocity", LEFT.getVelocity());
            LOG.writeToMemory(millis, "launcher - right velocity", RIGHT.getVelocity());
            LOG.writeToMemory(millis, "launcher - left current", LEFT.getCurrent(CurrentUnit.MILLIAMPS));
            LOG.writeToMemory(millis, "launcher - right current", RIGHT.getCurrent(CurrentUnit.MILLIAMPS));
            LOG.flushToDisc();
        }

        return SharedData.Launcher.isActive;
    }

    public void setTargetVelocity() {
        _targetVelocity = DEFAULT_VELOCITY;
        LEFT.setVelocity(DEFAULT_VELOCITY);
        RIGHT.setVelocity(DEFAULT_VELOCITY);
    }

    public void setVelocity(double velocity) {
        _targetVelocity = velocity;
        LEFT.setVelocity(velocity);
        RIGHT.setVelocity(velocity);
    }

    public Launcher setVelocityTolerance(double tolerance) {
        _velocityTolerance = tolerance;
        return this;
    }

    public Action setLauncherModeAction(boolean moveToIntake, double millis) {
        return new Action() {
            private boolean initialized = false;

            @Override
            public boolean run(@NonNull TelemetryPacket telemetryPacket) {
                if (!initialized) {
                    initialized = true;
                }

                if (moveToIntake) intakePos();
                else outtakePos();

                return false;
            }
        };
    }

    public Action startShootingAction(double velocity, double tolerance, double millis) {
        return new Action() {
            private boolean initialized = false;

            @Override
            public boolean run(@NonNull TelemetryPacket telemetryPacket) {
                if (!initialized) {
                    initialized = true;
                }

                setVelocityTolerance(tolerance).open().setVelocity(velocity);
                SharedData.Launcher.shotCount = 0;
                SharedData.Launcher.isActive = true;

                return false;
            }
        };
    }

    public Action stopVelocityAction(double millis) {
        return new Action() {
            private boolean initialized = false;

            @Override
            public boolean run(@NonNull TelemetryPacket telemetryPacket) {
                if (!initialized) {
                    initialized = true;
                }

                setVelocity(0);

                return false;
            }
        };
    }

    public Action closeAction(double millis) {
        return new Action() {
            private boolean initialized = false;

            @Override
            public boolean run(@NonNull TelemetryPacket telemetryPacket) {
                if (!initialized) {
                    initialized = true;
                }

                close();

                return false;
            }
        };
    }

    public Action shotResetActionTimerAction(double millis) {
        return new Action() {
            private boolean initialized = false;

            @Override
            public boolean run(@NonNull TelemetryPacket telemetryPacket) {
                if (!initialized) {
                    initialized = true;
                }

                SHOOT_ACTION_TIMEOUT.reset();

                return false;
            }
        };
    }

    public Action shootAction(double millis) {
        return new Action() {
            private boolean initialized = false;

            @Override
            public boolean run(@NonNull TelemetryPacket telemetryPacket) {
                if (!initialized) {
                    initialized = true;
                }

                LOG.writeToMemory(millis, "launcher - target velocity", _targetVelocity);
                LOG.writeToMemory(millis, "launcher - left velocity", LEFT.getVelocity());
                LOG.writeToMemory(millis, "launcher - right velocity", RIGHT.getVelocity());

                if ((LEFT.getVelocity() >= _targetVelocity - _velocityTolerance && RIGHT.getVelocity() >= _targetVelocity - _velocityTolerance)
                        || SHOOT_ACTION_TIMEOUT.allowExec()) {
                    SharedData.Spindexer.currentIndex += SharedData.Spindexer.currentIndex % 2 == 0 ? 1 : 2;
                    SharedData.Spindexer.targetPos = SharedData.Spindexer.POSITIONS[SharedData.Spindexer.currentIndex];

                    LOG.writeToMemory(millis, "launcher - spindexer target index", SharedData.Spindexer.currentIndex);
                    LOG.writeToMemory(millis, "launcher - spindexer target pos", SharedData.Spindexer.targetPos);
                    LOG.writeToMemory(millis, "launcher - spindexer current pos", SharedData.Spindexer.currentPos);
                    LOG.flushToDisc();
                    return false;
                } else if (SHOOT_ACTION_TIMEOUT.allowExec()) {
                    LOG.writeToMemory(millis, "launcher - move Ended", "Shoot action timeout");
                    LOG.flushToDisc();
                    return false;
                } else {
                    LOG.flushToDisc();
                    return true;
                }
            }
        };
    }

    public Action shotResetCompleteTimerAction(double millis) {
        return new Action() {
            private boolean initialized = false;

            @Override
            public boolean run(@NonNull TelemetryPacket telemetryPacket) {
                if (!initialized) {
                    initialized = true;
                }

                SHOT_COMPLETE_TIMEOUT.reset();

                return false;
            }
        };
    }

    public Action shotCompleteAction(double millis) {
        return new Action() {
            private boolean initialized = false;

            @Override
            public boolean run(@NonNull TelemetryPacket telemetryPacket) {
                if (!initialized) {
                    initialized = true;
                }

                double current = LEFT.getCurrent(CurrentUnit.MILLIAMPS);
                if (current > _maxCurrent) _maxCurrent = current;

                if (SHOT_COMPLETE_TIMEOUT.allowExec()) {
                    LOG.writeToMemory(millis, "launcher - maxCurrent", _maxCurrent);
                    LOG.writeToMemory(millis, "launcher - shot complete timeout", "completed");
                    _maxCurrent = 0;
                    return false;
                }
                else if (current >= 2000) {
                    LOG.writeToMemory(millis, "launcher - maxCurrent", _maxCurrent);
                    LOG.writeToMemory(millis, "launcher - left current", "completed");
                    _maxCurrent = 0;
                    return false;
                } else {
                    return true;
                }
            }
        };
    }

    public Action resetToIndexMode(double millis) {
        return new Action() {
            private boolean initialized = false;

            @Override
            public boolean run(@NonNull TelemetryPacket telemetryPacket) {
                if (!initialized) {
                    initialized = true;
                }

                SharedData.Launcher.isActive = false;
                SharedData.Launcher.shotCount = 0;
                SharedData.Spindexer.currentIndex = 0;
                SharedData.Spindexer.targetPos = SharedData.Spindexer.POSITIONS[0];

                SharedData.Pattern.actual = new String[] { "", "", "" };
                SharedData.Pattern.actualIndex = -1;

                SharedData.BallDetection.detectionCount = 0;

                setVelocity(0);
                intakePos();

                return false;
            }
        };
    }
}

package org.firstinspires.ftc.teamcode.decode.core;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.VoltageSensor;

import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;
import org.firstinspires.ftc.teamcode.decode.SharedData;
import org.firstinspires.ftc.teamcode.irondams.core.Logger;
import org.firstinspires.ftc.teamcode.irondams.core.WaitFor;

public class Launcher {
    private final Servo LAUNCHER_SERVO;
    private final Servo EEERRR_SERVO;
    private final VoltageSensor VOLTAGE_SENSOR;
    private final DcMotorEx LEFT;
    private final DcMotorEx RIGHT;
    private final PIDFCoefficients LEFT_PIDF;
    private final PIDFCoefficients RIGHT_PIDF;

    private final Logger LOG;

    private final double TARGET_VELOCITY = 2075;

    private final double RIGHT_TPS = 2245;
    private final double LEFT_TPS = 2305;

    private final double TARGET_VOLT = 12;

    private final double OPEN_POS = 0.37;
    private final double CLOSE_POS = 0;

    private final double INTAKE_POS = 0.3259;
    private final double OUTTAKE_POS = 0;

    private final int MAX_SHOT_COUNT = 3;

    public boolean isWaitingForCurrentSpike = false;

    private final WaitFor SHOT_COMPLETE_TIMEOUT = new WaitFor(1200);


    public Launcher(LinearOpMode opMode, VoltageSensor voltageSensor, Logger log) {
        LAUNCHER_SERVO = opMode.hardwareMap.get(Servo.class, "launcher");
        LAUNCHER_SERVO.setDirection(Servo.Direction.REVERSE);
        EEERRR_SERVO = opMode.hardwareMap.get(Servo.class, "eeerrr");

        LOG = log;

        VOLTAGE_SENSOR = voltageSensor;

        LEFT = opMode.hardwareMap.get(DcMotorEx.class, "leftOut");
        RIGHT = opMode.hardwareMap.get(DcMotorEx.class, "rightOut");

        RIGHT.setDirection(DcMotorEx.Direction.REVERSE);

        LEFT.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);
        RIGHT.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);

        LEFT_PIDF = new PIDFCoefficients(
                0, 0, 0.0005, (32767 / LEFT_TPS) * (TARGET_VOLT / VOLTAGE_SENSOR.getVoltage())
        );

        RIGHT_PIDF = new PIDFCoefficients(
                0, 0, 0.0005, (32767 / RIGHT_TPS) * (TARGET_VOLT / VOLTAGE_SENSOR.getVoltage())
        );

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

    public void runLauncher(double millis) {
        if (SharedData.Launcher.isActive) {
            if (SharedData.Launcher.shotCount < MAX_SHOT_COUNT) {
                if (LEFT.getVelocity() >= TARGET_VELOCITY - 100 && RIGHT.getVelocity() >= TARGET_VELOCITY - 100  && !isWaitingForCurrentSpike) {
                    isWaitingForCurrentSpike = true;
                    SharedData.Spindexer.currentIndex += SharedData.Spindexer.currentIndex % 2 == 0 ? 1 : 2;
                    SharedData.Spindexer.targetPos = SharedData.Spindexer.POSITIONS[SharedData.Spindexer.currentIndex];
                } else if (isWaitingForCurrentSpike) {
                    if (LEFT.getCurrent(CurrentUnit.MILLIAMPS) > 2000) {
                        SharedData.Launcher.shotCount += 1;
                        isWaitingForCurrentSpike = false;
                    }
                }
            } else {
                SharedData.Launcher.isActive = false;
                SharedData.Launcher.shotCount = 0;
                SharedData.Spindexer.currentIndex = 0;
                SharedData.Spindexer.targetPos = SharedData.Spindexer.POSITIONS[0];
                SharedData.Pattern.actual = new String[] { "", "", "" };
                SharedData.Pattern.actualIndex = -1;
            }

            LOG.writeToMemory(millis, "launcher target velocity", TARGET_VELOCITY);
            LOG.writeToMemory(millis, "launcher current spike", isWaitingForCurrentSpike);
            LOG.writeToMemory(millis, "launcher left velocity", LEFT.getVelocity());
            LOG.writeToMemory(millis, "launcher right velocity", RIGHT.getVelocity());
            LOG.writeToMemory(millis, "launcher left current", LEFT.getCurrent(CurrentUnit.MILLIAMPS));
            LOG.writeToMemory(millis, "launcher right current", RIGHT.getCurrent(CurrentUnit.MILLIAMPS));

            LOG.flushToDisc();
        }
    }

    public void setTargetVelocity() {
        LEFT_PIDF.f = (32767 / LEFT_TPS) * (TARGET_VOLT / VOLTAGE_SENSOR.getVoltage());
        RIGHT_PIDF.f = (32767 / RIGHT_TPS) * (TARGET_VOLT / VOLTAGE_SENSOR.getVoltage());

        LEFT.setVelocity(TARGET_VELOCITY);
        RIGHT.setVelocity(TARGET_VELOCITY);
    }

    public void setVelocity(double velocity) {
        LEFT.setVelocity(velocity);
        RIGHT.setVelocity(velocity);
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

    public Action startShootingAction(double velocity, double millis) {
        return new Action() {
            private boolean initialized = false;

            @Override
            public boolean run(@NonNull TelemetryPacket telemetryPacket) {
                if (!initialized) {
                    initialized = true;
                }

                open().setVelocity(velocity);

                return false;
            }
        };
    }

    public Action stopVelocitygAction(double millis) {
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

    public Action shootAction(double millis) {
        return new Action() {
            private boolean initialized = false;

            @Override
            public boolean run(@NonNull TelemetryPacket telemetryPacket) {
                if (!initialized) {
                    initialized = true;
                }

                SharedData.Spindexer.currentIndex += SharedData.Spindexer.currentIndex % 2 == 0 ? 1 : 2;
                SharedData.Spindexer.targetPos = SharedData.Spindexer.POSITIONS[SharedData.Spindexer.currentIndex];

                return false;
            }
        };
    }

    public Action shotResetTimerAction(double millis) {
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

                if (SHOT_COMPLETE_TIMEOUT.allowExec()) return false;
                else return LEFT.getCurrent(CurrentUnit.MILLIAMPS) < 2000;
            }
        };
    }
}

package org.firstinspires.ftc.teamcode.decode.core;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.VoltageSensor;

import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;
import org.firstinspires.ftc.teamcode.decode.Constants;
import org.firstinspires.ftc.teamcode.decode.SharedData;
import org.firstinspires.ftc.teamcode.ironDams.Config;
import org.firstinspires.ftc.teamcode.ironDams.core.WaitFor;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BooleanSupplier;

public class Shooter {
    private final Servo SERVO;
    private final VoltageSensor VOLTAGE_SENSOR;
    private final DcMotorEx LEFT;
    private final DcMotorEx RIGHT;
    private final PIDFCoefficients LEFT_PIDF;
    private final PIDFCoefficients RIGHT_PIDF;
    private final SharedData DATA;
    private final WaitFor USER_DELAY = new WaitFor(Config.USER_DELAY_MS);
    private final WaitFor SORT_DELAY = new WaitFor(500);

    private final Gamepad GAMEPAD2;

    public Shooter(LinearOpMode opMode, SharedData data) {
        SERVO = opMode.hardwareMap.get(Servo.class, Config.Hardware.Servos.Shooter.LEVER_ID);
        SERVO.setDirection(Servo.Direction.REVERSE);

        VOLTAGE_SENSOR = opMode.hardwareMap.voltageSensor.iterator().next();


        LEFT = opMode.hardwareMap.get(DcMotorEx.class, Config.Hardware.Motors.Shooter.LEFT_MOTOR_ID);
        RIGHT = opMode.hardwareMap.get(DcMotorEx.class, Config.Hardware.Motors.Shooter.RIGHT_MOTOR_ID);

        RIGHT.setDirection(DcMotorEx.Direction.REVERSE);

        LEFT.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);
        RIGHT.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);

        LEFT_PIDF = new PIDFCoefficients(
                0, 0, 0.0005, (32767 / Constants.Shooter.LEFT_TPS) * (Constants.Shooter.TARGET_VOLT / VOLTAGE_SENSOR.getVoltage())
        );

        RIGHT_PIDF = new PIDFCoefficients(
                0, 0, 0.0005, (32767 / Constants.Shooter.RIGHT_TPS) * (Constants.Shooter.TARGET_VOLT / VOLTAGE_SENSOR.getVoltage())
        );

        LEFT.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.FLOAT);
        RIGHT.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.FLOAT);

        SERVO.setPosition(Constants.Shooter.CLOSE_POS);

        GAMEPAD2 = opMode.gamepad2;
        DATA = data;
    }

    public void setVelocity() {
        setVelocity(Constants.Shooter.TARGET_VELOCITY);
    }

    public void setVelocity(double velocity) {
        double voltage = VOLTAGE_SENSOR.getVoltage();
        LEFT_PIDF.f = (32767 / Constants.Shooter.LEFT_TPS) * (Constants.Shooter.TARGET_VOLT / voltage);
        RIGHT_PIDF.f = (32767 / Constants.Shooter.LEFT_TPS) * (Constants.Shooter.TARGET_VOLT / voltage);
        LEFT.setVelocity(velocity);
        RIGHT.setVelocity(velocity);
    }

    public boolean readyForSort() {
        SERVO.setPosition(Constants.Shooter.CLOSE_POS);
        return SORT_DELAY.allowExec();
    }

    public Shooter close() {
        SERVO.setPosition(Constants.Shooter.CLOSE_POS);
        return this;
    }

    public Shooter open() {
        SERVO.setPosition(Constants.Shooter.OPEN_POS);
        return this;
    }

    public void stop() {
        LEFT.setVelocity(0);
        RIGHT.setVelocity(0);
    }

    public Action startAction(double velocity) {
        return new Action() {
            private boolean initialized = false;

            @Override
            public boolean run(@NonNull TelemetryPacket telemetryPacket) {
                if (!initialized) {
                    initialized = true;
                }

                open().setVelocity(velocity);

                return LEFT.getVelocity() < velocity - 100 || RIGHT.getVelocity() < velocity - 100;
            }
        };
    }

    public Action stopAction() {
        return new Action() {
            private boolean initialized = false;

            @Override
            public boolean run(@NonNull TelemetryPacket telemetryPacket) {
                if (!initialized) {
                    initialized = true;
                }

                stop();

                return false;
            }
        };
    }

    public Action closeAction() {
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

    public Action shootAction(BooleanSupplier requestSort) {
        return new Action() {
            private boolean initialized = false;

            @Override
            public boolean run(@NonNull TelemetryPacket packet) {
                if (!initialized) {
                    initialized = true;
                }

                double voltage = VOLTAGE_SENSOR.getVoltage();

                packet.put("Shooter Current Left", LEFT.getCurrent(CurrentUnit.MILLIAMPS));
                packet.put("Shooter Current Right", RIGHT.getCurrent(CurrentUnit.MILLIAMPS));
                packet.put("Shooter Velocity Right", RIGHT.getVelocity());
                packet.put("Shooter Velocity Left", LEFT.getVelocity());
                packet.put("Shooter Pow Left", LEFT.getPower());
                packet.put("Shooter Pow Right", RIGHT.getPower());
                packet.put("Shooter Target Velocity", Constants.Shooter.TARGET_VELOCITY);
                packet.put("Shooter DP DOWN", GAMEPAD2.dpad_down);
                packet.put("Shooter Voltage", voltage);
                packet.put("Shooter Sort Request", DATA.getHasPatternChanged().getAsBoolean());

                if (DATA.getSpindexerMode() == Constants.Spindexer.Mode.PRE_SHOOT) {
                    SERVO.setPosition(Constants.Shooter.OPEN_POS);
                    LEFT_PIDF.f = (32767 / Constants.Shooter.LEFT_TPS) * (Constants.Shooter.TARGET_VOLT / voltage);
                    RIGHT_PIDF.f = (32767 / Constants.Shooter.LEFT_TPS) * (Constants.Shooter.TARGET_VOLT / voltage);
                    LEFT.setVelocity(Constants.Shooter.TARGET_VELOCITY);
                    RIGHT.setVelocity(Constants.Shooter.TARGET_VELOCITY);

                    if (requestSort.getAsBoolean()) {
                        SERVO.setPosition(Constants.Shooter.CLOSE_POS);
                        if (SORT_DELAY.allowExec()) {
                            DATA.setSpindexerMode(Constants.Spindexer.Mode.SORT);
                            DATA.setHasPatternChanged(false);
                            DATA.setReadyToSort(true);
                        }
                    } else {
                        SORT_DELAY.reset();

                        if (GAMEPAD2.dpad_down && USER_DELAY.allowExec()) {
                            DATA.setSpindexerMode(Constants.Spindexer.Mode.SHOOT);
                            DATA.setSpindexerState(Constants.Spindexer.Mode.SHOOT.ordinal());
                            packet.put("Shooter SM", DATA.getSpindexerMode());
                        }
                    }
                } else if (DATA.getSpindexerMode() == Constants.Spindexer.Mode.INDEX) {
                    LEFT.setVelocity(0);
                    RIGHT.setVelocity(0);
                }

                return true;
            }
        };
    }
}
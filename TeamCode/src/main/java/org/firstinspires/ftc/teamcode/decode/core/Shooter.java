package org.firstinspires.ftc.teamcode.decode.core;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;
import org.firstinspires.ftc.teamcode.decode.Constants;
import org.firstinspires.ftc.teamcode.ironDams.core.WaitFor;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BooleanSupplier;

public class Shooter {
    private final Servo _servo;
    private final DcMotorEx _left;
    private final DcMotorEx _right;
    private final GreenBallPosition GREEN_BALL_POSITION;
    private final WaitFor SHOT_DELAY = new WaitFor(1000);

    private final AtomicBoolean shootComplete = new AtomicBoolean(false);
    private int _shotCount = 0;

    public void setShootComplete(boolean driveCompleted) {
        shootComplete.set(driveCompleted);
    }

    public BooleanSupplier getShootComplete() {
        return shootComplete::get;
    }

    public Shooter(LinearOpMode opMode, GreenBallPosition greenBallPosition) {
        _servo = opMode.hardwareMap.get(Servo.class, Constants.Shooter.LAUNCHER_ID);
        _servo.setDirection(Servo.Direction.REVERSE);

        _left = opMode.hardwareMap.get(DcMotorEx.class, Constants.Shooter.MOTOR_LEFT_ID);
        _right = opMode.hardwareMap.get(DcMotorEx.class, Constants.Shooter.MOTOR_RIGHT_ID);

        _right.setDirection(DcMotorEx.Direction.REVERSE);

        _left.setMode(DcMotorEx.RunMode.RUN_WITHOUT_ENCODER);
        _right.setMode(DcMotorEx.RunMode.RUN_WITHOUT_ENCODER);

        _left.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.FLOAT);
        _right.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.FLOAT);

        close();

        GREEN_BALL_POSITION = greenBallPosition;
    }

    public Shooter open() {
        _servo.setPosition(Constants.Shooter.OPEN_POS);
        return this;
    }

    public Shooter close() {
        _servo.setPosition(Constants.Shooter.CLOSED_POS);
        return this;
    }

    public void setPower(double power) {
        _left.setPower(power);
        _right.setPower(power);
    }

    public double getPower() {
        return _left.getPower();
    }

    public double getLeftCurrent(CurrentUnit currentUnit) {
        return _left.getCurrent(currentUnit);
    }

    public double getRightCurrent(CurrentUnit currentUnit) {
        return _right.getCurrent(currentUnit);
    }

    public boolean isInRange() {
        double leftCurrent = getLeftCurrent(CurrentUnit.MILLIAMPS);
        double rightCurrent = getRightCurrent(CurrentUnit.MILLIAMPS);

        return leftCurrent < Constants.Shooter.MAX_LEFT_CURRENT &&
                leftCurrent > Constants.Shooter.MIN_LEFT_CURRENT &&
                rightCurrent < Constants.Shooter.MAX_RIGHT_CURRENT &&
                rightCurrent > Constants.Shooter.MIN_RIGHT_CURRENT;


    }

    public Action start(BooleanSupplier driveComplete) {
        return new Action() {
            private boolean initialized = false;

            @Override
            public boolean run(@NonNull TelemetryPacket packet) {
                if (!initialized) {
                    initialized = true;
                }

                open().setPower(Constants.Shooter.MAX_POWER);
                _shotCount = 0;
                setShootComplete(false);
                SHOT_DELAY.reset();
                return false;
            }
        };
    }

    public Action shoot() {
        return new Action() {
            private boolean initialized = false;

            @Override
            public boolean run(@NonNull TelemetryPacket packet) {
                if (!initialized) {
                    initialized = true;
                }

                packet.put("Is In Range", isInRange());
                packet.put("Is Moving", GREEN_BALL_POSITION.getMoveSpindexer());
                packet.put("Shot Count", _shotCount);
                packet.put("Left Current", _left.getCurrent(CurrentUnit.MILLIAMPS));
                packet.put("Right Current", _right.getCurrent(CurrentUnit.MILLIAMPS));

                if (SHOT_DELAY.allowExec() && !GREEN_BALL_POSITION.getMoveSpindexer()) {
                    if (_shotCount == 0) GREEN_BALL_POSITION.setSpindexerCurrentPos(GREEN_BALL_POSITION.getSpindexerCurrentPos() + 1);
                    else GREEN_BALL_POSITION.setSpindexerCurrentPos(GREEN_BALL_POSITION.getSpindexerCurrentPos() + 2);
                    GREEN_BALL_POSITION.setMoveSpindexer(true);

                    _shotCount++;
                }

                boolean isComplete = _shotCount > 3;
                setShootComplete(isComplete);

                packet.put("Is In Range", isInRange());
                packet.put("Is Moving", GREEN_BALL_POSITION.getMoveSpindexer());
                packet.put("Shot Count", _shotCount);
                packet.put("Left Current", _left.getCurrent(CurrentUnit.MILLIAMPS));
                packet.put("Right Current", _right.getCurrent(CurrentUnit.MILLIAMPS));

                if (isComplete) packet.put("Status Shooter shoot", "Finished");
                else packet.put("Status Shooter shoot", "Running");
                return !isComplete;
            }
        };
    }

    public Action stop() {
        return new Action() {
            private boolean initialized = false;

            @Override
            public boolean run(@NonNull TelemetryPacket packet) {
                if (!initialized) {
                    initialized = true;
                }

                close().setPower(0);
                return false;
            }
        };
    }
}
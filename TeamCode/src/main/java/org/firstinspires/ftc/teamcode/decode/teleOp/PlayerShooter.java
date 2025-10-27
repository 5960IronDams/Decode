package org.firstinspires.ftc.teamcode.decode.teleOp;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;
import org.firstinspires.ftc.teamcode.decode.Constants;
import org.firstinspires.ftc.teamcode.decode.core.GreenBallPosition;
import org.firstinspires.ftc.teamcode.ironDams.core.WaitFor;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BooleanSupplier;

public class PlayerShooter {
    private final Servo _servo;
    private final DcMotorEx _left;
    private final DcMotorEx _right;
    private final GreenBallPosition GREEN_BALL_POSITION;
    private final WaitFor SHOT_DELAY = new WaitFor(1500);

    private final Gamepad GAME_PAD_2;

    private final AtomicBoolean shootComplete = new AtomicBoolean(false);
    private int _shotCount = 4;

    public void setShootComplete(boolean driveCompleted) {
        shootComplete.set(driveCompleted);
    }

    public BooleanSupplier getShootComplete() {
        return shootComplete::get;
    }

    public PlayerShooter(LinearOpMode opMode, GreenBallPosition greenBallPosition) {
        _servo = opMode.hardwareMap.get(Servo.class, Constants.Shooter.LAUNCHER_ID);
        _servo.setDirection(Servo.Direction.REVERSE);

        _left = opMode.hardwareMap.get(DcMotorEx.class, Constants.Shooter.MOTOR_LEFT_ID);
        _right = opMode.hardwareMap.get(DcMotorEx.class, Constants.Shooter.MOTOR_RIGHT_ID);

        _right.setDirection(DcMotorEx.Direction.REVERSE);

        _left.setMode(DcMotorEx.RunMode.RUN_WITHOUT_ENCODER);
        _right.setMode(DcMotorEx.RunMode.RUN_WITHOUT_ENCODER);

//        _left.setVelocity(450);
//        _right.setVelocity(450);

        _left.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.FLOAT);
        _right.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.FLOAT);

        close();

        GAME_PAD_2 = opMode.gamepad2;
        GREEN_BALL_POSITION = greenBallPosition;
    }

    /**
     * The controls are also listened to in PlayerSpindexer
     */
    public void open() {
        if (GAME_PAD_2.dpad_up) {
            _shotCount = 4;
            _servo.setPosition(Constants.Shooter.OPEN_POS);
            _left.setPower(Constants.Shooter.MAX_POWER);
            _right.setPower(Constants.Shooter.MAX_POWER);
        } else if (GAME_PAD_2.dpad_down) {
            _shotCount = 0;
            _servo.setPosition(Constants.Shooter.OPEN_POS);
            _left.setPower(Constants.Shooter.MAX_POWER);
            _right.setPower(Constants.Shooter.MAX_POWER);
        } else if (GAME_PAD_2.dpad_left) {
            close().stop();
        }
    }

    public PlayerShooter close() {
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

    public double getTicksPerSecondLeft() {
        return _left.getVelocity();
    }

    public double getTicksPerSecondRight() {
        return _right.getVelocity();
    }

    public double getEncoderLeft() {
        return _left.getCurrentPosition();
    }

    public double getEncoderRight() {
        return _right.getCurrentPosition();
    }

    public boolean isInRange() {
        double leftCurrent = getLeftCurrent(CurrentUnit.MILLIAMPS);
        double rightCurrent = getRightCurrent(CurrentUnit.MILLIAMPS);

        return leftCurrent < Constants.Shooter.MAX_LEFT_CURRENT &&
                leftCurrent > Constants.Shooter.MIN_LEFT_CURRENT &&
                rightCurrent < Constants.Shooter.MAX_RIGHT_CURRENT &&
                rightCurrent > Constants.Shooter.MIN_RIGHT_CURRENT;


    }

    public Action shootAction() {
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

                if (SHOT_DELAY.allowExec() && !GREEN_BALL_POSITION.getMoveSpindexer() && _shotCount < 4) {
                    if (_shotCount == 0) GREEN_BALL_POSITION.setSpindexerCurrentPos(GREEN_BALL_POSITION.getSpindexerCurrentPos() + 1);
                    else GREEN_BALL_POSITION.setSpindexerCurrentPos(GREEN_BALL_POSITION.getSpindexerCurrentPos() + 2);
                    GREEN_BALL_POSITION.setMoveSpindexer(true);

                    _shotCount++;
                }

                boolean isComplete = _shotCount > 3;

                if (isComplete) {
                    close().stop();
                }


                packet.put("Is In Range", isInRange());
                packet.put("Is Moving", GREEN_BALL_POSITION.getMoveSpindexer());
                packet.put("Shot Count", _shotCount);
                packet.put("Shot UP", GAME_PAD_2.dpad_up);
                packet.put("Shot LEFT", GAME_PAD_2.dpad_left);
                packet.put("Shot DOWN", GAME_PAD_2.dpad_down);
                packet.put("Shot RIGHT", GAME_PAD_2.dpad_right);
                packet.put("Shot Count", _shotCount);
                packet.put("Left Current", _left.getCurrent(CurrentUnit.MILLIAMPS));
                packet.put("Right Current", _right.getCurrent(CurrentUnit.MILLIAMPS));

               return true;
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
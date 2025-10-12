package org.firstinspires.ftc.teamcode.decode.core;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotorEx;

import org.firstinspires.ftc.teamcode.decode.Constants;

/**
 * Contains the logic for running the intake.
 */
public class Intake {
    private final LinearOpMode _opMode;
    private final DcMotorEx _motor;
    /**
     * The current mode of the intake. <b>ACTIVE, INACTIVE</b>
     */
    private Constants.Intake.Mode _mode = Constants.Intake.Mode.ACTIVE;

    /**
     * Creates a new intake object.
     * @param opMode The current op mode.
     */
    public Intake(LinearOpMode opMode) {
        _opMode = opMode;
        _motor = opMode.hardwareMap.get(DcMotorEx.class, Constants.Intake.INTAKE_ID);

        _motor.setDirection(DcMotorEx.Direction.REVERSE);
        _motor.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);
        _motor.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.FLOAT);
    }

    /**
     * Gets the current mode of the intake.
     * @return The current mode of the intake. <b>ACTIVE, INACTIVE</b>
     */
    public Constants.Intake.Mode getMode() {
        return _mode;
    }

    /**
     * Sets the mode of the intake.<br>
     * <u>GAMEPAD2</u>
     * <ul><li>Right Trigger - ACTIVE</li><li>Left Trigger - INACTIVE</li></ul>
     * @return The intake object.
     */
    public Intake setMode() {
        if (_opMode.gamepad2.right_trigger != 0) {
            _mode = Constants.Intake.Mode.ACTIVE;
            _opMode.sleep(Constants.WAIT_DURATION_MS);
        } else if (_opMode.gamepad2.left_trigger != 0) {
            _mode = Constants.Intake.Mode.INACTIVE;
            _opMode.sleep(Constants.WAIT_DURATION_MS);
        }

        return this;
    }

    public void stop() {
        _motor.setPower(0);
    }

    public void setPower() {
        _motor.setPower(Constants.Intake.MAX_POWER);
    }

    public double getPower() {
        return _motor.getPower();
    }
}

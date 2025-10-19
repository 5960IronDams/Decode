package org.firstinspires.ftc.teamcode.decode.core;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotorEx;

import org.firstinspires.ftc.teamcode.decode.Constants;
import org.firstinspires.ftc.teamcode.ironDams.core.WaitFor;

/**
 * Contains the logic for running the intake.
 */
public class Intake {
    private final LinearOpMode OP_MODE;
    private final DcMotorEx MOTOR;

    private final WaitFor USER_BTN_DELAY = new WaitFor(Constants.WAIT_DURATION_MS);

    /**
     * The current mode of the intake. <b>ACTIVE, INACTIVE</b>
     */
    private Constants.Intake.Mode _mode = Constants.Intake.Mode.ACTIVE;

    /**
     * Creates a new intake object.
     * @param opMode The current op mode.
     */
    public Intake(LinearOpMode opMode) {
        OP_MODE = opMode;
        MOTOR = opMode.hardwareMap.get(DcMotorEx.class, Constants.Intake.INTAKE_ID);

        MOTOR.setDirection(DcMotorEx.Direction.REVERSE);
        MOTOR.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);
        MOTOR.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.FLOAT);
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
     */
    public void setMode() {
        if (OP_MODE.gamepad2.right_trigger != 0 && USER_BTN_DELAY.allowExec()) {
            _mode = Constants.Intake.Mode.ACTIVE;
        } else if (OP_MODE.gamepad2.left_trigger != 0 && USER_BTN_DELAY.allowExec()) {
            _mode = Constants.Intake.Mode.INACTIVE;
        }

    }

    public void stop() {
        MOTOR.setPower(0);
    }

    public void setPower(double power) {
        MOTOR.setPower(power);
    }

    public double getPower() {
        return MOTOR.getPower();
    }
}

package org.firstinspires.ftc.teamcode.decode.teleOp;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.decode.Constants;
import org.firstinspires.ftc.teamcode.ironDams.core.WaitFor;
import org.firstinspires.ftc.teamcode.ironDams.core.driveTrain.GyroMecanumDrive;
import org.firstinspires.ftc.teamcode.ironDams.core.driveTrain.IDriveTrain;
import org.firstinspires.ftc.teamcode.ironDams.core.driveTrain.MecanumDrive;

public class WooshMachine {
    private final LinearOpMode OP_MODE;
    private final boolean USE_PINPOINT;

    private final WaitFor USER_DELAY = new WaitFor(Constants.WAIT_DURATION_MS);

    private final GyroMecanumDrive FIELD_DRIVE_VIEW;
    private final MecanumDrive ROBOT_DRIVE_VIEW;
    private IDriveTrain _driveTrain = null;
    private boolean _isGyro = true;

    public WooshMachine(LinearOpMode opMode, boolean usePinpoint) {
        OP_MODE = opMode;
        USE_PINPOINT = usePinpoint;
        FIELD_DRIVE_VIEW = new GyroMecanumDrive(opMode);
        ROBOT_DRIVE_VIEW = new MecanumDrive(opMode);
        _driveTrain = FIELD_DRIVE_VIEW;
    }

    private boolean switchDriveTrain(){
        if (OP_MODE.gamepad1.right_trigger != 0 && USER_DELAY.allowExec()) {
            _isGyro = !_isGyro;
        }

        return _isGyro;
    }

    private void checkDriveTrain() {
        if (switchDriveTrain()) {
            _driveTrain = FIELD_DRIVE_VIEW;
            OP_MODE.telemetry.addData("Drive", "Field View");
        } else {
            _driveTrain = ROBOT_DRIVE_VIEW;
            OP_MODE.telemetry.addData("Drive", "Robot View");
        }
    }

    public Action runDrive() {
        return new Action() {
            private boolean initialized = false;

            @Override
            public boolean run(@NonNull TelemetryPacket packet) {
                if (!initialized) {
                    initialized = _driveTrain != null;
                } else {
                    checkDriveTrain();
                    _driveTrain.drive(
                            OP_MODE.gamepad1.right_stick_x,
                            -OP_MODE.gamepad1.right_stick_y,
                            OP_MODE.gamepad1.left_stick_x
                    );
                }

                return true;
            }
        };
    }
}
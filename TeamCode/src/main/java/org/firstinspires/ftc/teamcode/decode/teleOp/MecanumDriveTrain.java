package org.firstinspires.ftc.teamcode.decode.teleOp;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.Gamepad;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.decode.Constants;
import org.firstinspires.ftc.teamcode.ironDams.core.WaitFor;
import org.firstinspires.ftc.teamcode.ironDams.core.driveTrain.GyroMecanumDrive;
import org.firstinspires.ftc.teamcode.ironDams.core.driveTrain.IDriveTrain;
import org.firstinspires.ftc.teamcode.ironDams.core.driveTrain.MecanumDrive;

public class MecanumDriveTrain {
    private final Telemetry TELEMETRY;
    private final Gamepad GAME_PAD_1;

    private final MecanumDrive ROBO_VIEW;
    private final GyroMecanumDrive FIELD_PER;

    private final WaitFor USER_DELAY = new WaitFor(Constants.WAIT_DURATION_MS);

    private IDriveTrain activeDrive;
    private boolean _isFieldPer = true;

    public MecanumDriveTrain(LinearOpMode opMode) {
        GAME_PAD_1 = opMode.gamepad1;

        TELEMETRY = opMode.telemetry;
        ROBO_VIEW = new MecanumDrive(opMode);
        FIELD_PER = new GyroMecanumDrive(opMode);

        activeDrive = _isFieldPer ? FIELD_PER : ROBO_VIEW;
    }

    public void switchDrive() {
        if (GAME_PAD_1.right_trigger != 0 && USER_DELAY.allowExec()) {
            _isFieldPer = !_isFieldPer;
            activeDrive = _isFieldPer ? FIELD_PER : ROBO_VIEW;
        }

        TELEMETRY.addData("Drive Mode", _isFieldPer ? "Field": "Robot");
    }

    public Action runDriveAction() {
        return new Action() {
            private boolean initialized = false;

            @Override
            public boolean run(@NonNull TelemetryPacket packet) {
                if (!initialized) {
                    initialized = true;
                }

                switchDrive();

                activeDrive.drive(
                        GAME_PAD_1.right_stick_x,
                        -GAME_PAD_1.right_stick_y,
                        GAME_PAD_1.left_stick_x
                );

                packet.put("Player Drive Mode", _isFieldPer ? "Field": "Robot");

                packet.put("Player Drive RT", GAME_PAD_1.left_trigger);
                packet.put("Player Drive LY", GAME_PAD_1.left_stick_y);
                packet.put("Player Drive LX", GAME_PAD_1.left_stick_x);
                packet.put("Player Drive RY", GAME_PAD_1.right_stick_y);
                packet.put("Player Drive RX", GAME_PAD_1.right_stick_x);

                return true;
            }
        };
    }
}

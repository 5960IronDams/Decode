package org.firstinspires.ftc.teamcode.ironDams.core.driveTrain;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.Gamepad;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.ironDams.Config;
import org.firstinspires.ftc.teamcode.ironDams.core.WaitFor;

public class MecanumDrive {
    private final Telemetry TELEMETRY;
    private final Gamepad GAMEPAD1;

    private final MecanumDriveTrain ROBO_VIEW;
    private final GyroMecanumDriveTrain FIELD_PER;

    private final WaitFor USER_DELAY = new WaitFor(Config.USER_DELAY_MS);

    private IDriveTrain activeDrive;
    private boolean _isFieldPer = true;

    public MecanumDrive(LinearOpMode opMode) {
        GAMEPAD1 = opMode.gamepad1;
        TELEMETRY = opMode.telemetry;
        ROBO_VIEW = new MecanumDriveTrain(opMode);
        FIELD_PER = new GyroMecanumDriveTrain(opMode);

        activeDrive = _isFieldPer ? FIELD_PER : ROBO_VIEW;
    }

    public void switchDrive() {
        if (GAMEPAD1.right_trigger != 0 && USER_DELAY.allowExec()) {
            _isFieldPer = !_isFieldPer;
            activeDrive = _isFieldPer ? FIELD_PER : ROBO_VIEW;
        }

        TELEMETRY.addData("Drive Mode", _isFieldPer ? "Field": "Robot");
    }

    public void drive() {
        activeDrive.drive(
                GAMEPAD1.right_stick_x,
                -GAMEPAD1.right_stick_y,
                GAMEPAD1.left_stick_x
        );
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

                packet.put("Player Drive Mode", _isFieldPer ? "Field": "Robot");

                packet.put("Player Drive RT", GAMEPAD1.left_trigger);
                packet.put("Player Drive LY", GAMEPAD1.left_stick_y);
                packet.put("Player Drive LX", GAMEPAD1.left_stick_x);
                packet.put("Player Drive RY", GAMEPAD1.right_stick_y);
                packet.put("Player Drive RX", GAMEPAD1.right_stick_x);

                return true;
            }
        };
    }
}
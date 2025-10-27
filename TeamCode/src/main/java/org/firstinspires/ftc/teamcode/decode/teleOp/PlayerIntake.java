package org.firstinspires.ftc.teamcode.decode.teleOp;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.Gamepad;

import org.firstinspires.ftc.teamcode.decode.Constants;
import org.firstinspires.ftc.teamcode.decode.core.Intake;

public class PlayerIntake {
    private final Gamepad GAME_PAD_2;

    private final Intake _intake;

    public PlayerIntake(LinearOpMode opMode) {
        GAME_PAD_2 = opMode.gamepad2;

        _intake = new Intake(opMode);
    }

    public void toggleIntakePower() {
        if (GAME_PAD_2.right_trigger != 0) {
            _intake.setPower(Constants.Intake.MAX_POWER);
        } else if (GAME_PAD_2.left_trigger != 0) {
            _intake.setPower(0);
        }
    }

    public Action powerAction(double pow) {
        return new Action() {
            private boolean initialized = false;

            @Override
            public boolean run(@NonNull TelemetryPacket packet) {
                if (!initialized) {
                    initialized = true;
                }

                _intake.setPower(pow);

                return false;
            }
        };
    }

    public Action runIntakeAction() {
        return new Action() {
            private boolean initialized = false;

            @Override
            public boolean run(@NonNull TelemetryPacket packet) {
                if (!initialized) {
                    initialized = true;
                }

                toggleIntakePower();

                packet.put("Intake Power", _intake.getPower());
                packet.put("Intake LT2", GAME_PAD_2.left_trigger);
                packet.put("Intake RT2", GAME_PAD_2.right_trigger);

                return true;
            }
        };
    }
}

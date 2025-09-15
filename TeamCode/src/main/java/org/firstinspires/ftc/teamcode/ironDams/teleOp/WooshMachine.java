package org.firstinspires.ftc.teamcode.ironDams.teleOp;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.ironDams.core.driveTrain.GyroMecanumDrive;
import org.firstinspires.ftc.teamcode.ironDams.core.driveTrain.IDriveTrain;

public class WooshMachine {
    IDriveTrain _driveTrain = null;

    public WooshMachine(LinearOpMode opMode, boolean usePinpoint) {
        _driveTrain = new GyroMecanumDrive(opMode.hardwareMap, opMode.gamepad1, usePinpoint);
    }

    public Action runDrive() {
        return new Action() {
            private boolean initialized = false;

            @Override
            public boolean run(@NonNull TelemetryPacket packet) {
                if (!initialized) {
                    initialized = true;
                }

                _driveTrain.drive();
                return true;
            }
        };
    }
}
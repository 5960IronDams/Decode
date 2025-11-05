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
    private final MecanumDriveTrain ROBO_VIEW;
    private final GyroMecanumDriveTrain FIELD_PER;

    private IDriveTrain activeDrive;
    private boolean _isFieldPer = false;

    public MecanumDrive(LinearOpMode opMode) {
        FourWheelDriveTrain dt = new FourWheelDriveTrain(opMode.hardwareMap);
        ROBO_VIEW = new MecanumDriveTrain(dt);
        FIELD_PER = new GyroMecanumDriveTrain(opMode, dt);
        activeDrive = _isFieldPer ? FIELD_PER : ROBO_VIEW;
    }

    public void resetFieldView() {
        FIELD_PER.reset();
    }

    public boolean switchDrive() {
        _isFieldPer = !_isFieldPer;
        activeDrive = _isFieldPer ? FIELD_PER : ROBO_VIEW;
        return _isFieldPer;
    }

    public void drive(double x, double y, double turn) {
        activeDrive.drive(x, -y, turn);
    }
}
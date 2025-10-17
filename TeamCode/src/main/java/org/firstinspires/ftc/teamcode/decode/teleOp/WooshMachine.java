package org.firstinspires.ftc.teamcode.decode.teleOp;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.decode.Constants;
import org.firstinspires.ftc.teamcode.ironDams.core.driveTrain.GyroMecanumDrive;
import org.firstinspires.ftc.teamcode.ironDams.core.driveTrain.IDriveTrain;
import org.firstinspires.ftc.teamcode.ironDams.core.driveTrain.MecanumDrive;

public class WooshMachine {
    private final LinearOpMode _opMode;
    private final boolean _usePinpoint;

    private final ElapsedTime _timer;
    private IDriveTrain _driveTrain = null;
    private boolean _isGyro = true;
    public WooshMachine(LinearOpMode opMode, boolean usePinpoint) {
        _opMode = opMode;
        _timer = new ElapsedTime();
        _usePinpoint = usePinpoint;
        _driveTrain = new GyroMecanumDrive(opMode);
    }

    private boolean switchDriveTrain(){
        if(_opMode.gamepad1.right_trigger != 0 && _timer.milliseconds() > Constants.WAIT_DURATION_MS){
            _isGyro = !_isGyro;
            _timer.reset();
        }

        return _isGyro;
    }

    private void checkDriveTrain() {
        if(switchDriveTrain()){
            _driveTrain = new MecanumDrive(_opMode);
        } else {
            _driveTrain = new GyroMecanumDrive(_opMode);
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
//                    checkDriveTrain();
                    _driveTrain.drive();
                }
                return true;
            }
        };
    }
}
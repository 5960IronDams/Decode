package org.firstinspires.ftc.teamcode.irondams.core.driveTrain;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

public class MecanumDrive {
    private final MecanumDriveTrain ROBO_VIEW;
    private final GyroMecanumDriveTrain FIELD_PER;

    private IDriveTrain activeDrive;
    private boolean _isFieldPer = true;

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

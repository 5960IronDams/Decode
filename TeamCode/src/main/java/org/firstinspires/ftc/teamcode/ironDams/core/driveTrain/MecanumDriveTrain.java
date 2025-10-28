package org.firstinspires.ftc.teamcode.ironDams.core.driveTrain;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

public class MecanumDriveTrain implements IDriveTrain {
    public MecanumDriveTrain(LinearOpMode opMode)  {
        FourWheelDriveTrain.init(opMode.hardwareMap);
    }

    public void init() { }

    @Override
    public void drive(double horizontal, double vertical, double pivot) {
        double flp = (pivot + vertical + horizontal);
        double frp = (-pivot + (vertical - horizontal));
        double rlp = (pivot + (vertical - horizontal));
        double rrp = (-pivot + vertical + horizontal);

        FourWheelDriveTrain.getLeftBackDrive().setPower(rlp);
        FourWheelDriveTrain.getRightBackDrive().setPower(rrp);
        FourWheelDriveTrain.getLeftFrontDrive().setPower(flp);
        FourWheelDriveTrain.getRightFrontDrive().setPower(frp);
    }
}
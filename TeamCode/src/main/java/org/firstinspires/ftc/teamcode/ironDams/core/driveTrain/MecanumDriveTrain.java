package org.firstinspires.ftc.teamcode.ironDams.core.driveTrain;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.ironDams.core.Acceleration;

public class MecanumDriveTrain implements IDriveTrain {
    private double currentHorizontal = 0;
    private double currentVertical = 0;
    private double currentPivot = 0;

    public MecanumDriveTrain(LinearOpMode opMode)  {
        FourWheelDriveTrain.init(opMode.hardwareMap);
    }

    @Override
    public void drive(double horizontal, double vertical, double pivot) {
        double newHorizontal = Acceleration.rampPower(currentHorizontal, horizontal);
        horizontal = newHorizontal;
        currentHorizontal = newHorizontal;

        double newVertical = Acceleration.rampPower(currentVertical, vertical);
        vertical = newVertical;
        currentVertical = newVertical;

        double newPivot = Acceleration.rampPower(currentPivot, pivot);
        pivot = newPivot;
        currentPivot = newPivot;

        double flp = (pivot + vertical + horizontal);
        double frp = (-pivot + (vertical - horizontal));
        double rlp = (pivot + (vertical - horizontal));
        double rrp = (-pivot + vertical + horizontal);

        FourWheelDriveTrain.getLeftBackDrive().setPower(rlp);
        FourWheelDriveTrain.getRightBackDrive().setPower(rrp);
        FourWheelDriveTrain.getLeftFrontDrive().setPower(flp);
        FourWheelDriveTrain.getRightFrontDrive().setPower(frp);
    }

    @Override
    public double getLeftPower() {
        return FourWheelDriveTrain.getLeftFrontDrive().getPower();
    }

    @Override
    public double getRightPower() {
        return FourWheelDriveTrain.getRightFrontDrive().getPower();
    }
}
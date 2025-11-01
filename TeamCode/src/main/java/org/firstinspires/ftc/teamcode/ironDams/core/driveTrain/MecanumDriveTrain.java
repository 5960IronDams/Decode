package org.firstinspires.ftc.teamcode.ironDams.core.driveTrain;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.ironDams.core.Acceleration;

public class MecanumDriveTrain implements IDriveTrain {
    private double currentHorizontal = 0;
    private double currentVertical = 0;
    private double currentPivot = 0;

    public final FourWheelDriveTrain DRIVETRAIN;


    public MecanumDriveTrain(FourWheelDriveTrain driveTrain)  {
        DRIVETRAIN = driveTrain;
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

        DRIVETRAIN.getLeftBackDrive().setPower(rlp);
        DRIVETRAIN.getRightBackDrive().setPower(rrp);
        DRIVETRAIN.getLeftFrontDrive().setPower(flp);
        DRIVETRAIN.getRightFrontDrive().setPower(frp);
    }
}
package org.firstinspires.ftc.teamcode.irondams.core.driveTrain;

import com.qualcomm.robotcore.hardware.DcMotorEx;

public class MecanumDriveTrain implements IDriveTrain {
    private double currentHorizontal = 0;
    private double currentVertical = 0;
    private double currentPivot = 0;

    public final FourWheelDriveTrain DRIVETRAIN;
    public final boolean ISAUTO;

    public MecanumDriveTrain(FourWheelDriveTrain driveTrain)  {
        DRIVETRAIN = driveTrain;
        ISAUTO = false;
    }

    public MecanumDriveTrain(FourWheelDriveTrain driveTrain, boolean isAuto)  {
        DRIVETRAIN = driveTrain;
        ISAUTO = isAuto;
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

        if (ISAUTO && vertical == 0 && pivot == 0) {
            flp *= 0.9333333333;
            frp *= 0.9733333333;
            rrp *= 0.96;
        }

        DRIVETRAIN.getLeftBackDrive().setPower(rlp);
        DRIVETRAIN.getRightBackDrive().setPower(rrp);
        DRIVETRAIN.getLeftFrontDrive().setPower(flp);
        DRIVETRAIN.getRightFrontDrive().setPower(frp);
    }

    public DcMotorEx[] getMotors() {
        return new DcMotorEx[] {
                DRIVETRAIN.getLeftBackDrive(),
                DRIVETRAIN.getRightBackDrive(),
                DRIVETRAIN.getLeftFrontDrive(),
                DRIVETRAIN.getRightFrontDrive()
        };
    }
}
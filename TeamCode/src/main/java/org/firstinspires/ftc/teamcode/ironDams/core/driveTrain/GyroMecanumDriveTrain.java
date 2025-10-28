package org.firstinspires.ftc.teamcode.ironDams.core.driveTrain;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.Gamepad;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.firstinspires.ftc.teamcode.ironDams.Config;
import org.firstinspires.ftc.teamcode.ironDams.core.WaitFor;

public class GyroMecanumDriveTrain implements IDriveTrain{
    private final Gamepad GAMEPAD1;
    private final BNO055IMU IMU;
    private final BNO055IMU.Parameters PARAMETERS = new BNO055IMU.Parameters();
    private final WaitFor USER_DELAY = new WaitFor(Config.USER_DELAY_MS);

    private Orientation angles = new Orientation();
    private double initYaw;

    public GyroMecanumDriveTrain(LinearOpMode opMode) {
        GAMEPAD1 = opMode.gamepad1;

        FourWheelDriveTrain.init(opMode.hardwareMap);

        PARAMETERS.angleUnit = BNO055IMU.AngleUnit.DEGREES;
        PARAMETERS.mode = BNO055IMU.SensorMode.IMU;
        PARAMETERS.accelUnit = BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC;
        PARAMETERS.loggingEnabled = false;

        IMU = opMode.hardwareMap.get(BNO055IMU.class, "imu2");

        initImu();
    }

    public void initImu() {
        IMU.initialize(PARAMETERS);
        angles = IMU.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);
        initYaw = angles.firstAngle;
    }

    public void reset() {
        if (GAMEPAD1.b && USER_DELAY.allowExec()) {
            initImu();
        }
    }

    public void drive(double x, double y, double turn) {
        angles = IMU.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);

        double zeroedYaw = -initYaw + angles.firstAngle;
        double theta = Math.atan2(y, x) * 180 / Math.PI; // aka angle
        double realTheta = (360 - zeroedYaw) + theta;
        double power = Math.hypot(x, y);
        double sin = Math.sin((realTheta * (Math.PI / 180)) - (Math.PI / 4));
        double cos = Math.cos((realTheta * (Math.PI / 180)) - (Math.PI / 4));
        double maxSinCos = Math.max(Math.abs(sin), Math.abs(cos));
        double leftFront = (power * cos / maxSinCos + turn);
        double rightFront = (power * sin / maxSinCos - turn);
        double leftBack = (power * sin / maxSinCos + turn);
        double rightBack = (power * cos / maxSinCos - turn);

        if ((power + Math.abs(turn)) > 1) {
            leftFront /= power + turn;
            rightFront /= power - turn;
            leftBack /= power + turn;
            rightBack /= power - turn;
        }

        FourWheelDriveTrain.getLeftFrontDrive().setPower(leftFront);
        FourWheelDriveTrain.getRightFrontDrive().setPower(rightFront);
        FourWheelDriveTrain.getLeftBackDrive().setPower(leftBack);
        FourWheelDriveTrain.getRightBackDrive().setPower(rightBack);

        reset();
    }
}
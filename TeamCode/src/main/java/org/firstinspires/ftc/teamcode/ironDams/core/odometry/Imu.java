package org.firstinspires.ftc.teamcode.ironDams.core.odometry;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.firstinspires.ftc.robotcore.external.navigation.Pose2D;

public class Imu implements IGyro {
    private final BNO055IMU _imu;

    private Orientation _angles = new Orientation();

    private double _initYaw;

    public Imu(HardwareMap hardwareMap) {
        _imu = hardwareMap.get(BNO055IMU.class, "imu");
        init();
    }

    private void init() {
        BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();
        parameters.angleUnit = BNO055IMU.AngleUnit.DEGREES;
        parameters.mode = BNO055IMU.SensorMode.IMU;
        parameters.accelUnit = BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC;
        parameters.loggingEnabled = false;
        _imu.initialize(parameters);

        _angles = _imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);
        _initYaw = _angles.firstAngle;
    }

    public double update() {
        _angles = _imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);
        return -_initYaw + _angles.firstAngle;
    }

    public void reset() {
        _initYaw = _imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES).firstAngle;
    }

    @Override
    public Pose2D getPose() {
        return new Pose2D(DistanceUnit.INCH, _angles.thirdAngle, _angles.secondAngle, AngleUnit.DEGREES, _angles.firstAngle);
    }
}

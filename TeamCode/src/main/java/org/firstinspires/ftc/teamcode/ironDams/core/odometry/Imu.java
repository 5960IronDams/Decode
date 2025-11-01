package org.firstinspires.ftc.teamcode.ironDams.core.odometry;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;
import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.firstinspires.ftc.robotcore.external.navigation.Pose2D;
import org.firstinspires.ftc.teamcode.ironDams.Config;

public class Imu implements IGyro {
    private final BNO055IMU IMU;

    private Orientation _angles = new Orientation();
    private double _initYaw;

    public Imu(HardwareMap hardwareMap) {
        IMU = hardwareMap.get(BNO055IMU.class, Config.Hardware.Gyros.EXP_IMU_ID);
        init();
    }

    private void init() {
        BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();
        parameters.angleUnit = BNO055IMU.AngleUnit.DEGREES;
        parameters.mode = BNO055IMU.SensorMode.IMU;
        parameters.accelUnit = BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC;
        parameters.loggingEnabled = false;
        IMU.initialize(parameters);

        _angles = IMU.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);
        _initYaw = _angles.firstAngle;
    }

    @Override
    public double update() {
        _angles = IMU.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);
        return -_initYaw + _angles.firstAngle;
    }

    @Override
    public void reset() {
        _initYaw = IMU.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES).firstAngle;
    }

    @Override
    public Pose2D getPose() {
        return new Pose2D(DistanceUnit.INCH, _angles.thirdAngle, _angles.secondAngle, AngleUnit.DEGREES, _angles.firstAngle);
    }

    public Action telemetryAction() {
        return new Action() {
            private boolean initialized = false;

            @Override
            public boolean run(@NonNull TelemetryPacket packet) {
                if (!initialized) {
                    initialized = true;
                }

                Pose2D pos = getPose();

                packet.put("Pinpoint x", pos.getX(DistanceUnit.INCH));
                packet.put("Pinpoint y", pos.getY(DistanceUnit.INCH));
                packet.put("Pinpoint z", pos.getHeading(AngleUnit.DEGREES));

                return true;
            }
        };
    }
}
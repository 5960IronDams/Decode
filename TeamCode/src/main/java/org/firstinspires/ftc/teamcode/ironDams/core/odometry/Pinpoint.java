package org.firstinspires.ftc.teamcode.ironDams.core.odometry;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose2D;

public class Pinpoint implements IGyro {

    private final GoBildaPinpointDriver _pinpoint;
    private double _initYaw;

    public Pinpoint(HardwareMap hardwareMap, Pose2D pose) {
        _pinpoint = hardwareMap.get(GoBildaPinpointDriver.class, "pinpoint");
        init(pose);
    }

    private void init(Pose2D pose) {
        _pinpoint.setOffsets(0.0, 0.0, DistanceUnit.INCH);
        _pinpoint.setEncoderResolution(GoBildaPinpointDriver.GoBildaOdometryPods.goBILDA_4_BAR_POD);
        _pinpoint.setEncoderDirections(GoBildaPinpointDriver.EncoderDirection.FORWARD, GoBildaPinpointDriver.EncoderDirection.FORWARD);
        _pinpoint.resetPosAndIMU();
        _pinpoint.setPosition(pose);

        _initYaw = getPose().getHeading(AngleUnit.DEGREES);

    }
    public double update() {
        return -_initYaw + getPose().getHeading(AngleUnit.DEGREES);
    }

    public void reset() {
        _initYaw = getPose().getHeading(AngleUnit.DEGREES);
    }

    public Pose2D getPose() {
        _pinpoint.update();
        return _pinpoint.getPosition();

    }
    public Action pinpointTelemetry() {
        return new Action() {
            private boolean initialized = false;

            @Override
            public boolean run(@NonNull TelemetryPacket packet) {
                if (!initialized) {
                    initialized = true;
                }
                _pinpoint.update();
                packet.put("x", _pinpoint.getEncoderX());
                packet.put("y", _pinpoint.getEncoderY());
                packet.put("Z", _pinpoint.getHeading(AngleUnit.DEGREES));
                return true;
            }
        };
    }
}

package org.firstinspires.ftc.teamcode.irondams.core.odometry;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose2D;

public class Pinpoint implements IGyro {
    private final GoBildaPinpointDriver PINPOINT;

    private double _initYaw;

    public Pinpoint(LinearOpMode opMode) {
        PINPOINT = opMode.hardwareMap.get(GoBildaPinpointDriver.class, "pinpoint");
        init();
    }

    private void init() {
        PINPOINT.setOffsets(0.0, 0.0, DistanceUnit.INCH);
        PINPOINT.setEncoderResolution(GoBildaPinpointDriver.GoBildaOdometryPods.goBILDA_4_BAR_POD);
        PINPOINT.setEncoderDirections(GoBildaPinpointDriver.EncoderDirection.FORWARD, GoBildaPinpointDriver.EncoderDirection.FORWARD);
        PINPOINT.resetPosAndIMU();

        _initYaw = getPose().getHeading(AngleUnit.DEGREES);
    }

    @Override
    public double update() {
        return -_initYaw + getPose().getHeading(AngleUnit.DEGREES);
    }

    @Override
    public void reset() {
        _initYaw = getPose().getHeading(AngleUnit.DEGREES);
    }

    @Override
    public Pose2D getPose() {
        PINPOINT.update();
        return PINPOINT.getPosition();

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
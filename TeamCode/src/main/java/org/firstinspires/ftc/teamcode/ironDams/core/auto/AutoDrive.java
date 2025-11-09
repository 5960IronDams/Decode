package org.firstinspires.ftc.teamcode.irondams.core.auto;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose2D;
import org.firstinspires.ftc.teamcode.irondams.core.driveTrain.Acceleration;
import org.firstinspires.ftc.teamcode.irondams.core.Logger;
import org.firstinspires.ftc.teamcode.irondams.core.driveTrain.IDriveTrain;
import org.firstinspires.ftc.teamcode.irondams.core.odometry.IGyro;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BooleanSupplier;

public class AutoDrive {
    private final IDriveTrain DRIVE_TRAIN;
    private final IGyro PINPOINT;
    private final double TOLERANCE = 1.0;

    private final ElapsedTime TIMER = new ElapsedTime();

    private  final Logger LOG;

    private double _startPos = 0;


    private final AtomicBoolean driveComplete = new AtomicBoolean(false);

    public AutoDrive(IDriveTrain drive, IGyro pinpoint, Logger log) {
        DRIVE_TRAIN = drive;
        PINPOINT = pinpoint;
        LOG = log;
    }

    public void setDriveCompleted(boolean driveCompleted) {
        driveComplete.set(driveCompleted);
    }

    public BooleanSupplier getDriveComplete() {
        return driveComplete::get;
    }

    private double normalizeHeading(double heading) {
        heading = heading % 360;
        if (heading < 0) heading += 360;
        return heading;
    }

    double normalizeAngle(double angle) {
        while (angle <= -Math.PI) angle += 2 * Math.PI;
        while (angle > Math.PI) angle -= 2 * Math.PI;
        return angle;
    }

    public void setStartingYPos() {
        Pose2D pos = PINPOINT.getPose();
        _startPos = pos.getY(DistanceUnit.INCH);
    }

    public void setStartingXPos() {
        Pose2D pos = PINPOINT.getPose();
        _startPos = pos.getX(DistanceUnit.INCH);
    }

    public void setStartingHeadingPos() {
        _startPos = normalizeHeading(PINPOINT.getPose().getHeading(AngleUnit.DEGREES));
    }

    public Action turnTo(double targetPos, double accelToDistance, double decelAtDistance, double minPower, double maxPower) {
        return new Action() {
            private boolean initialized = false;

            @Override
            public boolean run(@NonNull TelemetryPacket packet) {
                if (!initialized) {
                    initialized = true;
                }

                double milli = TIMER.milliseconds();

                double currentPos = normalizeHeading(PINPOINT.getPose().getHeading(AngleUnit.DEGREES));
                double pow = 0;
                double turnPower = 0;
                double minPow = minPower;
                double direction = 0;

                if ((currentPos > targetPos + TOLERANCE) || (currentPos < targetPos - TOLERANCE)) {
                    if (targetPos - currentPos <= decelAtDistance) minPow = 0;
                    double current = normalizeAngle(Math.toRadians(currentPos));
                    double target = normalizeAngle(Math.toRadians(targetPos));
                    double delta = normalizeAngle(target - current); // shortest angular distance
                    direction = Math.signum(delta);

                    pow = Acceleration.getPower(
                            targetPos,
                            currentPos,
                            _startPos,
                            accelToDistance,
                            decelAtDistance,
                            minPow,
                            maxPower);

                    pow = Math.max(minPower, Math.min(pow, maxPower));
                }

                if (pow != 0) turnPower = direction * pow;

                DRIVE_TRAIN.drive(0, 0, -turnPower);

                setDriveCompleted(pow == 0);

                logger(
                        packet, milli, _startPos, targetPos, accelToDistance, decelAtDistance, minPower, minPow,
                        maxPower, currentPos, pow, getDriveComplete().getAsBoolean(), direction
                );

                return pow != 0;
            }
        };
    }

    public Action driveTo(double targetPos, double accelToDistance, double decelAtDistance, double minPower, double maxPower) {
        return new Action() {
            private boolean initialized = false;

            @Override
            public boolean run(@NonNull TelemetryPacket packet) {
                if (!initialized) {
                    initialized = true;
                }

                double milli = TIMER.milliseconds();

                Pose2D pos = PINPOINT.getPose();
                double currentPos = pos.getX(DistanceUnit.INCH);
                double direction = Math.signum(targetPos - currentPos);
                double pow = 0;
                double minPow = minPower;
                if ((currentPos > targetPos + TOLERANCE) || (currentPos < targetPos - TOLERANCE)) {
                    if (targetPos - currentPos <= decelAtDistance) minPow = 0;
                    pow = Acceleration.getPower(
                            _startPos,
                            currentPos,
                            targetPos,
                            accelToDistance,
                            decelAtDistance,
                            minPower,
                            maxPower);

                    pow = Math.max(minPower, Math.min(pow, maxPower));
                }

                DRIVE_TRAIN.drive(0, pow * direction, 0);

                setDriveCompleted(pow == 0);

                logger(
                        packet, milli, _startPos, targetPos, accelToDistance, decelAtDistance, minPower, minPow,
                        maxPower, currentPos, pow, getDriveComplete().getAsBoolean(), direction
                );

                return pow != 0;
            }
        };
    }

    public Action strafeTo(double targetPos, double accelToDistance, double decelAtDistance, double minPower, double maxPower) {
        return new Action() {
            private boolean initialized = false;

            @Override
            public boolean run(@NonNull TelemetryPacket packet) {
                if (!initialized) {
                    initialized = true;
                }

                double milli = TIMER.milliseconds();

                Pose2D pos = PINPOINT.getPose();
                double currentPos = pos.getY(DistanceUnit.INCH);
                double direction = Math.signum(targetPos - currentPos);

                double pow = 0;
                double minPow = minPower;

                if ((currentPos > targetPos + TOLERANCE) || (currentPos < targetPos - TOLERANCE)) {
                    if (targetPos - currentPos <= decelAtDistance) minPow = 0;

                    pow = Acceleration.getPower(
                            targetPos,
                            currentPos,
                            _startPos,
                            accelToDistance,
                            decelAtDistance,
                            minPow,
                            maxPower);

                    pow = Math.max(minPower, Math.min(pow, maxPower));
                }

                DRIVE_TRAIN.drive(-(pow * direction), 0, 0);

                setDriveCompleted(pow == 0);

                logger(
                        packet, milli, _startPos, targetPos, accelToDistance, decelAtDistance, minPower, minPow,
                        maxPower, currentPos, pow, getDriveComplete().getAsBoolean(), direction
                );

                return pow != 0;
            }
        };
    }

    private void logger(
            @NotNull TelemetryPacket packet, double milli, double startPos, double targetPos, double accelToDistance,
            double decelAtDistance, double minPower, double minPow, double maxPower, double currentPos, double pow,
            boolean driveCompleted, double direction
    ) {

        packet.put("Drive Dir", direction);
        packet.put("Drive Pow", pow * direction);
        packet.put("Drive Start", startPos);
        packet.put("Drive Target", targetPos);
        packet.put("Drive Current", currentPos);

        LOG.writeToMemory(milli, "startPos", startPos);
        LOG.writeToMemory(milli, "targetPos", targetPos);
        LOG.writeToMemory(milli, "currentPos", currentPos);
        LOG.writeToMemory(milli, "AccelToDist", accelToDistance);
        LOG.writeToMemory(milli, "DecelToDist", decelAtDistance);
        LOG.writeToMemory(milli, "minPower", minPower);
        LOG.writeToMemory(milli, "minPow", minPow);
        LOG.writeToMemory(milli, "maxPower", maxPower);
        LOG.writeToMemory(milli, "distance", currentPos - startPos);
        LOG.writeToMemory(milli, "remainingDist", targetPos - currentPos);
        LOG.writeToMemory(milli, "power", pow);
        LOG.writeToMemory(milli, "driveCompleted", driveCompleted);
        LOG.writeToMemory(milli, "direction", direction);
        LOG.flushToDisc();
    }
}
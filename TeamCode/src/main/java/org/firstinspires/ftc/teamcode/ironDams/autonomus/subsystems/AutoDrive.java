package org.firstinspires.ftc.teamcode.ironDams.autonomus.subsystems;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose2D;
import org.firstinspires.ftc.teamcode.decode.core.GreenBallPosition;
import org.firstinspires.ftc.teamcode.ironDams.autonomus.Acceleration;
import org.firstinspires.ftc.teamcode.ironDams.core.driveTrain.IDriveTrain;
import org.firstinspires.ftc.teamcode.ironDams.core.odometry.IGyro;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BooleanSupplier;

public class AutoDrive {
    private final LinearOpMode OP_MODE;
    private final IDriveTrain DRIVE_TRAIN;
    private final IGyro PINPOINT;
    private final double TOLERANCE = 1.0;

    private double _startPos = 0;


    private final AtomicBoolean driveComplete = new AtomicBoolean(false);

    public AutoDrive(LinearOpMode opMode, IDriveTrain drive, IGyro pinpoint) {
        OP_MODE = opMode;
        DRIVE_TRAIN = drive;
        PINPOINT = pinpoint;
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

                double currentPos = normalizeHeading(PINPOINT.getPose().getHeading(AngleUnit.DEGREES));

                if ((currentPos > targetPos + TOLERANCE) || (currentPos < targetPos - TOLERANCE)) {
                    double current = normalizeAngle(Math.toRadians(currentPos));
                    double target = normalizeAngle(Math.toRadians(targetPos));
                    double delta = normalizeAngle(target - current); // shortest angular distance
                    double direction = Math.signum(delta);

                    double pow = Acceleration.getPower(
                            targetPos,
                            currentPos,
                            _startPos,
                            accelToDistance,
                            decelAtDistance,
                            minPower,
                            maxPower);

                    pow = Math.max(minPower, Math.min(pow, maxPower));
                    double turnPower = direction * pow;

                    packet.put("Turn Start Pos", _startPos);
                    packet.put("Turn Target Pos", targetPos);
                    packet.put("Turn Current Pos", currentPos);
                    packet.put("Turn Power", turnPower);
                    packet.put("Turn Direction", direction);
                    packet.put("Turn Delta", delta);

                    DRIVE_TRAIN.drive(0, 0, -turnPower);

                    packet.put("Status Drive Turn Right", "Running");
                    return true;
                } else {

                    packet.put("Turn Right Start Pos", _startPos);
                    packet.put("Turn Right Target Pos", targetPos);
                    packet.put("Turn Right Current Pos", currentPos);
                    packet.put("Turn Right Power", 0);

                    DRIVE_TRAIN.drive(0,0,0);
                    setDriveCompleted(true);
                    packet.put("Status Drive Turn Right", "Finished");
                    return false;
                }
            }
        };
    }

    public Action driveTurnLeftForward(double targetPos, double accelToDistance, double decelAtDistance, double minPower, double maxPower) {
        return new Action() {
            private boolean initialized = false;

            @Override
            public boolean run(@NonNull TelemetryPacket packet) {
                if (!initialized) {
                    initialized = true;
                }Pose2D pos = PINPOINT.getPose();
                double currentPos = pos.getY(DistanceUnit.INCH);

                if (currentPos < targetPos) {
                    pos = PINPOINT.getPose();
                    currentPos = pos.getY(DistanceUnit.INCH);

                    double pow = Acceleration.getPower(
                            targetPos,
                            currentPos,
                            _startPos,
                            accelToDistance,
                            decelAtDistance,
                            minPower,
                            maxPower);

                    DRIVE_TRAIN.drive(0, pow, -pow);
                    return true;
                } else {
                    DRIVE_TRAIN.drive(0,0,0);
                    return false;
                }
            }
        };
    }

    public Action driveTurnRightForward(double targetPos, double accelToDistance, double decelAtDistance, double minPower, double maxPower) {
        return new Action() {
            private boolean initialized = false;

            @Override
            public boolean run(@NonNull TelemetryPacket packet) {
                if (!initialized) {
                    initialized = true;
                }Pose2D pos = PINPOINT.getPose();
                double currentPos = pos.getY(DistanceUnit.INCH);

                if (currentPos < targetPos) {
                    pos = PINPOINT.getPose();
                    currentPos = pos.getY(DistanceUnit.INCH);

                    double pow = Acceleration.getPower(
                            targetPos,
                            currentPos,
                            _startPos,
                            accelToDistance,
                            decelAtDistance,
                            minPower,
                            maxPower);

                    DRIVE_TRAIN.drive(0, pow, pow);
                    return true;
                } else {
                    DRIVE_TRAIN.drive(0,0,0);
                    return false;
                }
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

                Pose2D pos = PINPOINT.getPose();
                double currentPos = pos.getX(DistanceUnit.INCH);
                double direction = Math.signum(targetPos - currentPos);
                double pow = 0;

                if ((currentPos > targetPos + TOLERANCE) || (currentPos < targetPos - TOLERANCE)) {
                    pow = Acceleration.getPower(
                            targetPos,
                            currentPos,
                            _startPos,
                            accelToDistance,
                            decelAtDistance,
                            minPower,
                            maxPower);

                    pow = Math.max(minPower, Math.min(pow, maxPower));
                }

                DRIVE_TRAIN.drive(0, pow * direction, 0);

                packet.put("Drive Dir", direction);
                packet.put("Drive Pow", pow * direction);
                packet.put("Drive Start", _startPos);
                packet.put("Drive Target", targetPos);
                packet.put("Drive Current", currentPos);

                setDriveCompleted(pow == 0);
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

                Pose2D pos = PINPOINT.getPose();
                double currentPos = pos.getY(DistanceUnit.INCH);
                double direction = Math.signum(targetPos - currentPos);

                double pow = 0;

                if ((currentPos > targetPos + TOLERANCE) || (currentPos < targetPos - TOLERANCE)) {
                    pow = Acceleration.getPower(
                            targetPos,
                            currentPos,
                            _startPos,
                            accelToDistance,
                            decelAtDistance,
                            minPower,
                            maxPower);

                    pow = Math.max(minPower, Math.min(pow, maxPower));
                }

                DRIVE_TRAIN.drive(pow * direction, 0, 0);

                packet.put("Strafe Dir", direction);
                packet.put("Strafe Pow", pow * direction);
                packet.put("Strafe Start", _startPos);
                packet.put("Strafe Target", targetPos);
                packet.put("Strafe Current", currentPos);

                setDriveCompleted(pow == 0);
                return pow != 0;
            }
        };
    }

//    public Action driveTurnTo(double targetPos, double accelToDistance, double decelAtDistance, double minPower, double maxPower) {
//        return new Action() {
//            private boolean initialized = false;
//
//            @Override
//            public boolean run(@NonNull TelemetryPacket packet) {
//                if (!initialized) {
//                    initialized = true;
//                }
//
//                Pose2D pos = PINPOINT.getPose();
//                double currentPos = pos.getX(DistanceUnit.INCH);
//                double direction = Math.signum(targetPos - currentPos);
//                double pow = 0;
//
//                if ((currentPos > targetPos + TOLERANCE) || (currentPos < targetPos - TOLERANCE)) {
//                    pow = Acceleration.getPower(
//                            targetPos,
//                            currentPos,
//                            _startPos,
//                            accelToDistance,
//                            decelAtDistance,
//                            minPower,
//                            maxPower);
//
//                    pow = Math.max(minPower, Math.min(pow, maxPower));
//                }
//
//                DRIVE_TRAIN.drive(0, pow * direction, -(pow * direction));
//
//                packet.put("Drive Dir", direction);
//                packet.put("Drive Pow", pow * direction);
//                packet.put("Drive Start", _startPos);
//                packet.put("Drive Target", targetPos);
//                packet.put("Drive Current", currentPos);
//
//                return pow != 0;
//            }
//        };
//    }
}

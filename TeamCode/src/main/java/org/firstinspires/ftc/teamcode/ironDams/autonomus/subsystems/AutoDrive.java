package org.firstinspires.ftc.teamcode.ironDams.autonomus.subsystems;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose2D;
import org.firstinspires.ftc.teamcode.ironDams.core.Acceleration;
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

    public Action turnLeft(double targetPos, double accelToDistance, double decelAtDistance, double minPower, double maxPower) {
        return new Action() {
            private boolean initialized = false;

            @Override
            public boolean run(@NonNull TelemetryPacket packet) {
                if (!initialized) {
                    initialized = true;
                }

                Pose2D pos = PINPOINT.getPose();
                double currentPos = pos.getHeading(AngleUnit.DEGREES);

                packet.put("Turn Right Start Pos", _startPos);
                packet.put("Turn Right Target Pos", targetPos);
                packet.put("Turn Right Current Pos", currentPos);

                if (currentPos < targetPos) {
                    pos = PINPOINT.getPose();
                    currentPos = pos.getHeading(AngleUnit.DEGREES);

                    double pow = minPower;

                    if (currentPos >= _startPos) {
                        pow = Acceleration.getPower(
                                targetPos,
                                currentPos,
                                _startPos,
                                accelToDistance,
                                decelAtDistance,
                                minPower,
                                maxPower);
                    }

                    packet.put("Turn Left Start Pos", _startPos);
                    packet.put("Turn Left Target Pos", targetPos);
                    packet.put("Turn Left Current Pos", currentPos);
                    packet.put("Turn Left Power", pow);

                    DRIVE_TRAIN.drive(0, 0, -pow);
                    return true;
                } else {
                    DRIVE_TRAIN.drive(0,0,0);
                    return false;
                }
            }
        };
    }

    public Action turnRight(double targetPos, double accelToDistance, double decelAtDistance, double minPower, double maxPower) {
        return new Action() {
            private boolean initialized = false;

            @Override
            public boolean run(@NonNull TelemetryPacket packet) {
                if (!initialized) {
                    initialized = true;
                }

                double currentPos = normalizeHeading(PINPOINT.getPose().getHeading(AngleUnit.DEGREES));

                packet.put("Turn Right Start Pos", _startPos);
                packet.put("Turn Right Target Pos", targetPos);
                packet.put("Turn Right Current Pos", currentPos);

                if (currentPos > targetPos + TOLERANCE) {
                    double pow = Acceleration.getPower(
                            targetPos,
                            currentPos,
                            _startPos,
                            accelToDistance,
                            decelAtDistance,
                            minPower,
                            maxPower);

                    pow = Math.max(minPower, Math.min(pow, maxPower));

                    packet.put("Turn Right Start Pos", _startPos);
                    packet.put("Turn Right Target Pos", targetPos);
                    packet.put("Turn Right Current Pos", currentPos);
                    packet.put("Turn Right Power", pow);

                    DRIVE_TRAIN.drive(0, 0, pow);

                    packet.put("Status Drive Turn Right", "Running");
                    return true;
                } else {
                    DRIVE_TRAIN.drive(0,0,0);
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

    public Action driveForward(double targetPos, double accelToDistance, double decelAtDistance, double minPower, double maxPower) {
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

                    DRIVE_TRAIN.drive(0, pow, 0);
                    return true;
                } else {
                    DRIVE_TRAIN.drive(0,0,0);
                    return false;
                }
            }
        };
    }

    public Action driveTurnLeftBackward(double targetPos, double accelToDistance, double decelAtDistance, double minPower, double maxPower) {
        return new Action() {
            private boolean initialized = false;

            @Override
            public boolean run(@NonNull TelemetryPacket packet) {
                if (!initialized) {
                    initialized = true;
                }Pose2D pos = PINPOINT.getPose();
                double currentPos = pos.getY(DistanceUnit.INCH);

                if (currentPos > targetPos) {
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

                    DRIVE_TRAIN.drive(0, -pow, -pow);
                    return true;
                } else {
                    DRIVE_TRAIN.drive(0,0,0);
                    return false;
                }
            }
        };
    }

    public Action driveTurnRightBackward(double targetPos, double accelToDistance, double decelAtDistance, double minPower, double maxPower) {
        return new Action() {
            private boolean initialized = false;

            @Override
            public boolean run(@NonNull TelemetryPacket packet) {
                if (!initialized) {
                    initialized = true;
                }Pose2D pos = PINPOINT.getPose();
                double currentPos = pos.getY(DistanceUnit.INCH);

                if (currentPos > targetPos) {
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

                    DRIVE_TRAIN.drive(0, -pow, pow);
                    return true;
                } else {
                    DRIVE_TRAIN.drive(0,0,0);
                    return false;
                }
            }
        };
    }

    public Action driveBackward(double targetPos, double accelToDistance, double decelAtDistance, double minPower, double maxPower) {
        return new Action() {
            private boolean initialized = false;

            @Override
            public boolean run(@NonNull TelemetryPacket packet) {
                if (!initialized) {
                    initialized = true;
                }Pose2D pos = PINPOINT.getPose();
                double currentPos = pos.getY(DistanceUnit.INCH);

                if (currentPos > targetPos) {
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

                    DRIVE_TRAIN.drive(0, -pow, 0);
                    return true;
                } else {
                    DRIVE_TRAIN.drive(0,0,0);
                    return false;
                }
            }
        };
    }

    public Action strafeLeft(double targetPos, double accelToDistance, double decelAtDistance, double minPower, double maxPower) {
        return new Action() {
            private boolean initialized = false;

            @Override
            public boolean run(@NonNull TelemetryPacket packet) {
                if (!initialized) {
                    initialized = true;
                }

                Pose2D pos = PINPOINT.getPose();
                double currentPos = pos.getX(DistanceUnit.INCH);

                packet.put("Strafe Left Start Pos", _startPos);
                packet.put("Strafe Left Target Pos", targetPos);
                packet.put("Strafe Left Current Pos", currentPos);

                if (currentPos < targetPos) {
                    pos = PINPOINT.getPose();
                    currentPos = pos.getX(DistanceUnit.INCH);

                    double pow = minPower;

                    if (currentPos >= _startPos) {
                        pow = Acceleration.getPower(
                                targetPos,
                                currentPos,
                                _startPos,
                                accelToDistance,
                                decelAtDistance,
                                minPower,
                                maxPower);
                    }

                    packet.put("Strafe Left Start Pos", _startPos);
                    packet.put("Strafe Left Target Pos", targetPos);
                    packet.put("Strafe Left Current Pos", currentPos);
                    packet.put("Strafe Left Power", pow);

                    DRIVE_TRAIN.drive(pow, 0, 0);
                    return true;
                } else {
                    DRIVE_TRAIN.drive(0,0,0);
                    return false;
                }
            }
        };
    }

    public Action strafeRight(double targetPos, double accelToDistance, double decelAtDistance, double minPower, double maxPower) {
        return new Action() {
            private boolean initialized = false;

            @Override
            public boolean run(@NonNull TelemetryPacket packet) {
                if (!initialized) {
                    initialized = true;
                }

                Pose2D pos = PINPOINT.getPose();
                double currentPos = pos.getX(DistanceUnit.INCH);

                packet.put("Strafe Right Start Pos", _startPos);
                packet.put("Strafe Right Target Pos", targetPos);
                packet.put("Strafe Right Current Pos", currentPos);

                if (currentPos < targetPos) {
                    pos = PINPOINT.getPose();
                    currentPos = pos.getX(DistanceUnit.INCH);

                    double pow = minPower;

                    if (currentPos >= _startPos) {
                        pow = Acceleration.getPower(
                                targetPos,
                                currentPos,
                                _startPos,
                                accelToDistance,
                                decelAtDistance,
                                minPower,
                                maxPower);
                    }

                    packet.put("Strafe Right Start Pos", _startPos);
                    packet.put("Strafe Right Target Pos", targetPos);
                    packet.put("Strafe Right Current Pos", currentPos);
                    packet.put("Strafe Right Power", pow);

                    DRIVE_TRAIN.drive(-pow, 0, 0);
                    return true;
                } else {
                    DRIVE_TRAIN.drive(0,0,0);
                    return false;
                }
            }
        };
    }
}

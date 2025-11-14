package org.firstinspires.ftc.teamcode.decode.auto;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.decode.SharedData;
import org.firstinspires.ftc.teamcode.irondams.core.Logger;
import org.firstinspires.ftc.teamcode.irondams.core.WaitFor;
import org.firstinspires.ftc.teamcode.irondams.core.cameras.AprilTagReader;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;

import java.util.List;
import java.util.function.BooleanSupplier;

public class TagDetection {
    private final AprilTagReader TAG_READER;
    private final Logger LOG;
    private final WaitFor TAG_TIMEOUT = new WaitFor(500);
    private boolean _isRed;

    public TagDetection(@NonNull LinearOpMode opMode, Logger log) {
        TAG_READER = new AprilTagReader(opMode.hardwareMap);
        LOG = log;

        SharedData.Pattern.actualIndex = -1;
        SharedData.Pattern.actual = new String[] { "", "", "" };
    }

    public void setIsRed(boolean isRed) {
        _isRed = isRed;
    }

    public void stopStreaming() {
        TAG_READER.stopStreaming();
    }

    private boolean setGBIndex(TelemetryPacket packet, double millis, int index) {
        SharedData.Pattern.targetIndex = index;
        LOG.writeToMemory(millis, "Webcam TargetIndex", index);
        packet.put("Webcam Target Index", SharedData.Pattern.targetIndex);
        packet.put("Status Webcam Read Tag", "Finished");
        LOG.flushToDisc();
        TAG_READER.stopStreaming();
        return false;
    }

    public Action webcamReadAction(BooleanSupplier driveComplete, double millis) {
        return new Action() {
            private boolean initialized = false;

            @Override
            public boolean run(@NonNull TelemetryPacket packet) {
                if (!initialized) {
                    initialized = true;
                }

                packet.put("Webcam Left Status", TAG_READER.leftState());
                packet.put("Webcam Right Status", TAG_READER.rightState());

                LOG.writeToMemory(millis, "Webcam Left Status", String.valueOf(TAG_READER.leftState()));
                LOG.writeToMemory(millis, "Webcam Right Status", String.valueOf(TAG_READER.rightState()));

                if (SharedData.Pattern.targetIndex != -1) {
                    packet.put("Status Webcam Read Tag", "Finished 1");
                    stopStreaming();
                    return false;
                }
                else if (TAG_READER.leftState() == VisionPortal.CameraState.STREAMING &&
                        TAG_READER.rightState() == VisionPortal.CameraState.STREAMING) {
                    int id = -1;

                    List<AprilTagDetection> left = TAG_READER.readLeft();
                    List<AprilTagDetection> right = TAG_READER.readRight();

                    if (!left.isEmpty()) id = left.get(0).id;
                    else if (!right.isEmpty()) id = right.get(0).id;

                    LOG.writeToMemory(millis, "Webcam Target Id", id);
                    packet.put("Webcam Target Id", id);

                    switch (id) {
                        case 21:
                            return setGBIndex(packet, millis, 0);
                        case 22:
                            return setGBIndex(packet, millis, 1);
                        case 23:
                            return setGBIndex(packet, millis, 2);
                        default:
                            packet.put("Webcam Target Index", SharedData.Pattern.targetIndex);
                    }
                } else {
                    TAG_READER.resumeStreaming();
                }

                LOG.flushToDisc();

                if (SharedData.Pattern.targetIndex != -1) {
                    packet.put("Status Webcam Read Tag", "Finished 2");
                    TAG_READER.stopStreaming();
                    return false;
                }
                else if (TAG_TIMEOUT.allowExec()) {
                    packet.put("Status Webcam Read Tag", "Finished 4");
                    TAG_READER.stopStreaming();
                    return false;
                }
                else {
                    if (driveComplete.getAsBoolean()) {
                        TAG_READER.stopStreaming();
                        packet.put("Status Webcam Read Tag", "Finished 3");
                        return false;
                    }
                    else {
                        packet.put("Status Webcam Read Tag", "Running");
                        return true;
                    }
                }
            }
        };
    }

    public Action webcamResetTimeout() {
        return new Action() {
            private boolean initialized = false;

            @Override
            public boolean run(@NonNull TelemetryPacket packet) {
                if (!initialized) {
                    initialized = true;
                }

                TAG_TIMEOUT.reset();

                return false;
            }
        };
    }

    public Action webcamStopStreamingAction() {
        return new Action() {
            private boolean initialized = false;

            @Override
            public boolean run(@NonNull TelemetryPacket packet) {
                if (!initialized) {
                    initialized = true;
                }

                TAG_READER.stopStreaming();

                return false;
            }
        };
    }

    public Action webcamResumeStreamingAction() {
        return new Action() {
            private boolean initialized = false;

            @Override
            public boolean run(@NonNull TelemetryPacket packet) {
                if (!initialized) {
                    initialized = true;
                }

                TAG_READER.resumeStreaming();

                return false;
            }
        };
    }
}
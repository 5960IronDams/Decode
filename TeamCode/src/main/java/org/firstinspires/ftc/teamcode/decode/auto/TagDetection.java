package org.firstinspires.ftc.teamcode.decode.auto;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.decode.SharedData;
import org.firstinspires.ftc.teamcode.ironDams.core.WaitFor;
import org.firstinspires.ftc.teamcode.ironDams.core.cameras.AprilTagReader;
import org.firstinspires.ftc.teamcode.ironDams.core.cameras.HuskyLensReader;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;

import java.util.List;
import java.util.function.BooleanSupplier;

public class TagDetection {
    private final SharedData DATA;
    private final AprilTagReader TAG_READER;

//    private final WaitFor GIVEUP = new WaitFor(1000);
//    private final HuskyLensReader HUSKY_READER;

    public TagDetection(LinearOpMode opMode, SharedData data) {
        DATA = data;
        TAG_READER = new AprilTagReader(opMode.hardwareMap);
//        HUSKY_READER = new HuskyLensReader(opMode.hardwareMap);
    }

    public void stopStreaming() {
        TAG_READER.stopStreaming();
    }

    private boolean setGBIndex(TelemetryPacket packet, int index) {
        DATA.setGreenBallTargetIndex(index);
        packet.put("Webcam Target Index", DATA.getGreenBallTargetIndex());
        packet.put("Status Webcam Read Tag", "Finished");
        TAG_READER.stopStreaming();
        return false;
    }

    public Action webcamReadAction(BooleanSupplier driveComplete) {
        return new Action() {
            private boolean initialized = false;

            @Override
            public boolean run(@NonNull TelemetryPacket packet) {
                if (!initialized) {
//                    GIVEUP.reset();
                    initialized = true;
                }

                packet.put("Webcam Left Status", TAG_READER.leftState());
                packet.put("Webcam Right Status", TAG_READER.rightState());

                if (DATA.getGreenBallTargetIndex() != -1) {
                    packet.put("Status Webcam Read Tag", "Finished");
                    TAG_READER.stopStreaming();
                    return false;
                }
                else if (TAG_READER.leftState() == VisionPortal.CameraState.STREAMING &&
                  TAG_READER.rightState() == VisionPortal.CameraState.STREAMING) {
                    int id = -1;

                    List<AprilTagDetection> left = TAG_READER.readLeft();
                    List<AprilTagDetection> right = TAG_READER.readRight();

                    if (!left.isEmpty()) id = left.get(0).id;
                    else if (!right.isEmpty()) id = right.get(0).id;

                    switch (id) {
                        case 21:
                            return setGBIndex(packet, 0);
                        case 22:
                            return setGBIndex(packet, 1);
                        case 23:
                            return setGBIndex(packet, 2);
                        default:
                            packet.put("Webcam Target Index", DATA.getGreenBallTargetIndex());
                    }
                } else {
                    TAG_READER.resumeStreaming();
                }

                if (DATA.getGreenBallTargetIndex() != -1) {
                    packet.put("Status Webcam Read Tag", "Finished");
                    TAG_READER.stopStreaming();
                    return false;
                }
                else {
//                    boolean givenUp = GIVEUP.allowExec(false);
                    if (driveComplete.getAsBoolean()) packet.put("Status Webcam Read Tag", "Finished");
                    else packet.put("Status Webcam Read Tag", "Running");

//                    if (givenUp) return false;
                    return !driveComplete.getAsBoolean();
                }
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

//    public Action huskyReadTag(BooleanSupplier driveComplete) {
//        return new Action() {
//            private boolean initialized = false;
//
//            @Override
//            public boolean run(@NonNull TelemetryPacket packet) {
//                if (!initialized) {
//                    initialized = true;
//                }
//
//                int huskyId = HUSKY_READER.getFirstId();
//
//                switch (huskyId) {
//                    case 1:
//                        DATA.setGreenBallTargetIndex(2);
//                        packet.put("Husky Target Index", DATA.getGreenBallTargetIndex());
//                        return false;
//                    case 2:
//                        DATA.setGreenBallTargetIndex(0);
//                        packet.put("Husky Target Index", DATA.getGreenBallTargetIndex());
//                        return false;
//                    case 3:
//                        DATA.setGreenBallTargetIndex(1);
//                        packet.put("Husky Target Index", DATA.getGreenBallTargetIndex());
//                        return false;
//                    default: packet.put("Husky Target Index", DATA.getGreenBallTargetIndex());
//                }
//
//                if (DATA.getGreenBallTargetIndex() != -1) {
//                    packet.put("Status Husky Read Tag", "Finished");
//                    return false;
//                }
//                else {
//                    if (driveComplete.getAsBoolean()) packet.put("Status Husky Read Tag", "Finished");
//                    else packet.put("Status Husky Read Tag", "Running");
//                    return !driveComplete.getAsBoolean();
//                }
//            }
//        };
//    }
}

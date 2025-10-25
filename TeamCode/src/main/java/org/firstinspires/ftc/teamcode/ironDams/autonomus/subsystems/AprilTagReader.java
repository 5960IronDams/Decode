package org.firstinspires.ftc.teamcode.ironDams.autonomus.subsystems;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.teamcode.decode.core.GreenBallPosition;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;

import java.util.List;
import java.util.function.BooleanSupplier;

public class AprilTagReader {

    private final AprilTagProcessor LEFT_PROCESSOR;
    private final AprilTagProcessor RIGHT_PROCESSOR;
    private final VisionPortal LEFT_VISION_PORTAL;
    private final VisionPortal RIGHT_VISION_PORTAL;

    private final GreenBallPosition GREEN_BALL_POSITION;

    public AprilTagReader(HardwareMap hardwareMap, GreenBallPosition greenBallPosition) {

        int[] viewIds = VisionPortal.makeMultiPortalView(2, VisionPortal.MultiPortalLayout.VERTICAL);
        int leftPortalViewId = viewIds[0];
        int rightPortalViewId = viewIds[1];

        LEFT_PROCESSOR = AprilTagProcessor.easyCreateWithDefaults();
        RIGHT_PROCESSOR = AprilTagProcessor.easyCreateWithDefaults();

        LEFT_VISION_PORTAL = new VisionPortal.Builder()
                .setCamera(hardwareMap.get(WebcamName.class, "Webcam 1"))
                .setLiveViewContainerId(leftPortalViewId)
                .addProcessor(LEFT_PROCESSOR)
                .build();
        RIGHT_VISION_PORTAL = new VisionPortal.Builder()
                .setCamera(hardwareMap.get(WebcamName.class, "Webcam 2"))
                .setLiveViewContainerId(rightPortalViewId)
                .addProcessor(RIGHT_PROCESSOR)
                .build();

        GREEN_BALL_POSITION = greenBallPosition;
    }

    public boolean isInitialized() {
        return LEFT_VISION_PORTAL != null && RIGHT_VISION_PORTAL != null;
    }

    public List<AprilTagDetection> readLeft() {
        return LEFT_PROCESSOR.getDetections();
    }

    public List<AprilTagDetection> readRight() {
        return RIGHT_PROCESSOR.getDetections();
    }

    public List<AprilTagDetection> read() {
        List<AprilTagDetection> detections = LEFT_PROCESSOR.getDetections();
        detections.addAll(RIGHT_PROCESSOR.getDetections());
        return detections;
    }

    public Action readTag(BooleanSupplier driveComplete) {
        return new Action() {
            private boolean initialized = false;

            @Override
            public boolean run(@NonNull TelemetryPacket packet) {
                if (!initialized) {
                    initialized = true;
                }

                int id = -1;

                List<AprilTagDetection> left = readLeft();
                List<AprilTagDetection> right = readRight();

                if (!left.isEmpty()) id = left.get(0).id;
                else if (!right.isEmpty()) id = right.get(0).id;

                switch (id) {
                    case 21:
                        GREEN_BALL_POSITION.setTargetIndex(0);
                        packet.put("Webcam Target Index", GREEN_BALL_POSITION.getTargetIndex());
                        packet.put("Status Webcam Read Tag", "Finished");
                        return false;
                    case 22:
                        GREEN_BALL_POSITION.setTargetIndex(1);
                        packet.put("Webcam Target Index", GREEN_BALL_POSITION.getTargetIndex());
                        packet.put("Status Webcam Read Tag", "Finished");
                        return false;
                    case 23:
                        GREEN_BALL_POSITION.setTargetIndex(2);
                        packet.put("Webcam Target Index", GREEN_BALL_POSITION.getTargetIndex());
                        packet.put("Status Webcam Read Tag", "Finished");
                        return false;
                    default: packet.put("Webcam Target Index", GREEN_BALL_POSITION.getTargetIndex());
                }

                if (GREEN_BALL_POSITION.getTargetIndex() != -1) {
                    packet.put("Status Webcam Read Tag", "Finished");
                    return false;
                }
                else {
                    if (driveComplete.getAsBoolean()) packet.put("Status Webcam Read Tag", "Finished");
                    else packet.put("Status Webcam Read Tag", "Running");
                    return !driveComplete.getAsBoolean();
                }
            }
        };
    }
}

package org.firstinspires.ftc.teamcode.ironDams.autonomus.subsystems;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;

import java.util.List;

public class AprilTagReader {

    private final AprilTagProcessor LEFT_PROCESSOR;
    private final AprilTagProcessor RIGHT_PROCESSOR;
    private final VisionPortal LEFT_VISION_PORTAL;
    private final VisionPortal RIGHT_VISION_PORTAL;

    public AprilTagReader(HardwareMap hardwareMap) {

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

//        RIGHT_VISION_PORTAL.resumeStreaming();
//        LEFT_VISION_PORTAL.resumeStreaming();
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
}

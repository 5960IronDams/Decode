package org.firstinspires.ftc.teamcode.ironDams.autonomus.subsystems;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.hardware.camera.BuiltinCameraDirection;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;

import java.util.List;

public class AprilTagReader {

    private final boolean USE_WEBCAM = true;
    private final HardwareMap hardwareMap;


    private AprilTagProcessor aprilTag;
    private VisionPortal visionPortal;

    public AprilTagReader(LinearOpMode opMode) {
        hardwareMap = opMode.hardwareMap;
        init();
    }

    private int sequenceCode;

    private void init() {
        aprilTag = AprilTagProcessor.easyCreateWithDefaults();

        if (USE_WEBCAM) {
            visionPortal = VisionPortal.easyCreateWithDefaults(
                    hardwareMap.get(WebcamName.class, "Webcam 1"), aprilTag);
        } else {
            visionPortal = VisionPortal.easyCreateWithDefaults(
                    BuiltinCameraDirection.BACK, aprilTag);
        }
    }

    public int getSequenceCode() {
        return sequenceCode;

    }

    private List<AprilTagDetection> read() {
        visionPortal.resumeStreaming();
        return aprilTag.getDetections();
    }

    public void stopStreaming() {
        visionPortal.stopStreaming();
    }

    public Action readTagAction() {
        return new Action() {
            private boolean initialized = false;

            @Override
            public boolean run(@NonNull TelemetryPacket packet) {
                if (!initialized) {
                    initialized = visionPortal != null;
                }

                List<AprilTagDetection> detections = read();

                packet.put("detections", detections);

                return detections.isEmpty();
            }
        };
    }
}

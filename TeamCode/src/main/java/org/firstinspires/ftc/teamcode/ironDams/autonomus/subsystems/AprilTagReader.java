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

    private final HardwareMap _hardwareMap;
    private final Telemetry _telemetry;
    private final boolean _continuous;

    private AprilTagProcessor _aprilTag;
    private VisionPortal _visionPortal;

    public AprilTagReader(LinearOpMode opMode, boolean continuous) {
        _continuous = continuous;
        _hardwareMap = opMode.hardwareMap;
        _telemetry = opMode.telemetry;
        init();
    }

    /*
     * 21: GPP
     * 22: PGP
     * 23: PPG
     * BLUE -ID 20:
     * RED -ID 24:
     */
    private int sequenceCode;

    private void init() {
        _aprilTag = AprilTagProcessor.easyCreateWithDefaults();

        _visionPortal = VisionPortal.easyCreateWithDefaults(
            _hardwareMap.get(WebcamName.class, "Webcam 1"),
                _aprilTag
        );
    }

    public int getSequenceCode() {
        return sequenceCode;
    }

    private List<AprilTagDetection> read() {
        _visionPortal.resumeStreaming();
        return _aprilTag.getDetections();
    }

    public void stopStreaming() {
        _visionPortal.stopStreaming();
    }

    public Action readTagAction() {
        return new Action() {
            private boolean initialized = false;

            @Override
            public boolean run(@NonNull TelemetryPacket packet) {
                if (!initialized) {
                    initialized = _visionPortal != null;
                }

                List<AprilTagDetection> detections = read();

                boolean detected = !detections.isEmpty();

                if (detected) {
                    AprilTagDetection detection = detections.get(0);
                    sequenceCode = detection.id;

                    packet.put("Sequence Code", sequenceCode);
                    packet.put("ftcPose.x", detection.ftcPose.x);
                    packet.put("ftcPose.y", detection.ftcPose.y);
                    packet.put("ftcPose.yaw", detection.ftcPose.yaw);
                }

                return !detected || _continuous;
            }
        };
    }
}

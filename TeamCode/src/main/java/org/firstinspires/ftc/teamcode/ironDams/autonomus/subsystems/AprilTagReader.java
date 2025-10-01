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

    private final AprilTagProcessor _aprilTag;
    private final VisionPortal _visionPortal;

    public AprilTagReader(HardwareMap hardwareMap) {
        _aprilTag = AprilTagProcessor.easyCreateWithDefaults();

        _visionPortal = VisionPortal.easyCreateWithDefaults(
                hardwareMap.get(WebcamName.class, "Webcam 1"),
                _aprilTag
        );
    }

    public boolean isInitialized() {
        return _visionPortal != null;
    }

    public List<AprilTagDetection> read() {
        _visionPortal.resumeStreaming();
        return _aprilTag.getDetections();
    }

    public void stopStreaming() {
        _visionPortal.stopStreaming();
    }
}

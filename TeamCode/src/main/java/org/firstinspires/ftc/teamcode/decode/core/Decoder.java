package org.firstinspires.ftc.teamcode.decode.core;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.ironDams.autonomus.subsystems.AprilTagReader;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;

import java.util.List;

public class Decoder {

    private final AprilTagReader _aprilTagReader;
    private final boolean _continuous;

    /*
     * 21: GPP
     * 22: PGP
     * 23: PPG
     * BLUE -ID 20:
     * RED -ID 24:
     */
    private int _sequenceCode;

    public int getSequenceCode() {
        return _sequenceCode;
    }

    public Decoder(HardwareMap hardwareMap, boolean continuous) {
        _aprilTagReader = new AprilTagReader(hardwareMap);
        _continuous = continuous;
    }

    public Action readTagAction() {
        return new Action() {
            private boolean initialized = false;

            @Override
            public boolean run(@NonNull TelemetryPacket packet) {
                if (!initialized) {
                    initialized = _aprilTagReader.isInitialized();
                }

                List<AprilTagDetection> detections = _aprilTagReader.read();

                boolean detected = !detections.isEmpty();

                if (detected) {
                    AprilTagDetection detection = detections.get(0);
                    _sequenceCode = detection.id;

                    packet.put("Sequence Code", _sequenceCode);
                    packet.put("ftcPose.x", detection.ftcPose.x);
                    packet.put("ftcPose.y", detection.ftcPose.y);
                    packet.put("ftcPose.yaw", detection.ftcPose.yaw);
                }

                return !detected || _continuous;
            }
        };
    }
}

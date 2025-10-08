package org.firstinspires.ftc.teamcode.decode.core;

import android.os.Build;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.ironDams.autonomus.subsystems.AprilTagReader;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;

import java.time.LocalDate;
import java.util.List;

public class Decoder {

    private final AprilTagReader _aprilTagReader;
    private final LinearOpMode _opMode;
    private final Gamepad _gamepad;
    private final Telemetry _telemetry;

    /*
     * 21: GPP
     * 22: PGP
     * 23: PPG
     * BLUE -ID 20:
     * RED -ID 24:
     */
    private int _sequenceCode = 21;

    public int getSequenceCode() {
        return _sequenceCode;
    }

    public String getSequenceCodeString() {
        switch (_sequenceCode) {
            case 21: return "GPP";
            case 22: return "PGP";
            case 23: return "PPG";
            default: return "Unknown";
        }
    }

    public Decoder(LinearOpMode opMode) {
        _opMode = opMode;
        _aprilTagReader = new AprilTagReader(opMode.hardwareMap);
        _telemetry = opMode.telemetry;
        _gamepad = opMode.gamepad2;
    }

    public Action readTagAction(boolean continuous) {
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

                return !detected || continuous;
            }
        };
    }

    public Action setSequence() {
        return new Action() {
            private boolean initialized = false;

            @Override
            public boolean run(@NonNull TelemetryPacket packet) {
                if (!initialized) {
                    initialized = true;
                }

                if (_gamepad.b) {
                    switch (_sequenceCode) {
                        case 21: _sequenceCode = 22; break;
                        case 22: _sequenceCode = 23; break;
                        case 23: _sequenceCode = 21; break;
                    }

                    _opMode.sleep(150);
                }

                _telemetry.addData("Pattern", getSequenceCodeString());

                packet.put("ChangePattern", _gamepad.b);
                packet.put("PatternId", _sequenceCode);
                packet.put("Pattern", getSequenceCodeString());

                return true;
            }
        };
    }
}

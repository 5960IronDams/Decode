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

    /*
     * 21: GPP
     * 22: PGP
     * 23: PPG
     * BLUE -ID 20:
     * RED -ID 24:
     */
    private boolean _codeRead;

    public Decoder(LinearOpMode opMode) {
        _opMode = opMode;
        _aprilTagReader = new AprilTagReader(opMode.hardwareMap);
    }

    public int readQr() {
        if (!_codeRead) {
            List<AprilTagDetection> detections = _aprilTagReader.read();

            boolean detected = !detections.isEmpty();

            if (detected) {
                _codeRead = true;
                AprilTagDetection detection = detections.get(0);
                return detection.id;
            }
        }

        return 0;
    }
}

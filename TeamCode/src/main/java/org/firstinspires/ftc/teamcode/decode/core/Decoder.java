package org.firstinspires.ftc.teamcode.decode.core;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.ironDams.autonomus.subsystems.AprilTagReader;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;

import java.util.List;

public class Decoder {

    private final AprilTagReader APRIL_TAG_READER;

    public Decoder(LinearOpMode opMode) {
        APRIL_TAG_READER = new AprilTagReader(opMode.hardwareMap);
    }

    public List<AprilTagDetection> getDetections() {
        return APRIL_TAG_READER.read();
    }

    public int readQr() {
            List<AprilTagDetection> left = APRIL_TAG_READER.readLeft();
            List<AprilTagDetection> right = APRIL_TAG_READER.readRight();
            if (!left.isEmpty()) return left.get(0).id;
            else if (!right.isEmpty()) return right.get(0).id;

            return -1;
    }
}

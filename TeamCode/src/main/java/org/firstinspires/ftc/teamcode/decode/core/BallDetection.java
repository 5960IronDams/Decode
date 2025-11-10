package org.firstinspires.ftc.teamcode.decode.core;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.NormalizedColorSensor;
import com.qualcomm.robotcore.hardware.NormalizedRGBA;

import org.firstinspires.ftc.teamcode.decode.SharedData;
import org.firstinspires.ftc.teamcode.irondams.core.Logger;
import org.firstinspires.ftc.teamcode.irondams.core.WaitFor;

import java.util.function.BooleanSupplier;

public class BallDetection {
    private final NormalizedColorSensor COLOR_SENSOR_0;
    private final NormalizedColorSensor COLOR_SENSOR_1;
    private final NormalizedColorSensor COLOR_SENSOR_2;

    private final int DELAY_DETECTION_MS = 250;
    private final WaitFor DELAY_DETECTION = new WaitFor(DELAY_DETECTION_MS);
    private final Logger LOG;

    private float _blue;
    private float _green;

    public BallDetection(LinearOpMode opMode, Logger log) {
        COLOR_SENSOR_0 = opMode.hardwareMap.get(NormalizedColorSensor.class, "colorL");
        COLOR_SENSOR_1 = opMode.hardwareMap.get(NormalizedColorSensor.class, "colorC");
        COLOR_SENSOR_2 = opMode.hardwareMap.get(NormalizedColorSensor.class, "colorR");

        SharedData.Pattern.actual = new String[] { "", "", "" };
        SharedData.Pattern.actualIndex = -1;
        SharedData.BallDetection.detectionCount = 0;

        LOG = log;
    }

    private void update(double millis) {
        NormalizedRGBA colors0 = COLOR_SENSOR_0.getNormalizedColors();
        NormalizedRGBA colors1 = COLOR_SENSOR_1.getNormalizedColors();
        NormalizedRGBA colors2 = COLOR_SENSOR_2.getNormalizedColors();

        if (colors0.blue >= colors1.blue && colors0.blue >= colors2.blue) _blue = colors0.blue;
        else if (colors1.blue >= colors0.blue && colors1.blue >= colors2.blue) _blue = colors1.blue;
        else _blue = colors2.blue;

        if (colors0.green >= colors1.green && colors0.green >= colors2.green) _green = colors0.green;
        else if (colors1.green >= colors0.green && colors1.green >= colors2.green) _green = colors1.green;
        else _green = colors2.green;

        LOG.writeToMemory(millis, "ball detection blue0", colors0.blue);
        LOG.writeToMemory(millis, "ball detection blue1", colors1.blue);
        LOG.writeToMemory(millis, "ball detection blue2", colors2.blue);

        LOG.writeToMemory(millis, "ball detection green0", colors0.green);
        LOG.writeToMemory(millis, "ball detection green1", colors1.green);
        LOG.writeToMemory(millis, "ball detection green2", colors2.green);
    }

    public boolean isBallDetected(double millis) {
        if (SharedData.Spindexer.currentPos == SharedData.Spindexer.targetPos && !SharedData.BallDetection.areAllDetected()) {
            update(millis);

            float threshold = 0.04F;

            LOG.writeToMemory(millis, "ball detection delay millis", DELAY_DETECTION.getCurrentMillis());
            LOG.writeToMemory(millis, "ball detection target delay millis", DELAY_DETECTION.getTargetMillis());

            if ((_blue > threshold || _green > threshold) && DELAY_DETECTION.allowExec()) {
                String colorCode = _blue > _green ? "P" : "G";

                LOG.writeToMemory(millis, "ball detection colorCode", colorCode);
                LOG.writeToMemory(millis, "ball detection detectionCount", SharedData.BallDetection.detectionCount);

                if (SharedData.BallDetection.detectionCount == 0) {
                    SharedData.Pattern.actual[2] = colorCode;
                    if (colorCode.equals("G")) SharedData.Pattern.actualIndex = 2;
                    SharedData.Spindexer.currentIndex += 2;
                } else if (SharedData.BallDetection.detectionCount == 1) {
                    SharedData.Pattern.actual[0] = colorCode;
                    if (colorCode.equals("G")) SharedData.Pattern.actualIndex = 0;
                    SharedData.Spindexer.currentIndex += 2;
                } else if (SharedData.BallDetection.detectionCount == 2) {
                    SharedData.Pattern.actual[1] = colorCode;
                    if (colorCode.equals("G")) SharedData.Pattern.actualIndex = 1;
                }

                LOG.writeToMemory(millis, "ball detection greenBallIndex", SharedData.Pattern.actualIndex);
                LOG.writeToMemory(millis, "ball detection actualPattern", String.join(",", SharedData.Pattern.actual));
                LOG.writeToMemory(millis, "ball detection all balls", SharedData.BallDetection.areAllDetected());

                SharedData.Spindexer.targetPos = SharedData.Spindexer.POSITIONS[SharedData.Spindexer.currentIndex];
                SharedData.BallDetection.detectionCount += 1;

                return true;
            }
        }

        LOG.flushToDisc();

        return false;
    }

    public Action detectBallAction(BooleanSupplier driveComplete, double millis) {
        return new Action() {
            private boolean initialized = false;

            @Override
            public boolean run(@NonNull TelemetryPacket packet) {
                if (!initialized) {
                    initialized = true;
                }

                return !isBallDetected(millis);
            }
        };
    }

    public Action clearActualAction(double millis) {
        return new Action() {
            private boolean initialized = false;

            @Override
            public boolean run(@NonNull TelemetryPacket packet) {
                if (!initialized) {
                    initialized = true;
                }

                SharedData.Pattern.actual = new String[] { "", "", "" };
                SharedData.Pattern.actualIndex = -1;

                return false;
            }
        };
    }
}

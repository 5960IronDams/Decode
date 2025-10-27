package org.firstinspires.ftc.teamcode.decode.core;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.ironDams.autonomus.subsystems.HuskyReader;

import java.util.function.BooleanSupplier;

public class Pattern {
    private final LinearOpMode OP_MODE;
//    private final Decoder DECODER;
    private final HuskyReader VISION;


    private int _targetGreenPos = -1;
    private int _actualGreenPos = -1;

    private String[] _actualPattern = { "", "", "" };

    public Pattern(LinearOpMode opMode) {
        OP_MODE = opMode;
//        DECODER = null;
        VISION = null;
    }

    public Pattern(LinearOpMode opMode, HuskyReader reader) {
        OP_MODE = opMode;
//        DECODER = decoder;
        VISION = reader;
    }

    public void makeActualMatchTarget() {
        String[] target = getTarget().split("");
        _actualPattern[0] = target[1];
        _actualPattern[1] = target[2];
        _actualPattern[2] = target[3];
        _actualGreenPos = _targetGreenPos;
    }

    public int getGreenTargetPos() {
        return _targetGreenPos;
    }

    public int getGreenActualPos() {
        return _actualGreenPos;
    }

    public String getTarget() {
        return _targetGreenPos == 0 ? "GPP" : _targetGreenPos == 1 ? "PGP" : _targetGreenPos == 2 ? "PPG" : "UUU";
    }

    public String getActual() {
        return String.join("", _actualPattern) ;
    }

    public String[] getActualPattern() {
        return _actualPattern;
    }

    private void setTargetWithHusky() {
        assert VISION != null;
        int huskyId = VISION.getFirstId();

        switch (huskyId) {
            case 1:
                _targetGreenPos = 2;
                break;
            case 2:
                _targetGreenPos = 0;
                break;
            case 3:
                _targetGreenPos = 1;
                break;
        }
    }

//    private void setTargetWithDecoder() {
//        assert DECODER != null;
//        int tagId = DECODER.readQr();
//
//        switch (tagId) {
//            case 22:
//                _targetGreenPos = 1;
//                break;
//            case 23:
//                _targetGreenPos = 2;
//                break;
//            case 21:
//                _targetGreenPos = 0;
//                break;
//        }
//    }

    private void setTargetWithPlayer() {
        assert OP_MODE != null;
        if (OP_MODE.gamepad2.x) {
            _targetGreenPos = 0;
        }
        else if (OP_MODE.gamepad2.a) {
            _targetGreenPos = 1;
        }
        else if (OP_MODE.gamepad2.b) {
            _targetGreenPos = 2;
        }
    }

    public Pattern setTargetPattern() {
        if (VISION != null) {
            setTargetWithHusky();
        }
//        else if (DECODER != null) {
//            setTargetWithDecoder();
//        }
        else if (OP_MODE != null) {
            setTargetWithPlayer();
        }

        return this;
    }

//    public void readPatternId() {
//        if (DECODER != null) {
//            int patternId = DECODER.readQr();
//            _targetGreenPos = patternId == 21 ? 0 : patternId == 22 ? 1 : patternId == 23 ? 2 : -1;
//        }
//    }

//    public boolean hasActualPattern(){
//        return !_actualPattern[0].isEmpty()
//        && !_actualPattern[1].isEmpty()
//        && !_actualPattern[2].isEmpty();
//    }

    public void updateActualPattern(int index, @NonNull String value) {
        if (value.equals("G")) _actualGreenPos = index;
        _actualPattern[index] = value;
    }

    public void clearActualPattern() {
        _actualGreenPos = -1;
        _actualPattern = new String[] { "", "", "" };
    }

    public Action runHuskyLens(BooleanSupplier driveComplete) {
        return new Action() {
            private boolean initialized = false;

            @Override
            public boolean run(@NonNull TelemetryPacket packet) {
                if (!initialized) {
                    initialized = true;
                }

                if (VISION == null) return false;

                setTargetWithHusky();

                packet.put("Husky Id", VISION.getFirstId());
                packet.put("Husky Target Green", _targetGreenPos);
                packet.put("Husky IsDriveComplete", driveComplete.getAsBoolean());

                return _targetGreenPos < 0 || !driveComplete.getAsBoolean();
            }
        };
    }

    public Action runWebcam(BooleanSupplier driveComplete) {
        return new Action() {
            private boolean initialized = false;

            @Override
            public boolean run(@NonNull TelemetryPacket packet) {
                if (!initialized) {
                    initialized = true;
                }

//                if (DECODER == null) return false;

//                setTargetWithDecoder();

//                packet.put("Decoder Vision Id", DECODER.readQr());
                packet.put("Decoder Target Green", _targetGreenPos);

                return _targetGreenPos < 0 || !driveComplete.getAsBoolean();
            }
        };
    }
}

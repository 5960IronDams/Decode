package org.firstinspires.ftc.teamcode.decode;

import androidx.annotation.NonNull;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.decode.core.Decoder;

public class Pattern {
    private final LinearOpMode OP_MODE;
    private final Decoder DECODER;

    private int _targetGreenPos = -1;
    private int _actualGreenPos = -1;

    private String[] _actualPattern = { "", "", "" };

    public Pattern(LinearOpMode opMode) {
        OP_MODE = opMode;
        DECODER = null;
    }

    public Pattern(Decoder decoder) {
        OP_MODE = null;
        DECODER = decoder;
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

    /**
     * Allows player 2 to rotate the pattern ids.<br>
     * <u>GAMEPAD2 x, a, b</u>
     * <li>X - 21:GPP</li>
     * <li>A - 22:PGP</li>
     * <li>B - 23:PPG</li>
     * @return The pattern object.
     */
    public Pattern setTargetPattern() {
        if (OP_MODE != null) {
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

        return this;
    }

    public void readPatternId() {
        if (DECODER != null) {
            int patternId = DECODER.readQr();
            _targetGreenPos = patternId == 21 ? 0 : patternId == 22 ? 1 : patternId == 23 ? 2 : -1;
        }
    }

    public boolean hasActualPattern(){
        return !_actualPattern[0].isEmpty()
        && !_actualPattern[1].isEmpty()
        && !_actualPattern[2].isEmpty();
    }

    public void updateActualPattern(int index, @NonNull String value) {
        if (value.equals("G")) _actualGreenPos = index;
        _actualPattern[index] = value;
    }

    public void clearActualPattern() {
        _actualGreenPos = -1;
        _actualPattern = new String[] { "", "", "" };
    }
}

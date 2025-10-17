package org.firstinspires.ftc.teamcode.decode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.decode.core.Decoder;

public class Pattern {
    private final LinearOpMode _opMode;
    private final Decoder _decoder;

    private int _patternId = 0;
    private String _target = "UUU";

    private String[] _patternBuilder = { "", "", "" };

    public Pattern(LinearOpMode opMode) {
        _opMode = opMode;
        _decoder = null;
    }

    public Pattern(Decoder decoder) {
        _opMode = null;
        _decoder = decoder;
    }

    public Pattern setTarget(String target) {
        _target = target;
        return this;
    }

    public String getTarget() {
        return _target;
    }

    public String getPattern() {
        return String.join("", _patternBuilder) ;
    }

    public void setPatternId(int id) {
        _patternId = id;
    }

    /**
     * Allows player 2 to rotate the pattern ids.<br>
     * <u>GAMEPAD2</u>
     * <li>X - Rotate Pattern ID: 21, 22, 23</li>
     * @return The pattern object.
     */
    public Pattern rotatePatternId() {
        if (_opMode != null && _opMode.gamepad2.x) {
            if (_patternId == 0 || _patternId == 23) _patternId = 21;
            else if (_patternId == 21) _patternId = 22;
            else if (_patternId == 22) _patternId = 23;
            else _patternId = 0;

            _opMode.sleep(Constants.WAIT_DURATION_MS);
        }

        return this;
    }

    public Pattern readPatternId() {
        if (_decoder != null) {
            _patternId = _decoder.readQr();
        }

        return this;
    }

    public void setTargetPattern() {
        if (_patternId == 21) {
            _target = "GPP";
        } else if (_patternId == 22) {
            _target = "PGP";
        } else if (_patternId == 23) {
            _target = "PPG";
        } else {
            _target = "UUU";
        }
    }

    public void updatePatternBuilder(int index, String value) {
        _patternBuilder[index] = value;
    }

    public void resetPatternBuilder() {
        _patternBuilder = new String[] { "", "", "" };
    }

    public int getGreenTarget() {
        String[] pt = _target.split("");
        if (pt[1].equals("G")) {
            return 0;
        } else if (pt[2].equals("G")) {
            return 1;
        } else if (pt[3].equals("G")) {
            return 2;
        }

        return -1;
    }

    public int getGreenPosition() {
        if (_patternBuilder[0].equals("G")) {
            return 0;
        } else if (_patternBuilder[1].equals("G")) {
            return 1;
        } else  if (_patternBuilder[2].equals("G"))  {
            return 2;
        }

        return -1;
    }
}

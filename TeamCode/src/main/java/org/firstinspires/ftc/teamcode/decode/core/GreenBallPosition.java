package org.firstinspires.ftc.teamcode.decode.core;

import java.util.Objects;

public class GreenBallPosition {
    private int _targetIndex = -1;
    private int _actualIndex = -1;

    private int _spindexerCurrentPos = 0;
    private int _spindexerDetectionPos = -1;
    private boolean _moveSpindexer = false;

    private String[] _actualPattern = { "", "", "" };
    private String _colorDetected = "";

    public String[] getActualPattern() {
        return _actualPattern;
    }

    public void setActualPattern(String[] pattern) {
        _actualPattern = pattern;
    }

    public void setActualColor(String color, int index) {
        if (Objects.equals(color, "G")) _actualIndex = index;
        _actualPattern[index] = color;
    }

    public boolean getMoveSpindexer() {
        return _moveSpindexer;
    }

    public void setMoveSpindexer(boolean move) {
        _moveSpindexer = move;
    }

    public int getTargetIndex() {
        return _targetIndex;
    }

    public void setTargetIndex(int index) {
        _targetIndex = index;
    }

    public int getActualIndex() {
        return _actualIndex;
    }

    public void setActualIndex(int index) {
        _actualIndex = index;
    }

    public String getColorDetected() {
        return _colorDetected;
    }

    public void setColorDetected(String color) {
        _colorDetected = color;
    }

    public int getSpindexerCurrentPos() {
        return _spindexerCurrentPos;
    }

    public void setSpindexerCurrentPos(int pos) {
        _spindexerCurrentPos = pos;
    }

    public int getSpindexerDetectionPos() {
        return _spindexerDetectionPos;
    }

    public void setSpindexerDetectionPos(int pos) {
        _spindexerDetectionPos = pos;
    }

    public boolean isLoaded() {
        return !_actualPattern[0].isEmpty() && !_actualPattern[1].isEmpty() && !_actualPattern[2].isEmpty();
    }
}

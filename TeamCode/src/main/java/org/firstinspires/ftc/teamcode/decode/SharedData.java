package org.firstinspires.ftc.teamcode.decode;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BooleanSupplier;
import java.util.function.IntSupplier;

public class SharedData {
    private final String[] ACTUAL_PATTERN = new String[] { "", "", "" };
    public String[] getActualPattern() {
        return ACTUAL_PATTERN;
    }

    public void resetActualPattern() {
        ACTUAL_PATTERN[0] = "";
        ACTUAL_PATTERN[1] = "";
        ACTUAL_PATTERN[2] = "";
    }

    public void setActualColorCode(String color, int index) {
        ACTUAL_PATTERN[index] = color;
        if (color.equals("G")) setGreenBallActualIndex(index);
    }

    private String _targetPattern = "";
    public String getTargetPattern() {
        return _targetPattern;
    }

    public void setTargetPattern(String pattern) {
        _targetPattern = pattern;
    }

    private int greenBallActalIndex = -1;
    public int getGreenBallActualIndex() {
        return greenBallActalIndex;
    }

    public void setGreenBallActualIndex(int index) {
        greenBallActalIndex = index;
    }

    private int greenBallTargetIndex = -1;
    public int getGreenBallTargetIndex() {
        return greenBallTargetIndex;
    }

    public void setGreenBallTargetIndex(int index) {
        greenBallTargetIndex = index;
    }

    public boolean isSpindexerLoaded() {
        return !Objects.equals(ACTUAL_PATTERN[0], "") &&
                !Objects.equals(ACTUAL_PATTERN[1], "") &&
                !Objects.equals(ACTUAL_PATTERN[2], "");
    }

    private int _spindexerCurrentIndex = 0;
    public int getSpindexerCurrentIndex() {
        return _spindexerCurrentIndex;
    }

    public void setSpindexerCurrentIndex(int index) {
        _spindexerCurrentIndex = index;
    }

    private int _spindexerDetectionIndex = -1;
    public int getSpindexerDetectionIndex() {
        return _spindexerDetectionIndex;
    }

    public void setSpindexerDetectionIndex(int index) {
        _spindexerDetectionIndex = index;
    }

    private boolean _moveSpindexer = false;
    public boolean getMoveSpindexer() {
        return _moveSpindexer;
    }

    public void setMoveSpindexer(boolean move) {
        _moveSpindexer = move;
    }

    public Constants.Spindexer.Mode _spindexerMode = Constants.Spindexer.Mode.INDEX;
    public Constants.Spindexer.Mode getSpindexerMode() {
        return _spindexerMode;
    }

    public void setSpindexerMode(Constants.Spindexer.Mode mode) {
        _spindexerMode = mode;
    }

    public final AtomicInteger _spindexerState = new AtomicInteger(0); // Index
    public IntSupplier getSpindexerStateSupplier() {
        return _spindexerState::get;
    }

    public void setSpindexerState(int state) {
        _spindexerState.set(state);
    }

    private int _shotCount = 0;
    public int getShotCount() {
        return _shotCount;
    }

    public void setShotCount(int shotCount) {
        _shotCount = shotCount;
    }

    private final AtomicBoolean _shootComplete = new AtomicBoolean(false);
    public BooleanSupplier getShootComplete() {
        return _shootComplete::get;
    }

    public void setShootComplete(boolean shootComplete) {
        _shootComplete.set(shootComplete);
    }


    private final AtomicBoolean _readyToSort = new AtomicBoolean(true);
    public BooleanSupplier getReadyToSort() {
        return _readyToSort::get;
    }

    public void setReadyToSort(boolean readyToSort) {
        _readyToSort.set(readyToSort);
    }

    private final AtomicBoolean hasPatternChanged = new AtomicBoolean(false);
    public BooleanSupplier getHasPatternChanged() {
        return hasPatternChanged::get;
    }

    public void setHasPatternChanged(boolean patternChanged) {
        hasPatternChanged.set(patternChanged);
    }
}

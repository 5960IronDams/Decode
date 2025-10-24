package org.firstinspires.ftc.teamcode.ironDams.core;

public final class Acceleration {

    private static boolean _isAccelerating;
    private static double _distance;
    private static double _calculatedCurrentPos;
    private static double _remainingDistance;

    private static double _percentInAccel;

    private static double _decelAtPos;
    private static double _distanceIntoDecel;
    private static double _percentInDecel;
    private static double _powerRange;

    public static boolean getIsAccelerating() { return _isAccelerating; }
    public static double getDistance() { return _distance; }
    public static double getCalculatedCurrentPos() { return _calculatedCurrentPos; }
    public static double getRemainingDistance() { return _remainingDistance; }

    public static double getPercentInAccel() { return _percentInAccel; }
    public static double getPowerRange() { return _powerRange; }

    public static double getDecelAtPos() { return _decelAtPos; }
    public static double getDistanceIntoDecel() { return _distanceIntoDecel; }
    public static double getPercentInDecel() { return _percentInDecel; }

    public static double getPower(double targetPos, double currentPos, double startPos,
                                  double accelToPos, double decelAtDistance, double minPower, double maxPower) {

        minPower = Math.abs(minPower);
        maxPower = Math.abs(maxPower);

        _isAccelerating = currentPos < accelToPos;
        _distance = Math.abs(startPos - targetPos);
        _calculatedCurrentPos = Math.abs(startPos - currentPos);
        _remainingDistance = targetPos - _calculatedCurrentPos;

        if (_isAccelerating) {
            return getAccelPower(currentPos, accelToPos, minPower, maxPower);
        } else {
            return getDecelPower(_distance, _remainingDistance, decelAtDistance, minPower, maxPower);
        }
    }

    private static double getDecelPower(double distance, double remainingDistance, double decelAtDistance, double minPower, double maxPower) {
        _decelAtPos = distance - decelAtDistance;
        _distanceIntoDecel = distance - _decelAtPos;

        if (_distanceIntoDecel >= 0) {
            _percentInDecel = remainingDistance / _distanceIntoDecel;
            return Math.max(0, Math.min(maxPower, _percentInDecel * maxPower));
        }

        return maxPower;
    }

    private static double getAccelPower(double calculatedCurrentPos, double accelToPos, double minPower, double maxPower) {
        _percentInAccel = calculatedCurrentPos / accelToPos;
        _powerRange = (maxPower - minPower) * _percentInAccel;

        return Math.max(minPower, Math.min(maxPower, minPower + _powerRange));
    }
}

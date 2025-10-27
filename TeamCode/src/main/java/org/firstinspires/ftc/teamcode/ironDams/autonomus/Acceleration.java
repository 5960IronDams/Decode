package org.firstinspires.ftc.teamcode.ironDams.autonomus;

public final class Acceleration {
    public static double getPower(double startPos, double currentPos, double targetPos,
                                  double accelZone, double decelZone,
                                  double minPower, double maxPower) {

        double traveled = Math.abs(currentPos - startPos);
        double totalDistance = Math.abs(targetPos - startPos);
//        double direction = Math.signum(targetPos - startPos);

        if (totalDistance < accelZone + decelZone) {
            return minPower;
        }

        if (traveled < accelZone) {
            double percent = traveled / accelZone;
            return (minPower + percent * (maxPower - minPower));
        }

        double decelStart = totalDistance - decelZone;
        double distancePastDecelStart = traveled - decelStart;
        double percent = Math.min(1.0, distancePastDecelStart / decelZone);
        return Math.max(minPower, maxPower * (1.0 - percent));
    }
//
//    private static double getDecelPower(double distance, double remainingDistance, double decelAtDistance, double minPower, double maxPower) {
//        _decelAtPos = distance - decelAtDistance;
//        _distanceIntoDecel = distance - _decelAtPos;
//
//        if (_distanceIntoDecel >= 0) {
//            _percentInDecel = remainingDistance / _distanceIntoDecel;
//            return Math.max(0, Math.min(maxPower, _percentInDecel * maxPower));
//        }
//
//        return maxPower;
//    }
//
//    private static double getAccelPower(double calculatedCurrentPos, double accelToPos, double minPower, double maxPower) {
//        _percentInAccel = calculatedCurrentPos / accelToPos;
//        _powerRange = (maxPower - minPower) * _percentInAccel;
//
//        return Math.max(minPower, Math.min(maxPower, minPower + _powerRange));
//    }
}

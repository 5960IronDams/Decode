package org.firstinspires.ftc.teamcode.irondams.core.driveTrain;

public final class Acceleration {
    public static double getPower(double startPos, double currentPos, double targetPos,
                                  double accelZone, double decelZone,
                                  double minPower, double maxPower) {

        double traveled = Math.abs(currentPos - startPos);
        double totalDistance = Math.abs(targetPos - startPos);

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

    public static double rampPower(double currentPower, double requestedPower) {
        if (requestedPower == 0) return 0;

        double maxDelta = 0.05;
        double delta = requestedPower - currentPower;

        if (Math.abs(delta) > maxDelta)
            delta = Math.signum(delta) * maxDelta;

        return currentPower + delta;
    }
}
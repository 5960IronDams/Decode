package org.firstinspires.ftc.teamcode.irondams.core.driveTrain;

public final class Acceleration {
    public static double getPower(double startPos, double currentPos, double targetPos,
                                  double accelZone, double decelZone,
                                  double minPower, double maxPower) {

        double traveled = Math.abs(currentPos - startPos);
        double remainingDist = Math.abs(targetPos - currentPos);

        double powerRange = maxPower - minPower;

        if (traveled < accelZone) {
            double percentage = traveled / accelZone;
            return minPower + percentage * powerRange;
        } else if (remainingDist < decelZone) {
            double percentage = remainingDist / decelZone;
            return percentage * maxPower;
        } else {
            return maxPower;
        }
    }

    public static double rampPower(double currentPower, double requestedPower) {
        if (requestedPower == 0) return 0;

        double maxDelta = 0.075;
        double delta = requestedPower - currentPower;

        if (Math.abs(delta) > maxDelta)
            delta = Math.signum(delta) * maxDelta;

        return currentPower + delta;
    }
}
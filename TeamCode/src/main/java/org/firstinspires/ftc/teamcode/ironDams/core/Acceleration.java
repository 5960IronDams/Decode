package org.firstinspires.ftc.teamcode.ironDams.core;

public final class Acceleration {
    public static double getPower(int targetPos, int currentPos, int startDeceleration, double maxPower) {
//        double decel = targetPos - startDeceleration;
        double distance = targetPos - currentPos;

        if (distance > startDeceleration) return maxPower;
        else return Math.max(-maxPower, Math.min(maxPower, Math.sqrt(distance / startDeceleration)));
    }
}

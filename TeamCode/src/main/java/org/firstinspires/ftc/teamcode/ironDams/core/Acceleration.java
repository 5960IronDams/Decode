package org.firstinspires.ftc.teamcode.ironDams.core;

public final class Acceleration {
//    public static double getPower(int targetPos, int currentPos, int startDeceleration, double maxPower) {
////        double decel = targetPos - startDeceleration;
//        double distance = targetPos - currentPos;
//
//        if (distance > startDeceleration) return maxPower;
//        else return Math.max(-maxPower, Math.min(maxPower, Math.sqrt(distance / startDeceleration)));
//    }

    public static double getPower(double maxPow,double startPow,int targetPos, int currentPos, int startPos,int accelTo, int deccelAt)
    {
        int decelDist = targetPos-deccelAt;
        if (currentPos > decelDist){
            int actualDist =  targetPos - currentPos;
            double deccelPerc = (double)actualDist / (double)decelDist;
            return maxPow * deccelPerc;
        } else {
         return 0;
        }
    }
}

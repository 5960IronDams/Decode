package org.firstinspires.ftc.teamcode.decode;

import java.util.Objects;

public final class SharedData {
    public static final class Spindexer {
        public static final double[] POSITIONS = {
                0.027,
                0.0605,
                0.089,
                0.127,
                0.1535,
                0.191,
                0.2225,
                0.2585,
                0.2865,
                0.3225,
                0.3555,
                0.39,
                0.42,
                0.4555,
                0.4905,
                0.523,
                0.5595,
                0.598,
                0.6295,
                0.6645,
                0.695,
                0.729,
                0.7605,
                0.793,
                0.8240,
                0.86,
                0.888,
                0.9235,
                0.958,
                0.99
        };

        public static int currentIndex;
        public static double targetPos;
        public static double currentPos;
    }

    public static final class Launcher {
        public static boolean isActive = false;
        public static int shotCount = 0;
    }

    public static final class Pattern {
        public static int targetIndex = -1;
        public static int actualIndex = -1;

        public static String target = "";
        public static String[] actual = new String[] { "", "", "" };
    }

    public static final class BallDetection {
        public static int detectionCount = 0;
        public static boolean areAllDetected() {
            return !Objects.equals(Pattern.actual[0], "") &&
                    !Objects.equals(Pattern.actual[1], "") &&
                    !Objects.equals(Pattern.actual[2], "");
        };
    }
}

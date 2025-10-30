package org.firstinspires.ftc.teamcode.decode;

public final class Constants {
    public final static class ColorVision {
        public static final int THRESHOLD = 3000;
    }

    public static final class Spindexer {
        public static final double[] Positions = { 0.022, 0.057, 0.084, 0.120, 0.1485, 0.1785, 0.215, 0.2495, 0.2815, 0.315, 0.348, 0.385, 0.4125, 0.453, 0.4845, 0.518, 0.5525,  0.5905, 0.6200, 0.66, 0.6875, 0.724, 0.7490, 0.7895, 0.8140, 0.8560, 0.8805, 0.916, 0.9455, 0.9875 };
        public enum Mode { INDEX, SORT, PRE_SHOOT, SHOOT }
    }

    public static final class Shooter {
        public static final int SHOOT_DELAY_MS = 1000;
        public static final int PATTERN_CHANGE_DELAY_MS = 1000;

        public static final double TARGET_VELOCITY = 1950;

        public static final double RIGHT_TPS = 2245;
        public static final double LEFT_TPS = 2305;

        public static final double TARGET_VOLT = 12;

        public static final double OPEN_POS = 0.37;
        public static final double CLOSE_POS = 0;
    }

    public static final class Intake {
        public static final double TARGET_VELOCITY = 1000;

        public static final double TPS = 2245;
        public static final double TARGET_VOLT = 12;

        public enum Mode {
            ACTIVE, INACTIVE
        }
    }
}

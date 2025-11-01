package org.firstinspires.ftc.teamcode.decode;

public final class Constants {
    public final static class ColorVision {
        public static final int BLUETHRESHOLD = 2500;
        public static final int GREENTHRESHOLD = 2500;
    }

    public static final class Spindexer {
        public static final double[] Positions = { 0.027, 0.058, 0.089, 0.127, 0.1535, 0.191, 0.22, 0.257, 0.2865, 0.3225, 0.3555, 0.39, 0.4175, 0.4555, 0.488, 0.523, 0.5585, 0.599, 0.6295, 0.6645, 0.6925, 0.7265, 0.7605, 0.798, 0.8240, 0.8575, 0.888, 0.9235, 0.958, 0.995 };
        public enum Mode { INDEX, SORT, PRE_SHOOT, SHOOT }
    }

    public static final class Shooter {
        public static final int SHOOT_DELAY_MS = 1000;
        public static final int PATTERN_CHANGE_DELAY_MS = 1000;

        public static final double TARGET_VELOCITY = 2075;

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

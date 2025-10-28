package org.firstinspires.ftc.teamcode.decode;

public final class Constants {
    public final static class ColorVision {
        public static final int THRESHOLD = 2000;
    }

    public static final class Spindexer {
        public static final double[] Positions = { 0.023, 0.056, 0.084, 0.1210, 0.1485, 0.1790, 0.2135, 0.25, 0.2815, 0.315, 0.3465, 0.3825, 0.4125, 0.4475, 0.4845, 0.5185, 0.5555,  0.5855, 0.6200, 0.6570, 0.6875, 0.7195, 0.7490, 0.7845, 0.8140, 0.8560, 0.8805, 0.9155, 0.9455, 0.9825 };
        public enum Mode { INDEX, SORT, PRE_SHOOT, SHOOT }
    }

    public static final class Shooter {
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

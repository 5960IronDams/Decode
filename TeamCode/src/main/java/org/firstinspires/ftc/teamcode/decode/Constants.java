package org.firstinspires.ftc.teamcode.decode;

public final class Constants {

    public static final long WAIT_DURATION_MS = 250;

    public static final class ColorVision {
        public static final int COLOR_THRESHOLD = 2000;
        public static final String COLOR_CENTER_ID = "colorC"; // Color Sensor
    }

    public static final class Spindexer {
        public static final String SPINDEXER_ID = "spindex"; // Positional Servo // 0.0295

        public static final double[] Positions = { 0.019, 0.053, 0.082, 0.1175, 0.1455, 0.1780, 0.2115, 0.2470, 0.2805, 0.3125, 0.3445, 0.3795, 0.4105, 0.4455, 0.4825, 0.5165, 0.5515,  0.5855, 0.6175, 0.6540, 0.6875, 0.7185, 0.7485, 0.7845, 0.8140, 0.8490, 0.8805, 0.9125, 0.9455, 0.9815 };

        public enum Mode {
            INDEX, SORT, SHOOT
        }
    }
    public static final class Intake {
        public static final String INTAKE_ID = "intake";
        public static final double MAX_POWER = 0.3;
        public static final double SORT_POWER = 0.2;

        public enum Mode {
            ACTIVE, INACTIVE
        }
    }

    public static final class Shooter {
        public static final String LAUNCHER_ID = "launcher";
        public static final String MOTOR_LEFT_ID = "leftOut";
        public static final String MOTOR_RIGHT_ID = "rightOut";

        public static final double LEFT_TICKS_PER_SEC = 2380;

        public static final double MAX_POWER = 0.72;
        public static final double OPEN_POS = 0.37;
        public static final double CLOSED_POS = 0.0;

        public static final int BALL_DROP_DELAY = 500;

        public static final long MIN_LEFT_CURRENT = 600;
        public static final long MAX_LEFT_CURRENT = 875;

        public static final long MIN_RIGHT_CURRENT = 650;
        public static final long MAX_RIGHT_CURRENT = 1015;

        public static final long BALL_DETECTION_CURRENT = 1250;
    }
}

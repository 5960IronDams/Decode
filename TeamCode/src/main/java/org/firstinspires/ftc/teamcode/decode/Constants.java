package org.firstinspires.ftc.teamcode.decode;

public final class Constants {

    public static final long WAIT_DURATION_MS = 250;

    public static final class ColorVision {
        public static final int COLOR_THRESHOLD = 1000;
        public static final String COLOR_CENTER_ID = "colorC"; // Color Sensor
    }

    public static final class Spindexer {
        public static final String SPINDEXER_ID = "spindex"; // Positional Servo // 0.0295

        public static final double[] Positions = { 0.022, 0.053, 0.085, 0.1195, 0.1475, 0.1795, 0.2145, 0.2500, 0.2825, 0.3145, 0.3445, 0.3795, 0.4135, 0.4465, 0.4845, 0.5165, 0.5515,  0.5855, 0.6205, 0.6550, 0.6865, 0.7185, 0.7505, 0.7865, 0.8174, 0.8514, 0.8825, 0.9145, 0.9455, 0.9825 };

        public enum Mode {
            INTAKE, SORT, SHOOT
        }
    }
    public static final class Intake {
        public static final String INTAKE_ID = "intake";
        public static final double MAX_POWER = 0.5;

        public enum Mode {
            ACTIVE, INACTIVE
        }
    }

    public static final class Launcher {
        public static final String LAUNCHER_ID = "launcher";
        public static final String MOTOR_LEFT_ID = "leftOut";
        public static final String MOTOR_RIGHT_ID = "rightOut";

        public static final double MAX_POWER = 0.58125;
        public static final double OPEN_POS = 0.37;
        public static final double CLOSED_POS = 0.0;

        public static final int BALL_DROP_DELAY = 3000;

        public static final long MIN_LEFT_CURRENT = 1030;
        public static final long MAX_LEFT_CURRENT = 1060;

        public static final long MIN_RIGHT_CURRENT = 1100;
        public static final long MAX_RIGHT_CURRENT = 1130;
    }
}

package org.firstinspires.ftc.teamcode.decode;

public final class Constants {

    public static final long WAIT_DURATION_MS = 100;

    public static final class ColorVision {
        public static final int COLOR_THRESHOLD = 250;
        public static final String COLOR_CENTER_ID = "colorC"; // Color Sensor
    }

    public static final class Spindexer {
        public static final String SPINDEXER_ID = "spindex"; // Positional Servo // 0.0295

        public static final double[] Positions = { 0, 0.028, 0.0575, 0.094, 0.1235, 0.161, 0.188, 0.2215, 0.2545, 0.288, 0.322, 0.354, 0.387, 0.425, 0.4535, 0.492, 0.528,  0.5575, 0.5975, 0.6265, 0.666, 0.6955, 0.7255, 0.7345, 0.7645, 0.7935, 0.823, 0.8525, 0.882, 0.9115, 0.941, 0.9705};

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

        public static final double MAX_POWER = 0.525;
        public static final double OPEN_POS = 0.27;
        public static final double CLOSED_POS = 0.0;

        public static final int BALL_DROP_DELAY = 3000;

        public static final long MIN_LEFT_CURRENT = 1030;
        public static final long MAX_LEFT_CURRENT = 1060;

        public static final long MIN_RIGHT_CURRENT = 1100;
        public static final long MAX_RIGHT_CURRENT = 1130;
    }
}

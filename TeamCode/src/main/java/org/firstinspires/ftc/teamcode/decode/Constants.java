package org.firstinspires.ftc.teamcode.decode;

public final class Constants {

    public static final long WAIT_DURATION_MS = 100;

    public static final class ColorVision {
        public static final int COLOR_THRESHOLD = 250;
        public static final String COLOR_CENTER_ID = "colorC"; // Color Sensor
    }

    public static final class Spindexer {
        public static final String SPINDEXER_ID = "spindex"; // Positional Servo

        public static final double[] Positions = { 0, 0.033, 0.066, 0.099, 0.132, 0.165, 0.198, 0.231, 0.264, 0.297, 0.330, 0.363 };

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

        public static final double MAX_POWER = 0.5;
        public static final double OPEN_POS = 0.27;
        public static final double CLOSED_POS = 0.0;

        public static final int BALL_DROP_DELAY = 1000;
    }
}

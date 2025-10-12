package org.firstinspires.ftc.teamcode.decode;

public final class Constants {

    public static final long WAIT_DURATION_MS = 100;

    public static final class Spindexer {
        public static final String COLOR_CENTER_ID = "colorC";
        public static final String SPINDEXER_ID = "spindex";
        public static final String ABS_ENCODER_ID = "enc";

        public static final int COLOR_THRESHOLD = 250;
        public static final int TURN_TICKS = 2730;

        public enum Mode {
            INTAKE, SORT, SHOOT
        }
    }
    public static final class Intake {
        public static final String INTAKE_ID = "intake";
        public static final double MAX_POWER = 0.5;
    }
}

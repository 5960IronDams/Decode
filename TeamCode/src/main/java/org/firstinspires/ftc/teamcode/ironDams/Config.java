package org.firstinspires.ftc.teamcode.ironDams;

public final class Config {

    public static final long USER_DELAY_MS = 500;

    public static final class Hardware {
        public static final class Motors {
            public static final class DriveTrain {
                public static final String LEFT_FRONT_MOTOR_ID = "leftFront";
                public static final String RIGHT_FRONT_MOTOR_ID = "rightFront";
                public static final String LEFT_BACK_MOTOR_ID = "leftBack";
                public static final String RIGHT_BACK_MOTOR_ID = "rightBack";
            }

            public static final class Intake {
                public static final String INTAKE_MOTOR_ID = "intake";
            }

            public static final class Shooter {
                public static final String LEFT_MOTOR_ID = "leftOut";
                public static final String RIGHT_MOTOR_ID = "rightOut";
            }
        }

        public static final class Servos {
            public static final class Shooter {
                public static final String LEVER_ID = "launcher";
                public static final String EEERRR_ID = "eeerrr";
            }

            public static final class Spindexer {
                public static final String SPINDEXER_ID = "spindex";
            }
        }

        public static final class Sensors {
            public static final class Spindexer {
                public static final String COLOR_ID = "colorC";
            }
        }

        public static final class Cameras {
            public static final String LEFT_CAMERA_ID = "Webcam 1";
            public static final String RIGHT_CAMERA_ID = "Webcam 2";
            public static final String HUSKY_ID = "huskylens";
        }

        public static final class Gyros {
            public static final String EXP_IMU_ID = "imu";
            public static final String CTRL_IMU_ID = "imu2";
            public static final String PINPOINT_ID = "pinpoint";
        }
    }
}

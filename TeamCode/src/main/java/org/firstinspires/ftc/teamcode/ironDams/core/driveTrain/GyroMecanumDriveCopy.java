//package org.firstinspires.ftc.teamcode.ironDams.core.driveTrain;
//
//import com.qualcomm.robotcore.hardware.Gamepad;
//import com.qualcomm.robotcore.hardware.HardwareMap;
//
//import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
//import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
//import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
//import org.firstinspires.ftc.robotcore.external.navigation.Pose2D;
//import org.firstinspires.ftc.teamcode.ironDams.core.odometry.IGyro;
//import org.firstinspires.ftc.teamcode.ironDams.core.odometry.Imu;
//import org.firstinspires.ftc.teamcode.ironDams.core.odometry.Pinpoint;
//
//public class GyroMecanumDriveCopy
//        extends FourWheelDrive
//        implements IDriveTrain {
//    private final IGyro _gyro;
//
//    public GyroMecanumDriveCopy(HardwareMap hardwareMap, Gamepad gamepad, boolean usePinpoint) {
//        super(hardwareMap);
//
//        _gyro = usePinpoint ? new Pinpoint(hardwareMap, new Pose2D(DistanceUnit.INCH, 0, 0, AngleUnit.DEGREES, 0))
//                : new Imu(hardwareMap);
//    }
//
//
//    @Override
//    public void init() { }
//
//    @Override
//    public void drive(double powerX, double powerY, double powerTurn) {
//        double zeroedYaw = _gyro.update();
//        double headingRadians = _gyro.getPose().getHeading(AngleUnit.RADIANS);
//
//        double y = -powerY;
//        double x = powerX;
//        double turn = powerTurn;
//
//        double rotateX = x * Math.cos(headingRadians) - y * Math.sin(headingRadians);
//        double rotateY = x * Math.sin(headingRadians) + y * Math.cos(headingRadians);
//
//        double denominator = Math.max(Math.abs(rotateY) + Math.abs(rotateX) + Math.abs(turn), 1);
//
//        double leftFront = (rotateY + rotateX + turn) / denominator;
//        double rightFront = (rotateY - rotateX - turn) / denominator;
//        double leftBack = (rotateY - rotateX + turn) / denominator;
//        double rightBack = (rotateY + rotateX - turn) / denominator;
//
//        _leftFrontDrive.setPower(leftFront);
//        _rightFrontDrive.setPower(rightFront);
//        _leftBackDrive.setPower(leftBack);
//        _rightBackDrive.setPower(rightBack);
//
//        // Buttons
////        if (_gamepad1.b) {
////            _gyro.reset();
////        }
//        //TODO
//    }
//}

package org.firstinspires.ftc.teamcode.ironDams.teleOp.backup;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;

@TeleOp(name = "GyroTele", group = "Primary")
public class GyroTele extends LinearOpMode {
    private DcMotor leftFrontDrive = null;
    private DcMotor leftBackDrive = null;
    private DcMotor rightFrontDrive = null;
    private DcMotor rightBackDrive = null;
    BNO055IMU imu;
    Orientation angles = new Orientation();

    double initYaw;
    double adjustedYaw;

    @Override
    public void runOpMode() throws InterruptedException {
        BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();
        parameters.angleUnit = BNO055IMU.AngleUnit.DEGREES;
        parameters.mode = BNO055IMU.SensorMode.IMU;
        parameters.accelUnit           = BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC;
        parameters.loggingEnabled      = false;


        imu = hardwareMap.get(BNO055IMU.class, "imu");

        imu.initialize(parameters);

        angles = imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);

        initYaw = angles.firstAngle;


        leftFrontDrive = hardwareMap.get(DcMotor.class, "leftFront");
        leftBackDrive = hardwareMap.get(DcMotor.class, "leftBack");
        rightBackDrive = hardwareMap.get(DcMotor.class, "rightBack");
        rightFrontDrive = hardwareMap.get(DcMotor.class, "rightFront");
        leftFrontDrive.setDirection(DcMotor.Direction.FORWARD);
        leftBackDrive.setDirection(DcMotor.Direction.FORWARD);
        rightFrontDrive.setDirection(DcMotor.Direction.REVERSE);
        rightBackDrive.setDirection(DcMotor.Direction.REVERSE);


        leftFrontDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        waitForStart();

        while (opModeIsActive()) {
            angles = imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);

            adjustedYaw = angles.firstAngle-initYaw;

            double zeroedYaw = -initYaw+angles.firstAngle;

            double x = gamepad1.right_stick_x;
            double y = -gamepad1.right_stick_y;
            double turn = -gamepad1.left_stick_x;

            double theta = Math.atan2(y, x) * 180/Math.PI; // aka angle

            double realTheta;

            realTheta = (360 - zeroedYaw) + theta;

            if (gamepad1.left_trigger != 0) {
                realTheta = Math.atan2(y, x) * 180 / Math.PI;
            } else if ((gamepad1.right_trigger != 0)) {
                theta = Math.atan2(y, x) * 180 / Math.PI;
                realTheta = (360 - zeroedYaw + theta) % 360;
            }
            double power = Math.hypot(x, y);

            double sin = Math.sin((realTheta * (Math.PI / 180)) - (Math.PI / 4));
            double cos = Math.cos((realTheta * (Math.PI / 180)) - (Math.PI / 4));
            double maxSinCos = Math.max(Math.abs(sin), Math.abs(cos));

            double leftFront = (power * cos / maxSinCos + turn);
            double rightFront = (power * sin / maxSinCos - turn);
            double leftBack = (power * sin / maxSinCos + turn);
            double rightBack = (power * cos / maxSinCos - turn);


            if ((power + Math.abs(turn)) > 1) {
                leftFront /= power + turn;
                rightFront /= power - turn;
                leftBack /= power + turn;
                rightBack /= power - turn;
            }

            leftFrontDrive.setPower(leftFront);
            rightFrontDrive.setPower(rightFront);
            leftBackDrive.setPower(leftBack);
            rightBackDrive.setPower(rightBack);

            // Buttons
            if (gamepad1.b){
                initYaw = imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES).firstAngle;
            }
        }
    }
}
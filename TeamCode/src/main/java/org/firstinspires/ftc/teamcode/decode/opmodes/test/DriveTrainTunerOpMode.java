package org.firstinspires.ftc.teamcode.decode.opmodes.test;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.MotorControlAlgorithm;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;
import org.firstinspires.ftc.teamcode.irondams.core.Logger;
import org.firstinspires.ftc.teamcode.irondams.core.WaitFor;
import org.firstinspires.ftc.teamcode.irondams.core.driveTrain.FourWheelDriveTrain;
import org.firstinspires.ftc.teamcode.irondams.core.driveTrain.MecanumDriveTrain;

@TeleOp(name = "DriveTrainTuner", group = "Test")
public class DriveTrainTunerOpMode extends LinearOpMode {
    @Override
    public void runOpMode() {
        ElapsedTime timer = new ElapsedTime();
        Logger log = new Logger(this.getClass().getSimpleName());
        FourWheelDriveTrain fourWheelDriveTrain = new FourWheelDriveTrain(hardwareMap);
        MecanumDriveTrain drive = new MecanumDriveTrain(fourWheelDriveTrain, true);

        telemetry.addLine("Ready");
        telemetry.update();

        double mlfv = 0;
        double mlbv = 0;
        double mrbv = 0;
        double mrfv = 0;

        double power = 0;

        waitForStart();

        timer.reset();

        while (opModeIsActive() && !isStopRequested()) {
            if (gamepad1.dpad_up) {
                timer.reset();
                drive.drive(1, 0, 0);
            } else if (gamepad1.dpad_down) {
                drive.drive(0, 0, 0);
            }

            double millis = timer.milliseconds();

            DcMotorEx[] motors = drive.getMotors();

            for (int i = 0; i < motors.length; i++) {
                telemetry.addData("motor " + i + " power", motors[i].getPower());
                telemetry.addData("motor " + i + " velocity", motors[i].getVelocity());

                log.writeToMemory(millis, "motor " + i + " power", motors[i].getPower());
                log.writeToMemory(millis, "motor " + i + " velocity", motors[i].getVelocity());
            }

            log.flushToDisc();
            telemetry.update();
        }
    }
}

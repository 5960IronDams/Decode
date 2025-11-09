package org.firstinspires.ftc.teamcode.decode.opmodes.player;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.VoltageSensor;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.irondams.core.driveTrain.MecanumDrive;
import org.firstinspires.ftc.teamcode.decode.SharedData;
import org.firstinspires.ftc.teamcode.decode.core.BallDetection;
import org.firstinspires.ftc.teamcode.decode.core.Intake;
import org.firstinspires.ftc.teamcode.decode.core.Launcher;
import org.firstinspires.ftc.teamcode.decode.core.Spindexer;
import org.firstinspires.ftc.teamcode.irondams.core.Logger;
import org.firstinspires.ftc.teamcode.irondams.core.WaitFor;

@TeleOp(name = "PlayerOpMode", group = "IronDams")
public class PlayerOpMode extends LinearOpMode {
    @Override
    public void runOpMode() {
        ElapsedTime timer = new ElapsedTime();
        Logger logger = new Logger(this.getClass().getSimpleName());

        WaitFor playerTwoDelay = new WaitFor(500);
        WaitFor playerOneDelay = new WaitFor(500);

        VoltageSensor voltageSensor = hardwareMap.voltageSensor.iterator().next();
        BallDetection ballDetection = new BallDetection(this, logger);
        Spindexer spindexer = new Spindexer(this, logger);
        Intake intake = new Intake(this, voltageSensor);
        Launcher launcher = new Launcher(this, voltageSensor, logger);
        MecanumDrive drive = new MecanumDrive(this);

        boolean driveMode = false;

        telemetry.addLine("Ready");
        telemetry.update();

        while (opModeInInit()) {
            setTargetPattern();

            if (gamepad1.right_trigger != 0 && playerOneDelay.allowExec()) {
                driveMode = drive.switchDrive();
            }
        }

        waitForStart();

        while (opModeIsActive() && !isStopRequested()) {

            double millis = timer.milliseconds();

            if (gamepad1.right_trigger != 0 && playerOneDelay.allowExec()) {
                driveMode = drive.switchDrive();
            }

            if (gamepad1.b && playerOneDelay.allowExec()) {
                drive.resetFieldView();
            }

            drive.drive(gamepad1.right_stick_x, gamepad1.right_stick_y, gamepad1.left_stick_x);

            if (gamepad2.dpad_down) {
                SharedData.Launcher.isActive = true;
            }

            if (SharedData.Launcher.isActive) {
              launcher.outtakePos().open().setTargetVelocity();
              launcher.run(millis);
              spindexer.moveSpindexer(millis);
            } else {
                setTargetPattern();
                ballDetection.isBallDetected(millis);

                if (SharedData.BallDetection.areAllDetected()) {
                    launcher.outtakePos().open();
                    intake.isActive = false;
                    intake.setVelocity(0);
                } else {
                    launcher.intakePos().close();
                }

                spindexer.sortBalls(millis);
                spindexer.moveSpindexer(millis);

                if (gamepad2.left_trigger != 0 && playerTwoDelay.allowExec()) {
                    intake.isActive = !intake.isActive;
                }

                if (intake.isActive) intake.setTargetVelocity();
                else intake.setVelocity(0);
            }

            telemetry.addData("Drive Mode", driveMode ? "Field": "Robot");
            telemetry.addData("Target Pattern", SharedData.Pattern.target);
            telemetry.addData("Actual Pattern", String.join(",", SharedData.Pattern.actual));
            telemetry.update();
        }
    }

    private void setTargetPattern() {
        if (gamepad2.a) {
            SharedData.Pattern.targetIndex = 1;
        } else if (gamepad2.b) {
            SharedData.Pattern.targetIndex = 2;
        } else if (gamepad2.x) {
            SharedData.Pattern.targetIndex = 0;
        }

        SharedData.Pattern.target =
                SharedData.Pattern.targetIndex == 0 ? "GPP" :
                    SharedData.Pattern.targetIndex == 1 ? "PGP" :
                        SharedData.Pattern.targetIndex == 2 ? "PPG": "";
    }
}

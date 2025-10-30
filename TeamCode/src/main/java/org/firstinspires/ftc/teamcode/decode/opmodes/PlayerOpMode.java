package org.firstinspires.ftc.teamcode.decode.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.decode.Constants;
import org.firstinspires.ftc.teamcode.decode.SharedData;
import org.firstinspires.ftc.teamcode.decode.core.BallDetection;
import org.firstinspires.ftc.teamcode.decode.core.Intake;
import org.firstinspires.ftc.teamcode.decode.core.Shooter;
import org.firstinspires.ftc.teamcode.decode.core.Spindexer;
import org.firstinspires.ftc.teamcode.decode.player.Pattern;
import org.firstinspires.ftc.teamcode.ironDams.Config;
import org.firstinspires.ftc.teamcode.ironDams.core.WaitFor;
import org.firstinspires.ftc.teamcode.ironDams.core.driveTrain.MecanumDrive;

@TeleOp(name = "PlayerOpMode", group = "@@@@IronDams")
public class PlayerOpMode extends LinearOpMode {
    @Override
    public void runOpMode() {
        WaitFor player1Delay = new WaitFor(Config.USER_DELAY_MS);
        WaitFor player2Delay = new WaitFor(Config.USER_DELAY_MS);
        WaitFor colorChangeDelay = new WaitFor(2000);
        WaitFor shootDelay = new WaitFor(Constants.Shooter.SHOOT_DELAY_MS);
        WaitFor patternChangeDelay = new WaitFor(Constants.Shooter.PATTERN_CHANGE_DELAY_MS);

        SharedData data = new SharedData();
        Shooter shooter = new Shooter(this, data);
        MecanumDrive drive = new MecanumDrive(this);
        Pattern pattern = new Pattern(this, data);
        BallDetection ballDetection = new BallDetection(this, data);
        Spindexer spindexer = new Spindexer(this, data);
        Intake intake = new Intake(this);

        boolean driveMode = true;

        while (opModeInInit()) {
            pattern.changePattern();
            telemetry.addLine("Ready");
            telemetry.addData("Mode", data.getSpindexerMode());
            telemetry.update();
        }

        waitForStart();

        intake.setMode(Constants.Intake.Mode.INACTIVE);
        if (intake.getMode() == Constants.Intake.Mode.ACTIVE) {
            intake.setVelocity(Constants.Intake.TARGET_VELOCITY);
        }

        while (opModeIsActive() && !isStopRequested()) {
            if (gamepad1.right_trigger != 0 && player1Delay.allowExec()) {
                driveMode = drive.switchDrive();
            }

            drive.drive(gamepad1.right_stick_x, gamepad1.right_stick_y, gamepad1.left_stick_x);

            if (gamepad2.right_trigger != 0) {
                intake.setVelocity(Constants.Intake.TARGET_VELOCITY);
                intake.setMode(Constants.Intake.Mode.ACTIVE);
            } else if (gamepad2.left_trigger != 0) {
                intake.setVelocity(0);
                intake.setMode(Constants.Intake.Mode.INACTIVE);
            }

            if (gamepad2.dpad_down) {
                data.setSpindexerMode(Constants.Spindexer.Mode.SHOOT);
            }

            if (data.getSpindexerMode() != Constants.Spindexer.Mode.SHOOT) {
                shootDelay.reset();
            }

            if (data.getSpindexerMode() == Constants.Spindexer.Mode.SHOOT)
            {
                shooter.open().setVelocity();
                intake.setVelocity(0);
                if (data.getShotCount() < 3 && shootDelay.allowExec()) {
                    spindexer.shoot();
                } else if (shootDelay.allowExec()) {
                    data.resetActualPattern();
                    data.setSpindexerMode(Constants.Spindexer.Mode.INDEX);
                    data.setShotCount(0);
                    spindexer.moveIndex(0);
                    shooter.close().stop();
                    if (intake.getMode() == Constants.Intake.Mode.ACTIVE) {
                        intake.setVelocity(Constants.Intake.TARGET_VELOCITY);
                    }
                }
            }
            else if (!data.isSpindexerLoaded()) {
                if (colorChangeDelay.allowExec(false)) {
                    if (ballDetection.autoProcessedColor()) {
                        if (!data.isSpindexerLoaded()) {
                            spindexer.moveDistance(2);
                            colorChangeDelay.reset();
                        }
                    }
                }
                pattern.changePattern();
                data.setHasPatternChanged(false);
                if (data.isSpindexerLoaded()) data.setSpindexerMode(Constants.Spindexer.Mode.SORT);
            } else {
                pattern.changePattern();
                if (!data.getHasPatternChanged().getAsBoolean()) patternChangeDelay.reset();

                if (data.getHasPatternChanged().getAsBoolean() && data.getSpindexerMode() != Constants.Spindexer.Mode.SHOOT) {
                    shooter.close();
                    if (patternChangeDelay.allowExec()) {
                        spindexer.sort();
                        data.setSpindexerMode(Constants.Spindexer.Mode.PRE_SHOOT);
                        data.setHasPatternChanged(false);
                    }
                }
                else if (data.getSpindexerMode() == Constants.Spindexer.Mode.SORT) {
                    spindexer.sort();
                }
            }

            telemetry.addData("Drive Mode", driveMode ? "Field": "Robot");
            telemetry.addData("Target Pattern", data.getTargetPattern());
            telemetry.addData("Actual Pattern", String.join("", data.getActualPattern()));
            telemetry.addData("Mode", data.getSpindexerMode());
            telemetry.update();
        }
    }
}
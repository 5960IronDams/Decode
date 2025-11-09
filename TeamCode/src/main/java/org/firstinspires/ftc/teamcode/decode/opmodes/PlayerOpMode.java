package org.firstinspires.ftc.teamcode.decode.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.decode.Constants;
import org.firstinspires.ftc.teamcode.decode.SharedData;
import org.firstinspires.ftc.teamcode.decode.core.BallDetection;
import org.firstinspires.ftc.teamcode.decode.core.Intake;
import org.firstinspires.ftc.teamcode.decode.core.Shooter;
import org.firstinspires.ftc.teamcode.decode.core.Spindexer;
import org.firstinspires.ftc.teamcode.ironDams.Config;
import org.firstinspires.ftc.teamcode.ironDams.core.WaitFor;
import org.firstinspires.ftc.teamcode.ironDams.core.driveTrain.MecanumDrive;

@TeleOp(name = "PlayerOpMode", group = "@@@@IronDams")
public class PlayerOpMode extends LinearOpMode {
    private final SharedData DATA = new SharedData();

    @Override
    public void runOpMode() {
        WaitFor player1Delay = new WaitFor(Config.USER_DELAY_MS);
        WaitFor player2Delay = new WaitFor(Config.USER_DELAY_MS);
        WaitFor colorChangeDelay = new WaitFor(2000);
        WaitFor shootDelay = new WaitFor(Constants.Shooter.SHOOT_DELAY_MS + 200);
        WaitFor patternChangeDelay = new WaitFor(Constants.Shooter.PATTERN_CHANGE_DELAY_MS);

        Shooter shooter = new Shooter(this, DATA);
        MecanumDrive drive = new MecanumDrive(this);
        BallDetection ballDetection = new BallDetection(this, DATA);
        Spindexer spindexer = new Spindexer(this, DATA);
        Intake intake = new Intake(this);

        boolean driveMode = false;

        while (opModeInInit()) {
            changeTargetPattern();
            telemetry.addLine("Ready");
            telemetry.addData("Mode", DATA.getSpindexerMode());
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

            if (gamepad1.b && player2Delay.allowExec()) {
                drive.resetFieldView();
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
                DATA.setSpindexerMode(Constants.Spindexer.Mode.SHOOT);
            }

            if (DATA.getSpindexerMode() != Constants.Spindexer.Mode.SHOOT) {
                shootDelay.reset();
            }

            if (DATA.getSpindexerMode() == Constants.Spindexer.Mode.SHOOT)
            {
                shooter.outtakePos().open().setVelocity(Constants.Shooter.TARGET_VELOCITY + 100);
                intake.setVelocity(0);
                if (DATA.getShotCount() < 3 && shootDelay.allowExec()) {
                    spindexer.shoot();
                    shooter.setVelocity(Constants.Shooter.TARGET_VELOCITY);
                } else if (shootDelay.allowExec()) {
                    DATA.resetActualPattern();
                    DATA.setSpindexerMode(Constants.Spindexer.Mode.INDEX);
                    DATA.setShotCount(0);
                    spindexer.moveIndex(0);
                    shooter.intakePos().stop();
                    if (intake.getMode() == Constants.Intake.Mode.ACTIVE) {
                        intake.setVelocity(Constants.Intake.TARGET_VELOCITY);
                    }
                }
            }
            else if (!DATA.isSpindexerLoaded()) {
                if (colorChangeDelay.allowExec(false)) {
                    if (ballDetection.autoProcessedColor()) {
                        shooter.close();
                        if (!DATA.isSpindexerLoaded()) {
                            spindexer.moveDistance(2);
                            colorChangeDelay.reset();
                        }
                    }
                }

                if (gamepad2.right_bumper && player2Delay.allowExec()) {
                    if (DATA.getActualPattern()[2].isEmpty()) DATA.setActualColorCode("G", 2);
                    else if (DATA.getActualPattern()[0].isEmpty()) DATA.setActualColorCode("G", 0);
                    else DATA.setActualColorCode("G", 1);

                    if (!DATA.isSpindexerLoaded()) {
                        spindexer.moveDistance(2);
                        colorChangeDelay.reset();
                    }
                } else if (gamepad2.left_bumper && player2Delay.allowExec()) {
                    if (DATA.getActualPattern()[2].isEmpty()) DATA.setActualColorCode("P", 2);
                    else if (DATA.getActualPattern()[0].isEmpty()) DATA.setActualColorCode("P", 0);
                    else DATA.setActualColorCode("P", 1);

                    if (!DATA.isSpindexerLoaded()) {
                        spindexer.moveDistance(2);
                        colorChangeDelay.reset();
                    }
                }

                changeTargetPattern();
                DATA.setHasPatternChanged(false);
                if (DATA.isSpindexerLoaded()) {
                    DATA.setSpindexerMode(Constants.Spindexer.Mode.SORT);
                    shooter.outtakePos();
                }
            } else {
                changeTargetPattern();
                if (!DATA.getHasPatternChanged().getAsBoolean()) patternChangeDelay.reset();

                if (DATA.getHasPatternChanged().getAsBoolean() && DATA.getSpindexerMode() != Constants.Spindexer.Mode.SHOOT) {
                    shooter.close();
                    if (patternChangeDelay.allowExec()) {
                        spindexer.sort();
                        DATA.setSpindexerMode(Constants.Spindexer.Mode.PRE_SHOOT);
                        DATA.setHasPatternChanged(false);
                    }
                }
                else if (DATA.getSpindexerMode() == Constants.Spindexer.Mode.SORT) {
                    spindexer.sort();
                }
            }

            telemetry.addData("Drive Mode", driveMode ? "Field": "Robot");
            telemetry.addData("Target Pattern", DATA.getTargetPattern());
            telemetry.addData("Actual Pattern", String.join("", DATA.getActualPattern()));
            telemetry.addData("Mode", DATA.getSpindexerMode());
            telemetry.update();
        }
    }

    void changeTargetPattern() {
        int index = DATA.getGreenBallTargetIndex();
        if (gamepad2.a) {
            index = 1;
            DATA.setHasPatternChanged(true);
        } else if (gamepad2.b) {
            index = 2;
            DATA.setHasPatternChanged(true);
        } else if (gamepad2.x) {
            index = 0;
            DATA.setHasPatternChanged(true);
        }

        DATA.setGreenBallTargetIndex(index);
        DATA.setTargetPattern(index == 0 ? "GPP" : index == 1 ? "PGP" : index == 2 ? "PPG": "");
    }
}
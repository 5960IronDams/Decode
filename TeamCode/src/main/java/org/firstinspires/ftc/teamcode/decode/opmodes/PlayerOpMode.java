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
        WaitFor userDelay = new WaitFor(Config.USER_DELAY_MS);
        WaitFor shootDelay = new WaitFor(1000);
        WaitFor patternChangeDelay = new WaitFor(750);

        SharedData data = new SharedData();
        Shooter shooter = new Shooter(this, data);
        MecanumDrive drive = new MecanumDrive(this);
        Pattern pattern = new Pattern(this, data);
        BallDetection ballDetection = new BallDetection(this, data);
        Spindexer spindexer = new Spindexer(this, data);
        Intake intake = new Intake(this);

        waitForStart();

        if (intake.getMode() == Constants.Intake.Mode.ACTIVE) {
            intake.setVelocity(Constants.Intake.TARGET_VELOCITY);
        }

        while (opModeIsActive() && !isStopRequested()) {
            drive.switchDrive();
            drive.drive();
            intake.toggleIntakeVelocity();

            if (!data.isSpindexerLoaded()) {
                ballDetection.processColor();
                pattern.changePattern();
                data.setHasPatternChanged(false);
                if (data.getMoveSpindexer()) {
                    data.setMoveSpindexer(false);
                    data.setSpindexerDetectionIndex(data.getSpindexerCurrentIndex());
                    data.setSpindexerCurrentIndex(data.getSpindexerCurrentIndex() + 2);
                    spindexer.setPos(Constants.Spindexer.Positions[data.getSpindexerCurrentIndex()]);
                    data.setSpindexerMode(Constants.Spindexer.Mode.SORT);
                }
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
                    data.setSpindexerMode(Constants.Spindexer.Mode.PRE_SHOOT);
                }
                else if (data.getSpindexerMode() == Constants.Spindexer.Mode.PRE_SHOOT)
                {
                    shooter.start();
                    if (gamepad2.dpad_down && userDelay.allowExec()) {
                        data.setSpindexerMode(Constants.Spindexer.Mode.SHOOT);
                    }
                }
                else if (data.getSpindexerMode() == Constants.Spindexer.Mode.SHOOT)
                {
                    intake.setVelocity(0);
                    if (data.getShotCount() < 3 && shootDelay.allowExec()) {
                        spindexer.shoot();
                    } else if (shootDelay.allowExec()) {
                        data.resetActualPattern();
                        data.setSpindexerMode(Constants.Spindexer.Mode.INDEX);
                        data.setShotCount(0);
                        data.setSpindexerCurrentIndex(0);
                        spindexer.setPos(Constants.Spindexer.Positions[data.getSpindexerCurrentIndex()]);
                        shooter.close().stop();
                        if (intake.getMode() == Constants.Intake.Mode.ACTIVE) {
                            intake.setVelocity(Constants.Intake.TARGET_VELOCITY);
                        }
                    }

                }
            }

            telemetry.addData("Target Pattern", data.getTargetPattern());
            telemetry.update();
        }
    }
}
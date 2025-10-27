package org.firstinspires.ftc.teamcode.decode.teleOp;

import com.acmerobotics.roadrunner.InstantAction;
import com.acmerobotics.roadrunner.ParallelAction;
import com.acmerobotics.roadrunner.SequentialAction;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.decode.Constants;
import org.firstinspires.ftc.teamcode.decode.core.GreenBallPosition;

@TeleOp(name = "PlayerOpMode2", group = "@@@@IronDams")
public class PlayerOpMode2 extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {

        GreenBallPosition greenBallPosition = new GreenBallPosition();
        MecanumDriveTrain driveTrain = new MecanumDriveTrain(this);
        PlayerIntake intake = new PlayerIntake(this);
        PlayerShooter shooter = new PlayerShooter(this, greenBallPosition);
        PlayerPattern pattern = new PlayerPattern(this, greenBallPosition);
        PlayerSpindexer spindexer = new PlayerSpindexer(this, greenBallPosition);
        PlayerColorVision colorVision = new PlayerColorVision(this, greenBallPosition);

        while(!isStopRequested() && !opModeIsActive()) { }

        if (isStopRequested()) {
            return;
        }

        waitForStart();

        if (isStopRequested()) {
            return;
        }

        Actions.runBlocking(
                new SequentialAction(
                        intake.powerAction(Constants.Intake.SORT_POWER),
                        new ParallelAction(
                                driveTrain.runDriveAction(),
                                intake.runIntakeAction(),
                                pattern.changeAction(),
                                colorVision.indexBallsAction(),
                                spindexer.indexBalls(),
                                spindexer.sortBalls(pattern.getHasPatternChanged()),
                                shooter.shootAction(),
                                spindexer.shootBalls(shooter.getShootComplete()),
                                new InstantAction(() -> pattern.setHasPatternChanged(false)),
                                new InstantAction(() -> telemetry.update())
                        )
                )
        );
    }
}
package org.firstinspires.ftc.teamcode.decode.autonomous;

import com.acmerobotics.roadrunner.InstantAction;
import com.acmerobotics.roadrunner.ParallelAction;
import com.acmerobotics.roadrunner.SequentialAction;
import com.acmerobotics.roadrunner.SleepAction;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.decode.Constants;
import org.firstinspires.ftc.teamcode.decode.core.ColorVision;
import org.firstinspires.ftc.teamcode.decode.core.GreenBallPosition;
import org.firstinspires.ftc.teamcode.decode.core.Intake;
import org.firstinspires.ftc.teamcode.decode.core.Shooter;
import org.firstinspires.ftc.teamcode.ironDams.autonomus.subsystems.AprilTagReader;
import org.firstinspires.ftc.teamcode.ironDams.autonomus.subsystems.AutoDrive;
import org.firstinspires.ftc.teamcode.ironDams.core.WaitFor;
import org.firstinspires.ftc.teamcode.ironDams.core.driveTrain.IDriveTrain;
import org.firstinspires.ftc.teamcode.ironDams.core.driveTrain.MecanumDrive;
import org.firstinspires.ftc.teamcode.ironDams.core.odometry.IGyro;
import org.firstinspires.ftc.teamcode.ironDams.core.odometry.Pinpoint;


@Autonomous(name = "AUTO_short_RED", group = "----0IronDams")
public class AUTO_short_RED extends LinearOpMode {

    @Override
    public void runOpMode() throws InterruptedException {
//        WaitFor MOTOR_DELAY = new WaitFor(250);

        GreenBallPosition greenBallPosition = new GreenBallPosition();
        Intake intake = new Intake(this);
        Shooter shooter = new Shooter(this, greenBallPosition);
        Spindexer spindexer = new Spindexer(this, greenBallPosition);
        ColorVision colorVision = new ColorVision(this, greenBallPosition);

        IDriveTrain drive = new MecanumDrive(this);
        IGyro pinpoint = new Pinpoint(this);
        AutoDrive autoDrive = new AutoDrive(this, drive, pinpoint);

//        HuskyReader husky = new HuskyReader(this.hardwareMap, greenBallPosition);
        AprilTagReader webCams = new AprilTagReader(this.hardwareMap, greenBallPosition);
//        webCams.stopStreaming();


        telemetry.addData("Program", "short_RED");
        telemetry.addData("IsReady", "Yes");
        telemetry.update();

        while(!isStopRequested() && !opModeIsActive()) { }

        if (isStopRequested()) {
            webCams.stopStreaming();
            return;
        }

        waitForStart();

        if (isStopRequested()) {
            webCams.stopStreaming();
            return;
        }

//        while (opModeIsActive()) {
//            webCams.stopStreamingAction();
//            telemetry.addLine("Running");
//            telemetry.addData("x", pinpoint.getPose().getX(DistanceUnit.INCH));
//            telemetry.addData("y", pinpoint.getPose().getX(DistanceUnit.INCH));
//            telemetry.update();
//        }

        Actions.runBlocking(
                new SequentialAction(
                        intake.run(Constants.Intake.MAX_POWER),
//                        webCams.resumeStreamingAction(),
                        new ParallelAction(
                            colorVision.indexBalls(autoDrive.getDriveComplete()),
                            spindexer.indexBalls(autoDrive.getDriveComplete())
                        ),
                        new InstantAction(() -> autoDrive.setDriveCompleted(false)),
                        new InstantAction(autoDrive::setStartingXPos),
                        new ParallelAction(
                                autoDrive.driveTo(-17, 2, 6, 0.2, 0.7),
                                webCams.readTag(autoDrive.getDriveComplete())
                        ),
                        spindexer.sortBalls(autoDrive.getDriveComplete()),
                        new SleepAction(0.5),
                        shooter.start(autoDrive.getDriveComplete()),
                        intake.run(Constants.Intake.SORT_POWER),
                        new SleepAction(0.5),
                        new ParallelAction(
                            shooter.shoot(),
                            spindexer.shootBalls(shooter.getShootComplete())
                        ),
                        new SleepAction(0.5),
                        shooter.stop(),
                        new InstantAction(() -> autoDrive.setDriveCompleted(false)),
                        new InstantAction(autoDrive::setStartingHeadingPos),
                        autoDrive.turnTo(315, 25, 45, 0.2, 0.3),
                        new SleepAction(0.15),
                        new InstantAction(() -> autoDrive.setDriveCompleted(false)),
                        new InstantAction(autoDrive::setStartingYPos),
                        autoDrive.strafeTo(18, 2, 8, 0.4, 0.7),
                        new SleepAction(0.15),
//                        intake.run(Constants.Intake.MAX_POWER),
//                        new SleepAction(0.25),
//                        new InstantAction(() -> autoDrive.setDriveCompleted(false)),
//                        new InstantAction(autoDrive::setStartingXPos),
//                        new ParallelAction(
//                                autoDrive.driveTo(84, 5, 42, 0.2, 0.7)//,
////                                spindexer.indexBalls(autoDrive.getDriveComplete()),
////                                husky.readTag(autoDrive.getDriveComplete()),
////                                webCams.readTag(autoDrive.getDriveComplete())
//                        ),
//                        new SleepAction(0.25),
//                        new InstantAction(() -> autoDrive.setDriveCompleted(false)),
//                        new InstantAction(autoDrive::setStartingHeadingPos),
//                        new ParallelAction(
//                            autoDrive.turnTo(315, 25, 25, 0.3, 0.7)
//                        ),
//                        new SleepAction(0.25),
//                        new InstantAction(() -> autoDrive.setDriveCompleted(false)),
//                        new InstantAction(autoDrive::setStartingXPos),
//                        autoDrive.driveTo(117, 5, 12, 0.2, 0.7),
//                        new InstantAction(() -> autoDrive.setDriveCompleted(false)),
//                        new InstantAction(autoDrive::setStartingYPos),
//                        autoDrive.strafeTo(6, 3, 3, 0.3, 0.7),
//                        new SleepAction(0.25),
//
//                        new InstantAction(() -> autoDrive.setDriveCompleted(false)),
//                        new InstantAction(autoDrive::setStartingYPos),
//                        autoDrive.strafeTo(0, 3, 3, 0.3, 0.7),
//                        new SleepAction(0.25),

                        webCams.stopStreamingAction()
                )
        );

        long start = System.currentTimeMillis();
        while (opModeIsActive() && System.currentTimeMillis() - start < 2500) {
            telemetry.addLine("Complete");
            telemetry.update();
        }

    }
}
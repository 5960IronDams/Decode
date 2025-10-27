package org.firstinspires.ftc.teamcode.decode.autonomous;

import com.acmerobotics.roadrunner.InstantAction;
import com.acmerobotics.roadrunner.ParallelAction;
import com.acmerobotics.roadrunner.SequentialAction;
import com.acmerobotics.roadrunner.SleepAction;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.decode.Constants;
import org.firstinspires.ftc.teamcode.decode.core.GreenBallPosition;
import org.firstinspires.ftc.teamcode.decode.core.Intake;
import org.firstinspires.ftc.teamcode.ironDams.autonomus.subsystems.AprilTagReader;
import org.firstinspires.ftc.teamcode.ironDams.autonomus.subsystems.AutoDrive;
import org.firstinspires.ftc.teamcode.ironDams.autonomus.subsystems.HuskyReader;
import org.firstinspires.ftc.teamcode.ironDams.core.WaitFor;
import org.firstinspires.ftc.teamcode.ironDams.core.driveTrain.IDriveTrain;
import org.firstinspires.ftc.teamcode.ironDams.core.driveTrain.MecanumDrive;
import org.firstinspires.ftc.teamcode.ironDams.core.odometry.IGyro;
import org.firstinspires.ftc.teamcode.ironDams.core.odometry.Pinpoint;


@Autonomous(name = "AUTO_LONG_RED", group = "----0IronDams")
public class AUTO_LONG_RED extends LinearOpMode {

    @Override
    public void runOpMode() throws InterruptedException {
        WaitFor MOTOR_DELAY = new WaitFor(250);

        GreenBallPosition greenBallPosition = new GreenBallPosition();
//        Spindexer spindexer = new Spindexer(this, greenBallPosition);
//        ColorVision colorVision = new ColorVision(this, greenBallPosition);

        IDriveTrain drive = new MecanumDrive(this);
        IGyro pinpoint = new Pinpoint(this);
        AutoDrive autoDrive = new AutoDrive(this, drive, pinpoint);

//        HuskyReader husky = new HuskyReader(this.hardwareMap, greenBallPosition);
        AprilTagReader webCams = new AprilTagReader(this.hardwareMap, greenBallPosition);
        Intake intake = new Intake(this);
//        Shooter shooter = new Shooter(this, greenBallPosition);

        telemetry.addData("Program", "LONG_RED");
        telemetry.addData("IsReady", "Yes");
        telemetry.update();

        while(!isStopRequested() && !opModeIsActive()) { }

        waitForStart();

        if (isStopRequested()) return;

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
                        new SleepAction(0.25),
                        new InstantAction(() -> autoDrive.setDriveCompleted(false)),
                        new InstantAction(autoDrive::setStartingXPos),
                        new ParallelAction(
                                autoDrive.driveTo(84, 5, 42, 0.2, 0.7)//,
//                                spindexer.indexBalls(autoDrive.getDriveComplete()),
//                                husky.readTag(autoDrive.getDriveComplete()),
//                                webCams.readTag(autoDrive.getDriveComplete())
                        ),
                        new SleepAction(0.25),
                        new InstantAction(() -> autoDrive.setDriveCompleted(false)),
                        new InstantAction(autoDrive::setStartingHeadingPos),
                        new ParallelAction(
                            autoDrive.turnTo(315, 25, 25, 0.3, 0.7)
                        ),
                        new SleepAction(0.25),
                        new InstantAction(() -> autoDrive.setDriveCompleted(false)),
                        new InstantAction(autoDrive::setStartingXPos),
                        autoDrive.driveTo(117, 5, 12, 0.2, 0.7),
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
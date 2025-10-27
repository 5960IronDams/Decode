package org.firstinspires.ftc.teamcode.decode.autonomous;

import com.acmerobotics.roadrunner.InstantAction;
import com.acmerobotics.roadrunner.ParallelAction;
import com.acmerobotics.roadrunner.SequentialAction;
import com.acmerobotics.roadrunner.SleepAction;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.decode.core.ColorVision;
import org.firstinspires.ftc.teamcode.decode.core.GreenBallPosition;
import org.firstinspires.ftc.teamcode.decode.core.Intake;
import org.firstinspires.ftc.teamcode.decode.core.Shooter;
import org.firstinspires.ftc.teamcode.ironDams.autonomus.subsystems.AprilTagReader;
import org.firstinspires.ftc.teamcode.ironDams.autonomus.subsystems.AutoDrive;
import org.firstinspires.ftc.teamcode.ironDams.autonomus.subsystems.HuskyReader;
import org.firstinspires.ftc.teamcode.ironDams.core.WaitFor;
import org.firstinspires.ftc.teamcode.ironDams.core.driveTrain.IDriveTrain;
import org.firstinspires.ftc.teamcode.ironDams.core.driveTrain.MecanumDrive;
import org.firstinspires.ftc.teamcode.ironDams.core.odometry.IGyro;
import org.firstinspires.ftc.teamcode.ironDams.core.odometry.Pinpoint;


@TeleOp(name = "AutoOpMode", group = "----0IronDams")
public class AutoOpMode extends LinearOpMode {

    @Override
    public void runOpMode() throws InterruptedException {
        WaitFor MOTOR_DELAY = new WaitFor(250);

        GreenBallPosition greenBallPosition = new GreenBallPosition();
        Spindexer spindexer = new Spindexer(this, greenBallPosition);
        ColorVision colorVision = new ColorVision(this, greenBallPosition);

        IDriveTrain drive = new MecanumDrive(this);
        IGyro pinpoint = new Pinpoint(this);
        AutoDrive autoDrive = new AutoDrive(this, drive, pinpoint);
        HuskyReader husky = new HuskyReader(this.hardwareMap, greenBallPosition);
        AprilTagReader webCams = new AprilTagReader(this.hardwareMap, greenBallPosition);
        Intake intake = new Intake(this);
        Shooter shooter = new Shooter(this, greenBallPosition);

        telemetry.addData("Program", "LONG_RED");
        telemetry.addData("IsReady", "Yes");
        telemetry.update();

        waitForStart();

        Actions.runBlocking(
                new SequentialAction(
//                        new InstantAction(() -> autoDrive.setDriveCompleted(false)),
//                        new InstantAction(autoDrive::setStartingYPos),
//                        autoDrive.driveForward(24, 4, 16, 0.2, 1.0),
//                        new InstantAction(() -> autoDrive.setDriveCompleted(false)),
//                        new InstantAction(autoDrive::setStartingXPos),
//                        new InstantAction(() -> sleep(250)),
//                        autoDrive.strafeLeft(12, 4, 4, 0.2, 1.0),
//                        new InstantAction(() -> autoDrive.setDriveCompleted(false)),
//                        new InstantAction(autoDrive::setStartingXPos),
//                        new InstantAction(() -> sleep(250)),
//                        autoDrive.strafeRight(12, 4, 4, 0.2, 1.0),
//                        new InstantAction(() -> autoDrive.setDriveCompleted(false)),
//                        new InstantAction(autoDrive::setStartingHeadingPos),
//                        new InstantAction(() -> sleep(250)),
//                        autoDrive.turnLeft(45, 10, 25, 0.2, 0.5),
                        new InstantAction(() -> autoDrive.setDriveCompleted(false)),
                        new InstantAction(autoDrive::setStartingHeadingPos),
                        new SleepAction(0.0250),
                        autoDrive.turnRight(45, 10, 25, 0.2, 0.5)
//                        new InstantAction(() -> autoDrive.setDriveCompleted(false)),
//                        new InstantAction(autoDrive::setStartingYPos),
//                        new InstantAction(() -> sleep(250)),
//                        autoDrive.driveBackward(0, 4, 16, 0.2, 1),
//                        new InstantAction(() -> autoDrive.setDriveCompleted(false)),
//                        new InstantAction(autoDrive::setStartingYPos),
//                        new InstantAction(() -> sleep(250)),
//                        autoDrive.driveTurnRightForward(12, 4, 8, 0.2, 0.5),
//                        new InstantAction(() -> autoDrive.setDriveCompleted(false)),
//                        new InstantAction(autoDrive::setStartingYPos),
//                        new InstantAction(() -> sleep(250)),
//                        autoDrive.driveTurnRightBackward(0, 4, 8, 0.2, 0.5)


//                        intake.run(0.3),
//                        new InstantAction(() -> autoDrive.setDriveCompleted(false)),
//                        new ParallelAction(
//                                colorVision.indexBalls(autoDrive.getDriveComplete()),
//                                spindexer.indexBalls(autoDrive.getDriveComplete()),
//                                husky.readTag(autoDrive.getDriveComplete()),
//                                webCams.readTag(autoDrive.getDriveComplete()),
//                                spindexer.sortBalls(autoDrive.getDriveComplete())
//                        ),
//                        intake.stop(),
//                        new InstantAction(() -> sleep(500)),
//                        shooter.start(autoDrive.getDriveComplete()),
//                        new ParallelAction(
//                                shooter.shoot(),
//                                spindexer.shootBalls(shooter.getShootComplete())
//                        ),
//                        shooter.stop()
                )
        );
    }
}
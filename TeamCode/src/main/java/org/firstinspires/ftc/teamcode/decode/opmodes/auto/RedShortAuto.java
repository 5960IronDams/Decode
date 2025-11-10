package org.firstinspires.ftc.teamcode.decode.opmodes.auto;


import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.InstantAction;
import com.acmerobotics.roadrunner.ParallelAction;
import com.acmerobotics.roadrunner.SequentialAction;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.VoltageSensor;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.decode.SharedData;
import org.firstinspires.ftc.teamcode.decode.auto.TagDetection;
import org.firstinspires.ftc.teamcode.decode.core.BallDetection;
import org.firstinspires.ftc.teamcode.decode.core.Intake;
import org.firstinspires.ftc.teamcode.decode.core.Launcher;
import org.firstinspires.ftc.teamcode.decode.core.Spindexer;
import org.firstinspires.ftc.teamcode.irondams.core.Logger;
import org.firstinspires.ftc.teamcode.irondams.core.WaitFor;
import org.firstinspires.ftc.teamcode.irondams.core.auto.AutoDrive;
import org.firstinspires.ftc.teamcode.irondams.core.driveTrain.FourWheelDriveTrain;
import org.firstinspires.ftc.teamcode.irondams.core.driveTrain.MecanumDrive;
import org.firstinspires.ftc.teamcode.irondams.core.driveTrain.MecanumDriveTrain;
import org.firstinspires.ftc.teamcode.irondams.core.odometry.Pinpoint;

import java.util.function.BooleanSupplier;

@Autonomous(name = "RED_SHORT", group = "IronDams")
public class RedShortAuto extends LinearOpMode {
    private ElapsedTime timer = new ElapsedTime();
    private Logger logger = new Logger(this.getClass().getSimpleName());

    private VoltageSensor voltageSensor = hardwareMap.voltageSensor.iterator().next();
    private BallDetection ballDetection = new BallDetection(this, logger);
    private Spindexer spindexer = new Spindexer(this, logger);
    private Intake intake = new Intake(this, voltageSensor);
    private Launcher launcher = new Launcher(this, voltageSensor, logger);

    private TagDetection tagDetection = new TagDetection(this, logger);

    private FourWheelDriveTrain fourWheelDriveTrain = new FourWheelDriveTrain(hardwareMap);
    private MecanumDriveTrain drive = new MecanumDriveTrain(fourWheelDriveTrain);
    private Pinpoint pinpoint = new Pinpoint(this);
    private AutoDrive autoDrive = new AutoDrive(drive, pinpoint, logger);

    @Override
    public void runOpMode() {
        launcher.outtakePos();

        if (isStopRequested()) {
            tagDetection.stopStreaming();
            return;
        }

        waitForStart();

        if (isStopRequested()) {
            tagDetection.stopStreaming();
            return;
        }

        timer.reset();

        Actions.runBlocking(
                new SequentialAction(
                        /* Indexing the artifacts that are in the spindexer while driving backwards. */
                        new InstantAction(() -> autoDrive.setDriveCompleted(false)),
                        new InstantAction(autoDrive::setStartingXPos),
                        new ParallelAction(
                                indexArtifacts(autoDrive.getDriveComplete(), timer.milliseconds()),
                                autoDrive.driveTo(-22, 4, 12, 0.3, 1.0) //TODO: adjust to minimize next move and readability of obelisk.
                        ),
                        new InstantAction(() -> autoDrive.setDriveCompleted(false)),

                        /* Read the obelisk */
                        tagDetection.webcamResetTimeout(),
                        tagDetection.webcamReadAction(autoDrive.getDriveComplete(), timer.milliseconds()),

                        /* Sort the artifacts in the spindexer */
                        spindexer.sortAction(autoDrive.getDriveComplete(), timer.milliseconds()),

                        /* shoot the balls */
                        shotArtifacts(),



                        /* stop streaming the camera */
                        tagDetection.webcamStopStreamingAction()
                )
        );

    }


    Action indexArtifacts(BooleanSupplier driveComplete, double millis) {
        return new SequentialAction(
                intake.setIntakeVelocityAction(timer.milliseconds(), 1000),
                ballDetection.detectBallAction(autoDrive.getDriveComplete(), timer.milliseconds()),
                spindexer.moveSpindexerAction(autoDrive.getDriveComplete(), timer.milliseconds()),
                ballDetection.detectBallAction(autoDrive.getDriveComplete(), timer.milliseconds()),
                spindexer.moveSpindexerAction(autoDrive.getDriveComplete(), timer.milliseconds()),
                ballDetection.detectBallAction(autoDrive.getDriveComplete(), timer.milliseconds()),
                spindexer.moveSpindexerAction(autoDrive.getDriveComplete(), timer.milliseconds()),
                intake.setIntakeVelocityAction(timer.milliseconds(), 0)
        );
    }

    Action shotArtifacts() {
        return new SequentialAction(
                launcher.setLauncherModeAction(false, timer.milliseconds()),
                launcher.startShootingAction(1000, timer.milliseconds()), //TODO: set velocity after testing.
                launcher.shotResetTimerAction(timer.milliseconds()),
                new ParallelAction(
                        launcher.shootAction(timer.milliseconds()),
                        spindexer.moveSpindexerAction(autoDrive.getDriveComplete(), timer.milliseconds()),
                        launcher.shotCompleteAction(timer.milliseconds())
                ),
                launcher.shotResetTimerAction(timer.milliseconds()),
                new ParallelAction(
                        launcher.shootAction(timer.milliseconds()),
                        spindexer.moveSpindexerAction(autoDrive.getDriveComplete(), timer.milliseconds()),
                        launcher.shotCompleteAction(timer.milliseconds())
                ),
                launcher.shotResetTimerAction(timer.milliseconds()),
                new ParallelAction(
                        launcher.shootAction(timer.milliseconds()),
                        spindexer.moveSpindexerAction(autoDrive.getDriveComplete(), timer.milliseconds()),
                        launcher.shotCompleteAction(timer.milliseconds())
                ),
                ballDetection.clearActualAction(timer.milliseconds()),
                spindexer.resetToZeroAction(autoDrive.getDriveComplete(), timer.milliseconds())
        );
    }

}
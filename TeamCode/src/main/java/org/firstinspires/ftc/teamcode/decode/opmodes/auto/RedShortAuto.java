package org.firstinspires.ftc.teamcode.decode.opmodes.auto;


import com.acmerobotics.roadrunner.InstantAction;
import com.acmerobotics.roadrunner.SequentialAction;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.VoltageSensor;
import com.qualcomm.robotcore.util.ElapsedTime;

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

@Autonomous(name = "RED_SHORT", group = "IronDams")
public class RedShortAuto extends LinearOpMode {
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

        TagDetection tagDetection = new TagDetection(this, logger);

        FourWheelDriveTrain fourWheelDriveTrain = new FourWheelDriveTrain(hardwareMap);
        MecanumDriveTrain drive = new MecanumDriveTrain(fourWheelDriveTrain);
        Pinpoint pinpoint = new Pinpoint(this);
        AutoDrive autoDrive = new AutoDrive(drive, pinpoint, logger);

        if (isStopRequested()) {
            tagDetection.stopStreaming();
            return;
        }

        waitForStart();

        while (opModeIsActive() && !isStopRequested()) {
            if (isStopRequested()) {
                tagDetection.stopStreaming();
                return;
            }

            Actions.runBlocking(
                    new SequentialAction(
                            new InstantAction(() -> autoDrive.setDriveCompleted(false)),
                            tagDetection.webcamReadAction(autoDrive.getDriveComplete(), timer.milliseconds())
                    )
            );
        }
    }


}
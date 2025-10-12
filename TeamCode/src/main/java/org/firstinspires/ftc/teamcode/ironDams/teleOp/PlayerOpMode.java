package org.firstinspires.ftc.teamcode.ironDams.teleOp;

import com.acmerobotics.roadrunner.ParallelAction;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose2D;
import org.firstinspires.ftc.teamcode.decode.core.ColorVision;
import org.firstinspires.ftc.teamcode.decode.core.Decoder;
import org.firstinspires.ftc.teamcode.decode.core.Intake;
import org.firstinspires.ftc.teamcode.decode.core.Launcher;
import org.firstinspires.ftc.teamcode.decode.core.Spindexer;
import org.firstinspires.ftc.teamcode.decode.teleOp.WooshMachine;
import org.firstinspires.ftc.teamcode.ironDams.core.odometry.IGyro;
import org.firstinspires.ftc.teamcode.ironDams.core.odometry.Pinpoint;

@TeleOp(name = "PlayerOpModeID", group = "_IronDams")
public class PlayerOpMode extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {
        WooshMachine drive = new WooshMachine(this, true);
        Decoder decoder = new Decoder(this);
        ColorVision colorVision = new ColorVision(hardwareMap);
        Launcher launcher = new Launcher(this);
        Intake intake = new Intake(this);
        Spindexer spindexer = new Spindexer(this, intake);

        IGyro pinpoint = new Pinpoint(hardwareMap, new Pose2D(DistanceUnit.INCH, 0 , 0, AngleUnit.DEGREES, 0));

        waitForStart();
        Actions.runBlocking(
            new ParallelAction(
//                    drive.runDrive(),
//                    intake.(),
//                    launcher.manageLauncher(),
                    spindexer.runAction()
            )
        );

        telemetry.addLine("complete");
        telemetry.update();
        sleep(5000);
    }
}
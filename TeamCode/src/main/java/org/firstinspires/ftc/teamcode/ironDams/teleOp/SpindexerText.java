package org.firstinspires.ftc.teamcode.ironDams.teleOp;

import com.acmerobotics.roadrunner.ParallelAction;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose2D;
import org.firstinspires.ftc.teamcode.decode.core.ColorVision;
import org.firstinspires.ftc.teamcode.decode.core.Intake;
import org.firstinspires.ftc.teamcode.decode.core.Spindexer;
import org.firstinspires.ftc.teamcode.decode.teleOp.WooshMachine;
import org.firstinspires.ftc.teamcode.ironDams.core.odometry.IGyro;
import org.firstinspires.ftc.teamcode.ironDams.core.odometry.Pinpoint;

@TeleOp(name = "SpindexerTest", group = "_IronDams")
public class SpindexerText extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {
         WooshMachine _drive = new WooshMachine(this, true);
        Spindexer spindexer = new Spindexer(hardwareMap);
        ColorVision colorVision = new ColorVision(hardwareMap);
        Intake intake = new Intake(hardwareMap);
        IGyro pinpoint = new Pinpoint(hardwareMap, new Pose2D(DistanceUnit.INCH, 0 , 0, AngleUnit.DEGREES, 0));
        waitForStart();
//        while (opModeIsActive()){
//            _drive.go();
//            spindexer.setPower(0.15);
//            intake.run(0.15);
//            Pose2D pos = pinpoint.getPose();
//            telemetry.addData("x", pos.getX(DistanceUnit.INCH));
//            telemetry.addData("y", pos.getY(DistanceUnit.INCH));
//            telemetry.addData("heading", pos.getHeading(AngleUnit.DEGREES));
//        }
        Actions.runBlocking(
            new ParallelAction(
                _drive.runDrive(),
//                    spindexer.manageSpindexer(),
//                    intake.manageIntake(),
                    colorVision.readPattern(),
                    colorVision.getCenterReading(),
                    colorVision.getLeftReading(),
                    colorVision.getRightReading()
            )
        );

        telemetry.addLine("complete");
        telemetry.update();
        sleep(5000);
    }
}
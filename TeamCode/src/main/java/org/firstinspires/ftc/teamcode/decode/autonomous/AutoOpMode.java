package org.firstinspires.ftc.teamcode.decode.autonomous;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.InstantAction;
import com.acmerobotics.roadrunner.ParallelAction;
import com.acmerobotics.roadrunner.SequentialAction;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose2D;
import org.firstinspires.ftc.teamcode.decode.core.Decoder;
import org.firstinspires.ftc.teamcode.decode.core.Pattern;
import org.firstinspires.ftc.teamcode.ironDams.autonomus.subsystems.AutoDrive;
import org.firstinspires.ftc.teamcode.ironDams.autonomus.subsystems.VisionReader;
import org.firstinspires.ftc.teamcode.ironDams.core.driveTrain.IDriveTrain;
import org.firstinspires.ftc.teamcode.ironDams.core.driveTrain.MecanumDrive;
import org.firstinspires.ftc.teamcode.ironDams.core.odometry.IGyro;
import org.firstinspires.ftc.teamcode.ironDams.core.odometry.Pinpoint;

import java.nio.charset.IllegalCharsetNameException;


@TeleOp(name = "AutoOpMode", group = "----0IronDams")
public class AutoOpMode extends LinearOpMode {

    @Override
    public void runOpMode() throws InterruptedException {
        IDriveTrain drive = new MecanumDrive(this);
        IGyro pinpoint = new Pinpoint(this);
        AutoDrive autoDrive = new AutoDrive(this, drive, pinpoint);
        VisionReader husky = new VisionReader(this.hardwareMap);
        Decoder decoder = new Decoder(this);
        Pattern pattern = new Pattern(this, husky, decoder);
        waitForStart();

//        while (opModeIsActive()) {
//            Pose2D pos = pinpoint.getPose();
//
//            telemetry.addData("heading", pos.getHeading(AngleUnit.DEGREES));
//            telemetry.update();
//        }

        Actions.runBlocking(
            new SequentialAction(
                new ParallelAction(
                        new InstantAction(() -> telemetry.addData("Step", "1")),
                        new SequentialAction(
                            new InstantAction(autoDrive::setStartingYPos),
                            autoDrive.driveForward(48, 3, 12, 0.2, 1),
                            new InstantAction(() -> autoDrive.setDriveCompleted(true))
                        ),
                        pattern.runHuskyLens(autoDrive.getDriveComplete()),
                        pattern.runWebcam(autoDrive.getDriveComplete()),
                        new InstantAction(() -> telemetry.update())
                ),
                new ParallelAction(
                        new InstantAction(() -> sleep(1000)),
                        new InstantAction(() -> autoDrive.setDriveCompleted(false)),
                        new InstantAction(() -> telemetry.addData("Step", "2")),
                        new SequentialAction(
                            new InstantAction(autoDrive::setStartingHeadingPos),
                            autoDrive.turnRight(-50, 10, 20, 0.2, 0.5),
                            new InstantAction(() -> autoDrive.setDriveCompleted(true))
                        ),
                        pattern.runHuskyLens(autoDrive.getDriveComplete()),
                        pattern.runWebcam(autoDrive.getDriveComplete()),
                        new InstantAction(() -> telemetry.update())
                )
            )
        );

        telemetry.addData("Complete", "");
        telemetry.update();
        sleep(2500);

//        while (opModeIsActive()) {
//            Pose2D pos = pinpoint.getPose();
//            telemetry.addData("x", pos.getX(DistanceUnit.INCH));
//            telemetry.addData("y", pos.getY(DistanceUnit.INCH));
//            telemetry.addData("heading", pos.getHeading(AngleUnit.DEGREES));
//
//            double startPos = pos.getY(DistanceUnit.INCH);
//            double targetPos = startPos + 24;
//            double currentPos = startPos;
//            if (currentPos < targetPos) {
//                while (currentPos < targetPos && opModeIsActive() && !isStopRequested()) {
//                    pos = pinpoint.getPose();
//                    currentPos = pos.getY(DistanceUnit.INCH);
//                    double pow = Acceleration.getPower(
//                            targetPos, currentPos, startPos, 2, 14, 0.2, 0.7);
//
////                    drive.drive(0, pow, 0);
//
//                    HuskyLens.Block[] blocks = husky.read();
//
//                    telemetry.addData("length", blocks.length);
//                    if (blocks.length > 0) {
//                        telemetry.addData("id", blocks[0].id);
//                    }
//
////                    telemetry.addData("pow", pow);
////                    telemetry.addData("str", startPos);
////                    telemetry.addData("cur", currentPos);
////                    telemetry.addData("tar", targetPos);
////
////                    telemetry.addData("isAcel", Acceleration.getIsAccelerating());
////                    telemetry.addData("dist", Acceleration.getDistance());
////                    telemetry.addData("calcPos", Acceleration.getCalculatedCurrentPos());
////                    telemetry.addData("remainingDist", Acceleration.getRemainingDistance());
////
////                    telemetry.addData("percAccel", Acceleration.getPercentInAccel());
////                    telemetry.addData("powerRange", Acceleration.getPowerRange());
////
////                    telemetry.addData("decelAtPos", Acceleration.getDecelAtPos());
////                    telemetry.addData("decelDist", Acceleration.getDistanceIntoDecel());
////                    telemetry.addData("percDecel", Acceleration.getPercentInDecel());
//
//                    telemetry.update();
//                }
//
//                drive.drive(0,0,0);
//
//                telemetry.addData("str", startPos);
//                telemetry.addData("cur", currentPos);
//                telemetry.addData("tar", targetPos);
//
//                telemetry.update();
//
//                sleep(15000);

//            }



//            telemetry.update();
//        }

    }

    public Action updateTelemetry() {
        return new Action() {
            private boolean initialized = false;

            @Override
            public boolean run(@NonNull TelemetryPacket packet) {
                if (!initialized) {
                    initialized = true;
                }

                telemetry.update();

                return true;
            }
        };
    }
}
package org.firstinspires.ftc.teamcode.ironDams;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.ParallelAction;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.ironDams.Test.ShooterTest;

@TeleOp(name = "TestOpMode", group = "_IronDams")
public class TestOpMode extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {
//        WaitForTest waitForTest = new WaitForTest(this);
//        LauncherTest launcherTest = new LauncherTest(this);
//        ColorVisionTest colorVisionTest = new ColorVisionTest(this);
//        PatternTest patternTest = new PatternTest(this);
        ShooterTest shooterTest = new ShooterTest(this);
//        SpindexerTest spindexerTest = new SpindexerTest(this);

        while (opModeInInit()) { };

        Actions.runBlocking(
            new ParallelAction(
//                    waitForTest.runTest(),
                    // gamepad2.dpad down -> power launcher
//                    launcherTest.runTest(),
                    // gamepad2.x, a, b -> change target pattern
//                    patternTest.runTest(),
//                    colorVisionTest.runTest(),
                    // gamepad1.x -> change pos index
                    // gamepad1.a -= pos, gamepad1.y += pos
//                     spindexerTest.runTest(),
                    // dpad down - toggle shooter
                    shooterTest.runTest(),
                    updateTelemetry()
            )
        );
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
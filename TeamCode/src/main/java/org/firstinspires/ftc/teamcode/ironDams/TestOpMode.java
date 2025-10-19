package org.firstinspires.ftc.teamcode.ironDams;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.ParallelAction;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.decode.Pattern;
import org.firstinspires.ftc.teamcode.decode.core.BallVision;
import org.firstinspires.ftc.teamcode.decode.core.ColorVision;
import org.firstinspires.ftc.teamcode.decode.core.Decoder;
import org.firstinspires.ftc.teamcode.decode.core.Intake;
import org.firstinspires.ftc.teamcode.decode.core.Launcher;
import org.firstinspires.ftc.teamcode.decode.core.Spindexer;
import org.firstinspires.ftc.teamcode.decode.teleOp.WooshMachine;
import org.firstinspires.ftc.teamcode.ironDams.Test.ColorVisionTest;
import org.firstinspires.ftc.teamcode.ironDams.Test.LauncherTest;
import org.firstinspires.ftc.teamcode.ironDams.Test.PatternTest;
import org.firstinspires.ftc.teamcode.ironDams.Test.SpindexerTest;
import org.firstinspires.ftc.teamcode.ironDams.Test.WaitForTest;
import org.firstinspires.ftc.teamcode.ironDams.core.WaitFor;

@TeleOp(name = "TestOpMode", group = "_IronDams")
public class TestOpMode extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {
        WaitForTest waitForTest = new WaitForTest(this);
        LauncherTest launcherTest = new LauncherTest(this);
        ColorVisionTest colorVisionTest = new ColorVisionTest(this);
        PatternTest patternTest = new PatternTest(this);
        SpindexerTest spindexerTest = new SpindexerTest(this);

        while (opModeInInit()) { };

        Actions.runBlocking(
            new ParallelAction(
                    waitForTest.runTest(),
                    // gamepad2.dpad down -> power launcher
                    launcherTest.runTest(),
                    // gamepad2.x, a, b -> change target pattern
                    patternTest.runTest(),
                    colorVisionTest.runTest(),
                    // gamepad1.x -> change pos index
                    // gamepad1.a -= pos, gamepad1.y += pos
                    spindexerTest.runTest(),
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
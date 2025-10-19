package org.firstinspires.ftc.teamcode.ironDams;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.ParallelAction;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.ironDams.Test.ColorVisionTest;
import org.firstinspires.ftc.teamcode.ironDams.Test.PatternTest;
import org.firstinspires.ftc.teamcode.ironDams.Test.ShooterTest;
import org.firstinspires.ftc.teamcode.ironDams.Test.SpindexerTest;
import org.firstinspires.ftc.teamcode.ironDams.Test.WaitForTest;

@TeleOp(name = "TestOpMode", group = "_IronDams")
public class TestOpMode extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {
        WaitForTest waitForTest = new WaitForTest(this);
        ColorVisionTest colorVisionTest = new ColorVisionTest(this);
        PatternTest patternTest = new PatternTest(this);
        ShooterTest shooterTest = new ShooterTest(this);
        SpindexerTest spindexerTest = new SpindexerTest(this);

        int testMode = 0;

        while (opModeInInit()) {
            if (this.gamepad1.right_trigger != 0 && testMode < 4) {
                testMode++;
                sleep(250);
            } else if (this.gamepad1.left_trigger != 0 && testMode > 0) {
                testMode--;
                sleep(250);
            }

            switch (testMode) {
                case 0:
                    telemetry.addData("Mode", "WaitFor");
                    break;
                case 1:
                    telemetry.addData("Mode", "Color Vision");
                    break;
                case 2:
                    // gamepad2.x, a, b -> change target pattern
                    telemetry.addData("Mode", "Pattern");
                    break;
                case 3:
                    // dpad down - toggle shooter
                    telemetry.addData("Mode", "Shooter");
                    break;
                case 4:
                    // gamepad1.x -> change pos index
                    // gamepad1.a -= pos, gamepad1.y += pos
                    telemetry.addData("Mode", "Spindexer");
                    break;
            }

            telemetry.update();
        };


        switch (testMode) {
            case 0:
                Actions.runBlocking(new ParallelAction(waitForTest.runTest(), updateTelemetry()));
                break;
            case 1:
                Actions.runBlocking(new ParallelAction(colorVisionTest.runTest(), updateTelemetry()));
                break;
            case 2:
                // gamepad2.x, a, b -> change target pattern
                Actions.runBlocking(new ParallelAction(patternTest.runTest(), updateTelemetry()));
                break;
            case 3:
                // dpad down - toggle shooter
                Actions.runBlocking(new ParallelAction(shooterTest.runTest(), updateTelemetry()));
                break;
            case 4:
                // gamepad1.x -> change pos index
                // gamepad1.a -= pos, gamepad1.y += pos
                Actions.runBlocking(new ParallelAction(spindexerTest.runTest(), updateTelemetry()));
                break;
        }
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
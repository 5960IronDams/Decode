package org.firstinspires.ftc.teamcode.decode.teleOp;

import android.os.Build;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.ParallelAction;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.decode.core.Decoder;
import org.firstinspires.ftc.teamcode.decode.core.Intake;
import org.firstinspires.ftc.teamcode.decode.core.Launcher;
import org.firstinspires.ftc.teamcode.decode.core.Spindexer;

import java.time.LocalDate;

@TeleOp(name = "PlayerOpMode", group = "_IronDams")
public class PlayerOpMode extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {
//        Decoder _decoder = new Decoder(this);
//        WooshMachine _drive = new WooshMachine(this, true);
        Intake _intake = new Intake(this);
        Spindexer _spindexer = new Spindexer(this, _intake);
//        Launcher _launcher =new Launcher(hardwareMap);

        /* The Drive Train will run based on controller motion
         * The intake and spindexer will run when there isn't 3 balls detected.
         *  When there are 3 balls detected the intake and spindexer will stop.
         *  Player two will trigger the out take to run, raising the blocker while also starting the spindexer if it's not already running.
         *   Once the player triggers the shooter it will shoot all 3, stop the out take and start the intake back up
         *
         *  Player two will run the lift one completed.
         */

        waitForStart();

        Actions.runBlocking(
            new ParallelAction(
                    _spindexer.runAction(),
//                _drive.runDrive(),
//                _decoder.setSequence(),
                updateTelemetry()
            )
        );

        telemetry.addData("Completed", "");
        telemetry.update();

        this.sleep(15000);
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
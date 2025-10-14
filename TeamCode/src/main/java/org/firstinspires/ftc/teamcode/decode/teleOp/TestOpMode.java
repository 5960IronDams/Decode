package org.firstinspires.ftc.teamcode.decode.teleOp;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.ParallelAction;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.decode.Pattern;
import org.firstinspires.ftc.teamcode.decode.core.ColorVision;
import org.firstinspires.ftc.teamcode.decode.core.Intake;
import org.firstinspires.ftc.teamcode.decode.core.Launcher;
import org.firstinspires.ftc.teamcode.decode.core.Spindexer;

@TeleOp(name = "TestOpMode", group = "_IronDams")
public class TestOpMode extends LinearOpMode {
    /**
     * <ul>
     *     <li>
     *         GAMEPAD 1<br>
     *         <ul>
     *             <li></li>
     *         </ul>
     *     </li>
     *     <li>
     *         GAMEPAD 2<br>
     *         <ul>
     *             <li>Left Trigger - Intake.INACTIVE</li>
     *             <li>Right Trigger - Intake.ACTIVE</li>
     *             <li>X - Pattern Id Rotation</li>
     *             <li>A - Activate Launcher</li>
     *         </ul>
     *     </li>
     * </ul>
     * @throws InterruptedException
     */
    @Override
    public void runOpMode() throws InterruptedException {
//        WooshMachine _drive = new WooshMachine(this, true);
        Intake _intake = new Intake(this);
        ColorVision _colorVision = new ColorVision(this);
        Launcher _launcher =new Launcher(this);
        Pattern _pattern = new Pattern(this);
        Spindexer _spindexer = new Spindexer(this, _intake, _colorVision, _launcher, _pattern);

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
                _spindexer.runSpinner(),
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
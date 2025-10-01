package org.firstinspires.ftc.teamcode.decode.teleOp;

import com.acmerobotics.roadrunner.ParallelAction;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@TeleOp(name = "PlayerOpMode", group = "_IronDams")
public class PlayerOpMode extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {
        WooshMachine _drive = new WooshMachine(this, true);

        /* The Drive Train will run based on controller motion
         * The intake and spindexer will run when there isn't 3 balls detected.
         *  When there are 3 balls detected the intake and spindexer will stop.
         *  Player two will trigger the out take to run, raising the blocker while also starting the spindexer if it's not already running.
         *   Once the player triggers the shooter it will shoot all 3, stop the out take and start the intake back up
         *
         *  Player two will run the lift one completed.
         */

        waitForStart();
//        while (opModeIsActive()){
//            _drive.go();
//        }
        Actions.runBlocking(
            new ParallelAction(
                _drive.runDrive()
            )
        );
    }
}
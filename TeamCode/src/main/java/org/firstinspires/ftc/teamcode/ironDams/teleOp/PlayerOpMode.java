package org.firstinspires.ftc.teamcode.ironDams.teleOp;

import com.acmerobotics.roadrunner.ParallelAction;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@TeleOp(name = "PlayerOpMode", group = "_IronDams")
public class PlayerOpMode extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {
        WooshMachine _drive = new WooshMachine(this, true);
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
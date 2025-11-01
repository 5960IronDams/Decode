package org.firstinspires.ftc.teamcode.decode.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.decode.Constants;
import org.firstinspires.ftc.teamcode.decode.SharedData;
import org.firstinspires.ftc.teamcode.decode.core.BallDetection;
import org.firstinspires.ftc.teamcode.decode.core.Spindexer;
import org.firstinspires.ftc.teamcode.ironDams.Config;
import org.firstinspires.ftc.teamcode.ironDams.core.WaitFor;

@TeleOp(name = "TestSpindexerOpMode", group = "@@@@IronDamsTest")
public class TestSpindexerOpMode extends LinearOpMode {
    @Override
    public void runOpMode() {
        WaitFor userDelay = new WaitFor(Config.USER_DELAY_MS);

        SharedData data = new SharedData();
        Spindexer spindexer = new Spindexer(this, data);
        BallDetection ballDetection = new BallDetection(this, data);

        int currentIndex = 0;
        double[] positions = Constants.Spindexer.Positions;

        waitForStart();

        while (opModeIsActive() && !isStopRequested()) {

            if (gamepad1.a && currentIndex > 0 && userDelay.allowExec()) {
                currentIndex--;
                spindexer.setPos(positions[currentIndex]);
            } else if (gamepad1.y && currentIndex < positions.length - 1 && userDelay.allowExec()) {
                currentIndex++;
                spindexer.setPos(positions[currentIndex]);
            } else if (gamepad1.x && userDelay.allowExec()) {
                spindexer.setPos(spindexer.getPos() - 0.0025);
            } else if (gamepad1.b && userDelay.allowExec()) {
                spindexer.setPos(spindexer.getPos() + 0.0025);
            }

            ballDetection.update();

            telemetry.addData("Green", ballDetection.getGreen());
            telemetry.addData("Blue", ballDetection.getBlue());
            telemetry.addData("Index", currentIndex);
            telemetry.addData("Pos", spindexer.getPos());
            telemetry.update();
        }
    }
}
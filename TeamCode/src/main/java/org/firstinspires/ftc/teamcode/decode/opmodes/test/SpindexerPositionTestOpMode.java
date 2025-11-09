package org.firstinspires.ftc.teamcode.decode.opmodes.test;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.decode.SharedData;
import org.firstinspires.ftc.teamcode.decode.core.Spindexer;
import org.firstinspires.ftc.teamcode.irondams.core.Logger;
import org.firstinspires.ftc.teamcode.irondams.core.WaitFor;

@TeleOp(name = "SpindexerPositionTest", group = "Test")
public class SpindexerPositionTestOpMode extends LinearOpMode {
    @Override
    public void runOpMode() {
        WaitFor userDelay = new WaitFor(500);
        Logger logger = new Logger(this.getClass().getSimpleName());
        Spindexer spindexer = new Spindexer(this, logger);

        int currentIndex = 0;
        double[] positions = SharedData.Spindexer.POSITIONS;

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

            telemetry.addData("Index", currentIndex);
            telemetry.addData("Pos", spindexer.getPos());
            telemetry.update();
        }
    }
}
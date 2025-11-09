package org.firstinspires.ftc.teamcode.decode.opmodes;

import android.app.VoiceInteractor;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose2D;
import org.firstinspires.ftc.teamcode.decode.SharedData;
import org.firstinspires.ftc.teamcode.ironDams.core.Acceleration;
import org.firstinspires.ftc.teamcode.ironDams.core.odometry.Pinpoint;

@TeleOp(name = "AcelTestOpMode", group = "@@@@IronDams")
public class AcelTestOpMode extends LinearOpMode {
    private final SharedData DATA = new SharedData();

    @Override
    public void runOpMode() {
        Pinpoint pinpoint = new Pinpoint(this);

        waitForStart();

        Pose2D pos = pinpoint.getPose();
        double startPos = pos.getX(DistanceUnit.INCH);

        while(opModeIsActive()) {
            pos = pinpoint.getPose();
            double minPower = 0.2;
            double maxPower = 1;
            if (22 - pos.getX(DistanceUnit.INCH) <= 10) minPower = 0;
            double pow = Acceleration.getPower(startPos, pos.getX(DistanceUnit.INCH), 22, 10, 10, minPower, maxPower);

            telemetry.addData("Start Pos", startPos);
            telemetry.addData("1st power", pow);



            double powTwo = Math.max(minPower, Math.min(pow, maxPower));

            telemetry.addData("2nd power", powTwo);
            telemetry.addData("Pose X", pos.getX(DistanceUnit.INCH));
            telemetry.addData("Pose Y", pos.getY(DistanceUnit.INCH));
            telemetry.addData("Pose Heading", pos.getHeading(AngleUnit.DEGREES));
            telemetry.update();
        }

    }
}
package org.firstinspires.ftc.teamcode.ironDams.opmodes;

import android.app.Application;
import android.content.Context;
import android.os.Environment;
import android.util.Log;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.qualcomm.robotcore.hardware.VoltageSensor;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose2D;
import org.firstinspires.ftc.teamcode.ironDams.Config;
import org.firstinspires.ftc.teamcode.ironDams.core.Logger;
import org.firstinspires.ftc.teamcode.ironDams.core.odometry.Pinpoint;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@TeleOp(name = "PinpointTest", group = "@@@@IronDamsTest")
public class PinpointTestOpMode extends LinearOpMode {

    private Pinpoint pinpoint;
    private VoltageSensor voltageSensor;

    private Logger logger;
    private ElapsedTime timer = new ElapsedTime();

    @Override
    public void runOpMode() throws InterruptedException {

        pinpoint = new Pinpoint(this);
        logger = new Logger("PinpointTest");

        telemetry.addLine(Environment.getExternalStorageDirectory().getPath());
        telemetry.update();

        voltageSensor = hardwareMap.voltageSensor.iterator().next();

        while(!isStopRequested() && !opModeIsActive()) { }

        if (isStopRequested()) {
            return;
        }

        waitForStart();

        if (isStopRequested()) {
            return;
        }

        Actions.runBlocking(tuneAction());
    }

    public Action tuneAction() {
        return new Action() {
            private boolean initialized = false;

            @Override
            public boolean run(@NonNull TelemetryPacket packet) {
                if (!initialized) {
                    initialized = true;
                }

                Pose2D pos = pinpoint.getPose();

                logger.write(timer.milliseconds(), "voltage", voltageSensor.getVoltage());
                logger.write(timer.milliseconds(), "x", pos.getX(DistanceUnit.INCH));
                logger.write(timer.milliseconds(), "y", pos.getY(DistanceUnit.INCH));
                logger.write(timer.milliseconds(), "heading", pos.getHeading(AngleUnit.DEGREES));

                packet.put("x", pos.getX(DistanceUnit.INCH));
                packet.put("y", pos.getY(DistanceUnit.INCH));
                packet.put("heading", pos.getHeading(AngleUnit.DEGREES));

                return true;
            }
        };
    }
}
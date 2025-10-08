package org.firstinspires.ftc.teamcode.decode.core;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;
import com.qualcomm.hardware.digitalchickenlabs.OctoQuad;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.PIDCoefficients;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;

import org.firstinspires.ftc.teamcode.ironDams.core.OctoQuadFWv3;

import java.util.Objects;

public class Spindexer {

    private final CRServo _spindexer;
    private final DcMotorEx enc;
    private final ColorVision _colorVision;
    private final Decoder _decoder;
    private final LinearOpMode _opMode;
    private Intake _intake;
    private Launcher _launcher;

    private int previousRotation = 0;

    public Spindexer(LinearOpMode opMode, Decoder decoder, ColorVision colorVision, Intake intake, Launcher launcher) {
        _launcher = launcher;
        _intake = intake;
        _opMode = opMode;
        _colorVision = colorVision;
        _decoder = decoder;
        _spindexer = opMode.hardwareMap.get(CRServo.class, "spindex");
        enc = opMode.hardwareMap.get(DcMotorEx.class, "enc");
        enc.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        previousRotation = readPos();
        closeLauncher();
    }
    public void openLauncher() {
        double openPos = 1.0;
    }

    public void closeLauncher() {
        double closedPos = 0.0;
    }

    public void setPower() {
        setPower(1.0);
    }

    private Spindexer toggleSpindexer(){

        if (_intake.isRunning()){
            setPower(0.25);
        }
        else if (_launcher.isRunning()){
            setPower(0.15);
        }
        else{
            stop();
        }
        return this;
    }
    public int readPos(){
        int y = enc.getCurrentPosition();
        return y / 2731;
    }
    public void setPower(double power) {
        _spindexer.setPower(power);
    }

    public void stop() {
        _spindexer.setPower(0);
    }

    public Action manageSpindexer() {
        return new Action() {
            private boolean initialized = false;

            @Override
            public boolean run(@NonNull TelemetryPacket packet) {
                if (!initialized) {
                    initialized = true;
                }
                toggleSpindexer();

//                int currentRotation = readPos();
//                String targetPattern = _decoder.getSequenceCodeString();
//                String currentPattern = "UUU";
//
//                if (previousRotation != currentRotation) {
//                    stop();
//                    previousRotation = currentRotation;
//                    currentPattern = _colorVision.getPattern();
//
//                    if (targetPattern.equalsIgnoreCase(currentPattern)) {
//                        stop();
//                    }
//                    else {
//                        setPower(0.7);
//                    }
//                }
//                packet.put("Target Pattern", targetPattern);
//                packet.put("Pattern Currently", currentPattern);
//
//                packet.put("Match", Objects.equals(targetPattern, currentPattern));
//                packet.put("Y", readPos());

                return true;
            }
        };
    }
}

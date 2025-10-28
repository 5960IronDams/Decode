package org.firstinspires.ftc.teamcode.decode.teleOp;

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

import org.firstinspires.ftc.teamcode.decode.Constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@TeleOp(name = "VelocityTuner", group = "@@@@IronDams")
public class VelocityTunerOpMode extends LinearOpMode {
    private int activeMotorIndex = 0;
    private DcMotorEx activeMotor;
    private List<Map.Entry<String, DcMotorEx>> entries;
    private final double INCREMENT = 100;
    private double targetVelocity = 0;
    private double rightTicksPerSec = 2245;
    private double leftTicksPerSec = 2305;
    private VoltageSensor batteryVoltageSensor;

    @Override
    public void runOpMode() throws InterruptedException {

        batteryVoltageSensor = hardwareMap.voltageSensor.iterator().next();

        DcMotorEx dmfl  = hardwareMap.get(DcMotorEx.class, "leftFront");
        DcMotorEx dmbl = hardwareMap.get(DcMotorEx.class, "leftBack");
        DcMotorEx dmbr = hardwareMap.get(DcMotorEx.class, "rightBack");
        DcMotorEx dmfr = hardwareMap.get(DcMotorEx.class, "rightFront");

        DcMotorEx mls = hardwareMap.get(DcMotorEx.class, Constants.Shooter.MOTOR_LEFT_ID);
        DcMotorEx mrs = hardwareMap.get(DcMotorEx.class, Constants.Shooter.MOTOR_RIGHT_ID);

        DcMotorEx mi = hardwareMap.get(DcMotorEx.class, Constants.Intake.INTAKE_ID);

        Map<String, DcMotorEx> motors = new HashMap<>();
        motors.put("Left Front Drive", dmfl);
        motors.put("Left Back Drive", dmbl);
        motors.put("Right Back Drive", dmbr);
        motors.put("Right Front Drive", dmfr);
        motors.put("Left Shooter", mls);
        motors.put("Right Shooter", mrs);
        motors.put("Intake", mi);

        entries = new ArrayList<>(motors.entrySet());
        activeMotor = entries.get(activeMotorIndex).getValue();

        dmfl.setDirection(DcMotorEx.Direction.FORWARD);
        dmbl.setDirection(DcMotorEx.Direction.FORWARD);
        dmbr.setDirection(DcMotorEx.Direction.REVERSE);
        dmfr.setDirection(DcMotorEx.Direction.REVERSE);

        mls.setDirection(DcMotorEx.Direction.REVERSE);
        mi.setDirection(DcMotorEx.Direction.REVERSE);

        for (Map.Entry<String, DcMotorEx> motor : motors.entrySet()) {
            motor.getValue().setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);
            motor.getValue().setMode(DcMotorEx.RunMode.STOP_AND_RESET_ENCODER);
            motor.getValue().setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);
        }

        dmfl.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);
        dmbl.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);
        dmbr.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);
        dmfr.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);

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

                if (gamepad1.left_trigger != 0 && activeMotorIndex > 0)
                {
                    activeMotorIndex--;
                    activeMotor = entries.get(activeMotorIndex).getValue();
                    sleep(250);
                } else if (gamepad1.right_trigger != 0 && activeMotorIndex < (entries.size() - 1)) {
                    activeMotorIndex++;
                    activeMotor = entries.get(activeMotorIndex).getValue();
                    sleep(250);
                }

                String activeMotorName = entries.get(activeMotorIndex).getKey();
                double f = 0;
                double d = .0005;
                PIDFCoefficients pidf = new PIDFCoefficients(0,0,0,0);
                if (activeMotorName.equals("Left Shooter")) {
                    f = (32767 / leftTicksPerSec) * 12.0 / batteryVoltageSensor.getVoltage();
                } else if (activeMotorName.equals("Right Shooter")) {
                    f = (32767 / rightTicksPerSec) * 12.0 / batteryVoltageSensor.getVoltage();
                }

                if (f != 0) pidf = new PIDFCoefficients(0, 0, d, f);

                if (gamepad1.a) {
                    if (targetVelocity > 0) {
                        activeMotor.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, pidf);

                        targetVelocity -= INCREMENT;
                        activeMotor.setVelocity(targetVelocity);
                    }
                    sleep(250);
                } else if (gamepad1.y) {
                    activeMotor.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, pidf);

                    targetVelocity += INCREMENT;
                    activeMotor.setVelocity(targetVelocity);
                    sleep(250);
                } else if (gamepad1.x) {
                    // Gets ticks per second
                    activeMotor.setPower(1);
                    sleep(250);
                } else if (gamepad1.b) {
                    activeMotor.setPower(0);
                    sleep(250);
                } else if (gamepad1.dpad_left) {
                    if (activeMotorName.equals("Left Shooter")) {
                        leftTicksPerSec -= 5;
                    } else if (activeMotorName.equals("Right Shooter")) {
                        rightTicksPerSec -= 5;
                    }
                    sleep(250);
                } else if (gamepad1.dpad_right) {
                    if (activeMotorName.equals("Left Shooter")) {
                        leftTicksPerSec += 5;
                    } else if (activeMotorName.equals("Right Shooter")) {
                        rightTicksPerSec += 5;
                    }
                    sleep(250);
                }

                packet.put("Power", activeMotor.getPower());
                packet.put("Velocity A", activeMotor.getVelocity());
                packet.put("Velocity T", targetVelocity);
                packet.put("Active Motor", activeMotorName);
                packet.put("TPS-L", leftTicksPerSec);
                packet.put("TPS-R", rightTicksPerSec);
                packet.put("Error", targetVelocity - activeMotor.getVelocity());
                packet.put("Voltage", batteryVoltageSensor.getVoltage());


                return true;
            }
        };
    }
}
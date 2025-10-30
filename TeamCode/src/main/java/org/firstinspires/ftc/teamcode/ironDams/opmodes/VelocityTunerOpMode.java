package org.firstinspires.ftc.teamcode.ironDams.opmodes;

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
import org.firstinspires.ftc.teamcode.ironDams.Config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@TeleOp(name = "VelocityTuner", group = "@@@@IronDamsTest")
public class VelocityTunerOpMode extends LinearOpMode {
    private int activeMotorIndex = 0;
    private DcMotorEx activeMotor;
    private List<Map.Entry<String, DcMotorEx>> entries;
    private final double INCREMENT = 100;
    private double targetVelocity = 0;
    private double rightTicksPerSec = 2245;
    private double leftTicksPerSec = 2305;
    private double ticksPerSec = 2245;
    private VoltageSensor batteryVoltageSensor;

    @Override
    public void runOpMode() throws InterruptedException {

        batteryVoltageSensor = hardwareMap.voltageSensor.iterator().next();

        DcMotorEx dmfl  = hardwareMap.get(DcMotorEx.class, Config.Hardware.Motors.DriveTrain.LEFT_FRONT_MOTOR_ID);
        DcMotorEx dmbl = hardwareMap.get(DcMotorEx.class, Config.Hardware.Motors.DriveTrain.LEFT_BACK_MOTOR_ID);
        DcMotorEx dmbr = hardwareMap.get(DcMotorEx.class, Config.Hardware.Motors.DriveTrain.RIGHT_BACK_MOTOR_ID);
        DcMotorEx dmfr = hardwareMap.get(DcMotorEx.class, Config.Hardware.Motors.DriveTrain.RIGHT_FRONT_MOTOR_ID);

        DcMotorEx mls = hardwareMap.get(DcMotorEx.class, Config.Hardware.Motors.Shooter.LEFT_MOTOR_ID);
        DcMotorEx mrs = hardwareMap.get(DcMotorEx.class, Config.Hardware.Motors.Shooter.RIGHT_MOTOR_ID);

        DcMotorEx mi = hardwareMap.get(DcMotorEx.class, Config.Hardware.Motors.Intake.INTAKE_MOTOR_ID);

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

                String activeMotorName = entries.get(activeMotorIndex).getKey();

                if (gamepad1.left_trigger != 0 && activeMotorIndex > 0)
                {
                    activeMotorIndex--;
                    activeMotor = entries.get(activeMotorIndex).getValue();
                    activeMotorName = entries.get(activeMotorIndex).getKey();
                    ticksPerSec = activeMotorName.equals("Left Shooter") ? leftTicksPerSec :
                        activeMotorName.equals("Right Shooter") ? rightTicksPerSec : 2245;
                    sleep(250);
                } else if (gamepad1.right_trigger != 0 && activeMotorIndex < (entries.size() - 1)) {
                    activeMotorIndex++;
                    activeMotor = entries.get(activeMotorIndex).getValue();
                    activeMotorName = entries.get(activeMotorIndex).getKey();
                    ticksPerSec = activeMotorName.equals("Left Shooter") ? leftTicksPerSec :
                            activeMotorName.equals("Right Shooter") ? rightTicksPerSec : 2245;
                    sleep(250);
                }


                double f = (32767 / ticksPerSec) * 12.0 / batteryVoltageSensor.getVoltage();;
                double d = .0005;
                PIDFCoefficients pidf = new PIDFCoefficients(0,0,d,f);

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
                    ticksPerSec -= 5;
                    sleep(250);
                } else if (gamepad1.dpad_right) {
                    ticksPerSec += 5;
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
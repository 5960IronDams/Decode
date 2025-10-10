package org.firstinspires.ftc.teamcode.decode.core;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class Spindexer {

    private final CRServo _spindexer;

    public Spindexer(HardwareMap hardwareMap) {
        _spindexer = hardwareMap.get(CRServo.class, "spindex");
        closeLauncher();
    }

    public void openLauncher() {
        double openPos = 1.0;
    }

    public void closeLauncher() {
        double closedPos = 0.0;
    }

    public void run() {
        run(1.0);
    }

    public void run(double power) {
        _spindexer.setPower(power);
    }

    public void stop() {
        _spindexer.setPower(0);
    }
}

package org.firstinspires.ftc.teamcode.ironDams.core;

import com.qualcomm.robotcore.hardware.Gamepad;

public class Gamepads {
    private final Gamepad GAMEPAD1;
    private final Gamepad GAMEPAD2;

    public Gamepads(Gamepad gamepad1, Gamepad gamepad2) {
        GAMEPAD1 = gamepad1;
        GAMEPAD2 = gamepad2;
    }

    public Gamepad getGamepad1() {
        return GAMEPAD1;
    }

    public Gamepad getGamePad2() {
        return GAMEPAD2;
    }
}

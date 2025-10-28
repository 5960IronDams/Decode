package org.firstinspires.ftc.teamcode.ironDams;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import java.util.function.BooleanSupplier;

public class WaitUntilFlagAction implements IAction {
    private final BooleanSupplier condition;

    public WaitUntilFlagAction(BooleanSupplier condition) {
        this.condition = condition;
    }

    @Override
    public boolean run(OpMode opMode) {
        return condition.getAsBoolean();
    }
}
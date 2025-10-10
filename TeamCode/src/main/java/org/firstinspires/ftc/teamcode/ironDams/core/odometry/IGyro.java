package org.firstinspires.ftc.teamcode.ironDams.core.odometry;

import org.firstinspires.ftc.robotcore.external.navigation.Pose2D;

public interface IGyro {
    double update();
    void reset();
    Pose2D getPose();

}

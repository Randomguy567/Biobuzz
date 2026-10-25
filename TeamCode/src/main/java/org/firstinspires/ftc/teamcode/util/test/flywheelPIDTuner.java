package org.firstinspires.ftc.teamcode.util.test;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotorEx;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import dev.nextftc.control.feedback.PIDController;

public class flywheelPIDTuner extends OpMode {

    public static double kP = 0,kI = 0, kD = 0;
    public static double ff = 0;
    public static double target;
    PIDController pid = new PIDController(kP,kI,kD);

    DcMotorEx flywheel;
    @Override
    public void init() {
        flywheel = hardwareMap.get(DcMotorEx.class, "flywheel");
        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());
    }

    @Override
    public void loop() {

        double error = target - flywheel.getVelocity();

        double power = pid.calculate(error);

        flywheel.setPower(power + ff);

        telemetry.addData("Target:",target);
        telemetry.addData("Current:",flywheel.getVelocity());


    }
}

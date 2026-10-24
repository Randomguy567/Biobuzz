package org.firstinspires.ftc.teamcode.util.test;


import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;


import dev.nextftc.control.feedback.PIDCoefficients;
import dev.nextftc.control.feedback.PIDController;

public class demo extends OpMode {

    double p = 0,i = 0,d = 0,f = 0.12;


    PIDController controller;



    DcMotor motor;

    @Override
    public void init() {
        motor = hardwareMap.get(DcMotor.class, "motor");
        controller = new PIDController(
                new PIDCoefficients(p,i,d)
        );
    }

    @Override
    public void loop() {

        double target = 1500;
        double motorPos = motor.getCurrentPosition();
        double error = target - motorPos;
        double power = controller.calculate(error);
        motor.setPower(power + f);

    }


    }





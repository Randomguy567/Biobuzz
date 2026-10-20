
package org.firstinspires.ftc.teamcode.util.test;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

@TeleOp
public class TeleOp1 extends OpMode {

    // motors
    DcMotor motorTR, motorTL, motorBL, motorBR;

    // blockers
    Servo pollenBlocker, nectarBlocker;

    final double POLLEN_OPEN = 0.0;
    final double POLLEN_CLOSED = 0.5;

    final double NECTAR_OPEN = 0.0;
    final double NECTAR_CLOSED = 0.5;

    boolean previousA = false;
    boolean blockersOpen = false;


    @Override
    public void init() {

        // Map drivetrain motors
        motorTR = hardwareMap.get(DcMotor.class, "motorTR");
        motorTL = hardwareMap.get(DcMotor.class, "motorTL");
        motorBL = hardwareMap.get(DcMotor.class, "motorBL");
        motorBR = hardwareMap.get(DcMotor.class, "motorBR");

        motorTL.setDirection(DcMotor.Direction.REVERSE);
        motorBL.setDirection(DcMotor.Direction.REVERSE);

        // Map servo blockers
        pollenBlocker = hardwareMap.get(Servo.class, "pollenBlocker");
        nectarBlocker = hardwareMap.get(Servo.class, "nectarBlocker");

        // Set initial blocker positions
        pollenBlocker.setPosition(POLLEN_CLOSED);
        nectarBlocker.setPosition(NECTAR_CLOSED);

    }

    @Override
    public void loop() {


        double y = -gamepad1.left_stick_y;
        double x = gamepad1.left_stick_x;
        double rx = gamepad1.right_stick_x;

        double denominator = Math.max(Math.abs(y) + Math.abs(x) + Math.abs(rx), 1);
        double frontLeftPower = (y + x + rx) / denominator;
        double backLeftPower = (y - x + rx) / denominator;
        double frontRightPower = (y - x - rx) / denominator;
        double backRightPower = (y + x - rx) / denominator;

        // Apply drivetrain powers
        motorTL.setPower(frontLeftPower);
        motorBL.setPower(backLeftPower);
        motorTR.setPower(frontRightPower);
        motorBR.setPower(backRightPower);


        boolean currentA = gamepad2.a;

        if (currentA && !previousA) {
            blockersOpen = !blockersOpen;

            if (blockersOpen) {
                pollenBlocker.setPosition(POLLEN_OPEN);
                nectarBlocker.setPosition(NECTAR_OPEN);
            } else {
                pollenBlocker.setPosition(POLLEN_CLOSED);
                nectarBlocker.setPosition(NECTAR_CLOSED);
            }
        }

        previousA = currentA;
    }
}

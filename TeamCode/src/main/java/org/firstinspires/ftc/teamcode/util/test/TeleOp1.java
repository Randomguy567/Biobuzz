
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
    IMU imu1;


    // blockers
    Servo pollenBlocker, nectarBlocker;

    final double POLLEN_OPEN = 0.0;
    final double POLLEN_CLOSED = 0.5;

    final double NECTAR_OPEN = 0.0;
    final double NECTAR_CLOSED = 0.5;

    boolean previousA = false;
    boolean pollenBlockersOpen = false;

    boolean nectarBlockersOpen = false;


    @Override
    public void init() {

        // Map drivetrain motors
        motorTR = hardwareMap.get(DcMotor.class, "motorTR");
        motorTL = hardwareMap.get(DcMotor.class, "motorTL");
        motorBL = hardwareMap.get(DcMotor.class, "motorBL");
        motorBR = hardwareMap.get(DcMotor.class, "motorBR");
        imu1 = hardwareMap.get(IMU.class, "imu");

        motorTL.setDirection(DcMotor.Direction.REVERSE);
        motorBL.setDirection(DcMotor.Direction.REVERSE);

        // Map servo blockers
        pollenBlocker = hardwareMap.get(Servo.class, "pollenBlocker");
        nectarBlocker = hardwareMap.get(Servo.class, "nectarBlocker");

        // Set initial blocker positions
        pollenBlocker.setPosition(POLLEN_CLOSED);
        nectarBlocker.setPosition(NECTAR_CLOSED);

        IMU.Parameters parameters = new IMU.Parameters(new RevHubOrientationOnRobot(
                RevHubOrientationOnRobot.LogoFacingDirection.LEFT,
                RevHubOrientationOnRobot.UsbFacingDirection.UP));
        // Without this, the REV Hub's orientation is assumed to be logo up / USB forward
        imu1.initialize(parameters);
    }

    @Override
    public void loop() {


        double y = -gamepad1.left_stick_y;
        double x = gamepad1.left_stick_x;
        double rx = gamepad1.right_stick_x;

        if (gamepad1.options) {
            imu1.resetYaw();
        }

        double botHeading = imu1.getRobotYawPitchRollAngles().getYaw(AngleUnit.RADIANS);

        // Rotate the movement direction counter to the bot's rotation
        double rotX = x * Math.cos(-botHeading) - y * Math.sin(-botHeading);
        double rotY = x * Math.sin(-botHeading) + y * Math.cos(-botHeading);

        rotX = rotX * 1.1;  // Counteract imperfect strafing

        // Denominator is the largest motor power (absolute value) or 1
        // This ensures all the powers maintain the same ratio,
        // but only if at least one is out of the range [-1, 1]
        double denominator = Math.max(Math.abs(rotY) + Math.abs(rotX) + Math.abs(rx), 1);
        double frontLeftPower = (rotY + rotX + rx) / denominator;
        double backLeftPower = (rotY - rotX + rx) / denominator;
        double frontRightPower = (rotY - rotX - rx) / denominator;
        double backRightPower = (rotY + rotX - rx) / denominator;

        motorTL.setPower(frontLeftPower);
        motorBL.setPower(backLeftPower);
        motorTR.setPower(frontRightPower);
        motorBR.setPower(backRightPower);

        if (gamepad1.aWasPressed()) {
            pollenBlockersOpen = !pollenBlockersOpen;

            if (pollenBlockersOpen) {
                pollenBlocker.setPosition(POLLEN_OPEN);
            } else {
                pollenBlocker.setPosition(POLLEN_CLOSED);
            }
        }

        if (gamepad1.bWasPressed()) {
            nectarBlockersOpen = !nectarBlockersOpen;

            if (nectarBlockersOpen) {
                nectarBlocker.setPosition(NECTAR_OPEN);
            } else {
                nectarBlocker.setPosition(NECTAR_CLOSED);
            }
        }

    }
}

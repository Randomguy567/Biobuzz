package org.firstinspires.ftc.teamcode.util.pedro;

import com.pedropathing.algorithm.Foresight;
import com.pedropathing.algorithm.ForesightConfig;
import com.pedropathing.controllers.Controller;
import com.pedropathing.follower.Follower;
import com.pedropathing.math.Matrix;
import com.pedropathing.math.Vector2D;
import com.pedropathing.revhub.drivetrains.Mecanum;
import com.pedropathing.revhub.drivetrains.MecanumConfig;
import com.pedropathing.revhub.localizers.PinpointConfig;
import com.pedropathing.revhub.localizers.PinpointLocalizer;
import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

public class Constants {
    public static Follower create(HardwareMap h) {


        return new Follower(
                new PinpointLocalizer(h, localizerConfig),
                new Mecanum(h, drivetrainConfig),
                new Foresight(foresightConfig)
        );

    }

    public static PinpointConfig localizerConfig = new PinpointConfig(c -> {
        c.name.set("pinpoint");
        c.podType.set(GoBildaPinpointDriver.GoBildaOdometryPods.goBILDA_4_BAR_POD);
        c.xPodOffset.set(-3.456256596122201);
        c.yPodOffset.set(-5.437473086860237);
        c.xPodDirection.set(GoBildaPinpointDriver.EncoderDirection.FORWARD);
        c.yPodDirection.set(GoBildaPinpointDriver.EncoderDirection.REVERSED);
        c.globalDistanceUnit.set(DistanceUnit.INCH);
        c.offsetUnits.set(DistanceUnit.INCH);
    });
    public static MecanumConfig drivetrainConfig = new MecanumConfig(c -> {
        c.frontLeftName.set("motorTL");
        c.frontRightName.set("motorTR");
        c.backLeftName.set("motorBL");
        c.backRightName.set("motorBR");
        c.frontLeftDirection.set(DcMotorSimple.Direction.REVERSE);
        c.frontRightDirection.set(DcMotorSimple.Direction.FORWARD);
        c.backLeftDirection.set(DcMotorSimple.Direction.REVERSE);
        c.backRightDirection.set(DcMotorSimple.Direction.FORWARD);
    });


    public static ForesightConfig foresightConfig = new ForesightConfig(
            c -> {
                Controller primaryTranslationalForward = Controller.proportional(0.26678150206891604);
                Controller secondaryTranslationalForward = Controller.proportional(0.09856863648144112);
                Controller primaryTranslationalLateral = Controller.proportional(0.3289135567581115);
                Controller secondaryTranslationalLateral = Controller.proportional(0.12152477049002124);

                c.forwardTranslational.set(Controller.piecewise(secondaryTranslationalForward).put(2.5, primaryTranslationalForward));
                c.strafeTranslational.set(Controller.piecewise(secondaryTranslationalLateral).put(2.5, primaryTranslationalLateral));

                c.coast.set(Controller.proportionalFeedforward(0.018057821572406915));
                c.brake.set(Controller.proportionalFeedforward(0.015349148336545878));

                c.headingFeedback.set(Controller.proportional(5.021729372539544));
                c.headingBrakeCoefficients.set(Vector2D.cartesian(0.05333404613085411, 0.0035150307632871493));

                c.linearBrakeCoefficients.set(Matrix.diag(0.047313872435405996, 0.06672148238438866));
                c.quadraticBrakeCoefficients.set(Matrix.diag(0.0016400954600230126, 0.0010550981001660408));

                c.maxAchievableForwardVelocity.set(59.27153224830094);
                c.maxAchievableStrafeVelocity.set(48.13046112020012);
                c.naturalForwardDeceleration.set(44.154707300270594);
                c.naturalStrafeDeceleration.set(73.00872672078374);
            }
    );

}
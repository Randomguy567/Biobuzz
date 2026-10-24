package org.firstinspires.ftc.teamcode.util.test;

import android.graphics.Color;
import android.util.Size;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.SortOrder;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.opencv.Circle;
import org.firstinspires.ftc.vision.opencv.ColorBlobLocatorProcessor;
import org.firstinspires.ftc.vision.opencv.ColorRange;
import org.firstinspires.ftc.vision.opencv.ColorSpace;
import org.opencv.core.Point;


import java.util.List;

import dev.nextftc.control.feedback.PIDCoefficients;
import dev.nextftc.control.feedback.PIDController;
import org.firstinspires.ftc.teamcode.util.lib.distanceLUT;
import org.opencv.core.Scalar;

@TeleOp
public class simpleBallFinder extends OpMode {

    //one thing that i think could be improved is to use trig and stuff to find the diagonal distance
    // and then use pathing to get to it

    //maybe also find a way to find multiple groups of pollen?




    //pid from nextFTC
    PIDCoefficients anglePIDcoeffs = new PIDCoefficients(0,0,0); // needs tuning
    PIDController anglePID;
    PIDCoefficients drivePIDcoeffs = new PIDCoefficients(0,0,0); // needs tuning
    PIDController drivePID;

    //whether the robot is angling towards a ball
    boolean angling = false;

    //whether the robot is driving towards a ball
    boolean driving = false;

    //whether the robot is instructed to use pids to get to ball
    boolean scanning = false;

    //whether the robot is instructed to turn around slowly to find a ball
    boolean searching = false;

    //last few seconds with driving
    boolean lastDrive = false;

    //distance between ball and robot
    double dist;

    //ball finder processor, might make a custom processor once i learn how to
    ColorBlobLocatorProcessor colorLocator;

    //vision portal
    VisionPortal portal;

    //drivetrain motors
    DcMotorEx fl,fr,br,bl;

    //camera specs
    int resX = 320;
    int resY = 240;

    int centerX = resX / 2;

    //selected blob to drive to
    ColorBlobLocatorProcessor.Blob blob;

    ElapsedTime driveTimer = new ElapsedTime();;
    @Override
    public void init() {

        //init ball finder
        colorLocator =  new ColorBlobLocatorProcessor.Builder()
                .setTargetColorRange(new ColorRange(ColorSpace.HSV,new Scalar(52,70,70),new Scalar(57,60,95)))
                .setContourMode(ColorBlobLocatorProcessor.ContourMode.EXTERNAL_ONLY)
                .setDrawContours(true)
                .setBoxFitColor(0)
                .setCircleFitColor(Color.rgb(255, 255, 0))
                .setBlurSize(5)
                .setDilateSize(15)
                .setErodeSize(15)
                .setMorphOperationType(ColorBlobLocatorProcessor.MorphOperationType.CLOSING)
                .build();
        //init vision portal
        portal = new VisionPortal.Builder()
                .addProcessor(colorLocator)
                .setCameraResolution(new Size(resX, resY))
                .setCamera(hardwareMap.get(WebcamName.class, "Webcam 1"))
                .build();

        //init drivetrain
        fl = hardwareMap.get(DcMotorEx.class,"fl");
        fr = hardwareMap.get(DcMotorEx.class,"fr");
        br = hardwareMap.get(DcMotorEx.class,"br");
        bl = hardwareMap.get(DcMotorEx.class,"bl");
        fr.setDirection(DcMotorSimple.Direction.REVERSE);
        br.setDirection(DcMotorSimple.Direction.REVERSE);

        //init LUT for distance
        distanceLUT.initLUT();

        //init timer
        driveTimer.reset();
    }

    @Override
    public void loop() {
        //filters
        List<ColorBlobLocatorProcessor.Blob> blobs = colorLocator.getBlobs();



        ColorBlobLocatorProcessor.Util.filterByCriteria(
                ColorBlobLocatorProcessor.BlobCriteria.BY_CONTOUR_AREA,
                100, 20000, blobs);

        ColorBlobLocatorProcessor.Util.filterByCriteria(
                ColorBlobLocatorProcessor.BlobCriteria.BY_CIRCULARITY,
                0.6, 1, blobs);

        ColorBlobLocatorProcessor.Util.sortByCriteria(
                ColorBlobLocatorProcessor.BlobCriteria.BY_CONTOUR_AREA,
                SortOrder.DESCENDING, blobs);


        //toggle for scanning
        if(gamepad1.aWasPressed()){
            scanning = !scanning;
        }

        //find balls by spinning around
        if(gamepad1.bWasPressed()){
            searching = !searching;
        }

        if(searching){
            fl.setPower(0.2);
            bl.setPower(0.2);
            fr.setPower(-0.2);
            br.setPower(-0.2);


            if(!blobs.isEmpty()){
                searching = false;
            }
        }


        if(lastDrive){
            fl.setPower(0.5);
            fr.setPower(0.5);
            bl.setPower(0.5);
            br.setPower(0.5);

            if(driveTimer.seconds() > 3){//placeholder time
                lastDrive = false;
            }
        }

        //below is fully autonomous

        //screen diagram

        /*
        +------------------------------------------------------+
        |  (0,0)      --------------------------------->   320 |
        |              x increases                             |
        |     |                                                |
        |     |                                                |
        |     |          (resX/2 , resY/2)                     |
        |     |                                                |
        |     |                                                |
        |     |  y increases                                   |
        |     ↓                                                |
        |     240                                  (320, 240)  |
        +------------------------------------------------------+
        */


        if(scanning) {

            //first step, find bottom most pixel
            int bottomYIndex = 0;
            double bottomY = 0; //bottom pixel y coord

            //loop through all blobs
            for (ColorBlobLocatorProcessor.Blob b : blobs) {

                //get contour points of current blob
                Point[] contourPoints = b.getContourPoints();


                //loop through each of the pixels in the contour
                for (int i = 0; i < contourPoints.length; i++) {

                    //if the y of the current pixel is greater than bottom pixel, make that the bottom pixel
                    // and make the index of closest contour
                    if (contourPoints[i].y > bottomY) {
                        bottomY = contourPoints[i].y;
                        bottomYIndex = i;
                    }
                }
            }

            blob = blobs.get(bottomYIndex);
            dist = distanceLUT.get(bottomY);

            //time to angle towards it with pid

            //get circleFit
            Circle circle = blob.getCircle();

            //error for pid
            double angleError = circle.getCenter().x - centerX;

            if (Math.abs(angleError) > 15) {//15 pixels, might have to scale based on distance
                angling = true;
                driving = false;
            } else {
                driving = true;
                angling = false;
            }

            //if error is greater than 15 pixels, then angle
            if (angling) {
                double output = anglePID.calculate(angleError);

                //error is negative when it is left of camera, so it needs to turn counterclockwise
                //im thinking that the pid will give a negative value
                //with that, fr and br will be -output bc they need to be positive
                // and with output being negative, it needs to be reversed

                //btw that logic can be used for right side of camera too


                fl.setPower(output);
                bl.setPower(output);

                //reversed
                fr.setPower(-output);
                br.setPower(-output);
            }
            //if angling is complete, then drive to the ball
            if(driving){

                //dist is the error bc we need to get it to 0
                fl.setPower(anglePID.calculate(dist));
                bl.setPower(anglePID.calculate(dist));
                br.setPower(anglePID.calculate(dist));
                fr.setPower(anglePID.calculate(dist));

                //once it gets close enough, the cam can't see it so

                if(dist < 5){//placeholder

                    driving = false;
                    //once camera can't see og ball, then it will start angling towards smt else
                    //so stopping the scanning will stop it
                    scanning = false;

                    //reset drivetimer for last drive
                    driveTimer.reset();
                    lastDrive = false;
                }


            }
        }
    }
}

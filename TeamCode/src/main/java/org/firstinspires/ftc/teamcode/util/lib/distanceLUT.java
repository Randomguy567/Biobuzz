package org.firstinspires.ftc.teamcode.util.lib;

import com.seattlesolvers.solverslib.util.InterpLUT;



public class distanceLUT {

    //an LUT where x is the bottommost point, and y is the distance
    static InterpLUT  lut = new InterpLUT();
    public static void initLUT(){

        //add points once I get data


        lut.createLUT();
    }

    public static double get(double input){
        return lut.get(input);
    }
}

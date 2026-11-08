package org.firstinspires.ftc.teamcode.util.lib;

import com.pedropathing.math.Pose;
import com.pedropathing.paths.Path;

import java.util.ArrayList;
import java.util.List;

public class PathOptions {

    List<Pose> poses;
    public PathOptions(List<Pose> poses){
        this.poses = poses;
    }

    public List<Pose> reflect(){
        List<Pose> reflected = new ArrayList<>();

        for(Pose pose:poses){
            reflected.add(new Pose(141.5 - pose.x(), 141.5 - pose.y(),pose.heading() - 180));
        }

        return  reflected;

    }

}

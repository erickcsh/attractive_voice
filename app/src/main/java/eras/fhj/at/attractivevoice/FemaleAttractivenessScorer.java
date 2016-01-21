package eras.fhj.at.attractivevoice;

import java.util.ArrayList;

/**
 * Created by erick on 1/20/16.
 */
public class FemaleAttractivenessScorer extends AttractivenessScorer {
    @Override
    public int score(ArrayList<Integer> samples) {
        int average = getAverage(samples);
        if(average < 200) {
            return -1;
        } else if(average > 800) {
            return 11;
        } else if(average > 750) {
            return 1;
        } else if(average > 650) {
            return 2;
        } else if (average > 550) {
            return 3;
        } else if (average > 370) {
            return 4;
        } else if (average > 300) {
            return 5;
        } else if (average < 200 || average > 290) {
            return 6;
        } else if (average < 206 || average > 286) {
            return 7;
        } else if (average < 240 || average > 278) {
            return 8;
        } else if (average < 266 || average > 272) {
            return 9;
        } else {
            return 10;
        }
    }
}

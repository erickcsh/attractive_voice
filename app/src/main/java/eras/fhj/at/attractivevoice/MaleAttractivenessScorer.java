package eras.fhj.at.attractivevoice;

import java.util.ArrayList;

/**
 * Created by erick on 1/20/16.
 */
public class MaleAttractivenessScorer extends AttractivenessScorer {

    @Override
    public int score(ArrayList<Integer> samples) {
        int average = getAverage(samples);
        if(average == 0) {
            return -1;
        } else if(average > 800) {
            return 11;
        } else if(average > 700) {
            return 1;
        } else if(average > 600) {
            return 2;
        } else if (average > 500) {
            return 3;
        } else if (average > 500) {
            return 4;
        } else if (average > 500) {
            return 5;
        } else if (average < 210) {
            return 6;
        } else if (average < 220) {
            return 7;
        } else if (average < 230 || average > 240) {
            return 8;
        } else if (average < 234 || average > 238) {
            return 9;
        } else {
            return 10;
        }
    }
}

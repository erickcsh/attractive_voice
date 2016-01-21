package eras.fhj.at.attractivevoice;

import java.util.ArrayList;

/**
 * Created by erick on 1/20/16.
 */
public class MaleAttractivenessScorer extends AttractivenessScorer {

    @Override
    public int score(ArrayList<Integer> samples) {
        int average = getAverage(samples);
        if(average < 204) {
            return -1;
        } else if(average > 800) {
            return 11;
        } else if(average > 650) {
            return 1;
        } else if(average > 500) {
            return 2;
        } else if (average > 350) {
            return 3;
        } else if (average > 280) {
            return 4;
        } else if (average > 250) {
            return 5;
        } else if (average < 200 || average > 240) {
            return 6;
        } else if (average < 206 || average > 234) {
            return 7;
        } else if (average < 212 || average > 229) {
            return 8;
        } else if (average < 220 || average > 224) {
            return 9;
        } else {
            return 10;
        }
    }
}

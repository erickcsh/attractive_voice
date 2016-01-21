package eras.fhj.at.attractivevoice;

import java.util.ArrayList;

/**
 * Created by erick on 1/20/16.
 */
public abstract class AttractivenessScorer {

    public abstract int score(ArrayList<Integer> samples);

    protected int getAverage(ArrayList<Integer> samples) {
        if(samples.size() == 0) {
            return 0;
        }
        int acc = 0;
        for(int i = 0; i < samples.size(); i++) {
            acc += samples.get(i);
        }
        int average = (int) acc/samples.size();
        return average;
    }
}

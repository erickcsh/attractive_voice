package eras.fhj.at.attractivevoice;

import android.util.Log;

/**
 * Created by erick on 1/20/16.
 */
public class AttractivenessScorerBuilder {

    public static AttractivenessScorer build(String genreValue) {
        if(genreValue == "Female") {
            return new FemaleAttractivenessScorer();
        } else {
            return new MaleAttractivenessScorer();
        }
    }
}

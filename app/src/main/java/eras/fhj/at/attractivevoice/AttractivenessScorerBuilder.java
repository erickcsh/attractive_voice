package eras.fhj.at.attractivevoice;

/**
 * Created by erick on 1/20/16.
 */
public class AttractivenessScorerBuilder {

    public static AttractivenessScorer build(String genreValue) {
        if(genreValue.equals("Female")) {
            return new FemaleAttractivenessScorer();
        } else {
            return new MaleAttractivenessScorer();
        }
    }
}

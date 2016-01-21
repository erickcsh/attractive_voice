package eras.fhj.at.attractivevoice;

/**
 * Created by erick on 11/30/15.
 */
public class AttractivenessDetectorMessager {

    public static String getMessageFor(int result) {
        String resultMessage = "You got a " + result + " out of 10. ";
        switch (result) {
            case -1:
                resultMessage += "Please speak louder";
                break;
            case 1:
                resultMessage += "Please stop talking. Sign language will sit better for you";
                break;
            case 2:
                resultMessage += "You voice is not the worst, but deaf people will like you more";
                break;
            case 3:
                resultMessage += "Well well, we hope you are funny, cause your voice does not call the attention";
                break;
            case 4:
                resultMessage += "Nice voice, well at least a 1 could maybe think it, not us sorry.";
                break;
            case 5:
                resultMessage += "Right in the middle. Not that impressive.";
                break;
            case 6:
                resultMessage += "You are above the average, still not that attractive.";
                break;
            case 7:
                resultMessage += "Yeahhh not an A++ but still we approve your voice.";
                break;
            case 8:
                resultMessage += "High enough to get out and get some numbers.";
                break;
            case 9:
                resultMessage += "Almost perfect. We oved your voice.";
                break;
            case 10:
                resultMessage += "You sexy beast go conquer the world with your voice, it is yours.";
                break;
            case 11:
                resultMessage += "Please avoid screaming or speak softly";
                break;
            default:
                break;
        }
        return resultMessage;
    }
}

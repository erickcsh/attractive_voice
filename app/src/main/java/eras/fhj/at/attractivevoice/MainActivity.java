package eras.fhj.at.attractivevoice;

import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

import fftpack.RealDoubleFFT;

public class MainActivity extends AppCompatActivity {

    Button play, stop, record;
    private MediaRecorder myAudioRecorder;
    private String outputFile = null;
    // START Sound Analyzer code block.
    int frequency = 8000;
    int channelConfiguration = AudioFormat.CHANNEL_IN_MONO;// CHANNEL_CONFIGURATION_MONO;
    int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;

    AudioRecord audioRecord;
    private RealDoubleFFT transformer;
    int blockSize = 256;
    boolean started = false;
    boolean CANCELLED_FLAG = false;

    RecordAudio recordTask;
    static AppCompatActivity mainActivity;
    // END Sound Analyser Block

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        play = (Button) findViewById(R.id.button3);
        stop = (Button) findViewById(R.id.button2);
        record = (Button) findViewById(R.id.button);

        stop.setEnabled(false);
        play.setEnabled(false);
        outputFile = Environment.getExternalStorageDirectory().getAbsolutePath() + "/recording.3gp";

        myAudioRecorder = new MediaRecorder();
        myAudioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        myAudioRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        myAudioRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        myAudioRecorder.setOutputFile(outputFile);

        record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*try {
                    myAudioRecorder.prepare();
                    myAudioRecorder.start();
                } catch (IllegalStateException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                */

                    //<< FTT CODE
                    started = true;
                    CANCELLED_FLAG = false;
                    recordTask = new RecordAudio();
                    recordTask.execute();
                    //>> FTT CODE

                    record.setEnabled(false);
                    stop.setEnabled(true);

                    Toast.makeText(getApplicationContext(), "Recording started", Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               /* myAudioRecorder.stop();
                myAudioRecorder.release();
                myAudioRecorder = null; */

                if (started == true) {
                    //started = false;
                    CANCELLED_FLAG = true;
                    //recordTask.cancel(true);
                    try{
                        audioRecord.stop();
                    }
                    catch(IllegalStateException e){
                        Log.e("Stop failed", e.toString());

                    }
                }

                stop.setEnabled(false);
                play.setEnabled(true);

                Toast.makeText(getApplicationContext(), "Audio recorded successfully", Toast.LENGTH_LONG).show();

                Intent resultsIntent = new Intent(MainActivity.this, ResultsActivity.class);
                resultsIntent.putExtra("resultValue", "6");
                MainActivity.this.startActivity(resultsIntent);
            }
        });

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) throws IllegalArgumentException, SecurityException, IllegalStateException {
                MediaPlayer m = new MediaPlayer();


                try {
                    m.setDataSource(outputFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                try {
                    m.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                m.start();
                Toast.makeText(getApplicationContext(), "Playing audio", Toast.LENGTH_LONG).show();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }



    // FTT ANALYSIS CODE START
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {

    }

    int backCount = 0;
    private class RecordAudio extends AsyncTask<Void, double[], Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {

            Log.e("doInBackground", "backCount = " + backCount++);

            //Toast.makeText(getApplicationContext(), "Analysing " + backCount++ , Toast.LENGTH_LONG).show();

            int bufferSize = AudioRecord.getMinBufferSize(frequency,
                    channelConfiguration, audioEncoding);
            audioRecord = new AudioRecord(
                    MediaRecorder.AudioSource.DEFAULT, frequency,
                    channelConfiguration, audioEncoding, bufferSize);
            int bufferReadResult;
            short[] buffer = new short[blockSize];
            double[] toTransform = new double[blockSize];
            try {
                audioRecord.startRecording();
            } catch (IllegalStateException e) {
                Log.e("Recording failed", e.toString());

            }
            while (started) {

                if (isCancelled() || (CANCELLED_FLAG == true)) {

                    started = false;
                    //publishProgress(cancelledResult);
                    Log.d("doInBackground", "Cancelling the RecordTask");
                    break;
                } else {
                    bufferReadResult = audioRecord.read(buffer, 0, blockSize);

                    for (int i = 0; i < blockSize && i < bufferReadResult; i++) {
                        toTransform[i] = (double) buffer[i] / 32768.0; // signed 16 bit
                    }

                    transformer.ft(toTransform);

                    publishProgress(toTransform);

                }

            }
            return true;
        }

        int maxY = 0;
        @Override
        protected void onProgressUpdate(double[]... progress) {
            Log.e("RecordingProgress", "Displaying in progress Ashish");

            Log.d("Test:", Integer.toString(progress[0].length));

            int width = 1024;

            if (width > 512) {
                for (int i = 0; i < progress[0].length; i++) {
                    int x = 2 * i;
                    int downy = (int) (150 - (progress[0][i] * 10));
                    int upy = 150;

                    if(maxY < downy)
                        maxY = downy;

                    TextView freqVal = (TextView) findViewById(R.id.textView);
                    freqVal.setText("X = " + x + " Y = " + downy + " Max Y = " + maxY);
                    //canvasDisplaySpectrum.drawLine(x, downy, x, upy, paintSpectrumDisplay);
                }

                //imageViewDisplaySectrum.invalidate();
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            try {
                audioRecord.stop();
            } catch (IllegalStateException e) {
                Log.e("Stop failed", e.toString());

            }

            //canvasDisplaySpectrum.drawColor(Color.BLACK);
            //imageViewDisplaySectrum.invalidate();

        }
    }

    protected void onCancelled(Boolean result) {

        try {
            audioRecord.stop();
        } catch (IllegalStateException e) {
            Log.e("Stop failed", e.toString());

        }
           /* //recordTask.cancel(true);
            Log.d("FFTSpectrumAnalyzer","onCancelled: New Screen");
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
*/
    }

    public void onClick(View v) {
        if (started == true) {
            //started = false;
            CANCELLED_FLAG = true;
            //recordTask.cancel(true);
            try {
                audioRecord.stop();
            } catch (IllegalStateException e) {
                Log.e("Stop failed", e.toString());

            }
            //canvasDisplaySpectrum.drawColor(Color.BLACK);

        } else {
            started = true;
            CANCELLED_FLAG = false;
            recordTask = new RecordAudio();
            recordTask.execute();
        }

    }

    static AppCompatActivity getMainActivity() {

        return mainActivity;
    }

    public void onStop() {
        super.onStop();
            /* try{
                 audioRecord.stop();
             }
             catch(IllegalStateException e){
                 Log.e("Stop failed", e.toString());

             }*/
        recordTask.cancel(true);
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    public void onStart() {
        super.onStart();

        transformer = new RealDoubleFFT(blockSize);
        mainActivity = this;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        try {
            audioRecord.stop();
        } catch (IllegalStateException e) {
            Log.e("Stop failed", e.toString());

        }
        recordTask.cancel(true);
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        try {
            audioRecord.stop();
        } catch (IllegalStateException e) {
            Log.e("Stop failed", e.toString());

        }
        recordTask.cancel(true);
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
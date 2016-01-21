package eras.fhj.at.attractivevoice;

import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.util.ArrayList;

import fftpack.RealDoubleFFT;

public class MainActivity extends AppCompatActivity {

    Button record;
    RadioGroup genreRadio;
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
    ArrayList<Integer> samples;
    static AppCompatActivity mainActivity;
    // END Sound Analyser Block

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        record = (Button) findViewById(R.id.button);

        genreRadio = (RadioGroup) findViewById(R.id.genreRadioGroup);

        samples = new ArrayList<Integer>();

        outputFile = Environment.getExternalStorageDirectory().getAbsolutePath() + "/recording.3gp";

        myAudioRecorder = new MediaRecorder();
        myAudioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        myAudioRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        myAudioRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        myAudioRecorder.setOutputFile(outputFile);

        record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
				MainActivity.this.startRecord();
            }
        });
    }

    public void startRecord() {
        MainActivity self = this;
        try {
            //<< FTT CODE
            started = true;
            CANCELLED_FLAG = false;
            recordTask = new RecordAudio();
            recordTask.execute();
            //>> FTT CODE

            record.setEnabled(false);

            Toast.makeText(getApplicationContext(), "Recording started", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                MainActivity.this.stopRecord();
            }
        }, 4000);
    }

    public void stopRecord() {
        if (started == true) {
            CANCELLED_FLAG = true;
            try{
                audioRecord.stop();
            }
            catch(IllegalStateException e){
                Log.e("Stop failed", e.toString());

            }
        }
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                MainActivity.this.calculateResults();
            }
        }, 2000);

        record.setEnabled(true);

        Toast.makeText(getApplicationContext(), "Audio recorded successfully. Calculating results", Toast.LENGTH_LONG).show();
    }

    public void calculateResults() {
        int selectedRadioId = genreRadio.getCheckedRadioButtonId();
        RadioButton selectedRadio = (RadioButton) findViewById(selectedRadioId);
        Log.d("INT RADIO ", Integer.toString(selectedRadioId));
        Log.d("INT RADIO ", selectedRadio.getText().toString());

        AttractivenessScorer scorer = AttractivenessScorerBuilder.build(selectedRadio.getText().toString());
        int score = scorer.score(samples);
        samples = new ArrayList<Integer>();
        Intent resultsIntent = new Intent(this, ResultsActivity.class);
        resultsIntent.putExtra("resultValue", score);
        startActivity(resultsIntent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    int backCount = 0;
    private class RecordAudio extends AsyncTask<Void, double[], Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {
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
            for (int i = 0; i < progress[0].length; i++) {
                int downy = (int) (150 - (progress[0][i] * 10));

                if(maxY < downy)
                    maxY = downy;

                if(downy > 200) {
                    samples.add(downy);
                    samples.add(maxY);
                }
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
        }
    }

    protected void onCancelled(Boolean result) {

        try {
            audioRecord.stop();
        } catch (IllegalStateException e) {
            Log.e("Stop failed", e.toString());

        }
    }

    public void onClick(View v) {
        if (started == true) {
            CANCELLED_FLAG = true;
            try {
                audioRecord.stop();
            } catch (IllegalStateException e) {
                Log.e("Stop failed", e.toString());
            }
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
        if(recordTask != null) {
            recordTask.cancel(true);
        }
        super.onStop();
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
            if(audioRecord != null) {
                audioRecord.stop();
            }
        } catch (IllegalStateException e) {
            Log.e("Stop failed", e.toString());

        }
        if(recordTask != null) {
            recordTask.cancel(true);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if(audioRecord != null) {
                audioRecord.stop();
            }
        } catch (IllegalStateException e) {
            Log.e("Stop failed", e.toString());

        }
        if(recordTask != null) {
            recordTask.cancel(true);
        }
    }
}
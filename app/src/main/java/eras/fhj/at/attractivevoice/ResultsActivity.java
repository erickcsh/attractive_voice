package eras.fhj.at.attractivevoice;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.telephony.TelephonyManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class ResultsActivity extends ActionBarActivity {

    final private String API_URL = "https://attractive-voice.herokuapp.com/results";
    TextView resultsLabel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        resultsLabel = (TextView) findViewById(R.id.resultsLabel);

        Intent resultsIntent = getIntent();
        int result = resultsIntent.getIntExtra("resultValue", 0);

        setResultsText(result);

        if(result != -1 && result != 11) {
            saveResultToAPI(result);
        }
    }

    public void retryClicked(View v) {
        this.onBackPressed();
    }

    private void setResultsText(int result) {
        String message = AttractivenessDetectorMessager.getMessageFor(result);
        this.resultsLabel.setText(message);
    }

    private void saveResultToAPI(int result) {
        HTTPResultSaver helper = new HTTPResultSaver();
        TelephonyManager tManager = (TelephonyManager)this.getSystemService(Context.TELEPHONY_SERVICE);
        String uid = tManager.getDeviceId();
        helper.execute(API_URL, Integer.toString(result), uid);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_results, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

package eras.fhj.at.attractivevoice;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.telephony.TelephonyManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
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
        this.ShowHearts(result);
    }

    private void ShowHearts(int result) {
        ImageView imageView = (ImageView)findViewById(R.id.hearts);
        if(result >= 0 && result < 2)
            imageView.setImageResource(R.drawable.one);
        else if (result >= 2 && result < 4)
            imageView.setImageResource(R.drawable.two);
        else if (result >= 4 && result < 6)
            imageView.setImageResource(R.drawable.three);
        else if (result >= 6 && result < 8)
            imageView.setImageResource(R.drawable.four);
        else if (result >= 8 && result < 10)
            imageView.setImageResource(R.drawable.five);
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

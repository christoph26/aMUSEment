package com.choosemuse.example.libmuse;

/**
 * Created by christoph on 23.04.17.
 */

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.support.annotation.IdRes;

import android.os.Bundle;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ProgressBar;
import android.widget.TextView;



import java.util.Timer;
import java.util.TimerTask;

public class ResultActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        final int percentage =
                (int) Math.floor(MuseConnection.getInstance().getRecording().getAverage()*100);


        final Timer numberDelay = new Timer();

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
                ObjectAnimator animation = ObjectAnimator.ofInt(progressBar, "progress", 0, percentage); // see this max value coming back here, we animale towards that value
                animation.setDuration(1500); //in milliseconds
                animation.setInterpolator(new DecelerateInterpolator());
                animation.start();


            }
        });
        numberDelay.schedule(new TimerTask() {
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        TextView result = (TextView) findViewById(R.id.resultpercentage);
                        result.setText(percentage + "%");
                    }
                });


            }
        }, 1500);

    }


    public void graphClick(View view) {
        Intent i = new Intent(ResultActivity.this,GraphResultActivity.class);
        startActivity(i);
    }
}
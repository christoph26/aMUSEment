package com.choosemuse.example.libmuse;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.interaxon.libmuse.ConnectionState;
import com.interaxon.libmuse.Muse;
import com.interaxon.libmuse.MuseArtifactPacket;
import com.interaxon.libmuse.MuseConnectionListener;
import com.interaxon.libmuse.MuseConnectionPacket;
import com.interaxon.libmuse.MuseDataListener;
import com.interaxon.libmuse.MuseDataPacket;
import com.interaxon.libmuse.MuseDataPacketType;
import com.interaxon.libmuse.MuseManager;
import com.interaxon.libmuse.MusePreset;
import com.interaxon.libmuse.MuseVersion;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity implements OnClickListener {

    MuseConnection museConnection;
    public MainActivity() {
        MuseConnection.init(this);
        museConnection = MuseConnection.getInstance();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button refreshButton = (Button) findViewById(R.id.refresh);
        refreshButton.setOnClickListener(this);
        Button connectButton = (Button) findViewById(R.id.connect);
        connectButton.setOnClickListener(this);

        refresh();

    }

    @Override
    public void onClick(View v) {
        Spinner musesSpinner = (Spinner) findViewById(R.id.muses_spinner);
        if (v.getId() == R.id.refresh) {
            refresh();
        } else if (v.getId() == R.id.connect) {
            List<Muse> pairedMuses = museConnection.getPairedMuses();
            if (pairedMuses.size() < 1 ||
                    musesSpinner.getAdapter().getCount() < 1) {
                Log.w("Muse Headband", "There is nothing to connect to");
            } else {
                Muse muse = museConnection.chooseMuse(musesSpinner.getSelectedItemPosition());
                ConnectionState state = muse.getConnectionState();
                if (state == ConnectionState.CONNECTED ||
                        state == ConnectionState.CONNECTING) {
                    Log.w("Muse Headband", "doesn't make sense to connect second time to the same muse");
                    return;
                }
                museConnection.configure_library();
                ProgressBar pb = (ProgressBar) findViewById(R.id.waitingNonZeroIcon);
                pb.setVisibility(View.VISIBLE);
                /**
                 * In most cases libmuse native library takes care about
                 * exceptions and recovery mechanism, but native code still
                 * may throw in some unexpected situations (like bad bluetooth
                 * connection). Print all exceptions here.
                 */
                try {
                    muse.runAsynchronously();
                } catch (Exception e) {
                    Log.e("Muse Headband", e.toString());
                }
            }
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void chooseVideoClick(View view) {
        Intent i = new Intent(MainActivity.this,ChooseVideoActivity.class);
        startActivity(i);
    }

    public void updateHorseshoeValues(final List<Double> horseValues) {
            this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    View horse0 =
                            (View) findViewById(R.id.horse0);
                    horse0.setBackgroundColor(getResources().getColor(getColorId((int) horseValues.get(0).doubleValue())));
                    View horse1 =
                            (View) findViewById(R.id.horse1);
                    horse1.setBackgroundColor(getResources().getColor(getColorId((int) horseValues.get(1).doubleValue())));
                    View horse2 =
                            (View) findViewById(R.id.horse2);
                    horse2.setBackgroundColor(getResources().getColor(getColorId((int) horseValues.get(2).doubleValue())));
                    View horse3 =
                            (View) findViewById(R.id.horse3);
                    horse3.setBackgroundColor(getResources().getColor(getColorId((int) horseValues.get(3).doubleValue())));



                }
            });
    }

    private int getColorId(int value) {
        if (value == 1) {
            return R.color.green;
        } else if (value == 2) {
            return R.color.yellow;
        } else {
            return R.color.red;
        }
    }

    private void refresh() {
        Spinner musesSpinner = (Spinner) findViewById(R.id.muses_spinner);
        List<String> spinnerItems = museConnection.getAvailableMuses();
        ArrayAdapter<String> adapterArray = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, spinnerItems);
        musesSpinner.setAdapter(adapterArray);
    }
}
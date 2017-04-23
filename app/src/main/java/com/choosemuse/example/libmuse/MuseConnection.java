package com.choosemuse.example.libmuse;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

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

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by christoph on 23.04.17.
 */

public class MuseConnection {
    private static MuseConnection instance = null;

    public static void init(MainActivity mainActivity){
        main = mainActivity;
    }

    public static MuseConnection getInstance(){
        if(instance == null){
            instance = new MuseConnection(main);
        }
        return instance;
    }

    private static MainActivity main;

    private MuseConnection(MainActivity main){
        this.main = main;

        // Create listeners and pass reference to activity to them
        WeakReference<Activity> weakActivity =
                new WeakReference<Activity>(main);

        connectionListener = new ConnectionListener(weakActivity);
        dataListener = new DataListener(weakActivity);
    }


    private boolean isRecording = false;
    private Recording recording = null;
    private boolean firstLaunch = true;

    public void toogleRecord() {
        if(!isRecording) {
            recording = new Recording();
            isRecording = true;
        } else {
            isRecording = false;
        }
    }

    public Recording getRecording(){
        return recording;
    }


    class ConnectionListener extends MuseConnectionListener {

        final WeakReference<Activity> activityRef;

        ConnectionListener(final WeakReference<Activity> activityRef) {
            this.activityRef = activityRef;
        }

        @Override
        public void receiveMuseConnectionPacket(MuseConnectionPacket p) {
            final ConnectionState current = p.getCurrentConnectionState();
            final String status = current==ConnectionState.CONNECTED?"CALIBRATING...":p.getPreviousConnectionState().toString() +
                    " -> " + current;


            final String full = "Muse " + p.getSource().getMacAddress() +
                    " " + status;
            Log.i("Muse Headband", full);
            final Activity activity = activityRef.get();
            if (activity != null) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        TextView statusText =
                                (TextView) activity.findViewById(R.id.con_status);
                        statusText.setText(status);
                    }
                });
            }
        }
    }

    class DataListener extends MuseDataListener {

        final WeakReference<Activity> activityRef;

        DataListener(final WeakReference<Activity> activityRef) {
            this.activityRef = activityRef;
        }

        @Override
        public void receiveMuseDataPacket(MuseDataPacket p) {
            switch (p.getPacketType()) {
                case CONCENTRATION:
                    //Log.i("CONCENTRATION", p.getValues().toString());
                    if(isRecording) {
                        recording.addSample(p.getValues().get(0));
                    }

                    if(firstLaunch && p.getValues().get(0) != 0){
                        firstLaunch = false;
                        enableContinueButton();
                    }

                    break;
                case HORSESHOE:
                    Log.i("HORSESHOE", p.getValues().toString());

                        //updateHorseshoeValues(p.getValues());

                default:
                    break;
            }
        }

        private void updateHorseshoeValues(List horseValues) {
            final Activity activity = activityRef.get();
            if (activity != null) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Button continueButton =
                                (Button) activity.findViewById(R.id.continue_button);
                        continueButton.setVisibility(View.VISIBLE);

                        ProgressBar pb = (ProgressBar) activity.findViewById(R.id.waitingNonZeroIcon);
                        pb.setVisibility(View.INVISIBLE);
                    }
                });
            }
        }

        private void enableContinueButton() {
            final Activity activity = activityRef.get();
            if (activity != null) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Button continueButton =
                                (Button) activity.findViewById(R.id.continue_button);
                        continueButton.setVisibility(View.VISIBLE);

                        ProgressBar pb = (ProgressBar) activity.findViewById(R.id.waitingNonZeroIcon);
                        pb.setVisibility(View.INVISIBLE);
                    }
                });
            }

        }

        @Override
        public void receiveMuseArtifactPacket(MuseArtifactPacket p) {
            if (p.getHeadbandOn() && p.getBlink()) {
                //   Log.i("Artifacts", "blink");
            }
        }


    }

    private Muse muse = null;
    private ConnectionListener connectionListener = null;
    private DataListener dataListener = null;
    private boolean dataTransmission = true;

    public List<String> getAvailableMuses() {
        MuseManager.refreshPairedMuses();
        List<Muse> pairedMuses = MuseManager.getPairedMuses();
        List<String> spinnerItems = new ArrayList<String>();
        for (Muse m : pairedMuses) {
            String dev_id = m.getName() + "-" + m.getMacAddress();
            Log.i("Muse Headband", dev_id);
            spinnerItems.add(dev_id);
        }

        return spinnerItems;
    }

    public List<Muse> getPairedMuses(){
        return MuseManager.getPairedMuses();
    }

    public void configure_library() {
        muse.registerConnectionListener(connectionListener);
        muse.registerDataListener(dataListener,
                MuseDataPacketType.CONCENTRATION);
        muse.registerDataListener(dataListener,
                MuseDataPacketType.HORSESHOE);
        muse.setPreset(MusePreset.PRESET_14);
        muse.enableDataTransmission(dataTransmission);
    }

    public Muse chooseMuse(int id){
        muse = getPairedMuses().get(id);
        return muse;
    }

}

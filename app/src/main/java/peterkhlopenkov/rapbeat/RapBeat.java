package peterkhlopenkov.rapbeat;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Binder;
import android.os.Debug;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.IOException;

public class RapBeat extends Service {

    private SoundPool[] soundpool;
    private int[][] id;
    public static int[] pattern;
    public static int[] sample;
    public static boolean[] mute;
    public static boolean play = false;
    public static int bpm;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Runnable Play_Beat = new Runnable() {
            @Override
            public void run() {

                //initialize array sizes
                mute = new boolean[getResources().getInteger(R.integer.numberOfInstruments)];
                soundpool = new SoundPool[getResources().getInteger(R.integer.numberOfInstruments)];
                pattern = new int[getResources().getInteger(R.integer.numberOfInstruments)];
                id = new int[getResources().getInteger(R.integer.numberOfInstruments)][];
                sample = new int[getResources().getInteger(R.integer.numberOfInstruments)];
                for (int i = 0; i < getResources().getInteger(R.integer.numberOfInstruments); i++) {
                    id[i] = new int[getResources().getIntArray(R.array.notesPerInstrument)[i]];
                }
                //retrieve saved configuration
                SharedPreferences sharedPref = getSharedPreferences("savedConfiguration", Context.MODE_PRIVATE);
                for (int i = 0; i < getResources().getInteger(R.integer.numberOfInstruments); i++) {
                    sample[i] = sharedPref.getInt("sample" + i, 2);
                    pattern[i] = sharedPref.getInt("pattern" + i, 2);
                    mute[i] = sharedPref.getBoolean("mute" + i, false);
                }
                bpm = sharedPref.getInt("bpm",80);

                sendUpdateUI();

                //initialize soundpools to manage each instrument
                for(int i = 0; i < getResources().getInteger(R.integer.numberOfInstruments); i++) {
                    soundpool[i] = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
                    Log.d("k",""+i);
                    loadInstrument(i);
                }
            }
        };

        //run service in a thread to maintain activity action
        Thread playbeats = new Thread(Play_Beat);
        playbeats.start();

        return START_STICKY;
    }

    public void loadInstrument(int instrument){
        try {
            for(int j = 0; j < getResources().getIntArray(R.array.notesPerInstrument)[instrument]; j++) {
                id[instrument][j] = soundpool[instrument].load(getAssets().openFd("instrument" + instrument + "/" + getAssets().list("instrument" + instrument)[sample[instrument]*getResources().getIntArray(R.array.notesPerInstrument)[instrument]+j]), 1);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendUpdateUI() {
        Intent updateUI = new Intent();
        updateUI.setAction("peterkhlopenkov.rapbeat.updateUI");
        updateUI.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        sendBroadcast(updateUI);
    }

    public void stopAllInstruments(){
        for (int i = 0; i < getResources().getInteger(R.integer.numberOfInstruments); i++){
            for(int j = 0; j < getResources().getIntArray(R.array.notesPerInstrument)[i]; j++)
                soundpool[i].stop(soundpool[i].play(id[i][j],1,1,1,0,1));
        }
    }

    // tick = 16th note
    public void player(int tick){

        if (tick == 64)
            tick = 0;

        //Timer in thread waiting to start next run of player
        final int finalTick = tick;
        Runnable counter = new Runnable() {
            @Override
            public void run() {
                double endTime = System.nanoTime() + 1500000000/(bpm/10);
                while (System.nanoTime() < endTime);
                if (play)
                    player(finalTick+1);
            }
        };
        Thread timer = new Thread(counter);
        timer.start();

        for(int i = 0; i<getResources().getInteger(R.integer.numberOfInstruments); i++){
            if(!mute[i]){
                if(getResources().getStringArray(getResources().obtainTypedArray(R.array.layoutArray).getResourceId(i,0))[pattern[i]].charAt(tick) != '0'){
                    soundpool[i].play(id[i][Character.getNumericValue(getResources().getStringArray(getResources().obtainTypedArray(R.array.layoutArray).getResourceId(i,0))[pattern[i]].charAt(tick))-1],1,1,1,0,1);
                }
            }
        }
    }

    @Override
    public void onDestroy() {

        play = false;

        for(int i = 0; i<getResources().getInteger(R.integer.numberOfInstruments); i++)
            soundpool[i].release();

        SharedPreferences.Editor editor = getSharedPreferences("savedConfiguration", Context.MODE_PRIVATE).edit();
        for(int i = 0; i<getResources().getInteger(R.integer.numberOfInstruments);i++){
            editor.putInt("sample"+i,sample[i]);
            editor.putInt("pattern"+i,pattern[i]);
            editor.putBoolean("mute"+i,mute[i]);
        }
        editor.putInt("bpm",bpm);
        editor.apply();

        super.onDestroy();
    }

    private final IBinder binder = new ServiceBinder();
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public class ServiceBinder extends Binder {
        RapBeat getService(){
            return RapBeat.this;
        }
    }
}
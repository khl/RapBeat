package peterkhlopenkov.rapbeat;


import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class Chords extends AppCompatActivity {

    private RapBeat mainService;
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mainService = ((RapBeat.ServiceBinder) service).getService();
        }
        @Override
        public void onServiceDisconnected(ComponentName componentName) {
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chords);
        bindService(new Intent(this, RapBeat.class), connection, 0);
        registerReceiver(receiver_updateUI, new IntentFilter("peterkhlopenkov.rapbeat.updateUI"));

        updateUI();
    }

    private final BroadcastReceiver receiver_updateUI = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateUI();
        }
    };

    public void updateUI(){
        ((RadioButton)((RadioGroup)findViewById(R.id.chords_samples)).getChildAt(RapBeat.sample[3])).toggle();
        ((RadioButton)((RadioGroup)findViewById(R.id.chords_patterns)).getChildAt(RapBeat.pattern[3])).toggle();
        update_chordsAlpha();
    }

    private void update_chordsAlpha(){
        if (!RapBeat.mute[3]) {
            (findViewById(R.id.Chords)).setAlpha(1);
        }
        else {
            (findViewById(R.id.Chords)).setAlpha(0.4f);
        }
    }

    public void sample_select(View view){
        RapBeat.sample[3] = ((RadioGroup)findViewById(R.id.chords_samples)).indexOfChild((findViewById(R.id.chords_samples)).findViewById(((RadioGroup)findViewById(R.id.chords_samples)).getCheckedRadioButtonId()));
        mainService.loadInstrument(3);
    }

    public void pattern_select(View view){
        RapBeat.pattern[3] = ((RadioGroup)findViewById(R.id.chords_patterns)).indexOfChild((findViewById(R.id.chords_patterns)).findViewById(((RadioGroup)findViewById(R.id.chords_patterns)).getCheckedRadioButtonId()));
    }

    public void mute_chords(View v){
        RapBeat.mute[3] = !RapBeat.mute[3];
        update_chordsAlpha();
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(receiver_updateUI);
        unbindService(connection);
        super.onDestroy();
    }
}



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

public class Bass extends AppCompatActivity {

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
        setContentView(R.layout.activity_bass);
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
        ((RadioButton)((RadioGroup)findViewById(R.id.bass_samples)).getChildAt(RapBeat.sample[4])).toggle();
        ((RadioButton)((RadioGroup)findViewById(R.id.bass_patterns)).getChildAt(RapBeat.pattern[4])).toggle();
        update_bassAlpha();
    }

    private void update_bassAlpha(){
        if (!RapBeat.mute[4]) {
            (findViewById(R.id.Bass)).setAlpha(1);
        }
        else {
            (findViewById(R.id.Bass)).setAlpha(0.4f);
        }
    }

    public void sample_select(View view){
        RapBeat.sample[4] = ((RadioGroup)findViewById(R.id.bass_samples)).indexOfChild((findViewById(R.id.bass_samples)).findViewById(((RadioGroup)findViewById(R.id.bass_samples)).getCheckedRadioButtonId()));
        mainService.loadInstrument(4);
    }

    public void pattern_select(View view){
        RapBeat.pattern[4] = ((RadioGroup)findViewById(R.id.bass_patterns)).indexOfChild((findViewById(R.id.bass_patterns)).findViewById(((RadioGroup)findViewById(R.id.bass_patterns)).getCheckedRadioButtonId()));
    }

    public void mute_bass(View v){
        RapBeat.mute[4] = !RapBeat.mute[4];
        update_bassAlpha();
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(receiver_updateUI);
        unbindService(connection);
        super.onDestroy();
    }
}



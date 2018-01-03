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

public class Snare extends AppCompatActivity {

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
        setContentView(R.layout.activity_snare);
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
        ((RadioButton)((RadioGroup)findViewById(R.id.snare_samples)).getChildAt(RapBeat.sample[1])).toggle();
        ((RadioButton)((RadioGroup)findViewById(R.id.snare_patterns)).getChildAt(RapBeat.pattern[1])).toggle();
        update_snareAlpha();
    }

    private void update_snareAlpha(){
        if (!RapBeat.mute[1]) {
            (findViewById(R.id.Snare)).setAlpha(1);
        }
        else {
            (findViewById(R.id.Snare)).setAlpha(0.4f);
        }
    }

    public void sample_select(View view){
        RapBeat.sample[1] = ((RadioGroup)findViewById(R.id.snare_samples)).indexOfChild((findViewById(R.id.snare_samples)).findViewById(((RadioGroup)findViewById(R.id.snare_samples)).getCheckedRadioButtonId()));
        mainService.loadInstrument(1);
    }

    public void pattern_select(View view){
        RapBeat.pattern[1] = ((RadioGroup)findViewById(R.id.snare_patterns)).indexOfChild((findViewById(R.id.snare_patterns)).findViewById(((RadioGroup)findViewById(R.id.snare_patterns)).getCheckedRadioButtonId()));
    }

    public void mute_snare(View v){
        RapBeat.mute[1] = !RapBeat.mute[1];
        update_snareAlpha();
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(receiver_updateUI);
        unbindService(connection);
        super.onDestroy();
    }
}



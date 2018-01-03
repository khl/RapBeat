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

public class Kick extends AppCompatActivity {

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
        setContentView(R.layout.activity_kick);
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
        ((RadioButton)((RadioGroup)findViewById(R.id.kick_samples)).getChildAt(RapBeat.sample[0])).toggle();
        ((RadioButton)((RadioGroup)findViewById(R.id.kick_patterns)).getChildAt(RapBeat.pattern[0])).toggle();
        update_kickAlpha();
    }

    private void update_kickAlpha(){
        if (!RapBeat.mute[0]) {
            (findViewById(R.id.Kick)).setAlpha(1);
        }
        else {
            (findViewById(R.id.Kick)).setAlpha(0.4f);
        }
    }

    public void sample_select(View view){
        RapBeat.sample[0] = ((RadioGroup)findViewById(R.id.kick_samples)).indexOfChild((findViewById(R.id.kick_samples)).findViewById(((RadioGroup)findViewById(R.id.kick_samples)).getCheckedRadioButtonId()));
        mainService.loadInstrument(0);
    }

    public void pattern_select(View view){
        RapBeat.pattern[0] = ((RadioGroup)findViewById(R.id.kick_patterns)).indexOfChild((findViewById(R.id.kick_patterns)).findViewById(((RadioGroup)findViewById(R.id.kick_patterns)).getCheckedRadioButtonId()));
    }

    public void mute_kick(View v){
        RapBeat.mute[0] = !RapBeat.mute[0];
        update_kickAlpha();
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(receiver_updateUI);
        unbindService(connection);
        super.onDestroy();
    }
}



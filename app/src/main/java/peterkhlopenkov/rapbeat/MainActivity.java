package peterkhlopenkov.rapbeat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import java.lang.Class;

public class MainActivity extends AppCompatActivity {

    int[] buttonIdStore = new int[] {R.id.Kick, R.id.Snare, R.id.Percussion, R.id.Chords, R.id.Bass, R.id.Fx};
    Class[] classStore = new Class[] {Kick.class, Snare.class, Percussion.class, Chords.class, Bass.class, Fx.class};
    Button[] instrumentButtons;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        instrumentButtons = new Button[getResources().getInteger(R.integer.numberOfInstruments)];

        for (int i = 0; i < getResources().getInteger(R.integer.numberOfInstruments); i++){
            final int iFinal = i;

            instrumentButtons[iFinal] = (Button)findViewById(buttonIdStore[iFinal]);

            instrumentButtons[iFinal].setOnLongClickListener(
                    new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View view) {

                            startActivity(new Intent(MainActivity.this, classStore[iFinal]));

                            return false;
                        }
                    }
            );
        }

        registerReceiver(receiver_updateUI, new IntentFilter("peterkhlopenkov.rapbeat.updateUI"));

        startService(new Intent(getApplicationContext(),RapBeat.class));
    }

    public void buttonClicked(View v){
        int instrumentNumber = 0;
        while (v.getId()!=buttonIdStore[instrumentNumber]){
            instrumentNumber++;
        }

        RapBeat.mute[instrumentNumber] = !RapBeat.mute[instrumentNumber];
        updateAlpha(instrumentNumber);
    }

    public void updateAlpha(int instrumentNumber){
        if(!RapBeat.mute[instrumentNumber])
            instrumentButtons[instrumentNumber].setAlpha(1);
        else
            instrumentButtons[instrumentNumber].setAlpha(0.4f);
    }

    private final BroadcastReceiver receiver_updateUI = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            update_UI();
        }
    };

    public void update_UI(){
        for(int i = 0; i < getResources().getInteger(R.integer.numberOfInstruments); i++)
           updateAlpha(i);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        update_UI();
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(receiver_updateUI);
        stopService(new Intent(this,RapBeat.class));
        super.onDestroy();
    }
}
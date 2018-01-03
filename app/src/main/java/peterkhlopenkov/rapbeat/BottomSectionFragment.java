package peterkhlopenkov.rapbeat;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import java.util.Random;

public class BottomSectionFragment extends Fragment{

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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.bottom_section_fragment, container, false);
        getActivity().getApplicationContext().bindService(new Intent(getActivity(), RapBeat.class), connection, 0);
        getActivity().getApplicationContext().registerReceiver(receiver_updateUI, new IntentFilter("peterkhlopenkov.rapbeat.updateUI"));

        view.findViewById(R.id.Tempo).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (RapBeat.bpm == 120)
                            RapBeat.bpm = 60;
                        else
                            RapBeat.bpm += 20;
                        setTempoText(view);
                    }
                }
        );

        view.findViewById(R.id.Main).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(!RapBeat.play) {
                            ((ImageButton) view.findViewById(R.id.Main)).setImageResource(R.drawable.stop);
                            RapBeat.play = true;
                            mainService.player(0);
                        }
                        else {
                            ((ImageButton) view.findViewById(R.id.Main)).setImageResource(R.drawable.triangle);
                            RapBeat.play = false;
                            mainService.stopAllInstruments();
                        }

                    }
                }
        );

        view.findViewById(R.id.Randomize).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainService.stopAllInstruments();
                for(int i = 0; i < getResources().getInteger(R.integer.numberOfInstruments); i++){
                    RapBeat.sample[i] = new Random().nextInt(5);
                    mainService.loadInstrument(i);
                    RapBeat.pattern[i] = new Random().nextInt(5);
                }
                int[] bpmSet = new int[]{60,80,100,120};
                RapBeat.bpm = bpmSet[new Random().nextInt(4)];
                mainService.sendUpdateUI();
            }
        });

        return view;
    }

    BroadcastReceiver receiver_updateUI = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateUI();
        }
    };

    private void updateUI(){
        if(RapBeat.play)
            ((ImageButton) getView().findViewById(R.id.Main)).setImageResource(R.drawable.stop);
        else
            ((ImageButton) getView().findViewById(R.id.Main)).setImageResource(R.drawable.triangle);

        setTempoText(getView());
    }

    public void setTempoText(View view){
        Button Tempo = (Button) view.findViewById(R.id.Tempo);
        Tempo.setText(RapBeat.bpm +" BPM");
    }

    @Override
    public void onDestroy() {
        getActivity().getApplicationContext().unregisterReceiver(receiver_updateUI);
        getActivity().getApplicationContext().unbindService(connection  );
        super.onDestroy();
    }

    @Override
    public void onResume() {
        updateUI();
        super.onResume();
    }
}

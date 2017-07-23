package cat.dev.fitness;

import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import java.util.Locale;

public class GoogleMapsFragment extends Fragment implements OnMapReadyCallback {

    private static final long REFRESH_RATE = 100L;

    private boolean pause;
    private long elapsedTime;
    private long startTime;
    private long timeOnPause;
    private long timeOnStart;
    private Handler mHandler;
    private TextView mActiveTimeView;

    private Runnable updateTimeThread = new Runnable() {
        @Override
        public void run() {
            elapsedTime = SystemClock.uptimeMillis() - timeOnStart;

            int elapse = (int) (timeOnPause + elapsedTime) / 1000;
            int minutes = elapse / 60;
            int seconds = elapse % 60;

            mActiveTimeView.setText(String.format(Locale.US, "%02d:%02d", minutes, seconds));

            mHandler.postDelayed(this, REFRESH_RATE);
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_google_maps, container, false);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mHandler = new Handler();

        ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map_view))
                .getMapAsync(this);

        mActiveTimeView = (TextView) getActivity().findViewById(R.id.active_time);

        final Button startPauseButton = (Button) getActivity().findViewById(R.id.start_pause_button);
        final Button stopButton = (Button) getActivity().findViewById(R.id.stop_button);

        startPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (startTime == 0L)
                    startTime = SystemClock.uptimeMillis();

                pause = !pause;

                startPauseButton.setText(pause ? R.string.action_pause : R.string.action_start);
                stopButton.setEnabled(pause);

                if (pause) {
                    timeOnStart = SystemClock.uptimeMillis();
                    mHandler.postDelayed(updateTimeThread, REFRESH_RATE);
                } else {
                    timeOnPause += elapsedTime;
                    mHandler.removeCallbacks(updateTimeThread);
                }
            }
        });

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mHandler.removeCallbacks(updateTimeThread);

                startPauseButton.setEnabled(false);

                int activeTime = (int) (timeOnPause + elapsedTime) / 1000;
                int totalTime = (int) (SystemClock.uptimeMillis() - startTime) / 1000;

                int minutes = activeTime / 60;
                int seconds = activeTime % 60;

                Log.d("GoogleMapsFrag", String.format(
                        Locale.US, "Active time: %02d:%02d", minutes, seconds));

                minutes = totalTime / 60;
                seconds = totalTime % 60;

                Log.d("GoogleMapsFrag", String.format(
                        Locale.US, "Total time: %02d:%02d", minutes, seconds));
            }
        });
    }
}

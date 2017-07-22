package cat.dev.fitness;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

public class GoogleMapsFragment extends Fragment implements OnMapReadyCallback {

    private boolean pause;

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

        ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map_view))
                .getMapAsync(this);

        final Button startPauseButton = (Button) getActivity().findViewById(R.id.start_pause_button);
        final Button stopButton = (Button) getActivity().findViewById(R.id.stop_button);

        startPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pause = !pause;
                startPauseButton.setText(pause ? R.string.action_pause : R.string.action_start);
                stopButton.setEnabled(pause);
            }
        });
    }
}

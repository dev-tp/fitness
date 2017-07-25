package cat.dev.fitness;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.Locale;

public class GoogleMapsFragment extends Fragment implements LocationListener, OnMapReadyCallback {

    private static final long REFRESH_RATE = 100L;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private static final String TAG = "GoogleMapsFrag";

    private boolean onStart;
    private long elapsedTime;
    private long startTime;
    private long timeOnPause;
    private long timeOnStart;
    private ArrayList<LatLng> coordinates;
    private GoogleMap mGoogleMap;
    private Handler mHandler;
    private LocationManager mLocationManager;
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

    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(getActivity(),
                    new String[] { Manifest.permission.ACCESS_FINE_LOCATION }, 1);

        } else if (mGoogleMap != null) {
            mGoogleMap.setMyLocationEnabled(true);

            if (mLocationManager != null) {
                mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                        5000L, 0.0f, this);

                Location location = mLocationManager.getLastKnownLocation(
                        LocationManager.GPS_PROVIDER);

                if (location != null) {
                    LatLng lastKnownLatLng = new LatLng(
                            location.getLatitude(), location.getLongitude());
                    mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastKnownLatLng, 15.0f));
                }
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_google_maps, container, false);
    }

    @Override
    public void onLocationChanged(Location location) {
        if (onStart) {
            LatLng coordinate = new LatLng(location.getLatitude(), location.getLongitude());
            coordinates.add(coordinate);

            mGoogleMap.clear();

            mGoogleMap.addPolyline(new PolylineOptions()
                    .addAll(coordinates)
                    .color(Color.BLUE)
                    .width(5.0f)
            );

            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(coordinate));
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.getUiSettings().setCompassEnabled(true);
        googleMap.getUiSettings().setMyLocationButtonEnabled(true);

        mGoogleMap = googleMap;

        enableMyLocation();
    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE)
            return;

        if (permissionWasGranted(permissions, grantResults,
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            enableMyLocation();
        } else {
            Log.e(TAG, "Permission was not granted.");
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        coordinates = new ArrayList<>();
        mHandler = new Handler();

        mLocationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);

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

                onStart = !onStart;

                startPauseButton.setText(onStart ? R.string.action_pause : R.string.action_start);
                stopButton.setEnabled(onStart);

                if (onStart) {
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

                Log.d(TAG, String.format(Locale.US, "Active time: %02d:%02d", minutes, seconds));

                minutes = totalTime / 60;
                seconds = totalTime % 60;

                Log.d(TAG, String.format(Locale.US, "Total time: %02d:%02d", minutes, seconds));
            }
        });
    }

    private static boolean permissionWasGranted(String[] permissions, int[] results, String permission) {
        for (int i = 0; i < permissions.length; i++)
            if (permission.equals(permissions[i]))
                return results[i] == PackageManager.PERMISSION_GRANTED;

        return false;
    }
}

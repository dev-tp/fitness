package cat.dev.fitness;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.SphericalUtil;

import java.util.ArrayList;
import java.util.Locale;

public class GoogleMapsFragment extends Fragment implements LocationListener, OnMapReadyCallback {

    private static final long REFRESH_RATE = 100L;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private static final String TAG = "GoogleMapsFrag";

    private boolean onStart;
    private double totalDistance;
    private double weight;
    private int burntCalories;
    private long elapsedTime;
    private long startTime;
    private long timeOnPause;
    private long timeOnStart;
    private ArrayList<LatLng> coordinates;
    private DatabaseHelper mDatabaseHelper;
    private GoogleMap mGoogleMap;
    private Handler mHandler;
    private LocationManager mLocationManager;
    private TextView mActiveTimeView;
    private TextView mAveragePaceView;
    private TextView mBurntCaloriesView;
    private TextView mTotalDistanceView;

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

            int size = coordinates.size();

            if (size > 1) {
                LatLng previous = coordinates.get(size - 2);

                mGoogleMap.addPolyline(new PolylineOptions()
                        .add(previous, coordinate)
                        .color(Color.BLUE)
                        .width(5.0f)
                );

                mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(coordinate));

                totalDistance += SphericalUtil.computeDistanceBetween(previous, coordinate);

                // Convert metres to miles
                double displayTotalDistance = totalDistance * 0.000621371;
                mTotalDistanceView.setText(String.format(Locale.US, "%.2f mi", displayTotalDistance));

                int activeTime = (int) (timeOnPause + elapsedTime) / 1000 / 60;
                double averagePace = activeTime / displayTotalDistance;
                int minutes = (int) averagePace;
                int seconds = (int) (averagePace % 1 * 60);

                mAveragePaceView.setText(String.format(Locale.US, "%d:%02d/mi", minutes, seconds));

                // http://runnersworld.com/tools/calories-burned-calculator
                // http://runnersworld.com/sites/runnersworld.com/files/custom-js/1263036-f1703073e5ee4e4ce5923dc03ff02975.js
                burntCalories = (int) (totalDistance / 1000 * weight * 1.036);
                mBurntCaloriesView.setText(String.format(Locale.US, "%d cal", burntCalories));
            }
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

        mDatabaseHelper = new DatabaseHelper(getContext());
        SQLiteDatabase database = mDatabaseHelper.getReadableDatabase();
        Cursor cursor = DatabaseHelper.getAllEntries(database, DatabaseHelper.User.TABLE_NAME);

        cursor.moveToNext();

        weight = cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.User.COLUMN_NAME_WEIGHT));
        weight *= 0.453592; // Convert pounds to kilograms

        cursor.close();
        mDatabaseHelper.close();

        coordinates = new ArrayList<>();
        mHandler = new Handler();

        mLocationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);

        ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map_view))
                .getMapAsync(this);

        mActiveTimeView = (TextView) getActivity().findViewById(R.id.active_time);
        mAveragePaceView = (TextView) getActivity().findViewById(R.id.average_pace);
        mBurntCaloriesView = (TextView) getActivity().findViewById(R.id.burnt_calories);
        mTotalDistanceView = (TextView) getActivity().findViewById(R.id.total_miles);

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
                    mActiveTimeView.setAnimation(null);
                } else {
                    timeOnPause += elapsedTime;
                    mHandler.removeCallbacks(updateTimeThread);

                    Animation animation = new AlphaAnimation(0.0f, 1.0f);
                    animation.setDuration(500L);
                    animation.setRepeatCount(Animation.INFINITE);
                    animation.setRepeatMode(Animation.REVERSE);
                    animation.setStartOffset(20L);

                    mActiveTimeView.setAnimation(animation);
                }
            }
        });

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onStart = false;

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

                SQLiteDatabase database = mDatabaseHelper.getWritableDatabase();

                ContentValues values = new ContentValues();
                values.put(DatabaseHelper.Workout.COLUMN_NAME_ACTIVE_TIME, activeTime);
                values.put(DatabaseHelper.Workout.COLUMN_NAME_BURNT_CALORIES, burntCalories);
                values.put(DatabaseHelper.Workout.COLUMN_NAME_DISTANCE, totalDistance);
                values.put(DatabaseHelper.Workout.COLUMN_NAME_TOTAL_TIME, totalTime);

                database.insert(DatabaseHelper.Workout.TABLE_NAME, null, values);

                mDatabaseHelper.close();
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

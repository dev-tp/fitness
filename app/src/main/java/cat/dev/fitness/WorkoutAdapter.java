package cat.dev.fitness;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;

import java.util.Locale;

import static cat.dev.fitness.DatabaseHelper.Workout.*;

class WorkoutAdapter extends RecyclerView.Adapter<WorkoutAdapter.ViewHolder> {

    private Cursor mCursor;
    private DatabaseHelper mDatabaseHelper;
    private FragmentActivity mActivity;

    WorkoutAdapter(FragmentActivity activity) {
        mDatabaseHelper = new DatabaseHelper(activity);
        updateCursor();
        mActivity = activity;
    }

    void deleteEntry(long id) {
        SQLiteDatabase database = mDatabaseHelper.getWritableDatabase();

        database.delete(DatabaseHelper.Coordinates.TABLE_NAME,
                DatabaseHelper.Coordinates.COLUMN_NAME_WORKOUT_ID + " = " + id, null);
        database.delete(TABLE_NAME, _ID + " = " + id, null);

        updateCursor();
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mCursor.getCount();
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View workoutSummary = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.item_workout_summary, parent, false);

        return new ViewHolder(workoutSummary);
    }

    void onDestroy() {
        if (mCursor != null)
            mCursor.close();

        if (mDatabaseHelper != null)
            mDatabaseHelper.close();
    }

    private void updateCursor() {
        String query = "SELECT * FROM " + TABLE_NAME + " ORDER BY " + _ID +  " DESC";
        mCursor = mDatabaseHelper.getReadableDatabase().rawQuery(query, null);
    }

    class ViewHolder extends RecyclerView.ViewHolder implements OnClickListener, OnMapReadyCallback {

        private long id;
        private MapView mMapView;
        private TextView mActiveTimeTextView;
        private TextView mDateTextView;
        private TextView mSummaryTextView;

        ViewHolder(View view) {
            super(view);

            mMapView = (MapView) view.findViewById(R.id.map_view);
            mActiveTimeTextView = (TextView) view.findViewById(R.id.active_time);
            mDateTextView = (TextView) view.findViewById(R.id.date);
            mSummaryTextView = (TextView) view.findViewById(R.id.summary);

            if (mMapView != null) {
                mMapView.onCreate(null);
                mMapView.getMapAsync(this);
            }

            view.setOnClickListener(this);
        }

        void bind(int position) {
            mCursor.moveToPosition(position);

            id = mCursor.getLong(mCursor.getColumnIndex(_ID));

            int activeTime = mCursor.getInt(mCursor.getColumnIndex(COLUMN_NAME_ACTIVE_TIME)) / 60;
            int calories = mCursor.getInt(mCursor.getColumnIndex(COLUMN_NAME_BURNT_CALORIES));
            int distance = (int) mCursor.getDouble(mCursor.getColumnIndex(COLUMN_NAME_DISTANCE));

            mActiveTimeTextView.setText(String.format(Locale.US, "%d min workout", activeTime));
            mDateTextView.setText(mCursor.getString(mCursor.getColumnIndex(COLUMN_NAME_DATE)));
            mSummaryTextView.setText(String.format(Locale.US, "%d miles - %d calories", distance, calories));

            itemView.setTag(id);
        }

        private LatLng getFirstCoordinate() {
            LatLng coordinate;

            String query = String.format(Locale.US, "SELECT %s, %s FROM %s WHERE %s = %d",
                    DatabaseHelper.Coordinates.COLUMN_NAME_LATITUDE,
                    DatabaseHelper.Coordinates.COLUMN_NAME_LONGITUDE,
                    DatabaseHelper.Coordinates.TABLE_NAME,
                    DatabaseHelper.Coordinates.COLUMN_NAME_WORKOUT_ID,
                    id
            );

            Cursor cursor = mDatabaseHelper.getReadableDatabase().rawQuery(query, null);
            cursor.moveToNext();

            coordinate = new LatLng(cursor.getDouble(0), cursor.getDouble(1));

            cursor.close();

            return coordinate;
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(mActivity, WorkoutSummaryActivity.class);
            intent.putExtra("workout_id", id);
            mActivity.startActivity(intent);
        }

        @Override
        public void onMapReady(GoogleMap googleMap) {
            MapsInitializer.initialize(mActivity);

            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(getFirstCoordinate(), 14.0f));

            mMapView.onResume();
        }
    }
}

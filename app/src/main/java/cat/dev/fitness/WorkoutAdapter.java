package cat.dev.fitness;

import android.content.Intent;
import android.database.Cursor;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
        mDatabaseHelper.getWritableDatabase().delete(TABLE_NAME, _ID + " = " + id, null);
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

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private long id;
        private TextView mActiveTimeTextView;
        private TextView mSummaryTextView;

        ViewHolder(View view) {
            super(view);

            mActiveTimeTextView = (TextView) view.findViewById(R.id.active_time);
            mSummaryTextView = (TextView) view.findViewById(R.id.summary);

            view.setOnClickListener(this);
        }

        void bind(int position) {
            mCursor.moveToPosition(position);

            id = mCursor.getLong(mCursor.getColumnIndex(_ID));

            int activeTime = mCursor.getInt(mCursor.getColumnIndex(COLUMN_NAME_ACTIVE_TIME)) / 60;
            int calories = mCursor.getInt(mCursor.getColumnIndex(COLUMN_NAME_BURNT_CALORIES));
            int distance = (int) mCursor.getDouble(mCursor.getColumnIndex(COLUMN_NAME_DISTANCE));

            mActiveTimeTextView.setText(String.format(Locale.US, "%d min workout", activeTime));
            mSummaryTextView.setText(String.format(Locale.US, "%d miles - %d calories", distance, calories));

            itemView.setTag(id);
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(mActivity, WorkoutSummaryActivity.class);
            intent.putExtra("workout_id", id);
            mActivity.startActivity(intent);
        }
    }
}

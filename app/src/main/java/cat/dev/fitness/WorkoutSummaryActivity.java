package cat.dev.fitness;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.util.Locale;

public class WorkoutSummaryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_summary);
        setTitle("Workout Overview");

        String query = "SELECT * FROM " + DatabaseHelper.Workout.TABLE_NAME + " WHERE _id = ?";
        long workoutId = getIntent().getLongExtra("workout_id", -1L);

        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        SQLiteDatabase database = databaseHelper.getReadableDatabase();
        Cursor cursor = database.rawQuery(query, new String[] { "" + workoutId });

        if (cursor.moveToNext()) {
            TextView activeTimeTextView = (TextView) findViewById(R.id.active_time);
            TextView averagePaceTextView = (TextView) findViewById(R.id.average_pace);
            TextView caloriesTextView = (TextView) findViewById(R.id.calories);
            TextView distanceTextView = (TextView) findViewById(R.id.distance);
            TextView stepsTextView = (TextView) findViewById(R.id.steps);
            TextView totalTimeTextView = (TextView) findViewById(R.id.total_time);

            double distance = cursor.getDouble(cursor.getColumnIndex(
                    DatabaseHelper.Workout.COLUMN_NAME_DISTANCE));
            int activeTime = cursor.getInt(cursor.getColumnIndex(
                    DatabaseHelper.Workout.COLUMN_NAME_ACTIVE_TIME));
            int calories = cursor.getInt(cursor.getColumnIndex(
                    DatabaseHelper.Workout.COLUMN_NAME_BURNT_CALORIES));
            int steps = cursor.getInt(cursor.getColumnIndex(
                    DatabaseHelper.Workout.COLUMN_NAME_STEPS));
            int totalTime = cursor.getInt(cursor.getColumnIndex(
                    DatabaseHelper.Workout.COLUMN_NAME_TOTAL_TIME));

            int minutes = activeTime / 60;
            int seconds = activeTime % 60;

            activeTimeTextView.setText(String.format(Locale.US, "%dm %ds", minutes, seconds));

            double averagePace = (activeTime / 60) / distance;

            minutes = (int) averagePace;
            seconds = (int) (averagePace % 1 * 60);

            averagePaceTextView.setText(String.format(Locale.US, "%d:%02d/mi", minutes, seconds));
            caloriesTextView.setText(String.format(Locale.US, "%d", calories));
            distanceTextView.setText(String.format(Locale.US, "%.2f", distance));
            stepsTextView.setText(String.format(Locale.US, "%d", steps));

            minutes = totalTime / 60;
            seconds = totalTime % 60;

            totalTimeTextView.setText(String.format(Locale.US, "%dm %ds", minutes, seconds));
        }

        cursor.close();
        databaseHelper.close();
    }
}

package cat.dev.fitness;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SplashScreenActivity extends AppCompatActivity {

    public boolean getUser() {
        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        SQLiteDatabase database = databaseHelper.getReadableDatabase();
        Cursor cursor = DatabaseHelper.getAllEntries(database, DatabaseHelper.User.TABLE_NAME);

        // If the method moveToNext() returns true, that implies there exists
        // an entry with the user's information.
        boolean userExists = cursor.moveToNext();

        cursor.close();
        databaseHelper.close();

        return userExists;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    sleep(1000);

                    Class activity = getUser() ? MainActivity.class : ProfileActivity.class;
                    startActivity(new Intent(SplashScreenActivity.this, activity));

                } catch (InterruptedException exception) {
                    exception.printStackTrace();
                }
            }
        };

        thread.start();
    }
}

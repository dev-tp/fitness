package cat.dev.fitness;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SplashScreenActivity extends AppCompatActivity {

    public boolean getUser() {
        // TODO If the user's name exists on the database, return true; otherwise false.
        return false;
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

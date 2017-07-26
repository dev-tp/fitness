package cat.dev.fitness;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SplashScreenActivity extends AppCompatActivity {

    public boolean getUser() {
        // TODO If the user's name exists on the database, return true; otherwise false.
        return true;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    sleep(1000);

                    if (getUser()) {
                        startActivity(new Intent(SplashScreenActivity.this, MainActivity.class));
                    } else {
                        // startActivity(new Intent(SplashScreenActivity.this, Profile.class));
                    }

                } catch (InterruptedException exception) {
                    exception.printStackTrace();
                }
            }
        };

        thread.start();
    }
}

package cat.dev.fitness;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        final EditText ageEditView = (EditText) findViewById(R.id.age);
        final EditText emailEditView = (EditText) findViewById(R.id.email);
        final EditText nameEditView = (EditText) findViewById(R.id.name);
        final EditText weightEditView = (EditText) findViewById(R.id.weight);
        final Spinner genderSpinner = (Spinner) findViewById(R.id.sex);

        Button submitButton = (Button) findViewById(R.id.submit_button);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = nameEditView.getText().toString();
                String email = emailEditView.getText().toString();
                int age = 0;
                double weight = 0.0;

                if (name.equals("")) {
                    nameEditView.setError("Name is required!");
                    nameEditView.requestFocus();
                }

                if (email.equals("")) {
                    emailEditView.setError("Email is required!");
                    emailEditView.requestFocus();
                }

                if (ageEditView.getText().toString().equals("")) {
                    ageEditView.setError("Age is required!");
                    ageEditView.requestFocus();
                } else {
                    age = Integer.parseInt(ageEditView.getText().toString());
                }

                if (weightEditView.getText().toString().equals("")) {
                    weightEditView.setError("Weight is required!");
                    weightEditView.requestFocus();
                    return;
                } else {
                    weight = Double.parseDouble(weightEditView.getText().toString());

                    if (weight < 50) {
                        weightEditView.setError("Your weight cannot be less than 50 pounds!");
                        weightEditView.requestFocus();
                        return;
                    }
                }

                boolean sex = genderSpinner.getSelectedItem().toString().equals("Female");

                // TODO Parsed data should be saved and retrieved from database.
                Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
                intent.putExtra("age", age);
                intent.putExtra("email", email);
                intent.putExtra("name", name);
                intent.putExtra("sex", sex); // female: true, male: false
                intent.putExtra("weight", weight);

                startActivity(intent);
            }
        });
    }
}

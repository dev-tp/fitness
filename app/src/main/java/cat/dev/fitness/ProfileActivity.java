package cat.dev.fitness;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.Locale;

public class ProfileActivity extends AppCompatActivity implements BirthdayDialog.OnCloseDialog {

    private EditText mBirthdayTextView;

    @Override
    public void onClose(int day, int month, int year) {
        mBirthdayTextView.setText(String.format(Locale.US, "%02d/%02d/%02d", month + 1, day, year));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        final EditText emailEditView = (EditText) findViewById(R.id.email);
        final EditText nameEditView = (EditText) findViewById(R.id.name);
        final EditText weightEditView = (EditText) findViewById(R.id.weight);
        final Spinner genderSpinner = (Spinner) findViewById(R.id.sex);

        mBirthdayTextView = (EditText) findViewById(R.id.birthday);
        mBirthdayTextView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus)
                    showBirthdayDialog();
            }
        });

        mBirthdayTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBirthdayDialog();
            }
        });

        Button submitButton = (Button) findViewById(R.id.submit_button);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = nameEditView.getText().toString();
                String email = emailEditView.getText().toString();
                String birthday = mBirthdayTextView.getText().toString();
                double weight;

                if (name.equals("")) {
                    nameEditView.setError("Name is required!");
                    nameEditView.requestFocus();
                }

                if (email.equals("")) {
                    emailEditView.setError("Email is required!");
                    emailEditView.requestFocus();
                }

                if (birthday.equals(""))
                    mBirthdayTextView.setError("Birthday is required!");

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

                boolean sex = genderSpinner.getSelectedItemPosition() == 0;

                // TODO Parsed data should be saved and retrieved from database.
                Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
                intent.putExtra("birthday", birthday);
                intent.putExtra("email", email);
                intent.putExtra("name", name);
                intent.putExtra("sex", sex); // female: true, male: false
                intent.putExtra("weight", weight);

                startActivity(intent);
            }
        });
    }

    private void showBirthdayDialog() {
        BirthdayDialog birthdayDialog = new BirthdayDialog();

        Bundle bundle = new Bundle();
        bundle.putString("date", mBirthdayTextView.getText().toString());

        birthdayDialog.setArguments(bundle);

        birthdayDialog.show(getFragmentManager(), "birthdayDialog");
    }
}

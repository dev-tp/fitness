package cat.dev.fitness;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.DatePicker;

public class BirthdayDialog extends DialogFragment implements OnClickListener {

    private DatePicker mBirthdayPicker;

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.done_button) {
            int day = mBirthdayPicker.getDayOfMonth();
            int month = mBirthdayPicker.getMonth();
            int year = mBirthdayPicker.getYear();

            ProfileActivity activity = (ProfileActivity) getActivity();
            activity.onClose(day, month, year);
        }

        dismiss();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_birthday_selector, container, false);
        view.findViewById(R.id.cancel_button).setOnClickListener(this);
        view.findViewById(R.id.done_button).setOnClickListener(this);

        mBirthdayPicker = (DatePicker) view.findViewById(R.id.birthday);

        String date = getArguments().getString("date");

        if (date != null && !date.equals("")) {
            String[] tokens = date.split("/");

            int day = Integer.parseInt(tokens[1]);
            int month = Integer.parseInt(tokens[0]) - 1;
            int year = Integer.parseInt(tokens[2]);

            mBirthdayPicker.updateDate(year, month, day);
        }

        return view;
    }

    interface OnCloseDialog {
        void onClose(int day, int month, int year);
    }
}

package dmitriy.com.travelsearch.datepicker;


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.widget.DatePicker;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;

public class DateFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    public interface DateChangeListener {
        void OnDateChanged(int y, int m, int d);
    }

    DateChangeListener mDateChangeListener = null;

    public void setListener(DateChangeListener listener) {
        mDateChangeListener = listener;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    @Override
    public void onDateSet(DatePicker datePicker, int y, int m, int d) {
        if (mDateChangeListener != null)
            mDateChangeListener.OnDateChanged(y, m, d);
    }
}


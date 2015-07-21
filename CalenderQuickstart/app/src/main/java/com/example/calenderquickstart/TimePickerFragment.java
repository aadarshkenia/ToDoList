
package com.example.calenderquickstart;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.calenderquickstart.MyUtility;
import com.example.calenderquickstart.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

/**
 * Created by aadarsh-ubuntu on 7/18/15.
 */
public class TimePickerFragment extends DialogFragment
        implements TimePickerDialog.OnTimeSetListener {

    public Calendar event_time=null;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current time as the default values for the picker
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        // Create a new instance of TimePickerDialog and return it
        return new TimePickerDialog(getActivity(), this, hour, minute,
                DateFormat.is24HourFormat(getActivity()));
    }

    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        // Do something with the time chosen by the user
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        int button_id = this.getArguments().getInt("id");
        event_time = new GregorianCalendar(year, month, day, hourOfDay, minute);
        updateTimeDisplay(button_id);
    }

    public void updateTimeDisplay(int button_id)
    {
        String prefix="";
        if(button_id==R.id.text_event_start || button_id==R.id.text_quickevent_start) {
            prefix= MyUtility.formatPrefix("START:");
        }
        if(button_id==R.id.text_event_end) {
            prefix=MyUtility.formatPrefix("END:");
        }
        TextView textView = (TextView)getActivity().findViewById(button_id);
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.US);
        String display = prefix+sdf.format(event_time.getTime());

        textView.setText(display);

    }
}

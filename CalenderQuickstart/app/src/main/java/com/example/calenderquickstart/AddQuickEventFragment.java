package com.example.calenderquickstart;

import android.app.Activity;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.Calendar;


public class AddQuickEventFragment extends Fragment implements View.OnClickListener {

    MyQuickEvent quick_event=null;
    TimePickerFragment startFragment = null;
    EventReaderDb dbhelper=null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_add_quick_event, container, false);

        //Listeners for time start, end, submit buttons
        TextView t = (TextView)rootView.findViewById(R.id.text_quickevent_start);
        t.setOnClickListener(this);
        Button b = (Button)rootView.findViewById(R.id.submit_quick_event);
        b.setOnClickListener(this);

        return rootView;
    }

    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.text_quickevent_start:
            {
                showTimePickerDialog(v);
                break;
            }

            case R.id.submit_quick_event:
            {
                addQuickEvent(v);
                break;
            }
        }
    }

    //Method to show time picker dialog
    public void showTimePickerDialog(View view)
    {
        int button_id = view.getId();
        Bundle args = new Bundle();
        args.putInt("id",button_id);
        startFragment = new TimePickerFragment();
        startFragment.setArguments(args);
        startFragment.show(getActivity().getFragmentManager(), "timePicker");
    }

    public void addQuickEvent(View v){
    //Construct quickevent object here
    try {
        EditText textbox = null;
        CheckBox check = null;
        Spinner spinner = null;

        //Local variables for constructor
        String title=null;
        int priority=0;
        Calendar event_d=null, start_t=null;
        boolean alarm_rem = false;


        event_d = Calendar.getInstance();
        //Clearing hour, min, sec and millisec
        event_d = MyUtility.clearHMS(event_d);

        FragmentActivity current_act = getActivity();
        textbox = (EditText) current_act.findViewById(R.id.edit_quicktitle);
        title = textbox.getText().toString();

        if(startFragment!=null)
            start_t = startFragment.event_time;

        spinner = (Spinner) current_act.findViewById(R.id.spinner_quickpriority);
        String temp = spinner.getSelectedItem().toString().toUpperCase();
        priority = MyUtility.getPriorityInt(temp);

        check = (CheckBox) current_act.findViewById(R.id.checkbox_quickalarm_reminder);
        if (check.isChecked())
            alarm_rem = true;

        quick_event = new MyQuickEvent(priority, title, start_t, event_d, alarm_rem);

    }
    catch (Exception e)
    {
        System.out.println(e.getMessage());
        e.printStackTrace();
    }

    //Pass quickevent object to next function
     addQuickEventToDB(quick_event);

    }

    public void addQuickEventToDB(MyQuickEvent quick_event)
    {
        if(dbhelper==null)
            dbhelper = new EventReaderDb(getActivity());

        SQLiteDatabase db = dbhelper.getWritableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(EventReaderContract.EventEntry.COLUMN_NAME_TITLE, quick_event.title);
        values.put(EventReaderContract.EventEntry.COLUMN_NAME_PRIORITY, quick_event.priority);

        long date_in_milli = quick_event.event_date.getTimeInMillis();
        values.put(EventReaderContract.EventEntry.COLUMN_NAME_DATE, date_in_milli);

        long time_in_milli=0;
        if(quick_event.start_time!=null)
            time_in_milli = quick_event.start_time.getTimeInMillis();
        values.put(EventReaderContract.EventEntry.COLUMN_NAME_START, time_in_milli);

        int alarm_db_val=0;
        if(quick_event.alarmReminder)
            alarm_db_val=1;
        values.put(EventReaderContract.EventEntry.COLUMN_NAME_ALARM, alarm_db_val);

        // Insert the new row, returning the primary key value of the new row
        long newRowId;
        newRowId = db.insert(
                EventReaderContract.EventEntry.TABLE_NAME,
                EventReaderContract.EventEntry.COLUMN_NAME_NULLABLE,
                values);

        //IF START TIME AND/OR ALARM
        System.out.println("DB WORK DONE");
        //IF NO START TIME
    }


}

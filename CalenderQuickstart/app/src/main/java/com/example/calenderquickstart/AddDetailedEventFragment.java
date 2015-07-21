package com.example.calenderquickstart;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.format.DateFormat;
import java.text.SimpleDateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.widget.Button;
import android.widget.TimePicker;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.CheckBox;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

public class AddDetailedEventFragment extends Fragment implements View.OnClickListener{

    MyDetailedEvent detailed_event=null;
    TimePickerFragment startFragment = null;
    TimePickerFragment endFragment = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_add_detailed_event, container, false);

        //Listeners for time start, end, submit buttons
        TextView t = (TextView)rootView.findViewById(R.id.text_event_start);
        t.setOnClickListener(this);
        t = (TextView)rootView.findViewById(R.id.text_event_end);
        t.setOnClickListener(this);
        Button b = (Button)rootView.findViewById(R.id.submit_detailed_event);
        b.setOnClickListener(this);

        return rootView;
    }

    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.text_event_start:
            {
                showTimePickerDialog(v);
                break;
            }
            case R.id.text_event_end:
            {
                showTimePickerDialog(v);
                break;
            }
            case R.id.submit_detailed_event:
            {
                addDetailedEvent();
                break;
            }
        }
    }

    //Method to show time picker dialog
    public void showTimePickerDialog(View view)
    {
        TimePickerFragment newFragment = null;
        int button_id = view.getId();
        Bundle args = new Bundle();
        args.putInt("id",button_id);

        if(button_id == R.id.text_event_start){
            startFragment = new TimePickerFragment();
            newFragment = startFragment;
        }

        if(button_id == R.id.text_event_end){
            endFragment = new TimePickerFragment();
            newFragment = endFragment;
        }

        newFragment.setArguments(args);
        newFragment.show(getActivity().getFragmentManager(), "timePicker");
    }


    //SUBMIT BUTTON ACTION

    public void addDetailedEvent()
    {
        addDetailedEventToCalendar();
        //ALSO ADD EVENT TO DATABASE
    }

    public void addDetailedEventToCalendar()
    {
        try {
            EditText textbox = null;
            CheckBox check = null;
            Spinner spinner = null;

            //Local variables for constructor
            String title, location, description;
            int priority;
            Calendar event_d=null;
            Calendar start_t=null, end_t=null;
            boolean email_rem = false, alarm_rem = false;


            event_d = Calendar.getInstance();
            FragmentActivity current_act = getActivity();
            textbox = (EditText) current_act.findViewById(R.id.edit_title);
            title = textbox.getText().toString();
            textbox = (EditText) current_act.findViewById(R.id.edit_location);
            location = textbox.getText().toString();
            textbox = (EditText) current_act.findViewById(R.id.edit_description);
            description = textbox.getText().toString();

            if(startFragment!=null)
                start_t = startFragment.event_time;
            if(endFragment!=null)
                end_t = endFragment.event_time;

            spinner = (Spinner) current_act.findViewById(R.id.spinner_priority);
            String temp = spinner.getSelectedItem().toString().toUpperCase();
            priority = MyUtility.getPriorityInt(temp);

            check = (CheckBox) current_act.findViewById(R.id.checkbox_email_reminder);
            if (check.isChecked())
                email_rem = true;
            check = (CheckBox) current_act.findViewById(R.id.checkbox_alarm_reminder);
            if (check.isChecked())
                alarm_rem = true;

            detailed_event = new MyDetailedEvent(priority, title, location, description, start_t, end_t, event_d, email_rem, alarm_rem);


            //Pass detailed event instance to AddEventAsyncTask
            new AddEventAsyncTask(detailed_event).execute();
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

}

package com.example.calenderquickstart;

import android.net.NetworkInfo;
import android.os.AsyncTask;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;


/**
 * Created by aadarsh-ubuntu on 7/11/15.
 */
public class AddEventAsyncTask extends AsyncTask<Void, Void, Void> {
    //NOTE: THIS IS NOT GOOGLE CALENDAR EVENT CLASS, ITS MY OWN DEFINED CLASS
    private MyEvent event;

    AddEventAsyncTask(MyEvent new_event)
    {
        if(new_event instanceof MyDetailedEvent)
            event = (MyDetailedEvent)new_event;
        if(new_event instanceof MyQuickEvent)
            event = (MyQuickEvent)new_event;
    }
    @Override
    protected Void doInBackground(Void... params)
    {
        if(event instanceof MyDetailedEvent)
            addDetailedEvent();

        return null;
    }

    //Extract details and add to Google Calendar
    private void addDetailedEvent()
    {
        Event cal_event = new Event();

        cal_event.setSummary(event.title);
        cal_event.setLocation(((MyDetailedEvent) event).location);
        cal_event.setDescription(((MyDetailedEvent) event).description);

        TimeZone tz = TimeZone.getDefault();

        if(((MyDetailedEvent) event).start_time!=null){
            DateTime startDateTime = new DateTime(((MyDetailedEvent) event).start_time.getTime(), tz);
            EventDateTime start = new EventDateTime();
            start.setDateTime(startDateTime);
            cal_event.setStart(start);
        }
        else
        {
            Calendar c = Calendar.getInstance();
            c=MyUtility.clearHMS(c);
            //Set today's date as default start date
            cal_event.setStart(new EventDateTime().setDateTime(new DateTime(c.getTime(), tz)));
        }

        if(((MyDetailedEvent) event).end_time!=null) {
            DateTime endDateTime = new DateTime(((MyDetailedEvent) event).end_time.getTime(), tz);
            EventDateTime end = new EventDateTime();
            end.setDateTime(endDateTime);
            cal_event.setEnd(end);
        }
        //Add to Primary Google Calendar
        addToPrimaryGoogleCal(cal_event);

    }

    //Extract details and add to Google Calendar / Local Database / Spreadsheet: DECIDE


    private void addToPrimaryGoogleCal(Event cal_event)
    {
        com.google.api.services.calendar.Calendar mService = SingletonUserSettings.getInstance().getServiceObject();
        try {
            mService.events().insert("primary", cal_event).execute();
        }
        catch (IOException e)
        {
            System.err.println("Error while inserting event to primary calendar.");
            System.err.print(e.getMessage());
        }
    }

    public void printValues()
    {
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy hh:mm a", Locale.US);
        System.out.println("IN ADD EVENT ASYNC TASK PRINTING VALS:");
        System.out.println(sdf.format(((MyDetailedEvent)event).start_time.getTime()));
        System.out.println(sdf.format(((MyDetailedEvent)event).end_time.getTime()));
    }


    /*
    @Override
    protected Void doInBackground(Void... params) {

        Event event = new Event();
        event.setSummary("Backlot Meeting");
        event.setLocation("CEWIT, Stony Brook");
        event.setDescription("BackLot Project Weekly Meeting");

        DateTime startDateTime = new DateTime("2015-07-13T10:30:00-05:00");
        EventDateTime start = new EventDateTime();
        start.setDateTime(startDateTime);
        event.setStart(start);

        DateTime endDateTime = new DateTime("2015-07-13T14:00:00-05:00");
        EventDateTime end = new EventDateTime();
        end.setDateTime(endDateTime);
        event.setEnd(end);

        try {
            mActivity.mService.events().insert("primary", event).execute();
        }
        catch (IOException e)
        {
            System.err.println("Error while inserting default event");
            System.err.print(e.getMessage());
        }
        return null;
    }
    */

}

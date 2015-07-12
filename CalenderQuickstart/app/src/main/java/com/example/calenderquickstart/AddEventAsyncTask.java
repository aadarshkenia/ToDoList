package com.example.calenderquickstart;

import android.os.AsyncTask;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;

import java.io.IOException;

/**
 * Created by aadarsh-ubuntu on 7/11/15.
 */
public class AddEventAsyncTask extends AsyncTask<Void, Void, Void> {
    private MainActivity mActivity;

    /**
     * Constructor.
     * @param activity MainActivity that spawned this task.
     */
    AddEventAsyncTask(MainActivity activity) {
        this.mActivity = activity;
    }

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


}

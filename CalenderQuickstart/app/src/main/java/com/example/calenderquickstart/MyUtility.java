package com.example.calenderquickstart;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;

/**
 * Created by aadarsh-ubuntu on 7/15/15.
 */


//ALL UTILITY FUNCTIONS HERE...GOOD DESIGN PRACTICE ROCKS :)


public class MyUtility {

    //Event types
    static final int event_local = 1;
    static final int event_calendar = 2;
    static final int event_evernote = 3;

    private static HashMap<String, Integer> priority_mappings = new HashMap<String, Integer>();
    static {
        priority_mappings.put("HIGH", 1);
        priority_mappings.put("MEDIUM", 2);
        priority_mappings.put("LOW", 3);
    }
    public static String formatPrefix(String str)
    {
        return String.format("%1$-10s",str);
    }
    public static int getPriorityInt(String priority){
        return (Integer)priority_mappings.get(priority.toUpperCase());
    }


    //Convert DateTime object for Calendar events to String representation for display
    public static String getDateTimeString(DateTime date)
    {
        long val = date.getValue();
        Date d = new Date(val);
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
        return sdf.format(d).toString();
    }

    public static Calendar clearHMS(Calendar c){
        c.clear(Calendar.HOUR);
        c.clear(Calendar.MINUTE);
        c.clear(Calendar.SECOND);
        c.clear(Calendar.MILLISECOND);
        return c;
    }

    //Get quickevents from DB
    public static ArrayList<String> getDbEvents(Context context)
    {
        ArrayList<String> allEvents = new ArrayList<String>();
        EventReaderDb dbhelper = new EventReaderDb(context);
        SQLiteDatabase db = dbhelper.getReadableDatabase();

        String[] projection = {
                EventReaderContract.EventEntry._ID,
                EventReaderContract.EventEntry.COLUMN_NAME_TITLE,
                EventReaderContract.EventEntry.COLUMN_NAME_START,
        };

        java.util.Calendar event_d = java.util.Calendar.getInstance();
        event_d.clear(java.util.Calendar.HOUR);
        event_d.clear(java.util.Calendar.MINUTE);
        event_d.clear(java.util.Calendar.SECOND);
        event_d.clear(java.util.Calendar.MILLISECOND);
        long current_date = event_d.getTimeInMillis();


        //ADD EVENTS WITH NON ZERO START TIME
        String selection =  EventReaderContract.EventEntry.COLUMN_NAME_DATE+" =? AND "+
                EventReaderContract.EventEntry.COLUMN_NAME_START + " <>?";
        String selection_args[] = {String.valueOf(current_date), "0"};
        String sortorder = EventReaderContract.EventEntry.COLUMN_NAME_START;
        Cursor c = db.query(EventReaderContract.EventEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                sortorder
        );
        c.moveToFirst();
        int title_col_index=c.getColumnIndex(EventReaderContract.EventEntry.COLUMN_NAME_TITLE);
        while(c.moveToNext())
        {
            String title = c.getString(title_col_index);
            allEvents.add(title);
        }

        return allEvents;
    }

    //Display items on listview with array adapter
    public static void displayItems(Activity mActivity, int listview_id, ArrayList<String> result, int event_type){

        ListView lv = (ListView) mActivity.findViewById(listview_id);
        MyAdapter arrayAdapter= new MyAdapter(mActivity, R.layout.my_listlayout, result, event_type);
        lv.setAdapter(arrayAdapter);
    }


}

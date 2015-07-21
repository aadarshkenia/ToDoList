package com.example.calenderquickstart;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by aadarsh-ubuntu on 7/14/15.
 */
public abstract class MyEvent {

    static int id_counter=0;
    int id;
    int priority;
    String title;
    Calendar event_date;

    public void setID()
    {
        this.id = ++id_counter;
    }
}

class MyDetailedEvent extends MyEvent
{

    String location;
    String description;
    Calendar start_time;
    Calendar end_time;
    boolean emailReminder=false;
    boolean alarmReminder=false;

    MyDetailedEvent(int priority, String title, String location, String description, Calendar start, Calendar end, Calendar event_date, boolean emailReminder, boolean alarmReminder)
    {
        setID();
        this.priority = priority;
        this.title=title;
        this.location=location;
        this.description=description;
        this.start_time=start;
        this.end_time=end;
        this.event_date=event_date;
        this.emailReminder=emailReminder;
        this.alarmReminder=alarmReminder;
    }

    MyDetailedEvent()
    {

    }
}

class MyQuickEvent extends MyEvent
{
    Calendar start_time;
    boolean alarmReminder=false;

    MyQuickEvent(int priority, String title, Calendar start, Calendar event_date, boolean alarmReminder)
    {
        setID();
        this.priority=priority;
        this.title=title;
        this.start_time = start;
        this.event_date=event_date;
        this.alarmReminder = alarmReminder;
    }

    MyQuickEvent()
    {

    }
}
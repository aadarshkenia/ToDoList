package com.example.calenderquickstart;

/**
 * Created by aadarsh-ubuntu on 7/16/15.
 */
public class SingletonUserSettings {
    private static SingletonUserSettings userSettings = null;
    private com.google.api.services.calendar.Calendar mService;

    private SingletonUserSettings(){}

    public static SingletonUserSettings getInstance()
    {
        if(userSettings==null)
            userSettings = new SingletonUserSettings();
        return userSettings;
    }

    public com.google.api.services.calendar.Calendar getServiceObject()
    {
        return mService;
    }

    public void setServiceObject(com.google.api.services.calendar.Calendar service_obj)
    {
        mService = service_obj;
    }

}

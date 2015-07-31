package com.example.calenderquickstart;


import com.evernote.client.android.EvernoteSession;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.gson.Gson;

import android.accounts.AccountManager;
import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.os.Handler;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class MainActivity extends Activity {

    private Context context=null;
    /**
     * A Google Calendar API service object used to access the API.
     * Note: Do not confuse this class with API library's model classes, which
     * represent specific data structures.
     */
    com.google.api.services.calendar.Calendar mService;
    GoogleAccountCredential credential;
    private TextView mStatusText;
    private TextView mResultsText;
    final HttpTransport transport = AndroidHttp.newCompatibleTransport();
    final JsonFactory jsonFactory = GsonFactory.getDefaultInstance();

    static final int REQUEST_ACCOUNT_PICKER = 1000;
    static final int REQUEST_AUTHORIZATION = 1001;
    static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    private static final String PREF_ACCOUNT_NAME = "accountName";
    private static final String[] SCOPES = { CalendarScopes.CALENDAR };

    private int syncInterval = 5000;
    private Handler handler = null;

    //Evernote
    String consumerKey=null, consumerSecret=null;
    private static final EvernoteSession.EvernoteService EVERNOTE_SERVICE = EvernoteSession.EvernoteService.SANDBOX;


    /**
     * Create the main activity.
     * @param savedInstanceState previously saved instance data.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);

        context = this;
        TextView mTodayText = (TextView) findViewById(R.id.today_text);
        java.util.Calendar cal = java.util.Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("E, MMM-dd", Locale.US);
        mTodayText.setText(sdf.format(cal.getTime()));

        mStatusText = (TextView)findViewById(R.id.status_text);
        mResultsText = (TextView)findViewById(R.id.result_text);

        // Initialize credentials and service object.
        SharedPreferences settings = getPreferences(Context.MODE_PRIVATE);
        credential = GoogleAccountCredential.usingOAuth2(
                getApplicationContext(), Arrays.asList(SCOPES))
                .setBackOff(new ExponentialBackOff())
                .setSelectedAccountName(settings.getString(PREF_ACCOUNT_NAME, null));

        mService = new com.google.api.services.calendar.Calendar.Builder(
                transport, jsonFactory, credential)
                .setApplicationName("Google Calendar API Android Quickstart")
                .build();

        //Set service object in singleton class
        SingletonUserSettings userSettings = SingletonUserSettings.getInstance();
        userSettings.setServiceObject(mService);

        //Format Action Bar
        ActionBar actionBar = getActionBar();
        formatActionBar(actionBar);

        //Temporary: Remove later
        EventReaderDb dbhelper= new EventReaderDb(this);
        SQLiteDatabase db = dbhelper.getWritableDatabase();
        db.execSQL(EventReaderContract.getDeleteTableSyntax());
        db.execSQL(EventReaderContract.getCreateTableSyntax());

        /*
        //Setup Handler and syncing with Google Calendar
        handler = new Handler();
        startSyncProcess();
        */


        //For evernote
        consumerKey = BuildConfig.EVERNOTE_CONSUMER_KEY;
        consumerSecret=BuildConfig.EVERNOTE_CONSUMER_SECRET;
        //Set up the Evernote singleton session, use EvernoteSession.getInstance() later
        new EvernoteSession.Builder(this)
                .setEvernoteService(EVERNOTE_SERVICE)
                .build(consumerKey, consumerSecret)
                .asSingleton();
        boolean loggedIn = EvernoteSession.getInstance().isLoggedIn();
        System.out.println("BOOLEAN: "+loggedIn);
        if (!loggedIn) {
            System.out.println("Went here");
            startActivity(new Intent(this, LoginActivity.class));
        }


    }


    @Override
    protected void onResume() {
        super.onResume();
        mStatusText = (TextView)findViewById(R.id.status_text);
        mResultsText = (TextView)findViewById(R.id.result_text);
        if (isGooglePlayServicesAvailable()) {
            refreshResults();
        } else {
            mStatusText.setText("Google Play Services required: " +
                    "after installing, close and relaunch this app.");
        }
    }

    /**
     * Called when an activity launched here (specifically, AccountPicker
     * and authorization) exits, giving you the requestCode you started it with,
     * the resultCode it returned, and any additional data from it.
     * @param requestCode code indicating which activity result is incoming.
     * @param resultCode code indicating the result of the incoming
     *     activity result.
     * @param data Intent (containing result data) returned by incoming
     *     activity result.
     */
    @Override
    protected void onActivityResult(
            int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case REQUEST_GOOGLE_PLAY_SERVICES:
                if (resultCode != RESULT_OK) {
                    isGooglePlayServicesAvailable();
                }
                break;
            case REQUEST_ACCOUNT_PICKER:
                if (resultCode == RESULT_OK && data != null &&
                        data.getExtras() != null) {
                    String accountName =
                            data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                    if (accountName != null) {
                        credential.setSelectedAccountName(accountName);
                        SharedPreferences settings =
                                getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString(PREF_ACCOUNT_NAME, accountName);
                        editor.commit();
                    }
                } else if (resultCode == RESULT_CANCELED) {
                    mStatusText.setText("Account unspecified.");
                }
                break;
            case REQUEST_AUTHORIZATION:
                if (resultCode != RESULT_OK) {
                    chooseAccount();
                }
                break;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * Attempt to get a set of data from the Google Calendar API to display. If the
     * email address isn't known yet, then call chooseAccount() method so the
     * user can pick an account.
     */
    private void refreshResults() {
        if (credential.getSelectedAccountName() == null) {
            chooseAccount();
        } else {
            if (isDeviceOnline()) {
                new ApiAsyncTask(this).execute();
            } else {
                mStatusText.setText("No network connection available.");
            }
        }

        //Show quickevents from DB
        ArrayList<String> allEvents = MyUtility.getDbEvents(this);
        System.out.println("HERE IN REFRESH: "+allEvents.size());
        for(int i=0;i<allEvents.size();i++)
            System.out.println((String)allEvents.get(i));

        ListView lv = (ListView) findViewById(R.id.list);
        ArrayAdapter<String> arrayAdapter= new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, allEvents);
        lv.setAdapter(arrayAdapter);

    }

    /**
     * Clear any existing Google Calendar API data from the TextView and update
     * the header message; called from background threads and async tasks
     * that need to update the UI (in the UI thread).
     */
    public void clearResultsText() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mStatusText.setText("Retrieving data…");
                mResultsText.setText("");
            }
        });
    }

    /**
     * Fill the data TextView with the given List of Strings; called from
     * background threads and async tasks that need to update the UI (in the
     * UI thread).
     * @param dataStrings a List of Strings to populate the main TextView with.
     */
    public void updateResultsText(final List<String> dataStrings) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (dataStrings == null) {
                    mStatusText.setText("Error retrieving data!");
                } else if (dataStrings.size() == 0) {
                    mStatusText.setText("No data found.");
                } else {
                    mStatusText.setText("Data retrieved using" +
                            " the Google Calendar API:");
                    mResultsText.setText(TextUtils.join("\n\n", dataStrings));
                }
            }
        });
    }

    /**
     * Show a status message in the list header TextView; called from background
     * threads and async tasks that need to update the UI (in the UI thread).
     * @param message a String to display in the UI header TextView.
     */
    public void updateStatus(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mStatusText.setText(message);
            }
        });
    }

    /**
     * Starts an activity in Google Play Services so the user can pick an
     * account.
     */
    private void chooseAccount() {
        startActivityForResult(
                credential.newChooseAccountIntent(), REQUEST_ACCOUNT_PICKER);
    }

    /**
     * Checks whether the device currently has a network connection.
     * @return true if the device has a network connection, false otherwise.
     */
    private boolean isDeviceOnline() {
        ConnectivityManager connMgr =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    /**
     * Check that Google Play services APK is installed and up to date. Will
     * launch an error dialog for the user to update Google Play Services if
     * possible.
     * @return true if Google Play Services is available and up to
     *     date on this device; false otherwise.
     */
    private boolean isGooglePlayServicesAvailable() {
        final int connectionStatusCode =
                GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (GooglePlayServicesUtil.isUserRecoverableError(connectionStatusCode)) {
            showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
            return false;
        } else if (connectionStatusCode != ConnectionResult.SUCCESS ) {
            return false;
        }
        return true;
    }

    /**
     * Display an error dialog showing that Google Play Services is missing
     * or out of date.
     * @param connectionStatusCode code describing the presence (or lack of)
     *     Google Play Services on this device.
     */
    void showGooglePlayServicesAvailabilityErrorDialog(
            final int connectionStatusCode) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Dialog dialog = GooglePlayServicesUtil.getErrorDialog(
                        connectionStatusCode,
                        MainActivity.this,
                        REQUEST_GOOGLE_PLAY_SERVICES);
                dialog.show();
            }
        });
    }

    public void addEvent(View v)
    {
        Intent intent = new Intent(this, AddEvent.class);
        startActivity(intent);
    }

    public void formatActionBar(ActionBar actionBar)
    {
        actionBar.setTitle(getString(R.string.today));
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.LTGRAY));
    }




    Runnable syncFromCalendar = new Runnable() {
        @Override
        public void run() {

                //Get Calendar events and update UI
                //ArrayList<String> cal_events = (ArrayList<String>)MyUtility.getDataFromApi(mService);
                //Show quickevents from DB
                //ArrayList<String> allEvents = MyUtility.getDbEvents(context);
                /*
                ListView lv = (ListView) findViewById(R.id.list);
                ArrayAdapter<String> arrayAdapter= new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, cal_events);
                lv.setAdapter(arrayAdapter);
                handler.postDelayed(syncFromCalendar, syncInterval);
                */
        }
    };

    public void startSyncProcess()
    {
        syncFromCalendar.run();
    }

    public void stopSyncProcess()
    {
        handler.removeCallbacks(syncFromCalendar);
    }


}//end of MainActivity class


package com.example.calenderquickstart;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

/**
 * Created by aadarsh-ubuntu on 7/24/15.
 */
public class EventReaderDb extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "EventReader.db";

    public EventReaderDb(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    //This is where the creation of tables and the initial population of the tables should happen.
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(EventReaderContract.getDeleteTableSyntax());
        db.execSQL(EventReaderContract.getCreateTableSyntax());
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(EventReaderContract.getDeleteTableSyntax());
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}

//EVENT TABLE RELATED OPERATIONS
class EventReaderContract {
    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    public EventReaderContract() {}

    /* Inner class that defines the table contents */
    public static abstract class EventEntry implements BaseColumns {
        public static final String TABLE_NAME = "EVENTS";

        //All columns
        public static final String COLUMN_NAME_ENTRY_ID = "entryid";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_PRIORITY = "priority";
        public static final String COLUMN_NAME_START = "start_time";
        public static final String COLUMN_NAME_DATE = "event_date";
        public static final String COLUMN_NAME_ALARM = "alarm";

        //Nullable column name
        public static final String COLUMN_NAME_NULLABLE = "COLUMN_NAME_TITLE";
    }

    private static final String TEXT_TYPE = " TEXT";
    private static final String INT_TYPE = " INTEGER";

    private static final String COMMA_SEP = ",";

    private static final String SQL_CREATE_TABLE =
            "CREATE TABLE " + EventEntry.TABLE_NAME + " (" +
                    EventEntry._ID + " INTEGER PRIMARY KEY," +
                    EventEntry.COLUMN_NAME_TITLE + TEXT_TYPE + COMMA_SEP +
                    EventEntry.COLUMN_NAME_PRIORITY + INT_TYPE + COMMA_SEP +
                    EventEntry.COLUMN_NAME_DATE + INT_TYPE + COMMA_SEP +
                    EventEntry.COLUMN_NAME_START + INT_TYPE + COMMA_SEP +
                    EventEntry.COLUMN_NAME_ALARM + INT_TYPE + " );";

    private static final String SQL_DELETE_TABLE =
            "DROP TABLE IF EXISTS " + EventEntry.TABLE_NAME;

    public static String getCreateTableSyntax(){
        return SQL_CREATE_TABLE;
    }
    public static String getDeleteTableSyntax(){
        return SQL_DELETE_TABLE;
    }


}
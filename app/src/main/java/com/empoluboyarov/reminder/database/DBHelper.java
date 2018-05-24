package com.empoluboyarov.reminder.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import com.empoluboyarov.reminder.model.ModelTask;


public class DBHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "reminder_database";
    public static final String TASKS_TABLE = "tasks_table";

    public static final String TASK_TITLE_COLUMN = "task_column";
    public static final String TASK_DATE_COLUMN = "task_date";
    public static final String TASK_PRIORITY_COLUMN = "task_priority";
    public static final String TASK_STATUS_COLUMN = "task_status";
    public static final String TASK_TIME_STAMP_COLUMN = "task_time_stamp";

    private static final String TASKS_TABLE_CREATE_SCRIPT = "CREATE TABLE "
            + TASKS_TABLE + " (" + BaseColumns._ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT, " + TASK_TITLE_COLUMN + " TEXT NOT NULL, "
            + TASK_DATE_COLUMN + " LONG, " + TASK_PRIORITY_COLUMN + " INTEGER, "
            + TASK_STATUS_COLUMN + " INTEGER, " + TASK_TIME_STAMP_COLUMN + " LONG);";

    private DBUpdateManager updateManager;
    private DBQueryManager queryManager;

    public static final String SELECTION_STATUS = TASK_STATUS_COLUMN + " = ?";
    public static final String SELECTION_TIME_STAMP = TASK_TIME_STAMP_COLUMN + " = ?";
    public static final String SELECTION_LIKE_TITLE = TASK_TITLE_COLUMN + " LIKE ?";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        updateManager = new DBUpdateManager(getWritableDatabase());
        queryManager = new DBQueryManager(getReadableDatabase());
    }

    public DBUpdateManager update(){
        return updateManager;
    }

    public DBQueryManager query(){
        return queryManager;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TASKS_TABLE_CREATE_SCRIPT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE " + TASKS_TABLE);
        onCreate(db);
    }

    public void saveTask(ModelTask task){
        ContentValues values = new ContentValues();

        values.put(TASK_TITLE_COLUMN, task.getTitle());
        values.put(TASK_DATE_COLUMN, task.getDate());
        values.put(TASK_PRIORITY_COLUMN, task.getPriority());
        values.put(TASK_STATUS_COLUMN, task.getStatus());
        values.put(TASK_TIME_STAMP_COLUMN, task.getTimeStamp());

        getWritableDatabase().insert(TASKS_TABLE, null, values);
    }

    public void removeTask(long timeStamp){
        getWritableDatabase().delete(TASKS_TABLE, SELECTION_TIME_STAMP, new String[]{Long.toString(timeStamp)});
    }
}

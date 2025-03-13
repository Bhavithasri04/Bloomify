package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class RoutineDBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "routineDB.db";
    private static final int DATABASE_VERSION = 3; // Incremented version number for adding the 'is_completed' column

    private static final String TABLE_ROUTINE = "routines";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_TITLE = "title";
    private static final String COLUMN_TIME = "time";
    private static final String COLUMN_SCHEDULE_TYPE = "schedule_type";
    private static final String COLUMN_IS_COMPLETED = "is_completed"; // New column for completion status

    public RoutineDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_ROUTINE_TABLE = "CREATE TABLE " + TABLE_ROUTINE + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_TITLE + " TEXT,"
                + COLUMN_TIME + " TEXT,"
                + COLUMN_SCHEDULE_TYPE + " TEXT,"
                + COLUMN_IS_COMPLETED + " INTEGER DEFAULT 0" + ")";
        db.execSQL(CREATE_ROUTINE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 3) {
            String ALTER_TABLE_ADD_COLUMN = "ALTER TABLE " + TABLE_ROUTINE + " ADD COLUMN "
                    + COLUMN_IS_COMPLETED + " INTEGER DEFAULT 0";
            db.execSQL(ALTER_TABLE_ADD_COLUMN);
        }
    }

    // Add a routine
    public void addRoutine(Routine routine) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, routine.getTitle());
        values.put(COLUMN_TIME, routine.getTime());
        values.put(COLUMN_SCHEDULE_TYPE, routine.getScheduleType());
        values.put(COLUMN_IS_COMPLETED, routine.isCompleted() ? 1 : 0);

        db.insert(TABLE_ROUTINE, null, values);
        db.close();
    }

    // Get routine by ID
    @SuppressLint("Range")
    public Routine getRoutineById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(
                TABLE_ROUTINE,
                null,
                COLUMN_ID + " = ?",
                new String[]{String.valueOf(id)},
                null, null, null
        );

        if (cursor != null) {
            cursor.moveToFirst();

            int routineId;
            routineId = cursor.getInt(cursor.getColumnIndex(COLUMN_ID));
            String title = cursor.getString(cursor.getColumnIndex(COLUMN_TITLE));
            String time = cursor.getString(cursor.getColumnIndex(COLUMN_TIME));
            String scheduleType = cursor.getString(cursor.getColumnIndex(COLUMN_SCHEDULE_TYPE));
            boolean isCompleted = cursor.getInt(cursor.getColumnIndex(COLUMN_IS_COMPLETED)) > 0;

            Routine routine = new Routine(routineId, title, time, scheduleType, isCompleted);
            cursor.close();
            return routine;
        } else {
            cursor.close();
            return null;
        }
    }

    // Get all routines
    @SuppressLint("Range")
    public ArrayList<Routine> getAllRoutines() {
        ArrayList<Routine> routineList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(
                TABLE_ROUTINE,
                null,
                null, null, null, null, null
        );

        if (cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                int routineId;
                routineId = cursor.getInt(cursor.getColumnIndex(COLUMN_ID));
                String title = cursor.getString(cursor.getColumnIndex(COLUMN_TITLE));
                String time = cursor.getString(cursor.getColumnIndex(COLUMN_TIME));
                String scheduleType = cursor.getString(cursor.getColumnIndex(COLUMN_SCHEDULE_TYPE));
                boolean isCompleted = cursor.getInt(cursor.getColumnIndex(COLUMN_IS_COMPLETED)) > 0;

                Routine routine = new Routine(routineId, title, time, scheduleType, isCompleted);
                routineList.add(routine);
                cursor.moveToNext();
            }
            cursor.close();
        }
        return routineList;
    }

    // Update routine completion status
    public boolean updateRoutineCompletionStatus(int routineId, boolean isCompleted) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_IS_COMPLETED, isCompleted ? 1 : 0);
        int rowsAffected = db.update(TABLE_ROUTINE, values, COLUMN_ID + "=?", new String[]{String.valueOf(routineId)});
        db.close();
        return rowsAffected > 0;
    }

    public void deleteRoutine(int routineId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_ROUTINE, COLUMN_ID + "=?", new String[]{String.valueOf(routineId)});
        db.close();
    }

}

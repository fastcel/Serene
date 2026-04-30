package com.example.serene;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "Serene.db";
    private static final int DB_VERSION = 1;

    // ================= USERS TABLE =================
    private static final String TABLE_USERS = "users";
    private static final String COL_ID = "id";
    private static final String COL_UID = "uid";
    private static final String COL_USERNAME = "username";
    private static final String COL_EMAIL = "email";

    // ================= JOURNALS TABLE =================
    private static final String TABLE_JOURNALS = "journals";
    private static final String COL_JOURNAL_ID = "journal_id";
    private static final String COL_USER_UID = "user_uid";
    private static final String COL_TEXT = "text";
    private static final String COL_DATE = "date";

    // ================= GOALS TABLE =================
    private static final String TABLE_GOALS = "goals";
    private static final String COL_GOAL_ID = "goal_id";
    private static final String COL_GOAL_TEXT = "goal_text";
    private static final String COL_IS_DONE = "is_done";

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    // ================= CREATE TABLES =================
    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE " + TABLE_USERS + " ("
                + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_UID + " TEXT UNIQUE, "
                + COL_USERNAME + " TEXT, "
                + COL_EMAIL + " TEXT)");

        db.execSQL("CREATE TABLE " + TABLE_JOURNALS + " ("
                + COL_JOURNAL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_USER_UID + " TEXT, "
                + COL_TEXT + " TEXT, "
                + COL_DATE + " TEXT)");

        db.execSQL("CREATE TABLE " + TABLE_GOALS + " ("
                + COL_GOAL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_USER_UID + " TEXT, "
                + COL_GOAL_TEXT + " TEXT, "
                + COL_IS_DONE + " INTEGER DEFAULT 0)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_JOURNALS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_GOALS);
        onCreate(db);
    }

    // ================= USERS =================
    public boolean insertUser(String uid, String username, String email) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COL_UID, uid);
        values.put(COL_USERNAME, username);
        values.put(COL_EMAIL, email);

        return db.insert(TABLE_USERS, null, values) != -1;
    }

    // cleaner version (recommended instead of Cursor in activities)
    public String getUsername(String uid) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT " + COL_USERNAME + " FROM " + TABLE_USERS + " WHERE " + COL_UID + "=?",
                new String[]{uid}
        );

        String username = null;

        if (cursor.moveToFirst()) {
            username = cursor.getString(0);
        }

        cursor.close();
        return username;
    }

    // ================= JOURNALS =================
    public boolean addJournal(String uid, String text, String date) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COL_USER_UID, uid);
        values.put(COL_TEXT, text);
        values.put(COL_DATE, date);

        return db.insert(TABLE_JOURNALS, null, values) != -1;
    }

    public Cursor getUserJournals(String uid) {
        SQLiteDatabase db = this.getReadableDatabase();

        return db.rawQuery(
                "SELECT * FROM " + TABLE_JOURNALS +
                        " WHERE " + COL_USER_UID + "=? ORDER BY " + COL_JOURNAL_ID + " DESC",
                new String[]{uid}
        );
    }

    // ================= GOALS =================
    public boolean addGoal(String uid, String goalText) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COL_USER_UID, uid);
        values.put(COL_GOAL_TEXT, goalText);
        values.put(COL_IS_DONE, 0);

        return db.insert(TABLE_GOALS, null, values) != -1;
    }

    public Cursor getGoals(String uid) {
        SQLiteDatabase db = this.getReadableDatabase();

        return db.rawQuery(
                "SELECT * FROM " + TABLE_GOALS +
                        " WHERE " + COL_USER_UID + "=?",
                new String[]{uid}
        );
    }

    public boolean markGoalDone(int goalId) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COL_IS_DONE, 1);

        return db.update(
                TABLE_GOALS,
                values,
                COL_GOAL_ID + "=?",
                new String[]{String.valueOf(goalId)}
        ) > 0;
    }
}
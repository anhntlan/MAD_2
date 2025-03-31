package com.example.hipenjava;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {
    // Database Name & Version
    private static final String DATABASE_NAME = "UserDatabase.db";
    private static final int DATABASE_VERSION = 1;

    // Table Name & Columns
    private static final String TABLE_USERS = "users";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_EMAIL = "email";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_PASSWORD = "password";
    private static final String COLUMN_ROLE = "role";
    // Table Creation Query
    private static final String CREATE_TABLE_USERS =
            "CREATE TABLE " + TABLE_USERS + " ("
                    + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + COLUMN_EMAIL + " TEXT UNIQUE, "
                    + COLUMN_NAME + " TEXT, "
                    + COLUMN_PASSWORD + " TEXT, "
                    + COLUMN_ROLE + " TEXT DEFAULT 'user')";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_USERS);

        // Insert an admin account (Hardcoded admin for testing)
        ContentValues adminValues = new ContentValues();
        adminValues.put(COLUMN_EMAIL, "admin@example.com");
        adminValues.put(COLUMN_PASSWORD, "admin123"); // Hash this in real applications!
        adminValues.put(COLUMN_ROLE, "admin");
        db.insert(TABLE_USERS, null, adminValues);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }


    // Insert user data into the database
    public boolean insertUser(String email, String name, String password, String role) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_EMAIL, email);
        values.put(COLUMN_NAME, name);
        values.put(COLUMN_PASSWORD, password);
        values.put(COLUMN_ROLE, role);


        long result = db.insert(TABLE_USERS, null, values);
        db.close();

        return result != -1; // If result == -1, insertion failed
    }

    // Check if user exists
    public boolean checkUser(String email, String password) {
//        String email = etemail.getText().toString();
//        String password = etpassword.getText().toString();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USERS + " WHERE email=? AND password=?",
                new String[]{email, password});

        boolean exists = cursor.getCount() > 0;
        cursor.close();
        db.close();

        return exists;
    }
    public void logAllUsers() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM users", null);

        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                @SuppressLint("Range") String email = cursor.getString(cursor.getColumnIndex("email")); // Replace with your column name
                @SuppressLint("Range") String password = cursor.getString(cursor.getColumnIndex("password")); // Replace with your column name
                Log.d("DB_CHECK", "User: " + email + " | Password: " + password);
            }
        } else {
            Log.d("DB_CHECK", "No users found in the database.");
        }
        cursor.close();
    }

    public String getUserRole(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("users", new String[]{"role"},
                "email=? AND password=?", new String[]{email, password},
                null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            String role = cursor.getString(0);
            cursor.close();
            return role;  // Returns "admin" or "user"
        }
        return null; // Login failed
    }



}

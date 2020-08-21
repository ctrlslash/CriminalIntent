package com.example.criminalintent.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import static com.example.criminalintent.database.CrimeDBSchema.CrimeTable.*;

public class CrimeBaseHelper extends SQLiteOpenHelper {

    public CrimeBaseHelper(@Nullable Context context) {
        super(context, CrimeDBSchema.NAME, null, CrimeDBSchema.VERSION);
    }

    /**
     * create the database with all tables, constraints, ...
     * it's like implementing the ERD.
     *
     * @param db
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + NAME + "(" +
                COLS.ID + " integer primary key autoincrement," +
                COLS.UUID + " text," +
                COLS.TITLE + " text," +
                COLS.DATE + " long," +
                COLS.SOLVED + " integer," +
                COLS.SUSPECT + " text" +
                ");");
    }

    /**
     * update the database with all alters.
     *
     * @param db
     * @param oldVersion
     * @param newVersion
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}

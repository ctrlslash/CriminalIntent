package com.example.criminalintent.database.cursorwrapper;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.example.criminalintent.database.CrimeDBSchema;
import com.example.criminalintent.model.Crime;

import java.util.Date;
import java.util.UUID;

public class CrimeCursorWrapper extends CursorWrapper {

    /**
     * Creates a cursor wrapper.
     *
     * @param cursor The underlying cursor to wrap.
     */
    public CrimeCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Crime getCrime() {
        String stringUUID = getString(getColumnIndex(CrimeDBSchema.CrimeTable.COLS.UUID));
        String title = getString(getColumnIndex(CrimeDBSchema.CrimeTable.COLS.TITLE));
        Date date = new Date(getLong(getColumnIndex(CrimeDBSchema.CrimeTable.COLS.DATE)));
        boolean solved = getInt(getColumnIndex(CrimeDBSchema.CrimeTable.COLS.SOLVED)) == 0 ? false : true;

        Crime crime = new Crime(UUID.fromString(stringUUID), title, date, solved);
        return crime;
    }
}

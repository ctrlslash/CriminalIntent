package com.example.criminalintent.repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.criminalintent.database.CrimeBaseHelper;
import com.example.criminalintent.database.CrimeDBSchema;
import com.example.criminalintent.model.Crime;
import com.example.criminalintent.database.CrimeDBSchema.CrimeTable.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class CrimeDBRepository implements IRepository<Crime> {

    private static CrimeDBRepository sCrimeRepository;

    //future referenced: memory leaks
    private static Context mContext;

    private SQLiteDatabase mDatabase;

    public static CrimeDBRepository getInstance(Context context) {
        mContext = context.getApplicationContext();
        if (sCrimeRepository == null)
            sCrimeRepository = new CrimeDBRepository();

        return sCrimeRepository;
    }

    private CrimeDBRepository() {
        CrimeBaseHelper crimeBaseHelper = new CrimeBaseHelper(mContext);
        mDatabase = crimeBaseHelper.getWritableDatabase();
    }

    //Read all
    @Override
    public List<Crime> getList() {
        List<Crime> crimes = new ArrayList<>();
        Cursor cursor = mDatabase.query(CrimeDBSchema.CrimeTable.NAME,
                null,
                null,
                null,
                null,
                null,
                null);
        try {
            cursor.moveToFirst();

            while (!cursor.isAfterLast()) {
                String stringUUID = cursor.getString(cursor.getColumnIndex(COLS.UUID));
                String title = cursor.getString(cursor.getColumnIndex(COLS.TITLE));
                Date date = new Date(cursor.getLong(cursor.getColumnIndex(COLS.DATE)));
                boolean solved = cursor.getInt(cursor.getColumnIndex(COLS.SOLVED)) == 0 ? false : true;

                Crime crime = new Crime(UUID.fromString(stringUUID), title, date, solved);
                crimes.add(crime);

                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }

        return crimes;
    }

    /*@Override
    public void setList(List<Crime> crimes) {
        mCrimes = crimes;
    }*/

    //Read one
    @Override
    public Crime get(UUID uuid) {
        /*for (Crime crime: mCrimes) {
            if (crime.getId().equals(uuid))
                return crime;
        }*/

        return null;
    }

    //Update one
    @Override
    public void update(Crime crime) {
        Crime updateCrime = get(crime.getId());
        updateCrime.setTitle(crime.getTitle());
        updateCrime.setDate(crime.getDate());
        updateCrime.setSolved(crime.isSolved());
    }

    //Delete
    @Override
    public void delete(Crime crime) {
        /*for (int i = 0; i < mCrimes.size(); i++) {
            if (mCrimes.get(i).getId().equals(crime.getId())) {
                mCrimes.remove(i);
                return;
            }
        }*/
    }

    //Create: Insert
    @Override
    public void insert(Crime crime) {
//        mCrimes.add(crime);
        ContentValues values = getCrimeContentValue(crime);
        mDatabase.insert(CrimeDBSchema.CrimeTable.NAME, null, values);
    }

    //Create: Insert
    @Override
    public void insertList(List<Crime> crimes) {
//        mCrimes.addAll(crimes);
    }

    @Override
    public int getPosition(UUID uuid) {
        /*for (int i = 0; i < mCrimes.size(); i++) {
            if (mCrimes.get(i).getId().equals(uuid))
                return i;
        }*/
        return -1;
    }

    /**
     * Convert crime pojo to ContentValue
     * @param crime
     * @return
     */
    private ContentValues getCrimeContentValue(Crime crime) {
        ContentValues values = new ContentValues();
        values.put(COLS.UUID, crime.getId().toString());
        values.put(COLS.TITLE, crime.getTitle());
        values.put(COLS.DATE, crime.getDate().getTime());
        values.put(COLS.SOLVED, crime.isSolved() ? 1 : 0);

        return values;
    }
}

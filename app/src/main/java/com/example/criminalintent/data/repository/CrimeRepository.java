package com.example.criminalintent.data.repository;

import android.content.Context;

import androidx.lifecycle.LiveData;

import com.example.criminalintent.data.room.CrimeRoomDataBase;
import com.example.criminalintent.data.room.dao.CrimeDAO;
import com.example.criminalintent.data.room.entities.Crime;

import java.io.File;
import java.util.List;
import java.util.UUID;

public class CrimeRepository {

    private static CrimeRepository sInstance;

    private Context mContext;
    private CrimeDAO mCrimeDAO;

    public static synchronized CrimeRepository getInstance(Context context) {
        if (sInstance == null)
            sInstance = new CrimeRepository(context);

        return sInstance;
    }

    private CrimeRepository(Context context) {
        mContext = context;

        CrimeRoomDataBase mCrimeRoomDataBase = CrimeRoomDataBase.getDataBase(mContext);
        mCrimeDAO = mCrimeRoomDataBase.getCrimeDAO();
    }

    public LiveData<List<Crime>> getCrimesLiveData() {
        return mCrimeDAO.getCrimes();
    }

    public LiveData<Crime> getCrimeLiveData(UUID uuid) {
        return mCrimeDAO.getCrime(uuid);
    }

    public void updateCrime(Crime crime) {
        CrimeRoomDataBase.dataBaseWriteExecutor.execute(() -> mCrimeDAO.updateCrimes(crime));
    }

    public void deleteCrime(Crime crime) {
        CrimeRoomDataBase.dataBaseWriteExecutor.execute(() -> mCrimeDAO.deleteCrimes(crime));
    }

    public void insertCrime(Crime crime) {
        CrimeRoomDataBase.dataBaseWriteExecutor.execute(() -> mCrimeDAO.insertCrimes(crime));
    }

    public void insertCrimes(List<Crime> list) {
        CrimeRoomDataBase.dataBaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                mCrimeDAO.insertCrimes(list.toArray(new Crime[]{}));
            }
        });
    }

    public File getPhotoFile(Crime crime) {
        File photoFile = new File(mContext.getFilesDir(), crime.getPhotoFileName());
        return photoFile;
    }

}

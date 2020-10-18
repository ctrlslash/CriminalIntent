package com.example.criminalintent.repository;

import android.content.Context;

import androidx.lifecycle.LiveData;

import com.example.criminalintent.database.CrimeRoomDataBase;
import com.example.criminalintent.database.dao.CrimeDAO;
import com.example.criminalintent.database.entities.Crime;

import java.io.File;
import java.util.List;
import java.util.UUID;

public class CrimeRepository {

    private static CrimeRepository sInstance;

    private Context mContext;
    private CrimeDAO mCrimeDAO;

    private LiveData<List<Crime>> mCrimesLiveData;
    private LiveData<Crime> mCrimeLiveData;

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

    public int getPosition(UUID uuid) {
        List<Crime> crimes = getCrimesLiveData().getValue();
        Crime crime = getCrimeLiveData(uuid).getValue();

        if (crimes == null || crime == null)
            return 0;

        return crimes.indexOf(crime);
    }

    public File getPhotoFile(Crime crime) {
        File photoFile = new File(mContext.getFilesDir(), crime.getPhotoFileName());
        return photoFile;
    }

}

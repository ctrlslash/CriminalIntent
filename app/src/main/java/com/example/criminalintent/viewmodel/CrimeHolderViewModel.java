package com.example.criminalintent.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.criminalintent.data.room.entities.Crime;

public class CrimeHolderViewModel extends AndroidViewModel {

    private MutableLiveData<Crime> mCrimeHolderLiveData = new MutableLiveData<>();

    public MutableLiveData<Crime> getCrimeHolderLiveData() {
        return mCrimeHolderLiveData;
    }

    public void setCrimeHolderLiveData(MutableLiveData<Crime> crimeHolderLiveData) {
        mCrimeHolderLiveData = crimeHolderLiveData;
    }

    public CrimeHolderViewModel(@NonNull Application application) {
        super(application);
    }
}

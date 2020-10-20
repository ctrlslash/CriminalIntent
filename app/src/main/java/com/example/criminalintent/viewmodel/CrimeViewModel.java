package com.example.criminalintent.viewmodel;

import android.app.Application;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.criminalintent.R;
import com.example.criminalintent.data.repository.CrimeRepository;
import com.example.criminalintent.data.room.entities.Crime;
import com.example.criminalintent.utilities.Constants;
import com.example.criminalintent.view.activity.CrimePagerActivity;
import com.example.criminalintent.view.fragment.CrimeDetailFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CrimeViewModel extends AndroidViewModel {

    private CrimeRepository mRepository;
    private boolean mCrimeDetailSubtitleVisible = false;

    //the result of live data fetch from room will be saved in this field.
    private List<Crime> mCrimesSubject = new ArrayList<>();

    private LiveData<List<Crime>> mCrimesLiveData;
    private MutableLiveData<Crime> mCrimeSelectedLiveData;

    public boolean isCrimeDetailSubtitleVisible() {
        return mCrimeDetailSubtitleVisible;
    }

    public void setCrimeDetailSubtitleVisible(boolean crimeDetailSubtitleVisible) {
        mCrimeDetailSubtitleVisible = crimeDetailSubtitleVisible;
    }

    public List<Crime> getCrimesSubject() {
        return mCrimesSubject;
    }

    public void setCrimesSubject(List<Crime> crimesSubject) {
        mCrimesSubject = crimesSubject;
    }

    public LiveData<List<Crime>> getCrimesLiveData() {
        return mCrimesLiveData;
    }

    public MutableLiveData<Crime> getCrimeSelectedLiveData() {
        /*
            always create a new object for this live data, because this view model is
            lifecycle aware and the same live data use for every new [Activity/Fragment] and
            will be called onConfiguration changes without need.
        */
        mCrimeSelectedLiveData = new MutableLiveData<>();
        return mCrimeSelectedLiveData;
    }

    public CrimeViewModel(@NonNull Application application) {
        super(application);

        mRepository = CrimeRepository.getInstance(application);
        fetchCrimesFromDbToLiveData();
    }

    public void toggleCrimeDetailSubtitleVisibility() {
        mCrimeDetailSubtitleVisible = !mCrimeDetailSubtitleVisible;
    }

    public void fetchCrimesFromDbToLiveData() {
        mCrimesLiveData = mRepository.getCrimesLiveData();
    }

    public void onCrimeSelectedLiveData(Crime crime) {
        mCrimeSelectedLiveData.setValue(crime);
    }

    public void insertNewCrime() {
        Crime crime = new Crime();
        mRepository.insertCrime(crime);

        //select the crime after creating new one to add details to it.
        onCrimeSelectedLiveData(crime);
    }

    public int getNumberOfCrimes() {
        return getCrimesSubject() == null ? 0 : getCrimesSubject().size();
    }

    public int getPosition(UUID crimeId) {
        if (mCrimesSubject != null)
            for (int i = 0; i < mCrimesSubject.size(); i++) {
                if (mCrimesSubject.get(i).getId().equals(crimeId))
                    return i;
            }

        return 0;
    }

    public void navigateToDetail(FragmentActivity activity, Crime crime, boolean isMasterDetail) {
        Log.d(Constants.APP_TAG, "startActivityOrReplaceFragment");

        if (!isMasterDetail) {
            Log.d(Constants.APP_TAG, "startActivity");

            Intent intent = CrimePagerActivity.newIntent(activity, crime.getId());
            activity.startActivity(intent);
        } else {
            Log.d(Constants.APP_TAG, "ReplaceFragment");

            CrimeDetailFragment fragment = CrimeDetailFragment.newInstance(crime.getId());

            activity.getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.detail_fragment_container, fragment)
                    .commit();
        }
    }
}

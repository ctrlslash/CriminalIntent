package com.example.criminalintent.repository;

import com.example.criminalintent.model.Crime;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CrimeRepository implements RepositoryInterface {

    private static CrimeRepository sCrimeRepository;
    private static final int NUMBER_OF_CRIMES = 100;

    public static CrimeRepository getInstance() {
        if (sCrimeRepository == null)
            sCrimeRepository = new CrimeRepository();

        return sCrimeRepository;
    }

    private List<Crime> mCrimes;

    //Read all
    public List<Crime> getCrimes() {
        return mCrimes;
    }

    public void setCrimes(List<Crime> crimes) {
        mCrimes = crimes;
    }

    //Create
    private CrimeRepository() {
        mCrimes = new ArrayList<>();
        for (int i = 0; i < NUMBER_OF_CRIMES; i++) {
            Crime crime = new Crime();
            crime.setTitle("Crime#" + (i + 1));
            crime.setSolved(i % 2 == 0);

            mCrimes.add(crime);
        }
    }

    //Read one
    public Crime getCrime(UUID uuid) {
        for (Crime crime: mCrimes) {
            if (crime.getId().equals(uuid))
                return crime;
        }

        return null;
    }

    //Update one
    public void updateCrime(Crime crime) {
        Crime updateCrime = getCrime(crime.getId());
        updateCrime.setTitle(crime.getTitle());
        updateCrime.setDate(crime.getDate());
        updateCrime.setSolved(crime.isSolved());
    }

    //Delete
    public void deleteCrime(Crime crime) {
        for (int i = 0; i < mCrimes.size(); i++) {
            if (mCrimes.get(i).getId().equals(crime.getId())) {
                mCrimes.remove(i);
                return;
            }
        }
    }
}

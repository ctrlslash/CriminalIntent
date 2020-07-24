package com.example.criminalintent.repository;

import com.example.criminalintent.model.Crime;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CrimeRepository implements RepositoryInterface<Crime> {

    private static CrimeRepository sCrimeRepository;
    private static final int NUMBER_OF_CRIMES = 100;

    private List<Crime> mCrimes;

    public static CrimeRepository getInstance() {
        if (sCrimeRepository == null)
            sCrimeRepository = new CrimeRepository();

        return sCrimeRepository;
    }

    private CrimeRepository() {
        mCrimes = new ArrayList<>();
        for (int i = 0; i < NUMBER_OF_CRIMES; i++) {
            Crime crime = new Crime();
            crime.setTitle("Crime#" + (i + 1));
            crime.setSolved(i % 2 == 0);

            mCrimes.add(crime);
        }
    }

    //Read all
    @Override
    public List<Crime> getList() {
        return mCrimes;
    }

    @Override
    public void setList(List<Crime> crimes) {
        mCrimes = crimes;
    }

    //Read one
    @Override
    public Crime get(UUID uuid) {
        for (Crime crime: mCrimes) {
            if (crime.getId().equals(uuid))
                return crime;
        }

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
        for (int i = 0; i < mCrimes.size(); i++) {
            if (mCrimes.get(i).getId().equals(crime.getId())) {
                mCrimes.remove(i);
                return;
            }
        }
    }

    //Create: Insert
    @Override
    public void insert(Crime crime) {
        mCrimes.add(crime);
    }

    //Create: Insert
    @Override
    public void insertList(List<Crime> crimes) {
        mCrimes.addAll(crimes);
    }
}

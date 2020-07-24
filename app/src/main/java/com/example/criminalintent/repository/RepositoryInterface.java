package com.example.criminalintent.repository;

import com.example.criminalintent.model.Crime;

import java.util.List;
import java.util.UUID;

public interface RepositoryInterface {
    List<Crime> getCrimes();
    Crime getCrime(UUID uuid);
    void updateCrime(Crime crime);
    void deleteCrime(Crime crime);
    void insertCrime(Crime crime);
    void insertCrimes(List<Crime> crimes);
}

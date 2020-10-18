package com.example.criminalintent.data.room.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.criminalintent.data.room.entities.Crime;

import java.util.List;
import java.util.UUID;

@Dao
public interface CrimeDAO {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertCrimes(Crime... crimes);

    @Update
    void updateCrimes(Crime... crimes);

    @Delete
    void deleteCrimes(Crime... crimes);

    @Query("select * from crime")
    LiveData<List<Crime>> getCrimes();

    @Query("select * from crime where id = :id")
    LiveData<Crime> getCrime(UUID id);
}

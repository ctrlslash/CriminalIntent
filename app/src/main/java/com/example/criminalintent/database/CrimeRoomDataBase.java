package com.example.criminalintent.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.example.criminalintent.database.dao.CrimeDAO;
import com.example.criminalintent.database.entities.Crime;
import com.example.criminalintent.database.entities.RoomConverters;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(
        entities = {Crime.class},
        version = 1,
        exportSchema = false)
@TypeConverters({RoomConverters.class})
public abstract class CrimeRoomDataBase extends RoomDatabase {

    private static final String DATABASE_NAME = "criminal_intent.db";
    private static final int NUMBER_OF_THREADS = 4;

    public static ExecutorService dataBaseWriteExecutor = Executors.newFixedThreadPool(4);

    public abstract CrimeDAO getCrimeDAO();

    public static CrimeRoomDataBase getDataBase(Context context) {
        return Room.databaseBuilder(
                context.getApplicationContext(),
                CrimeRoomDataBase.class,
                CrimeRoomDataBase.DATABASE_NAME).build();
    }
}

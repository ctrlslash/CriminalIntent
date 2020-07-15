package com.example.criminalintent;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.util.Log;

public class CrimeDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //1. find all fragments and attach them to activity: onAttach
        //2. create all fragments: onCreate
        //3. create all view of fragments: onCreateView
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crime_detail);
    }

    @Override
    protected void onPause() {
        super.onPause();

        //1. find fragment manager of this acitvity
        FragmentManager fragmentManager = getSupportFragmentManager();

        //2. create add transaction and submit it in fragment manager
        fragmentManager
                .beginTransaction()
                .add(R.id.fragment_container, new CrimeDetailFragment())
                .commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
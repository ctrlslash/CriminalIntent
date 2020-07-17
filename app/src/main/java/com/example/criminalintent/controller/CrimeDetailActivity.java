package com.example.criminalintent.controller;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;

import com.example.criminalintent.R;

public class CrimeDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //1. find all fragments and attach them to activity: onAttach
        //2. create all fragments: onCreate
        //3. create all view of fragments: onCreateView
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crime_detail);

        //1. find fragment manager of this acitvity
        FragmentManager fragmentManager = getSupportFragmentManager();

        //check if fragment exists or not
        Fragment fragment = fragmentManager.findFragmentById(R.id.fragment_container);

        if (fragment == null) {
            //2. create add transaction and submit it in fragment manager
            fragmentManager
                    .beginTransaction()
                    .add(R.id.fragment_container, new CrimeDetailFragment())
                    .commit();
        }
    }
}
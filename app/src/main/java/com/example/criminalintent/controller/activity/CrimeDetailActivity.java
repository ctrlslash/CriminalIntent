package com.example.criminalintent.controller.activity;

import android.content.Context;
import android.content.Intent;

import androidx.fragment.app.Fragment;

import com.example.criminalintent.controller.fragment.CrimeDetailFragment;

import java.util.UUID;

public class CrimeDetailActivity extends SingleFragmentActivity {

    private static final String EXTRA_CRIME_ID = "com.example.criminalintent.CrimeId";

    /**
     * Every component that want to start this activity
     * must create its intent from this method
     * @param context context of src
     * @param crimeId this activity need a crime id to work
     * @return an intent that can start this activity
     */
    public static Intent newIntent(Context context, UUID crimeId) {
        Intent intent = new Intent(context, CrimeDetailActivity.class);
        intent.putExtra(EXTRA_CRIME_ID, crimeId);
        return intent;
    }

    @Override
    public Fragment createFragment() {
        UUID crimeId = (UUID) getIntent().getSerializableExtra(EXTRA_CRIME_ID);
        return CrimeDetailFragment.newInstance(crimeId);
    }
}
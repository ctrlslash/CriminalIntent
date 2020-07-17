package com.example.criminalintent.controller.activity;

import androidx.fragment.app.Fragment;

import com.example.criminalintent.controller.fragment.CrimeDetailFragment;

public class CrimeDetailActivity extends SingleFragmentActivity {

    @Override
    public Fragment createFragment() {
        return new CrimeDetailFragment();
    }
}
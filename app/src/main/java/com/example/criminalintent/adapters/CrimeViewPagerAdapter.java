package com.example.criminalintent.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.criminalintent.view.fragment.CrimeDetailFragment;
import com.example.criminalintent.viewmodel.CrimeViewModel;

public class CrimeViewPagerAdapter extends FragmentStateAdapter {

    private CrimeViewModel mCrimeViewModel;

    public CrimeViewPagerAdapter(
            @NonNull FragmentActivity fragmentActivity, CrimeViewModel crimeViewModel) {
        super(fragmentActivity);
        mCrimeViewModel = crimeViewModel;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return CrimeDetailFragment.newInstance(
                mCrimeViewModel.getCrimesSubject().get(position).getId());
    }

    @Override
    public int getItemCount() {
        return mCrimeViewModel.getCrimesSubject().size();
    }
}
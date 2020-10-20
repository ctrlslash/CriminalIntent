package com.example.criminalintent.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.criminalintent.view.fragment.CrimeDetailFragment;
import com.example.criminalintent.viewmodel.CrimeListViewModel;

public class CrimeViewPagerAdapter extends FragmentStateAdapter {

    private CrimeListViewModel mCrimeListViewModel;

    public CrimeViewPagerAdapter(
            @NonNull FragmentActivity fragmentActivity, CrimeListViewModel crimeListViewModel) {
        super(fragmentActivity);
        mCrimeListViewModel = crimeListViewModel;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return CrimeDetailFragment.newInstance(
                mCrimeListViewModel.getCrimesSubject().get(position).getId());
    }

    @Override
    public int getItemCount() {
        return mCrimeListViewModel.getCrimesSubject().size();
    }
}
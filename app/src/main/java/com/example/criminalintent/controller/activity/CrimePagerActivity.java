package com.example.criminalintent.controller.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.criminalintent.R;
import com.example.criminalintent.controller.fragment.CrimeDetailFragment;
import com.example.criminalintent.database.entities.Crime;
import com.example.criminalintent.databinding.ActivityCrimePagerBinding;
import com.example.criminalintent.repository.CrimeRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CrimePagerActivity extends AppCompatActivity implements CrimeDetailFragment.Callbacks {

    private static final String EXTRA_CRIME_ID = "com.example.criminalintent.CrimeId";
    public static final String TAG = "CPA";

    private ActivityCrimePagerBinding mBinding;

    private CrimeRepository mRepository;

    public static Intent newIntent(Context context, UUID crimeId) {
        Intent intent = new Intent(context, CrimePagerActivity.class);
        intent.putExtra(EXTRA_CRIME_ID, crimeId);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_crime_pager);

        mRepository = CrimeRepository.getInstance(this);

        LiveData<List<Crime>> crimesLiveData = mRepository.getCrimesLiveData();
        crimesLiveData.observe(this, new Observer<List<Crime>>() {
            @Override
            public void onChanged(List<Crime> crimes) {
                Log.d(TAG, "1111111111111111111111111111111111111111111111111111111111111");
                crimesLiveData.removeObserver(this);

                UUID crimeId = (UUID) getIntent().getSerializableExtra(EXTRA_CRIME_ID);
                int position = mRepository.getPosition(crimeId);
                setUI(crimes, position);

            }
        });
    }

    private void setUI(List<Crime> crimes, int position) {
        if (crimes == null)
            crimes = new ArrayList<>();

        FragmentStateAdapter adapter =
                new CrimeViewPagerAdapter(this, crimes);
        mBinding.crimeViewPager.setAdapter(adapter);

        //this method "must" be placed after setAdapter.
        mBinding.crimeViewPager.setCurrentItem(position);
    }

    @Override
    public void onCrimeUpdated(Crime crime) {
        //nothing
    }

    private class CrimeViewPagerAdapter extends FragmentStateAdapter {

        private List<Crime> mCrimes;

        public List<Crime> getCrimes() {
            return mCrimes;
        }

        public void setCrimes(List<Crime> crimes) {
            mCrimes = crimes;
        }

        public CrimeViewPagerAdapter(@NonNull FragmentActivity fragmentActivity, List<Crime> crimes) {
            super(fragmentActivity);

            mCrimes = crimes;
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            Log.d(TAG, "position: " + (position + 1));
            return CrimeDetailFragment.newInstance(mCrimes.get(position).getId());
        }

        @Override
        public int getItemCount() {
            return mCrimes.size();
        }
    }
}
package com.example.criminalintent.controller.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.example.criminalintent.R;
import com.example.criminalintent.controller.fragment.CrimeDetailFragment;
import com.example.criminalintent.model.Crime;
import com.example.criminalintent.repository.CrimeDBRepository;
import com.example.criminalintent.repository.IRepository;

import java.util.List;
import java.util.UUID;

public class CrimePagerActivity extends AppCompatActivity {

    private static final String EXTRA_CRIME_ID = "com.example.criminalintent.CrimeId";
    public static final String TAG = "CPA";

    private ViewPager2 mCrimeViewPager;
    private IRepository mRepository;

    public static Intent newIntent(Context context, UUID crimeId) {
        Intent intent = new Intent(context, CrimePagerActivity.class);
        intent.putExtra(EXTRA_CRIME_ID, crimeId);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crime_pager);

        mRepository = CrimeDBRepository.getInstance(this);
        UUID crimeId = (UUID) getIntent().getSerializableExtra(EXTRA_CRIME_ID);
        int position = mRepository.getPosition(crimeId);

        findViews();
        setUI(position);
    }

    private void findViews() {
        mCrimeViewPager = findViewById(R.id.crime_view_pager);
    }

    private void setUI(int position) {
        FragmentStateAdapter adapter = new CrimeViewPagerAdapter(this, mRepository.getList());
        mCrimeViewPager.setAdapter(adapter);

        //this method "must" be placed after setAdapter.
        mCrimeViewPager.setCurrentItem(position);
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
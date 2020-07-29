package com.example.criminalintent.controller.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.example.criminalintent.R;
import com.example.criminalintent.controller.fragment.CrimeDetailFragment;
import com.example.criminalintent.model.Crime;
import com.example.criminalintent.repository.CrimeRepository;
import com.example.criminalintent.repository.IRepository;

import java.util.List;

public class CrimePagerActivity extends AppCompatActivity {

    private ViewPager2 mCrimeViewPager;
    private IRepository mRepository;

    public static Intent newIntent(Context context) {
        return new Intent(context, CrimePagerActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crime_pager);

        mRepository = CrimeRepository.getInstance();

        findViews();
        setUI();
    }

    private void findViews() {
        mCrimeViewPager = findViewById(R.id.crime_view_pager);
    }

    private void setUI() {
        FragmentStateAdapter adapter = new CrimeViewPagerAdapter(this, mRepository.getList());
        mCrimeViewPager.setAdapter(adapter);
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
            return CrimeDetailFragment.newInstance(mCrimes.get(position).getId());
        }

        @Override
        public int getItemCount() {
            return mCrimes.size();
        }
    }
}
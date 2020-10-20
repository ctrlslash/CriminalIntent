package com.example.criminalintent.view.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.criminalintent.R;
import com.example.criminalintent.adapters.CrimeViewPagerAdapter;
import com.example.criminalintent.data.room.entities.Crime;
import com.example.criminalintent.databinding.ActivityCrimePagerBinding;
import com.example.criminalintent.utilities.Constants;
import com.example.criminalintent.view.observers.SingleEventObserver;
import com.example.criminalintent.viewmodel.CrimeListViewModel;

import java.util.List;
import java.util.UUID;

public class CrimePagerActivity extends AppCompatActivity {

    private static final String EXTRA_CRIME_ID = "com.example.criminalintent.CrimeId";
    public static final String TAG = "CPA";

    private ActivityCrimePagerBinding mBinding;
    private CrimeListViewModel mCrimeListViewModel;

    public static Intent newIntent(Context context, UUID crimeId) {
        Intent intent = new Intent(context, CrimePagerActivity.class);
        intent.putExtra(EXTRA_CRIME_ID, crimeId);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_crime_pager);

        mCrimeListViewModel = new ViewModelProvider(this).get(CrimeListViewModel.class);

        LiveData<List<Crime>> crimesLiveData = mCrimeListViewModel.getCrimesLiveData();
        crimesLiveData.observe(
                this,
                new SingleEventObserver<List<Crime>>(this, crimesLiveData) {

                    @Override
                    public void onChanged(List<Crime> crimes) {
                        super.onChanged(crimes);
                        Log.d(Constants.APP_TAG, "OnChanged: CrimePagerActivity");

                        mCrimeListViewModel.setCrimesSubject(crimes);

                        UUID crimeId = (UUID) getIntent().getSerializableExtra(EXTRA_CRIME_ID);
                        int position = mCrimeListViewModel.getPosition(crimeId);
                        setUI(position);
                    }
                });
    }

    private void setUI(int position) {
        FragmentStateAdapter adapter =
                new CrimeViewPagerAdapter(this, mCrimeListViewModel);
        mBinding.crimeViewPager.setAdapter(adapter);

        //this method "must" be placed after setAdapter.
        mBinding.crimeViewPager.setCurrentItem(position);
    }
}
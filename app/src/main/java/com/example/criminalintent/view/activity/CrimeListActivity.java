package com.example.criminalintent.view.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.criminalintent.R;
import com.example.criminalintent.data.room.entities.Crime;
import com.example.criminalintent.utilities.Constants;
import com.example.criminalintent.view.fragment.CrimeListFragment;
import com.example.criminalintent.viewmodel.CrimeViewModel;

public class CrimeListActivity extends SingleFragmentActivity {

    private CrimeViewModel mCrimeViewModel;

    public static Intent newIntent(Context context) {
        Intent intent = new Intent(context, CrimeListActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mCrimeViewModel = new ViewModelProvider(this).get(CrimeViewModel.class);
        mCrimeViewModel.getCrimeSelectedLiveData().observe(this, new Observer<Crime>() {
            @Override
            public void onChanged(Crime crime) {
                Log.d(Constants.APP_TAG, "onChanged: CrimeListActivity");

                mCrimeViewModel.navigateToDetail(
                        CrimeListActivity.this,
                        crime,
                        isMasterDetail());
            }
        });
    }

    @Override
    public Fragment createFragment() {
        return CrimeListFragment.newInstance();
    }

    @Override
    public int getLayoutResId() {
        return R.layout.activity_master_detail;
    }

    private boolean isMasterDetail() {
        return findViewById(R.id.detail_fragment_container) != null;
    }
}
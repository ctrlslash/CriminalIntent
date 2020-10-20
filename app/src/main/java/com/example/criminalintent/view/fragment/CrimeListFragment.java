package com.example.criminalintent.view.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.criminalintent.R;
import com.example.criminalintent.adapters.CrimeAdapter;
import com.example.criminalintent.data.room.entities.Crime;
import com.example.criminalintent.databinding.FragmentCrimeListBinding;
import com.example.criminalintent.utilities.Constants;
import com.example.criminalintent.viewmodel.CrimeDetailViewModel;
import com.example.criminalintent.viewmodel.CrimeViewModel;

import java.util.ArrayList;
import java.util.List;

public class CrimeListFragment extends Fragment {

    public static final String TAG = "CLF";

    private FragmentCrimeListBinding mBinding;
    private CrimeAdapter mAdapter;

    //shared view model between: this, CrimeListActivity, CrimeAdapter
    private CrimeViewModel mCrimeViewModel;

    public CrimeListFragment() {
        // Required empty public constructor
    }

    public static CrimeListFragment newInstance() {

        Bundle args = new Bundle();

        CrimeListFragment fragment = new CrimeListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        mCrimeViewModel = new ViewModelProvider(requireActivity()).get(CrimeViewModel.class);
        mCrimeViewModel.getCrimesLiveData().observe(this, new Observer<List<Crime>>() {
            @Override
            public void onChanged(List<Crime> crimes) {
                Log.d(Constants.APP_TAG, "OnChanged: CrimeListFragment");

                mCrimeViewModel.setCrimesSubject(crimes);
                updateUI();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_crime_list,
                container,
                false);

        //recyclerview responsibility: positioning
        mBinding.recyclerViewCrimes.setLayoutManager(new LinearLayoutManager(getActivity()));

        return mBinding.getRoot();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_crime_list, menu);
        updateMenuItemSubtitle(menu.findItem(R.id.menu_item_subtitle));
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_new_crime:
                addCrime();
                return true;
            case R.id.menu_item_subtitle:
                mCrimeViewModel.toggleCrimeDetailSubtitleVisibility();
                updateSubtitle();
                getActivity().invalidateOptionsMenu();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    private void addCrime() {
        mCrimeViewModel.insertNewCrime();
    }

    public void updateUI() {
        if (mAdapter == null) {
            mAdapter = new CrimeAdapter(mCrimeViewModel);
            mBinding.recyclerViewCrimes.setAdapter(mAdapter);
        } else {
            mAdapter.notifyDataSetChanged();
        }

        updateSubtitle();
    }

    private void updateSubtitle() {
        AppCompatActivity activity = (AppCompatActivity) getActivity();

        String subtitle = mCrimeViewModel.isCrimeDetailSubtitleVisible() ?
                getString(R.string.subtitle_format, mCrimeViewModel.getNumberOfCrimes()) :
                null;

        activity.getSupportActionBar().setSubtitle(subtitle);
    }

    private void updateMenuItemSubtitle(@NonNull MenuItem item) {
        item.setTitle(mCrimeViewModel.isCrimeDetailSubtitleVisible() ?
                R.string.hide_subtitle :
                R.string.show_subtitle);
    }
}
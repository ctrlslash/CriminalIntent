package com.example.criminalintent.view.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;

import com.example.criminalintent.R;
import com.example.criminalintent.data.room.entities.Crime;
import com.example.criminalintent.databinding.FragmentCrimeDetailBinding;
import com.example.criminalintent.utilities.Constants;
import com.example.criminalintent.view.observers.SingleEventObserver;
import com.example.criminalintent.viewmodel.CrimeDetailViewModel;

import java.util.Date;
import java.util.UUID;

public class CrimeDetailFragment extends Fragment {

    public static final String TAG = "CDF";
    public static final String ARG_CRIME_ID = "CrimeId";
    public static final String DIALOG_FRAGMENT_TAG = "Dialog";

    public static final int REQUEST_CODE_DATE_PICKER = 0;
    public static final int REQUEST_CODE_SELECT_CONTACT = 1;
    public static final int REQUEST_CODE_IMAGE_CAPTURE = 2;

    private CrimeDetailViewModel mCrimeDetailViewModel;
    private FragmentCrimeDetailBinding mBinding;

    public CrimeDetailFragment() {
        //empty public constructor
    }

    public static CrimeDetailFragment newInstance(UUID crimeId) {

        Bundle args = new Bundle();
        args.putSerializable(ARG_CRIME_ID, crimeId);

        CrimeDetailFragment fragment = new CrimeDetailFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");

        UUID crimeId = (UUID) getArguments().getSerializable(ARG_CRIME_ID);
        mCrimeDetailViewModel = new ViewModelProvider(this).get(CrimeDetailViewModel.class);

        mCrimeDetailViewModel.fetchCrimeFromDbToLiveData(crimeId);
        LiveData<Crime> crimeLiveData = mCrimeDetailViewModel.getCrimeLiveData();
        crimeLiveData.observe(this,
                new SingleEventObserver<Crime>(this, crimeLiveData){
            @Override
            public void onChanged(Crime crime) {
                super.onChanged(crime);
                Log.d(Constants.APP_TAG, "onChanged: CrimeDetailFragment");

                mCrimeDetailViewModel.setCrimeSubject(crime);
                updatePhotoView();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");

        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_crime_detail,
                container,
                false);

        mBinding.setCrimeDetailViewModel(mCrimeDetailViewModel);
        mBinding.setCrimeDetailFragment(this);
        mBinding.setLifecycleOwner(this);
        Log.d(Constants.APP_TAG, "setVariable");

        return mBinding.getRoot();
    }

    private void updateCrimeSubject() {
        mCrimeDetailViewModel.updateCrimeSubject();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode != Activity.RESULT_OK || data == null)
            return;

        if (requestCode == REQUEST_CODE_DATE_PICKER) {
            //get response from intent extra, which is user selected date
            Date userSelectedDate =
                    mCrimeDetailViewModel.getUserSelectedDateResult(data);

            mCrimeDetailViewModel.getCrimeSubjectValue().setDate(userSelectedDate);
            mBinding.crimeDate.setText(userSelectedDate.toString());
            updateCrimeSubject();
        } else if (requestCode == REQUEST_CODE_SELECT_CONTACT) {
            Uri contactUri = data.getData();
            String suspect = mCrimeDetailViewModel.queryDisplayNameOfContact(contactUri);
            mBinding.chooseSuspect.setText(suspect);
        } else if (requestCode == REQUEST_CODE_IMAGE_CAPTURE) {
            updatePhotoView();
            mCrimeDetailViewModel.revokePermission();
        }
    }

    private void updatePhotoView() {
        if (mCrimeDetailViewModel.getPhotoFile() == null ||
                !mCrimeDetailViewModel.getPhotoFile().exists()) {
            mBinding.crimePicture.setImageDrawable(
                    getResources().getDrawable(R.drawable.ic_person));
        } else {
            Bitmap bitmap = mCrimeDetailViewModel.getPhotoScaledBitmap(getActivity());
            mBinding.crimePicture.setImageBitmap(bitmap);
        }
    }
}
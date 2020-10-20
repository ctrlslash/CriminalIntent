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
        /*
        This is only for education:
            Note: using crimeId as unique key to separate view model foreach fragment inside
            viewpager. if we don't use key then only one view model will be share between all
            fragments that will cause lots of mistakes and unpredictable actions.
            (for instance when one model update in one fragment all observers in
            other fragment will get called).
        */
        mCrimeDetailViewModel = new ViewModelProvider(requireActivity())
                .get(crimeId.toString(), CrimeDetailViewModel.class);

        mCrimeDetailViewModel.fetchCrimeFromDbToLiveData(crimeId);

        LiveData<Crime> crimeLiveData = mCrimeDetailViewModel.getCrimeLiveData();
        crimeLiveData.observe(this, new SingleEventObserver<Crime>(this, crimeLiveData){
            @Override
            public void onChanged(Crime crime) {
                super.onChanged(crime);
                Log.d(Constants.APP_TAG, "onChanged: CrimeDetailFragment");

                mCrimeDetailViewModel.setCrimeSubject(crime);
                initViews();
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

        setListeners();

        return mBinding.getRoot();
    }

    private void initViews() {
        mBinding.crimeTitle.setText(mCrimeDetailViewModel.getCrimeSubject().getTitle());
        mBinding.crimeSolved.setChecked(mCrimeDetailViewModel.getCrimeSubject().isSolved());
        mBinding.crimeDate.setText(mCrimeDetailViewModel.getCrimeSubject().getDate().toString());

        if (mCrimeDetailViewModel.getCrimeSubject().getSuspect() != null)
            mBinding.chooseSuspect.setText(mCrimeDetailViewModel.getCrimeSubject().getSuspect());

        updatePhotoView();
    }

    private void setListeners() {
        mBinding.crimeTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mCrimeDetailViewModel.getCrimeSubject().setTitle(charSequence.toString());
                Log.d("CPA", mCrimeDetailViewModel.getCrimeSubject().getTitle());

                updateCrimeSubject();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        mBinding.crimeSolved.setOnCheckedChangeListener((compoundButton, checked) -> {
            mCrimeDetailViewModel.getCrimeSubject().setSolved(checked);
            Log.d(TAG, mCrimeDetailViewModel.getCrimeSubject().toString());

            updateCrimeSubject();
        });
        mBinding.crimeDate.setOnClickListener(view -> {
            mCrimeDetailViewModel.showDataPickerFragment(CrimeDetailFragment.this);
        });

        mBinding.shareReport.setOnClickListener(v -> {
            mCrimeDetailViewModel.shareReportWithApps(CrimeDetailFragment.this);
        });

        mBinding.chooseSuspect.setOnClickListener(v -> {
            mCrimeDetailViewModel.pickContactFromApps(CrimeDetailFragment.this);
        });

        mBinding.captureImage.setOnClickListener(v -> {
            mCrimeDetailViewModel.takePicture(CrimeDetailFragment.this);
        });
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

            mCrimeDetailViewModel.getCrimeSubject().setDate(userSelectedDate);
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
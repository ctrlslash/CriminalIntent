package com.example.criminalintent.controller.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.criminalintent.R;
import com.example.criminalintent.model.Crime;
import com.example.criminalintent.repository.CrimeDBRepository;
import com.example.criminalintent.repository.IRepository;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class CrimeDetailFragment extends Fragment {

    public static final String TAG = "CDF";
    public static final String BUNDLE_CRIME = "crime";
    public static final String ARG_CRIME_ID = "CrimeId";
    public static final String DIALOG_FRAGMENT_TAG = "Dialog";
    public static final int DATE_PICKER_REQUEST_CODE = 0;

    private Crime mCrime;
    private IRepository<Crime> mRepository;

    private EditText mEditTextCrimeTitle;
    private Button mButtonDate;
    private CheckBox mCheckBoxSolved;
    private Button mButtonSuspect;
    private Button mButtonShareReport;

    public CrimeDetailFragment() {
        //empty public constructor
    }

    /**
     * Using factory pattern to create this fragment. every class that want
     * to create this fragment should always call this method "only".
     * no class should call constructor any more.
     * @param crimeId this fragment need crime id to work properly.
     * @return new CrimeDetailFragment
     */
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

        mRepository = CrimeDBRepository.getInstance(getActivity());

        //This is very very wrong: this is memory of hosted activity
        //UUID crimeId = (UUID) getActivity().getIntent().getSerializableExtra(CrimeDetailActivity.EXTRA_CRIME_ID);

        UUID crimeId = (UUID) getArguments().getSerializable(ARG_CRIME_ID);
        mCrime = mRepository.get(crimeId);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_crime_detail, container, false);

        findViews(view);
        initViews();
        setListeners();

        return view;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putSerializable(BUNDLE_CRIME, mCrime);
    }

    @Override
    public void onPause() {
        super.onPause();

        updateCrime();
    }

    private void findViews(View view) {
        mEditTextCrimeTitle = view.findViewById(R.id.crime_title);
        mButtonDate = view.findViewById(R.id.crime_date);
        mCheckBoxSolved = view.findViewById(R.id.crime_solved);
        mButtonSuspect = view.findViewById(R.id.choose_suspect);
        mButtonShareReport = view.findViewById(R.id.share_report);
    }

    private void initViews() {
        mEditTextCrimeTitle.setText(mCrime.getTitle());
        mCheckBoxSolved.setChecked(mCrime.isSolved());
        mButtonDate.setText(mCrime.getDate().toString());
    }

    private void setListeners() {
        mEditTextCrimeTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mCrime.setTitle(charSequence.toString());
                Log.d(TAG, mCrime.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        mCheckBoxSolved.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                mCrime.setSolved(checked);
                Log.d(TAG, mCrime.toString());
            }
        });
        mButtonDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerFragment datePickerFragment = DatePickerFragment.newInstance(mCrime.getDate());

                //create parent-child relations between CrimeDetailFragment-DatePickerFragment
                datePickerFragment.setTargetFragment(CrimeDetailFragment.this, DATE_PICKER_REQUEST_CODE);

                datePickerFragment.show(getFragmentManager(), DIALOG_FRAGMENT_TAG);
            }
        });

        mButtonShareReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sendIntent = new Intent(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, getReportText());
                sendIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.crime_report_subject));
                sendIntent.setType("text/plain");

                Intent shareIntent = Intent.createChooser(sendIntent, null);
                startActivity(shareIntent);
            }
        });

        mButtonSuspect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: choose suspect from contact application
            }
        });
    }

    private String getReportText() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd");
        String dateString = simpleDateFormat.format(mCrime.getDate()) ;

        String solvedString = mCrime.isSolved() ?
                getString(R.string.crime_report_solved) :
                getString(R.string.crime_report_unsolved);

        //TODO: implement the suspect string after adding suspect.
        String suspectString = "no suspect";

        String report = getString(R.string.crime_report,
                mCrime.getTitle(),
                dateString,
                solvedString,
                suspectString);

        return report;
    }

    private void updateCrime() {
        mRepository.update(mCrime);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode != Activity.RESULT_OK || data == null)
            return;

        if (requestCode == DATE_PICKER_REQUEST_CODE) {
            //get response from intent extra, which is user selected date
            Date userSelectedDate = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_USER_SELECTED_DATE);

            mCrime.setDate(userSelectedDate);
            mButtonDate.setText(mCrime.getDate().toString());

            updateCrime();
        }
    }

    /*void onResultFromDatePicker(Date userPickedDate) {
        mCrime.setDate(userPickedDate);
        mButtonDate.setText(mCrime.getDate().toString());

        updateCrime();
    }*/
}
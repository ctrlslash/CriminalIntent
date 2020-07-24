package com.example.criminalintent.controller.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.criminalintent.R;
import com.example.criminalintent.model.Crime;
import com.example.criminalintent.repository.CrimeRepository;
import com.example.criminalintent.repository.RepositoryInterface;

import java.util.List;

public class CrimeListFragment extends Fragment {

    public static final String TAG = "CLF";
    private RecyclerView mRecyclerView;
    private RepositoryInterface mRepository;

    public CrimeListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mRepository = CrimeRepository.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_crime_list, container, false);

        findViews(view);

        //recyclerview responsibility: positioning
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        initUI();

        return view;
    }

    private void findViews(View view) {
        mRecyclerView = view.findViewById(R.id.recycler_view_crimes);
    }

    private void initUI() {
        List<Crime> crimes = mRepository.getCrimes();
        CrimeAdapter adapter = new CrimeAdapter(crimes);
        mRecyclerView.setAdapter(adapter);
    }

    //view holder responsibility: hold reference to row views.
    private class CrimeHolder extends RecyclerView.ViewHolder {

        private TextView mTextViewTitle;
        private TextView mTextViewDate;
        private ImageView mImageViewSolved;

        public CrimeHolder(@NonNull View itemView) {
            super(itemView);

            mTextViewTitle = itemView.findViewById(R.id.list_row_crime_title);
            mTextViewDate = itemView.findViewById(R.id.list_row_crime_date);
            mImageViewSolved = itemView.findViewById(R.id.imgview_solved);
        }

        public void bindCrime(Crime crime) {
            mTextViewTitle.setText(crime.getTitle());
            mTextViewDate.setText(crime.getDate().toString());

            mImageViewSolved.setVisibility(crime.isSolved() ? View.VISIBLE : View.INVISIBLE);
        }
    }

    /*adapter responsibilities:
        1. getItemCounts
        2. create view holder
        3. bind view holder
     */
    private class CrimeAdapter extends RecyclerView.Adapter<CrimeHolder> {

        private List<Crime> mCrimes;

        public List<Crime> getCrimes() {
            return mCrimes;
        }

        public void setCrimes(List<Crime> crimes) {
            mCrimes = crimes;
        }

        public CrimeAdapter(List<Crime> crimes) {
            mCrimes = crimes;
        }

        @Override
        public int getItemCount() {
            return mCrimes.size();
        }

        @NonNull
        @Override
        public CrimeHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            Log.d(TAG, "onCreateViewHolder");

            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View view = inflater.inflate(R.layout.list_row_crime, parent, false);

            CrimeHolder crimeHolder = new CrimeHolder(view);
            return crimeHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull CrimeHolder holder, int position) {
            Log.d(TAG, "onBindViewHolder: " + position);

            Crime crime = mCrimes.get(position);
            holder.bindCrime(crime);
        }
    }
}
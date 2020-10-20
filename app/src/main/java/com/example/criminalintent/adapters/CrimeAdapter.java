package com.example.criminalintent.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.criminalintent.R;
import com.example.criminalintent.data.room.entities.Crime;
import com.example.criminalintent.databinding.ListRowCrimeBinding;
import com.example.criminalintent.viewmodel.CrimeViewModel;

public class CrimeAdapter extends RecyclerView.Adapter<CrimeAdapter.CrimeHolder> {

    private static final String TAG = "CrimeAdapter";
    private CrimeViewModel mCrimeViewModel;

    public CrimeAdapter(CrimeViewModel crimeViewModel) {
        mCrimeViewModel = crimeViewModel;
    }

    @Override
    public int getItemCount() {
        return mCrimeViewModel.getCrimesSubject().size();
    }

    @NonNull
    @Override
    public CrimeHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder");

        LayoutInflater inflater = LayoutInflater.from(mCrimeViewModel.getApplication());
        ListRowCrimeBinding listRowCrimeBinding = DataBindingUtil.inflate(
                inflater,
                R.layout.list_row_crime,
                parent,
                false);

        CrimeHolder crimeHolder = new CrimeHolder(listRowCrimeBinding);
        return crimeHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CrimeHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: " + position);

        Crime crime = mCrimeViewModel.getCrimesSubject().get(position);
        holder.bindCrime(crime);
    }

    class CrimeHolder extends RecyclerView.ViewHolder {

        private ListRowCrimeBinding mListRowCrimeBinding;
        private Crime mCrime;

        public CrimeHolder(ListRowCrimeBinding listRowCrimeBinding) {
            super(listRowCrimeBinding.getRoot());

            mListRowCrimeBinding = listRowCrimeBinding;

            mListRowCrimeBinding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mCrimeViewModel.onCrimeSelectedLiveData(mCrime);
                }
            });
        }

        public void bindCrime(Crime crime) {
            mCrime = crime;
            mListRowCrimeBinding.listRowCrimeTitle.setText(crime.getTitle());
            mListRowCrimeBinding.listRowCrimeDate.setText(crime.getDate().toString());

            mListRowCrimeBinding.imgviewSolved.setVisibility(
                    crime.isSolved() ? View.VISIBLE : View.INVISIBLE);
        }
    }
}

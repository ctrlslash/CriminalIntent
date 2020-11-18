package com.example.criminalintent.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.example.criminalintent.R;
import com.example.criminalintent.data.room.entities.Crime;
import com.example.criminalintent.databinding.ListRowCrimeBinding;
import com.example.criminalintent.viewmodel.CrimeListViewModel;

public class CrimeAdapter extends RecyclerView.Adapter<CrimeAdapter.CrimeHolder> {

    private static final String TAG = "CrimeAdapter";
    private final CrimeListViewModel mCrimeListViewModel;
    private final LifecycleOwner mOwner;

    public CrimeAdapter(LifecycleOwner owner, CrimeListViewModel crimeListViewModel) {
        mOwner = owner;
        mCrimeListViewModel = crimeListViewModel;
    }

    @Override
    public int getItemCount() {
        return mCrimeListViewModel.getCrimesSubject().size();
    }

    @NonNull
    @Override
    public CrimeHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder");

        LayoutInflater inflater = LayoutInflater.from(mCrimeListViewModel.getApplication());
        ListRowCrimeBinding listRowCrimeBinding = DataBindingUtil.inflate(
                inflater,
                R.layout.list_row_crime,
                parent,
                false);

        return new CrimeHolder(listRowCrimeBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull CrimeHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: " + position);

        Crime crime = mCrimeListViewModel.getCrimesSubject().get(position);
        holder.bindCrime(position);
    }

    class CrimeHolder extends RecyclerView.ViewHolder {

        private final ListRowCrimeBinding mListRowCrimeBinding;

        public CrimeHolder(ListRowCrimeBinding listRowCrimeBinding) {
            super(listRowCrimeBinding.getRoot());

            mListRowCrimeBinding = listRowCrimeBinding;
            mListRowCrimeBinding.setCrimeListViewModel(mCrimeListViewModel);
            mListRowCrimeBinding.setLifecycleOwner(mOwner);
        }

        public void bindCrime(int position) {
            mListRowCrimeBinding.setPosition(position);
            mListRowCrimeBinding.executePendingBindings();
        }
    }
}

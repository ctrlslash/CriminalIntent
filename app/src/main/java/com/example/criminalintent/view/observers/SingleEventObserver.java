package com.example.criminalintent.view.observers;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

/**
 * If observer live data must work only one time extend this class.
 * @param <T>
 */
public abstract class SingleEventObserver<T> implements Observer<T> {

    private LifecycleOwner mLifecycleOwner;
    private LiveData mLiveData;

    public SingleEventObserver(LifecycleOwner lifecycleOwner, LiveData liveData) {
        mLifecycleOwner = lifecycleOwner;
        mLiveData = liveData;
    }

    @Override
    public void onChanged(T t) {
        mLiveData.removeObservers(mLifecycleOwner);
    }
}

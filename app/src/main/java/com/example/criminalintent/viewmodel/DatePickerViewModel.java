package com.example.criminalintent.viewmodel;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.AndroidViewModel;

import java.util.Date;

public class DatePickerViewModel extends AndroidViewModel {

    public static final String EXTRA_USER_SELECTED_DATE =
            "com.example.criminalintent.userSelectedDate";

    public DatePickerViewModel(@NonNull Application application) {
        super(application);
    }

    public void setResult(Fragment fragment, Date userSelectedDate) {
        Fragment targetFragment = fragment.getTargetFragment();
        Intent intent = new Intent();
        intent.putExtra(EXTRA_USER_SELECTED_DATE, userSelectedDate);
        targetFragment.onActivityResult(fragment.getTargetRequestCode(), Activity.RESULT_OK, intent);
    }
}

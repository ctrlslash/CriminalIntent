package com.example.criminalintent.viewmodel;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.criminalintent.R;
import com.example.criminalintent.data.repository.CrimeRepository;
import com.example.criminalintent.data.room.entities.Crime;
import com.example.criminalintent.utilities.Constants;
import com.example.criminalintent.utilities.PictureUtils;
import com.example.criminalintent.view.fragment.CrimeDetailFragment;
import com.example.criminalintent.view.fragment.DatePickerFragment;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class CrimeDetailViewModel extends AndroidViewModel {

    public static final String FILE_PROVIDER_AUTHORITY = "com.example.criminalintent.fileprovider";

    private CrimeRepository mRepository;
    private File mPhotoFile;

    //the result of live data fetch from room will be saved in this field.
    private MutableLiveData<Crime> mCrimeSubjectLiveData = new MutableLiveData<>();

    //this is live data object for room (only fetching).
    private LiveData<Crime> mCrimeLiveData;

    public MutableLiveData<Crime> getCrimeSubject() {
        return mCrimeSubjectLiveData;
    }

    public Crime getCrimeSubjectValue() {
        return mCrimeSubjectLiveData.getValue();
    }

    public void setCrimeSubject(Crime crimeSubject) {
        mCrimeSubjectLiveData.setValue(crimeSubject);
        setPhotoFile(crimeSubject);
    }

    public LiveData<Crime> getCrimeLiveData() {
        return mCrimeLiveData;
    }

    public File getPhotoFile() {
        return mPhotoFile;
    }

    public void setPhotoFile(Crime crime) {
        mPhotoFile = mRepository.getPhotoFile(crime);
    }

    public CrimeDetailViewModel(@NonNull Application application) {
        super(application);

        mRepository = CrimeRepository.getInstance(application);
    }

    public void fetchCrimeFromDbToLiveData(UUID crimeId) {
        mCrimeLiveData = mRepository.getCrimeLiveData(crimeId);
    }

    public void updateCrimeSubject() {
        mRepository.updateCrime(getCrimeSubjectValue());
    }

    public Date getUserSelectedDateResult(Intent data) {
        return (Date) data.getSerializableExtra(DatePickerViewModel.EXTRA_USER_SELECTED_DATE);
    }

    public Bitmap getPhotoScaledBitmap(Activity activity) {
        return PictureUtils.getScaledBitmap(
                getPhotoFile().getPath(),
                activity);
    }

    public void onTextChangedCrimeTitle(CharSequence charSequence, int i, int i1, int i2) {
        getCrimeSubjectValue().setTitle(charSequence.toString());
        Log.d(Constants.APP_TAG, getCrimeSubjectValue().getTitle());

        updateCrimeSubject();
    }

    public void onCheckedChangedSolved(CompoundButton buttonView, boolean isChecked) {
        getCrimeSubjectValue().setSolved(isChecked);
        Log.d(Constants.APP_TAG, getCrimeSubject().toString());

        updateCrimeSubject();
    }

    public void onClickCrimeDate(Fragment fragment) {
        DatePickerFragment datePickerFragment =
                DatePickerFragment.newInstance(getCrimeSubjectValue().getDate());

        //create parent-child relations between CrimeDetailFragment-DatePickerFragment
        datePickerFragment.setTargetFragment(fragment,
                CrimeDetailFragment.REQUEST_CODE_DATE_PICKER);

        datePickerFragment.show(fragment.getParentFragmentManager(),
                CrimeDetailFragment.DIALOG_FRAGMENT_TAG);
    }

    public void onClickShareReport(Fragment fragment) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT, getReportText());
        intent.putExtra(Intent.EXTRA_SUBJECT,
                getApplication().getString(R.string.crime_report_subject));
        intent.setType("text/plain");

        Intent sendIntent = Intent.createChooser(intent, null);
        if (sendIntent.resolveActivity(getApplication().getPackageManager()) != null)
            fragment.startActivity(sendIntent);
    }

    public void onClickChooseSuspect(Fragment fragment) {
        Intent pickContactIntent = new Intent(Intent.ACTION_PICK);
        pickContactIntent.setType(ContactsContract.Contacts.CONTENT_TYPE);

        if (pickContactIntent.resolveActivity(getApplication().getPackageManager()) != null)
            fragment.startActivityForResult(pickContactIntent,
                    CrimeDetailFragment.REQUEST_CODE_SELECT_CONTACT);
    }

    public void onClickCaptureImage(Fragment fragment) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getApplication().getPackageManager()) != null) {
            if (getPhotoFile() == null)
                return;

            Uri photoURI = FileProvider.getUriForFile(
                    getApplication(),
                    FILE_PROVIDER_AUTHORITY,
                    getPhotoFile());

            grantTemPermissionForTakePicture(takePictureIntent, photoURI);

            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            fragment.startActivityForResult(takePictureIntent,
                    CrimeDetailFragment.REQUEST_CODE_IMAGE_CAPTURE);
        }
    }

    private void grantTemPermissionForTakePicture(Intent takePictureIntent, Uri photoURI) {
        PackageManager packageManager = getApplication().getPackageManager();
        List<ResolveInfo> activities = packageManager.queryIntentActivities(
                takePictureIntent,
                PackageManager.MATCH_DEFAULT_ONLY);

        for (ResolveInfo activity : activities) {
            getApplication().grantUriPermission(activity.activityInfo.packageName,
                    photoURI,
                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        }
    }

    public void revokePermission() {
        Uri photoUri = FileProvider.getUriForFile(
                getApplication(),
                FILE_PROVIDER_AUTHORITY,
                getPhotoFile());
        getApplication().revokeUriPermission(photoUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
    }

    public String queryDisplayNameOfContact(Uri contactUri) {
        String result = null;

        String[] columns = new String[]{ContactsContract.Contacts.DISPLAY_NAME};
        Cursor cursor = getApplication().getContentResolver().query(contactUri,
                columns,
                null,
                null,
                null);

        if (cursor == null || cursor.getCount() == 0)
            return result;

        try {
            cursor.moveToFirst();

            result = cursor.getString(
                    cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
            getCrimeSubjectValue().setSuspect(result);
            updateCrimeSubject();
        } finally {
            cursor.close();
            return result;
        }
    }

    private String getReportText() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd");
        String dateString = simpleDateFormat.format(getCrimeSubjectValue().getDate());

        String solvedString = getCrimeSubjectValue().isSolved() ?
                getApplication().getString(R.string.crime_report_solved) :
                getApplication().getString(R.string.crime_report_unsolved);

        String suspectString = getCrimeSubjectValue().getSuspect() == null ?
                getApplication().getString(R.string.crime_report_no_suspect) :
                getApplication().getString(R.string.crime_report_suspect,
                        getCrimeSubjectValue().getSuspect());

        String report = getApplication().getString(R.string.crime_report,
                getCrimeSubjectValue().getTitle(),
                dateString,
                solvedString,
                suspectString);

        return report;
    }
}

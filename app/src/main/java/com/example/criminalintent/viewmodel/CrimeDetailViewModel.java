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

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.criminalintent.R;
import com.example.criminalintent.data.repository.CrimeRepository;
import com.example.criminalintent.data.room.entities.Crime;
import com.example.criminalintent.utilities.PictureUtils;
import com.example.criminalintent.view.fragment.DatePickerFragment;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static com.example.criminalintent.view.fragment.CrimeDetailFragment.DIALOG_FRAGMENT_TAG;
import static com.example.criminalintent.view.fragment.CrimeDetailFragment.REQUEST_CODE_DATE_PICKER;
import static com.example.criminalintent.view.fragment.CrimeDetailFragment.REQUEST_CODE_IMAGE_CAPTURE;
import static com.example.criminalintent.view.fragment.CrimeDetailFragment.REQUEST_CODE_SELECT_CONTACT;

public class CrimeDetailViewModel extends AndroidViewModel {

    public static final String FILE_PROVIDER_AUTHORITY = "com.example.criminalintent.fileprovider";

    private CrimeRepository mRepository;
    private Crime mCrimeSubject;
    private File mPhotoFile;

    private LiveData<Crime> mCrimeLiveData;

    public Crime getCrimeSubject() {
        return mCrimeSubject;
    }

    public void setCrimeSubject(Crime crimeSubject) {
        mCrimeSubject = crimeSubject;
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
        mRepository.updateCrime(mCrimeSubject);
    }

    public boolean resolveActivity(Intent intent) {
        return intent.resolveActivity(getApplication().getPackageManager()) != null;
    }

    public Date getUserSelectedDateResult(Intent data) {
        return (Date) data.getSerializableExtra(DatePickerViewModel.EXTRA_USER_SELECTED_DATE);
    }

    public Bitmap getPhotoScaledBitmap(Activity activity) {
        return PictureUtils.getScaledBitmap(
                getPhotoFile().getPath(),
                activity);
    }

    public void showDataPickerFragment(Fragment fragment) {
        DatePickerFragment datePickerFragment =
                DatePickerFragment.newInstance(mCrimeSubject.getDate());

        //create parent-child relations between CrimeDetailFragment-DatePickerFragment
        datePickerFragment.setTargetFragment(fragment, REQUEST_CODE_DATE_PICKER);

        datePickerFragment.show(fragment.getParentFragmentManager(), DIALOG_FRAGMENT_TAG);
    }

    public void shareReportWithApps(Fragment fragment) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT, getReportText());
        intent.putExtra(Intent.EXTRA_SUBJECT,
                getApplication().getString(R.string.crime_report_subject));
        intent.setType("text/plain");

        Intent sendIntent = Intent.createChooser(intent, null);
        if (sendIntent.resolveActivity(getApplication().getPackageManager()) != null)
            fragment.startActivity(sendIntent);
    }

    public void pickContactFromApps(Fragment fragment) {
        Intent pickContactIntent = new Intent(Intent.ACTION_PICK);
        pickContactIntent.setType(ContactsContract.Contacts.CONTENT_TYPE);

        if (pickContactIntent.resolveActivity(getApplication().getPackageManager()) != null)
            fragment.startActivityForResult(pickContactIntent, REQUEST_CODE_SELECT_CONTACT);
    }

    public void takePicture(Fragment fragment) {
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
            fragment.startActivityForResult(takePictureIntent, REQUEST_CODE_IMAGE_CAPTURE);
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

            result = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
            mCrimeSubject.setSuspect(result);
            updateCrimeSubject();
        } finally {
            cursor.close();
            return result;
        }
    }

    private String getReportText() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd");
        String dateString = simpleDateFormat.format(mCrimeSubject.getDate());

        String solvedString = mCrimeSubject.isSolved() ?
                getApplication().getString(R.string.crime_report_solved) :
                getApplication().getString(R.string.crime_report_unsolved);

        String suspectString = mCrimeSubject.getSuspect() == null ?
                getApplication().getString(R.string.crime_report_no_suspect) :
                getApplication().getString(R.string.crime_report_suspect,
                        mCrimeSubject.getSuspect());

        String report = getApplication().getString(R.string.crime_report,
                mCrimeSubject.getTitle(),
                dateString,
                solvedString,
                suspectString);

        return report;
    }
}

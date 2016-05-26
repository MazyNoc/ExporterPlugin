package com.nordicusability.exporterplugin;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.nordicusability.jiffy.contract.ExportData;
import com.nordicusability.jiffy.contract.OwnerContract;
import com.nordicusability.jiffy.contract.TimeEntryContract;
import com.nordicusability.jiffy.contract.data.OwnerHierarchy;
import com.nordicusability.jiffy.contract.data.TimeEntry;
import com.nordicusability.jiffy.contract.data.TimeEntryFactory;

import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<MainActivity.ReportData> {

    private static final String TAG = MainActivity.class.getSimpleName();

    public static class ReportData {

        private final OwnerHierarchy timeOwners;
        private final List<TimeEntry> entries;

        public ReportData(final OwnerHierarchy timeOwners, final List<TimeEntry> entries) {
            this.timeOwners = timeOwners;
            this.entries = entries;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate() called with: " + "savedInstanceState = [" + savedInstanceState + "]");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ExportData exportData = null;
        if (getIntent() != null && getIntent().getExtras() != null) {
            exportData = new ExportData(getIntent().getExtras());
        } else {
            exportData = new ExportData(null, System.currentTimeMillis(), System.currentTimeMillis());
        }

        Bundle bundle = new Bundle();
        bundle.putParcelable("EXPORT_DATA", exportData);
        getSupportLoaderManager().initLoader(1, bundle, this);
        Log.d(TAG, "onCreate() ");
    }

    @Override
    public Loader<ReportData> onCreateLoader(final int id, final Bundle args) {
        return new AsyncTaskLoaderEx<ReportData>(this, args) {

            @Override
            public ReportData loadInBackground() {
                ExportData exportData = args.getParcelable("EXPORT_DATA");

                Cursor query = getContentResolver().query(OwnerContract.OWNERS_URI, OwnerContract.ALL_COLUMNS, null, null, null);
                OwnerHierarchy hierarchy = new OwnerHierarchy(query);

                long startTime = exportData.getStartTime();
                long stopTime = exportData.getStopTime();

                Cursor timeEntryCursor = getContentResolver().query(TimeEntryContract.TIME_ENTRIES_URI, TimeEntryContract.ALL_COLUMNS, null, new String[]{String.valueOf(startTime), String.valueOf(stopTime)}, null);
                List<TimeEntry> timeEntries = TimeEntryFactory.convert(timeEntryCursor);

                return new ReportData(hierarchy, timeEntries);
            }
        };
    }

    @Override
    public void onLoadFinished(final Loader<ReportData> loader, final ReportData data) {

    }

    @Override
    public void onLoaderReset(final Loader<ReportData> loader) {

    }
}

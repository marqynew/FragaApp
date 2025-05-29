package com.example.fraga.viewmodel;

import android.app.Application;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.fraga.db.AppDatabase;
import com.example.fraga.db.dao.ActivityLogDao;
import com.example.fraga.db.entity.ActivityLog; // Menggunakan ActivityLog untuk tipe LiveData
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FeedViewModel extends AndroidViewModel {

    private static final String TAG = "FeedViewModel";
    private ActivityLogDao activityLogDao;
    private final ExecutorService databaseWriteExecutor;

    // LiveData utama sekarang akan bertipe List<ActivityLog>
    private LiveData<List<ActivityLog>> publicActivitiesFeed;

    public FeedViewModel(@NonNull Application application) {
        super(application);
        AppDatabase db = AppDatabase.getInstance(application);
        if (db != null) {
            activityLogDao = db.activityLogDao();
        } else {
            Log.e(TAG, "AppDatabase instance is null. DAO cannot be initialized.");
        }
        databaseWriteExecutor = Executors.newSingleThreadExecutor();

        if (activityLogDao != null) {
            // Memanggil metode DAO yang mengembalikan LiveData<List<ActivityLog>>
            publicActivitiesFeed = activityLogDao.getAllPublicActivityLogs();
            Log.d(TAG, "Public activities (ActivityLog) LiveData initialized from DAO.");
        } else {
            Log.e(TAG, "ActivityLogDao is null. Initializing publicActivitiesFeed with empty LiveData.");
            MutableLiveData<List<ActivityLog>> emptyData = new MutableLiveData<>();
            emptyData.setValue(new ArrayList<>());
            publicActivitiesFeed = emptyData;
        }
    }

    /**
     * Mengembalikan LiveData yang berisi daftar ActivityLog publik.
     * Ini akan dipanggil oleh FeedFragment.
     */
    public LiveData<List<ActivityLog>> getPublicActivitiesFeed() { // Nama metode ini dipanggil oleh Fragment
        return publicActivitiesFeed;
    }

    public void incrementKudos(int activityId) {
        if (activityLogDao != null) {
            databaseWriteExecutor.execute(() -> {
                activityLogDao.incrementKudosCount(activityId);
                Log.d(TAG, "Kudos incremented for activityId: " + activityId);
            });
        } else {
            Log.e(TAG, "Cannot increment kudos, DAO is null.");
        }
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (databaseWriteExecutor != null && !databaseWriteExecutor.isShutdown()) {
            databaseWriteExecutor.shutdown();
        }
        Log.d(TAG, "FeedViewModel cleared and executor shutdown.");
    }
}
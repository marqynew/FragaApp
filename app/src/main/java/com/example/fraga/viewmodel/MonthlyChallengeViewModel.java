package com.example.fraga.viewmodel;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData; // Untuk kasus fallback

import com.example.fraga.LoginActivity; // Untuk konstanta SharedPreferences
import com.example.fraga.db.AppDatabase;
import com.example.fraga.db.dao.MonthlyChallengeDao;
import com.example.fraga.db.dao.UserChallengeStatusDao;
import com.example.fraga.db.entity.MonthlyChallenge;
import com.example.fraga.db.entity.UserChallengeStatus;
// Jika Anda membuat POJO untuk tantangan dengan status user:
// import com.example.fraga.db.relation.ChallengeWithUserStatus;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MonthlyChallengeViewModel extends AndroidViewModel {

    private static final String TAG = "MonthlyChallengeVM";

    private MonthlyChallengeDao monthlyChallengeDao;
    private UserChallengeStatusDao userChallengeStatusDao;
    private ExecutorService databaseWriteExecutor;

    private LiveData<MonthlyChallenge> featuredChallenge;
    private LiveData<List<MonthlyChallenge>> activeChallenges;
    // Untuk mendapatkan status user pada featured challenge:
    // private LiveData<UserChallengeStatus> featuredChallengeUserStatus;

    private int currentUserId = -1;

    public MonthlyChallengeViewModel(@NonNull Application application) {
        super(application);
        AppDatabase db = AppDatabase.getInstance(application);
        if (db != null) {
            monthlyChallengeDao = db.monthlyChallengeDao();
            userChallengeStatusDao = db.userChallengeStatusDao();
        } else {
            Log.e(TAG, "AppDatabase instance is null. DAOs cannot be initialized.");
        }
        databaseWriteExecutor = Executors.newSingleThreadExecutor();

        SharedPreferences prefs = application.getSharedPreferences(LoginActivity.PREFS_NAME, Context.MODE_PRIVATE);
        currentUserId = prefs.getInt(LoginActivity.KEY_USER_ID, -1);

        long currentTime = System.currentTimeMillis();

        if (monthlyChallengeDao != null) {
            featuredChallenge = monthlyChallengeDao.getFeaturedChallenge(currentTime);
            activeChallenges = monthlyChallengeDao.getCurrentActiveChallenges(currentTime);
            // Anda mungkin perlu mengambil status pengguna untuk featured challenge secara terpisah jika diperlukan
            // if (currentUserId != -1 && featuredChallenge.getValue() != null) {
            //    featuredChallengeUserStatus = userChallengeStatusDao.getUserChallengeStatus(currentUserId, featuredChallenge.getValue().getId());
            // }
        } else {
            Log.e(TAG, "DAOs are null. Initializing with empty LiveData.");
            MutableLiveData<MonthlyChallenge> emptyFeatured = new MutableLiveData<>();
            emptyFeatured.setValue(null);
            featuredChallenge = emptyFeatured;

            MutableLiveData<List<MonthlyChallenge>> emptyActive = new MutableLiveData<>();
            emptyActive.setValue(new ArrayList<>());
            activeChallenges = emptyActive;
        }
    }

    public LiveData<MonthlyChallenge> getFeaturedChallenge() {
        return featuredChallenge;
    }

    public LiveData<List<MonthlyChallenge>> getActiveChallenges() {
        return activeChallenges;
    }

    // Metode untuk mendapatkan UserChallengeStatus untuk tantangan tertentu (bisa dipanggil dari Fragment)
    public LiveData<UserChallengeStatus> getUserStatusForChallenge(int challengeId) {
        if (userChallengeStatusDao != null && currentUserId != -1) {
            return userChallengeStatusDao.getUserChallengeStatus(currentUserId, challengeId);
        }
        MutableLiveData<UserChallengeStatus> emptyStatus = new MutableLiveData<>();
        emptyStatus.setValue(null);
        return emptyStatus;
    }

    public void joinChallenge(int challengeId) {
        if (userChallengeStatusDao != null && currentUserId != -1) {
            databaseWriteExecutor.execute(() -> {
                // Cek dulu apakah user sudah join
                UserChallengeStatus existingStatus = userChallengeStatusDao.getUserChallengeStatusSync(currentUserId, challengeId);
                if (existingStatus == null) {
                    UserChallengeStatus newStatus = new UserChallengeStatus(currentUserId, challengeId, System.currentTimeMillis());
                    userChallengeStatusDao.insertUserChallengeStatus(newStatus);
                    Log.d(TAG, "User " + currentUserId + " joined challenge " + challengeId);
                } else {
                    Log.d(TAG, "User " + currentUserId + " already joined challenge " + challengeId);
                }
            });
        } else {
            Log.e(TAG, "Cannot join challenge: DAO is null or UserID is invalid.");
        }
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (databaseWriteExecutor != null && !databaseWriteExecutor.isShutdown()) {
            databaseWriteExecutor.shutdown();
        }
    }
}
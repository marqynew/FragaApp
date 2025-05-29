package com.example.fraga.viewmodel;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.fraga.LoginActivity; // Untuk konstanta SharedPreferences
import com.example.fraga.db.AppDatabase;
import com.example.fraga.db.dao.PersonalGoalDao;
import com.example.fraga.db.entity.PersonalGoal;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PersonalGoalViewModel extends AndroidViewModel {

    private static final String TAG = "PersonalGoalViewModel";
    private PersonalGoalDao personalGoalDao;
    private ExecutorService databaseWriteExecutor;
    private int currentUserId = -1;

    private LiveData<List<PersonalGoal>> inProgressGoals;
    private LiveData<List<PersonalGoal>> completedGoals;

    public PersonalGoalViewModel(@NonNull Application application) {
        super(application);
        AppDatabase db = AppDatabase.getInstance(application);
        if (db != null) {
            personalGoalDao = db.personalGoalDao();
        } else {
            Log.e(TAG, "AppDatabase instance is null. DAO cannot be initialized.");
        }
        databaseWriteExecutor = Executors.newSingleThreadExecutor();

        SharedPreferences prefs = application.getSharedPreferences(LoginActivity.PREFS_NAME, Context.MODE_PRIVATE);
        currentUserId = prefs.getInt(LoginActivity.KEY_USER_ID, -1);

        if (personalGoalDao != null && currentUserId != -1) {
            inProgressGoals = personalGoalDao.getGoalsByStatusForUser(currentUserId, "in_progress");
            completedGoals = personalGoalDao.getGoalsByStatusForUser(currentUserId, "completed");
        } else {
            Log.w(TAG, "DAO is null or UserID is invalid. Initializing with empty LiveData.");
            MutableLiveData<List<PersonalGoal>> emptyInProgress = new MutableLiveData<>();
            emptyInProgress.setValue(new ArrayList<>());
            inProgressGoals = emptyInProgress;

            MutableLiveData<List<PersonalGoal>> emptyCompleted = new MutableLiveData<>();
            emptyCompleted.setValue(new ArrayList<>());
            completedGoals = emptyCompleted;
        }
    }

    public LiveData<List<PersonalGoal>> getInProgressGoals() {
        return inProgressGoals;
    }

    public LiveData<List<PersonalGoal>> getCompletedGoals() {
        return completedGoals;
    }

    public void insertGoal(PersonalGoal goal) {
        if (personalGoalDao != null && goal.getUserId() != -1) { // Pastikan userId di goal sudah di-set
            databaseWriteExecutor.execute(() -> {
                long id = personalGoalDao.insertGoal(goal);
                Log.d(TAG, "Inserted new personal goal with id: " + id);
            });
        } else {
            Log.e(TAG, "Cannot insert goal: DAO is null or userId in goal is invalid.");
        }
    }

    public void updateGoalProgress(int goalId, double progressToAddDouble, int progressToAddInt, boolean isDoubleType) {
        if (personalGoalDao != null && currentUserId != -1) {
            databaseWriteExecutor.execute(() -> {
                // Ambil goal saat ini untuk mendapatkan progres dan target
                // Ini adalah operasi sinkron, jadi aman di dalam executor
                // PersonalGoal currentGoal = personalGoalDao.getGoalByIdSync(goalId, currentUserId); // Anda perlu metode ini di DAO
                // if (currentGoal != null) {
                //     if (isDoubleType) {
                //         currentGoal.setCurrentProgressDouble(currentGoal.getCurrentProgressDouble() + progressToAddDouble);
                //     } else {
                //         currentGoal.setCurrentProgressInt(currentGoal.getCurrentProgressInt() + progressToAddInt);
                //     }
                //     // Cek apakah goal selesai
                //     boolean completed = false;
                //     if (isDoubleType && currentGoal.getCurrentProgressDouble() >= currentGoal.getTargetValueDouble()) {
                //         completed = true;
                //     } else if (!isDoubleType && currentGoal.getCurrentProgressInt() >= currentGoal.getTargetValueInt()) {
                //         completed = true;
                //     }
                //
                //     if (completed && !"completed".equalsIgnoreCase(currentGoal.getStatus())) {
                //         currentGoal.setStatus("completed");
                //         currentGoal.setCompletionDateMs(System.currentTimeMillis());
                //     }
                //     personalGoalDao.updateGoal(currentGoal);
                // }

                // Pendekatan lebih sederhana jika DAO mendukung update progres langsung
                if (isDoubleType) {
                    personalGoalDao.addDoubleProgress(goalId, currentUserId, progressToAddDouble);
                } else {
                    personalGoalDao.addIntProgress(goalId, currentUserId, progressToAddInt);
                }
                // Logika untuk update status ke "completed" mungkin perlu query tambahan setelah update progres
                // atau trigger di database jika didukung.
                // Untuk sekarang, kita akan mengandalkan LiveData untuk refresh UI.
                Log.d(TAG, "Updated progress for goalId: " + goalId);
            });
        } else {
            Log.e(TAG, "Cannot update goal progress: DAO is null or UserID is invalid.");
        }
    }

    public void updateGoal(PersonalGoal goal) {
        if (personalGoalDao != null) {
            databaseWriteExecutor.execute(() -> {
                personalGoalDao.updateGoal(goal);
                Log.d(TAG, "Updated personal goal with id: " + goal.getId());
            });
        } else {
            Log.e(TAG, "Cannot update goal: DAO is null.");
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
package com.example.fraga.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.example.fraga.db.entity.ActivityLog;
import com.example.fraga.db.relation.ActivityFeedItem; // Pastikan POJO ini ada jika menggunakan getPublicActivityFeedItems

import java.util.List;

@Dao
public interface ActivityLogDao {

    // --- Operasi Dasar CRUD ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertActivityLog(ActivityLog activityLog);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAllActivityLogs(List<ActivityLog> activityLogs);

    @Update
    void updateActivityLog(ActivityLog activityLog);

    @Delete
    void deleteActivityLog(ActivityLog activityLog);

    @Query("DELETE FROM activity_logs WHERE id = :activityId")
    void deleteActivityLogById(int activityId);

    // --- Kueri Pengambilan Data ActivityLog Tunggal ---
    @Query("SELECT * FROM activity_logs WHERE id = :activityId LIMIT 1")
    ActivityLog getActivityLogByIdSync(int activityId); // Sinkron

    @Query("SELECT * FROM activity_logs WHERE id = :activityId LIMIT 1")
    LiveData<ActivityLog> getActivityLogById(int activityId); // LiveData

    // --- Kueri Pengambilan Daftar ActivityLog ---
    @Query("SELECT * FROM activity_logs WHERE user_id = :userId ORDER BY timestamp_ms DESC")
    LiveData<List<ActivityLog>> getActivityLogsForUser(int userId);

    @Query("SELECT * FROM activity_logs WHERE user_id = :userId ORDER BY timestamp_ms DESC LIMIT :limit")
    LiveData<List<ActivityLog>> getRecentActivityLogsForUser(int userId, int limit);

    @Query("SELECT * FROM activity_logs ORDER BY timestamp_ms DESC")
    LiveData<List<ActivityLog>> getAllActivityLogsSortedByDate();

    /**
     * Mengambil semua log aktivitas publik dari semua pengguna, diurutkan berdasarkan waktu terbaru.
     * Mengembalikan ActivityLog dasar.
     * @return LiveData yang membungkus List dari ActivityLog.
     */
    @Query("SELECT * FROM activity_logs WHERE privacy_setting = 'Public' ORDER BY timestamp_ms DESC")
    LiveData<List<ActivityLog>> getAllPublicActivityLogs(); // METODE INI PENTING UNTUK VIEWMODEL SAAT INI

    /**
     * (Untuk penggunaan di masa depan jika ingin info user di-JOIN)
     * Mengambil semua log aktivitas publik bersama dengan informasi pengguna terkait.
     * @return LiveData yang membungkus List dari ActivityFeedItem.
     */
    @Transaction
    @Query("SELECT al.*, u.name as user_name, u.profile_image_url as user_profile_image_url " +
            "FROM activity_logs al " +
            "INNER JOIN users u ON al.user_id = u.id " +
            "WHERE al.privacy_setting = 'Public' " +
            "ORDER BY al.timestamp_ms DESC")
    LiveData<List<ActivityFeedItem>> getPublicActivityFeedItems();


    // --- Kueri Statistik dan Update Spesifik ---
    @Query("SELECT SUM(distance_km) FROM activity_logs WHERE user_id = :userId")
    LiveData<Double> getTotalDistanceForUser(int userId);

    @Query("SELECT COUNT(*) FROM activity_logs WHERE user_id = :userId")
    LiveData<Integer> getTotalActivitiesForUser(int userId);

    @Query("SELECT SUM(kudos_count) FROM activity_logs WHERE user_id = :userId")
    LiveData<Integer> getTotalKudosReceivedByUser(int userId);

    @Query("UPDATE activity_logs SET kudos_count = kudos_count + 1 WHERE id = :activityId")
    void incrementKudosCount(int activityId);

    @Query("SELECT * FROM activity_logs WHERE user_id = :userId AND status = :status ORDER BY timestamp_ms DESC")
    LiveData<List<ActivityLog>> getActivityLogsByStatusForUser(int userId, String status);
}
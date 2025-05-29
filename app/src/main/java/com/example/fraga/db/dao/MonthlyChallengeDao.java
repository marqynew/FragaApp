package com.example.fraga.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.fraga.db.entity.MonthlyChallenge;

import java.util.List;

@Dao
public interface MonthlyChallengeDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertChallenge(MonthlyChallenge challenge);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAllChallenges(List<MonthlyChallenge> challenges);

    @Update
    void updateChallenge(MonthlyChallenge challenge);

    @Query("SELECT * FROM monthly_challenges WHERE id = :challengeId LIMIT 1")
    LiveData<MonthlyChallenge> getChallengeById(int challengeId);

    // Mengambil tantangan unggulan saat ini (berdasarkan tanggal dan flag is_featured)
    @Query("SELECT * FROM monthly_challenges WHERE is_featured = 1 AND :currentTimeMs >= start_date_ms AND :currentTimeMs <= end_date_ms ORDER BY start_date_ms DESC LIMIT 1")
    LiveData<MonthlyChallenge> getFeaturedChallenge(long currentTimeMs);

    // Mengambil semua tantangan aktif untuk bulan tertentu
    @Query("SELECT * FROM monthly_challenges WHERE challenge_month_year = :monthYear AND :currentTimeMs <= end_date_ms ORDER BY start_date_ms ASC")
    LiveData<List<MonthlyChallenge>> getChallengesForMonth(String monthYear, long currentTimeMs);

    // Mengambil semua tantangan yang sedang berlangsung
    @Query("SELECT * FROM monthly_challenges WHERE :currentTimeMs >= start_date_ms AND :currentTimeMs <= end_date_ms ORDER BY start_date_ms ASC")
    LiveData<List<MonthlyChallenge>> getCurrentActiveChallenges(long currentTimeMs);

    // Mengambil semua tantangan yang akan datang
    @Query("SELECT * FROM monthly_challenges WHERE :currentTimeMs < start_date_ms ORDER BY start_date_ms ASC")
    LiveData<List<MonthlyChallenge>> getUpcomingChallenges(long currentTimeMs);

    // Mengambil semua tantangan yang sudah lewat
    @Query("SELECT * FROM monthly_challenges WHERE :currentTimeMs > end_date_ms ORDER BY end_date_ms DESC")
    LiveData<List<MonthlyChallenge>> getPastChallenges(long currentTimeMs);

}
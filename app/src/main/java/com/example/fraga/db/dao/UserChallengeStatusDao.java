package com.example.fraga.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.fraga.db.entity.UserChallengeStatus;
// Jika Anda ingin mengambil data gabungan, Anda mungkin perlu kelas POJO khusus
// import com.example.fraga.db.relation.UserChallengeWithDetails;

import java.util.List;

@Dao
public interface UserChallengeStatusDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE) // Abaikan jika pengguna sudah join challenge ini
    long insertUserChallengeStatus(UserChallengeStatus status);

    @Update
    void updateUserChallengeStatus(UserChallengeStatus status);

    // Mengambil status partisipasi pengguna dalam tantangan tertentu
    @Query("SELECT * FROM user_challenge_status WHERE user_id = :userId AND challenge_id = :challengeId LIMIT 1")
    LiveData<UserChallengeStatus> getUserChallengeStatus(int userId, int challengeId);

    @Query("SELECT * FROM user_challenge_status WHERE user_id = :userId AND challenge_id = :challengeId LIMIT 1")
    UserChallengeStatus getUserChallengeStatusSync(int userId, int challengeId); // Versi sinkron

    // Mengambil semua tantangan yang diikuti oleh pengguna tertentu
    @Query("SELECT * FROM user_challenge_status WHERE user_id = :userId ORDER BY join_date_ms DESC")
    LiveData<List<UserChallengeStatus>> getAllChallengesForUser(int userId);

    // Mengambil semua pengguna yang berpartisipasi dalam tantangan tertentu (misal, untuk leaderboard tantangan)
    @Query("SELECT * FROM user_challenge_status WHERE challenge_id = :challengeId ORDER BY current_progress_double DESC, current_progress_int DESC")
    LiveData<List<UserChallengeStatus>> getAllParticipantsForChallenge(int challengeId);

    // Contoh Query untuk mengambil UserChallengeStatus bersama dengan detail MonthlyChallenge
    // Anda perlu membuat kelas POJO `UserChallengeWithChallengeDetails` yang menggabungkan field dari kedua entitas.
    // @Query("SELECT ucs.*, mc.title as challengeTitle, mc.badge_image_url as challengeBadgeUrl " +
    //        "FROM user_challenge_status ucs INNER JOIN monthly_challenges mc ON ucs.challenge_id = mc.id " +
    //        "WHERE ucs.user_id = :userId")
    // LiveData<List<UserChallengeWithChallengeDetails>> getChallengesWithDetailsForUser(int userId);

    // Query untuk memperbarui progres
    @Query("UPDATE user_challenge_status SET current_progress_double = :progress, status = :newStatus WHERE user_id = :userId AND challenge_id = :challengeId")
    void updateDoubleProgress(int userId, int challengeId, double progress, String newStatus);

    @Query("UPDATE user_challenge_status SET current_progress_int = :progress, status = :newStatus WHERE user_id = :userId AND challenge_id = :challengeId")
    void updateIntProgress(int userId, int challengeId, int progress, String newStatus);

    @Query("UPDATE user_challenge_status SET status = :newStatus, completion_date_ms = :completionDate WHERE user_id = :userId AND challenge_id = :challengeId")
    void updateChallengeCompletionStatus(int userId, int challengeId, String newStatus, long completionDate);
}
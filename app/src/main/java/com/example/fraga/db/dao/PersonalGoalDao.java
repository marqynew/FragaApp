package com.example.fraga.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Delete;

import com.example.fraga.db.entity.PersonalGoal;

import java.util.List;

@Dao
public interface PersonalGoalDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertGoal(PersonalGoal goal);

    @Update
    void updateGoal(PersonalGoal goal);

    @Delete
    void deleteGoal(PersonalGoal goal);

    @Query("SELECT * FROM personal_goals WHERE id = :goalId AND user_id = :userId LIMIT 1")
    LiveData<PersonalGoal> getGoalById(int goalId, int userId);

    @Query("SELECT * FROM personal_goals WHERE user_id = :userId AND status = :status ORDER BY creation_date_ms DESC")
    LiveData<List<PersonalGoal>> getGoalsByStatusForUser(int userId, String status);

    @Query("SELECT * FROM personal_goals WHERE user_id = :userId ORDER BY creation_date_ms DESC")
    LiveData<List<PersonalGoal>> getAllGoalsForUser(int userId);

    // Query untuk memperbarui progres (contoh)
    @Query("UPDATE personal_goals SET current_progress_int = current_progress_int + :progressToAdd WHERE id = :goalId AND user_id = :userId")
    void addIntProgress(int goalId, int userId, int progressToAdd);

    @Query("UPDATE personal_goals SET current_progress_double = current_progress_double + :progressToAdd WHERE id = :goalId AND user_id = :userId")
    void addDoubleProgress(int goalId, int userId, double progressToAdd);

    @Query("UPDATE personal_goals SET status = :newStatus, completion_date_ms = :completionDate WHERE id = :goalId AND user_id = :userId")
    void updateGoalStatus(int goalId, int userId, String newStatus, long completionDate);

}
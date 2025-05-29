package com.example.fraga.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Delete;
import androidx.room.Ignore;

import com.example.fraga.db.entity.User;

import java.util.List;

@Dao
public interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertUser(User user);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long insertUserIfNotExists(User user); // Mengembalikan long (rowId atau -1 jika diabaikan)

    @Update
    void updateUser(User user);

    @Delete
    void deleteUser(User user);

    @Query("SELECT * FROM users WHERE id = :userId LIMIT 1")
    User getUserById(int userId);

    @Query("SELECT * FROM users WHERE id = :userId LIMIT 1")
    LiveData<User> getLiveUserById(int userId);

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    User findUserByEmail(String email);

    @Query("SELECT * FROM users ORDER BY name ASC")
    LiveData<List<User>> getAllUsers();

    // Contoh query untuk memperbarui preferensi
    @Query("UPDATE users SET distance_unit_pref = :distanceUnit WHERE id = :userId")
    void updateDistanceUnitPreference(int userId, String distanceUnit);

    @Query("UPDATE users SET weight_unit_pref = :weightUnit WHERE id = :userId")
    void updateWeightUnitPreference(int userId, String weightUnit);

    // Contoh: Mendapatkan jumlah teman (memerlukan entitas Friendship dan FriendshipDao)
    // @Query("SELECT COUNT(*) FROM friendships WHERE userId1 = :userId OR userId2 = :userId")
    // LiveData<Integer> getFriendCount(int userId);
}
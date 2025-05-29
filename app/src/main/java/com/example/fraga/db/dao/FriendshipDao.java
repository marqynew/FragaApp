package com.example.fraga.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Delete;

import com.example.fraga.db.entity.Friendship;
import com.example.fraga.db.entity.User; // Diperlukan untuk mengambil daftar teman

import java.util.List;

@Dao
public interface FriendshipDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void addFriendship(Friendship friendship);

    @Delete
    void removeFriendship(Friendship friendship);

    // Query untuk menghapus pertemanan berdasarkan ID kedua pengguna
    // Pastikan untuk memberikan ID dalam urutan yang benar (userId1 < userId2)
    @Query("DELETE FROM friendships WHERE (user_id_1 = :userId1 AND user_id_2 = :userId2) OR (user_id_1 = :userId2 AND user_id_2 = :userId1)")
    void removeFriendshipByUsers(int userId1, int userId2);

    // Query untuk memeriksa apakah dua pengguna sudah berteman
    @Query("SELECT * FROM friendships WHERE (user_id_1 = :userId1 AND user_id_2 = :userId2) OR (user_id_1 = :userId2 AND user_id_2 = :userId1) LIMIT 1")
    Friendship findFriendshipBetweenUsers(int userId1, int userId2);

    // Query untuk mendapatkan semua teman dari seorang pengguna
    // Ini akan mengembalikan User ID dari teman, bukan objek User secara langsung dari query ini.
    // Anda perlu melakukan query tambahan ke tabel User atau menggunakan JOIN.
    @Query("SELECT " +
            "CASE user_id_1 WHEN :userId THEN user_id_2 ELSE user_id_1 END as friend_user_id " +
            "FROM friendships " +
            "WHERE user_id_1 = :userId OR user_id_2 = :userId")
    LiveData<List<Integer>> getFriendIdsForUser(int userId);


    // Query yang lebih kompleks untuk mendapatkan objek User dari teman-teman seorang pengguna
    // Ini adalah contoh JOIN. Anda mungkin perlu membuat POJO khusus untuk hasilnya jika kolomnya tidak semua dari User.
    @Query("SELECT u.* FROM users u " +
            "INNER JOIN friendships f ON (u.id = f.user_id_1 OR u.id = f.user_id_2) " +
            "WHERE (f.user_id_1 = :userId OR f.user_id_2 = :userId) AND u.id != :userId " +
            "ORDER BY u.name ASC")
    LiveData<List<User>> getFriendsForUser(int userId);

    // Query untuk menghitung jumlah teman (followers/following bisa sama dalam model ini)
    @Query("SELECT COUNT(*) FROM friendships WHERE user_id_1 = :userId OR user_id_2 = :userId")
    LiveData<Integer> getFriendCountForUser(int userId);
}
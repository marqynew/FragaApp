package com.example.fraga.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.fraga.db.entity.FriendRequest;
// Jika Anda ingin menampilkan informasi User pengirim dalam list permintaan:
// import com.example.fraga.db.relation.FriendRequestWithSenderInfo;

import java.util.List;

@Dao
public interface FriendRequestDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long insertRequest(FriendRequest request);

    @Update
    void updateRequest(FriendRequest request);

    @Query("SELECT * FROM friend_requests WHERE to_user_id = :userId AND status = 'PENDING' ORDER BY request_timestamp_ms DESC")
    LiveData<List<FriendRequest>> getPendingFriendRequestsForUser(int userId);

    // Jika Anda ingin mengambil data User pengirim bersamaan dengan permintaan:
    // @Query("SELECT fr.*, u.name as sender_name, u.profile_image_url as sender_profile_image_url " +
    //        "FROM friend_requests fr INNER JOIN users u ON fr.from_user_id = u.id " +
    //        "WHERE fr.to_user_id = :userId AND fr.status = 'PENDING' ORDER BY fr.request_timestamp_ms DESC")
    // LiveData<List<FriendRequestWithSenderInfo>> getPendingFriendRequestsWithSenderInfo(int userId);

    @Query("SELECT * FROM friend_requests WHERE id = :requestId LIMIT 1")
    FriendRequest getRequestById(int requestId);

    @Query("UPDATE friend_requests SET status = :newStatus WHERE id = :requestId")
    void updateRequestStatus(int requestId, String newStatus);

    // Untuk memeriksa apakah sudah ada permintaan pending antara dua user
    @Query("SELECT * FROM friend_requests WHERE " +
            "((from_user_id = :userId1 AND to_user_id = :userId2) OR (from_user_id = :userId2 AND to_user_id = :userId1)) " +
            "AND status = 'PENDING' LIMIT 1")
    FriendRequest findPendingRequestBetweenUsers(int userId1, int userId2);

    @Query("DELETE FROM friend_requests WHERE id = :requestId")
    void deleteRequestById(int requestId);
}
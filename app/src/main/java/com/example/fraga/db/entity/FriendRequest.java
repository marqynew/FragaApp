package com.example.fraga.db.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.room.Ignore;

@Entity(tableName = "friend_requests",
        foreignKeys = {
                @ForeignKey(entity = User.class,
                        parentColumns = "id",
                        childColumns = "from_user_id",
                        onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = User.class,
                        parentColumns = "id",
                        childColumns = "to_user_id",
                        onDelete = ForeignKey.CASCADE)
        },
        indices = {
                @Index(value = {"from_user_id"}),
                @Index(value = {"to_user_id"}),
                @Index(value = {"from_user_id", "to_user_id"}, unique = true) // Mencegah permintaan duplikat
        })
public class FriendRequest {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int id;

    @ColumnInfo(name = "from_user_id")
    private int fromUserId; // ID pengguna yang mengirim permintaan

    @ColumnInfo(name = "to_user_id")
    private int toUserId; // ID pengguna yang menerima permintaan

    @ColumnInfo(name = "message") // Pesan opsional saat mengirim permintaan
    private String message;

    @ColumnInfo(name = "status", defaultValue = "PENDING") // Status: "PENDING", "ACCEPTED", "DECLINED"
    private String status;

    @ColumnInfo(name = "request_timestamp_ms")
    private long requestTimestampMs; // Waktu permintaan dikirim

    // Konstruktor kosong untuk Room
    public FriendRequest() {
    }

    // Konstruktor yang berguna
    @Ignore
    public FriendRequest(int fromUserId, int toUserId, String message, long requestTimestampMs) {
        this.fromUserId = fromUserId;
        this.toUserId = toUserId;
        this.message = message;
        this.requestTimestampMs = requestTimestampMs;
        this.status = "PENDING"; // Default status saat dibuat
    }

    // --- Getter dan Setter ---
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getFromUserId() {
        return fromUserId;
    }

    public void setFromUserId(int fromUserId) {
        this.fromUserId = fromUserId;
    }

    public int getToUserId() {
        return toUserId;
    }

    public void setToUserId(int toUserId) {
        this.toUserId = toUserId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getRequestTimestampMs() {
        return requestTimestampMs;
    }

    public void setRequestTimestampMs(long requestTimestampMs) {
        this.requestTimestampMs = requestTimestampMs;
    }
}
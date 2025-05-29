package com.example.fraga.db.entity;

import androidx.room.Ignore;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "friendships",
        foreignKeys = {
                @ForeignKey(entity = User.class,
                        parentColumns = "id",
                        childColumns = "user_id_1",
                        onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = User.class,
                        parentColumns = "id",
                        childColumns = "user_id_2",
                        onDelete = ForeignKey.CASCADE)
        },
        // Pastikan kombinasi userId1 dan userId2 unik, dan userId1 < userId2 untuk menghindari duplikasi
        // dan memudahkan pencarian.
        indices = {
                @Index(value = {"user_id_1", "user_id_2"}, unique = true),
                @Index(value = {"user_id_1"}), // Indeks untuk pencarian teman user_id_1
                @Index(value = {"user_id_2"})  // Indeks untuk pencarian teman user_id_2
        })
public class Friendship {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int id;

    @ColumnInfo(name = "user_id_1")
    private int userId1; // ID pengguna pertama

    @ColumnInfo(name = "user_id_2")
    private int userId2; // ID pengguna kedua

    @ColumnInfo(name = "friendship_date_ms") // Waktu pertemanan terjalin
    private long friendshipDateMs;

    // Konstruktor kosong untuk Room
    public Friendship() {
    }

    // Konstruktor yang berguna
    // Pastikan userId1 selalu lebih kecil dari userId2 untuk konsistensi dan menghindari duplikasi
    @Ignore
    public Friendship(int userId1, int userId2, long friendshipDateMs) {
        if (userId1 < userId2) {
            this.userId1 = userId1;
            this.userId2 = userId2;
        } else {
            this.userId1 = userId2;
            this.userId2 = userId1;
        }
        this.friendshipDateMs = friendshipDateMs;
    }

    // --- Getter dan Setter ---
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId1() {
        return userId1;
    }

    public void setUserId1(int userId1) {
        // Jaga konsistensi urutan
        if (this.userId2 != 0 && userId1 >= this.userId2) {
            this.userId1 = this.userId2;
            this.userId2 = userId1;
        } else {
            this.userId1 = userId1;
        }
    }

    public int getUserId2() {
        return userId2;
    }

    public void setUserId2(int userId2) {
        // Jaga konsistensi urutan
        if (this.userId1 != 0 && userId2 <= this.userId1) {
            this.userId2 = this.userId1;
            this.userId1 = userId2;
        } else {
            this.userId2 = userId2;
        }
    }

    public long getFriendshipDateMs() {
        return friendshipDateMs;
    }

    public void setFriendshipDateMs(long friendshipDateMs) {
        this.friendshipDateMs = friendshipDateMs;
    }
}
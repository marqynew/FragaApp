package com.example.fraga.db.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.room.Ignore;

@Entity(tableName = "user_challenge_status",
        foreignKeys = {
                @ForeignKey(entity = User.class,
                        parentColumns = "id",
                        childColumns = "user_id",
                        onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = MonthlyChallenge.class,
                        parentColumns = "id",
                        childColumns = "challenge_id",
                        onDelete = ForeignKey.CASCADE)
        },
        // Pastikan kombinasi userId dan challengeId unik,
        // sehingga satu pengguna hanya bisa join satu tantangan sekali.
        indices = {
                @Index(value = {"user_id", "challenge_id"}, unique = true),
                @Index(value = {"user_id"}),
                @Index(value = {"challenge_id"})
        })
public class UserChallengeStatus {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int id;

    @ColumnInfo(name = "user_id")
    private int userId;

    @ColumnInfo(name = "challenge_id")
    private int challengeId;

    // Progres bisa double atau int tergantung tipe tantangan
    @ColumnInfo(name = "current_progress_double")
    private double currentProgressDouble;

    @ColumnInfo(name = "current_progress_int")
    private int currentProgressInt;

    @ColumnInfo(name = "status", defaultValue = "JOINED") // "JOINED", "IN_PROGRESS", "COMPLETED", "FAILED"
    private String status;

    @ColumnInfo(name = "join_date_ms")
    private long joinDateMs; // Waktu pengguna bergabung dengan tantangan

    @ColumnInfo(name = "completion_date_ms", defaultValue = "0") // Diisi saat status menjadi "COMPLETED"
    private long completionDateMs;

    // Konstruktor kosong untuk Room
    public UserChallengeStatus() {
    }

    // Konstruktor yang berguna
    @Ignore
    public UserChallengeStatus(int userId, int challengeId, long joinDateMs) {
        this.userId = userId;
        this.challengeId = challengeId;
        this.joinDateMs = joinDateMs;
        this.status = "JOINED"; // Status awal saat bergabung
        this.currentProgressDouble = 0.0;
        this.currentProgressInt = 0;
    }

    // --- Getter dan Setter ---
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getChallengeId() {
        return challengeId;
    }

    public void setChallengeId(int challengeId) {
        this.challengeId = challengeId;
    }

    public double getCurrentProgressDouble() {
        return currentProgressDouble;
    }

    public void setCurrentProgressDouble(double currentProgressDouble) {
        this.currentProgressDouble = currentProgressDouble;
    }

    public int getCurrentProgressInt() {
        return currentProgressInt;
    }

    public void setCurrentProgressInt(int currentProgressInt) {
        this.currentProgressInt = currentProgressInt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getJoinDateMs() {
        return joinDateMs;
    }

    public void setJoinDateMs(long joinDateMs) {
        this.joinDateMs = joinDateMs;
    }

    public long getCompletionDateMs() {
        return completionDateMs;
    }

    public void setCompletionDateMs(long completionDateMs) {
        this.completionDateMs = completionDateMs;
    }
}
package com.example.fraga.db.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

@Entity(tableName = "personal_goals",
        foreignKeys = @ForeignKey(entity = User.class,
                parentColumns = "id",
                childColumns = "user_id",
                onDelete = ForeignKey.CASCADE),
        indices = {@Index(value = {"user_id"}),
                @Index(value = {"status"})})
public class PersonalGoal {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int id;

    @ColumnInfo(name = "user_id")
    private int userId;

    @ColumnInfo(name = "title")
    private String title;

    @ColumnInfo(name = "description")
    private String description;

    @ColumnInfo(name = "goal_type") // Misal: "run_x_times_week", "reach_y_steps_daily", "run_z_distance", "cycle_total_km_month"
    private String goalType;

    @ColumnInfo(name = "target_value_double") // Untuk target seperti jarak (km), langkah
    private double targetValueDouble;

    @ColumnInfo(name = "target_value_int") // Untuk target seperti jumlah run, hari streak
    private int targetValueInt;

    @ColumnInfo(name = "current_progress_double")
    private double currentProgressDouble;

    @ColumnInfo(name = "current_progress_int")
    private int currentProgressInt;

    @ColumnInfo(name = "unit") // Misal: "km", "steps", "times", "days", "minutes"
    private String unit;

    @ColumnInfo(name = "creation_date_ms")
    private long creationDateMs;

    @ColumnInfo(name = "target_date_ms", defaultValue = "0") // Opsional, 0 jika tidak ada target tanggal spesifik
    private long targetDateMs;

    @ColumnInfo(name = "status", defaultValue = "in_progress") // "in_progress", "completed", "failed", "paused"
    private String status;

    @ColumnInfo(name = "completion_date_ms", defaultValue = "0") // Opsional, diisi saat selesai
    private long completionDateMs;

    // Konstruktor kosong untuk Room
    public PersonalGoal() {
    }

    // Konstruktor yang lebih berguna (gunakan @Ignore jika tidak ingin Room menggunakannya untuk query)
    @Ignore
    public PersonalGoal(int userId, String title, String description, String goalType,
                        double targetValueDouble, int targetValueInt, String unit, long creationDateMs, String status) {
        this.userId = userId;
        this.title = title;
        this.description = description;
        this.goalType = goalType;
        this.targetValueDouble = targetValueDouble;
        this.targetValueInt = targetValueInt;
        this.unit = unit;
        this.creationDateMs = creationDateMs;
        this.status = status;
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getGoalType() {
        return goalType;
    }

    public void setGoalType(String goalType) {
        this.goalType = goalType;
    }

    public double getTargetValueDouble() {
        return targetValueDouble;
    }

    public void setTargetValueDouble(double targetValueDouble) {
        this.targetValueDouble = targetValueDouble;
    }

    public int getTargetValueInt() {
        return targetValueInt;
    }

    public void setTargetValueInt(int targetValueInt) {
        this.targetValueInt = targetValueInt;
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

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public long getCreationDateMs() {
        return creationDateMs;
    }

    public void setCreationDateMs(long creationDateMs) {
        this.creationDateMs = creationDateMs;
    }

    public long getTargetDateMs() {
        return targetDateMs;
    }

    public void setTargetDateMs(long targetDateMs) {
        this.targetDateMs = targetDateMs;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getCompletionDateMs() {
        return completionDateMs;
    }

    public void setCompletionDateMs(long completionDateMs) {
        this.completionDateMs = completionDateMs;
    }

    // Metode utilitas untuk menampilkan progres (opsional)
    @Ignore
    public String getFormattedProgress() {
        if ("km".equalsIgnoreCase(unit) || "steps".equalsIgnoreCase(unit) /* atau tipe lain yang pakai double */) {
            return String.format(Locale.getDefault(), "%.1f / %.1f %s", currentProgressDouble, targetValueDouble, unit);
        } else { // Untuk "times", "days"
            return String.format(Locale.getDefault(), "%d / %d %s", currentProgressInt, targetValueInt, unit);
        }
    }

    @Ignore
    public int getProgressPercentage() {
        if ("km".equalsIgnoreCase(unit) || "steps".equalsIgnoreCase(unit)) {
            if (targetValueDouble <= 0) return 0;
            return (int) ((currentProgressDouble / targetValueDouble) * 100);
        } else {
            if (targetValueInt <= 0) return 0;
            return (int) (((double) currentProgressInt / targetValueInt) * 100);
        }
    }
}
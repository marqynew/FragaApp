package com.example.fraga.db.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.Index;

import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

@Entity(tableName = "monthly_challenges",
        indices = {@Index(value = {"challenge_month_year"}, unique = true)}) // Mungkin ada beberapa challenge per bulan, jadi unique bisa dipertimbangkan ulang atau buat ID unik lain
public class MonthlyChallenge {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int id;

    @ColumnInfo(name = "title")
    private String title; // Misal: "August Distance Challenge", "September 5K Run"

    @ColumnInfo(name = "description")
    private String description;

    @ColumnInfo(name = "challenge_month_year") // Format YYYY-MM, misal "2025-08"
    private String challengeMonthYear;

    @ColumnInfo(name = "goal_type") // Misal: "TOTAL_DISTANCE_KM", "FASTEST_5K_MIN", "ACTIVITY_COUNT"
    private String goalType;

    @ColumnInfo(name = "target_value_double") // Untuk target seperti jarak, waktu (dalam menit/detik)
    private double targetValueDouble;

    @ColumnInfo(name = "target_value_int") // Untuk target seperti jumlah aktivitas
    private int targetValueInt;

    @ColumnInfo(name = "unit") // Misal: "km", "minutes", "activities"
    private String unit;

    @ColumnInfo(name = "badge_image_url") // URL atau nama resource drawable untuk badge
    private String badgeImageUrl;

    @ColumnInfo(name = "start_date_ms")
    private long startDateMs;

    @ColumnInfo(name = "end_date_ms")
    private long endDateMs;

    @ColumnInfo(name = "is_featured", defaultValue = "0") // 0 = false, 1 = true
    private boolean isFeatured;

    // Konstruktor kosong untuk Room
    public MonthlyChallenge() {
    }

    // Konstruktor yang berguna
    @Ignore
    public MonthlyChallenge(String title, String description, String challengeMonthYear,
                            String goalType, double targetValueDouble, int targetValueInt, String unit,
                            String badgeImageUrl, long startDateMs, long endDateMs, boolean isFeatured) {
        this.title = title;
        this.description = description;
        this.challengeMonthYear = challengeMonthYear;
        this.goalType = goalType;
        this.targetValueDouble = targetValueDouble;
        this.targetValueInt = targetValueInt;
        this.unit = unit;
        this.badgeImageUrl = badgeImageUrl;
        this.startDateMs = startDateMs;
        this.endDateMs = endDateMs;
        this.isFeatured = isFeatured;
    }

    // --- Getter dan Setter ---
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getChallengeMonthYear() {
        return challengeMonthYear;
    }

    public void setChallengeMonthYear(String challengeMonthYear) {
        this.challengeMonthYear = challengeMonthYear;
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

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getBadgeImageUrl() {
        return badgeImageUrl;
    }

    public void setBadgeImageUrl(String badgeImageUrl) {
        this.badgeImageUrl = badgeImageUrl;
    }

    public long getStartDateMs() {
        return startDateMs;
    }

    public void setStartDateMs(long startDateMs) {
        this.startDateMs = startDateMs;
    }

    public long getEndDateMs() {
        return endDateMs;
    }

    public void setEndDateMs(long endDateMs) {
        this.endDateMs = endDateMs;
    }

    public boolean isFeatured() {
        return isFeatured;
    }

    public void setFeatured(boolean featured) {
        isFeatured = featured;
    }

    // Metode utilitas (opsional)
    @Ignore
    public String getDaysLeft() {
        long currentTime = System.currentTimeMillis();
        if (currentTime > endDateMs) {
            return "Ended";
        }
        if (currentTime < startDateMs) {
            // Hitung hari menuju mulai
            long diff = startDateMs - currentTime;
            long days = TimeUnit.MILLISECONDS.toDays(diff);
            return "Starts in " + (days + 1) + " days";
        }
        long diff = endDateMs - currentTime;
        long days = TimeUnit.MILLISECONDS.toDays(diff);
        return (days + 1) + " days left"; // Tambah 1 karena hari ini juga dihitung
    }
}
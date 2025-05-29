package com.example.fraga.db.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit; // Untuk konversi durasi

@Entity(tableName = "activity_logs",
        foreignKeys = @ForeignKey(entity = User.class, // Mereferensikan entitas User
                parentColumns = "id", // Kolom 'id' di tabel User
                childColumns = "user_id", // Kolom 'user_id' di tabel ini
                onDelete = ForeignKey.CASCADE), // Jika User dihapus, log aktivitasnya juga terhapus
        indices = {
                @Index(value = {"user_id"}), // Indeks untuk query berdasarkan user_id
                @Index(value = {"timestamp_ms"}) // Indeks untuk query/sort berdasarkan waktu
        })
public class ActivityLog {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int id;

    @ColumnInfo(name = "user_id")
    private int userId; // Foreign key yang merujuk ke User.id

    @ColumnInfo(name = "title")
    private String title; // Misal: "Lari Pagi", "Bersepeda Sore"

    @ColumnInfo(name = "description")
    private String description; // Catatan tambahan dari pengguna

    @ColumnInfo(name = "activity_type") // Misal: "Running", "Cycling", "Swimming", "Walking", "Hiking"
    private String activityType;

    @ColumnInfo(name = "timestamp_ms") // Waktu mulai aktivitas dalam milidetik (Unix timestamp)
    private long timestampMs;

    @ColumnInfo(name = "duration_ms") // Durasi aktivitas dalam milidetik
    private long durationMs;

    @ColumnInfo(name = "distance_km") // Jarak dalam kilometer
    private double distanceKm;

    // Pace bisa dihitung saat dibutuhkan, atau disimpan jika sering diakses.
    // Jika disimpan, pastikan konsisten.
    // @ColumnInfo(name = "pace_seconds_per_km")
    // private long paceSecondsPerKm;

    @ColumnInfo(name = "calories_kcal") // Perkiraan kalori yang terbakar
    private int caloriesKcal;

    @ColumnInfo(name = "avg_heart_rate_bpm")
    private int avgHeartRateBpm; // Rata-rata detak jantung (opsional)

    @ColumnInfo(name = "max_heart_rate_bpm")
    private int maxHeartRateBpm; // Detak jantung maksimal (opsional)

    @ColumnInfo(name = "elevation_gain_m") // Kenaikan elevasi total dalam meter (opsional)
    private int elevationGainM;

    @ColumnInfo(name = "route_data_json")
    // Menyimpan data rute (misal, List<LatLng> sebagai String JSON)
    // Pertimbangkan TypeConverter jika ingin menyimpan objek List<LatLng> secara langsung
    private String routeDataJson;

    @ColumnInfo(name = "kudos_count", defaultValue = "0")
    private int kudosCount;

    @ColumnInfo(name = "privacy_setting", defaultValue = "Public") // Misal: "Public", "Friends Only", "Private"
    private String privacySetting;

    // Status aktivitas, misal: "COMPLETED", "SCHEDULED", "IN_PROGRESS_TRACKING", "PLANNED"
    @ColumnInfo(name = "status", defaultValue = "COMPLETED")
    private String status;

    // --- Konstruktor ---
    public ActivityLog() {
        // Room memerlukan konstruktor kosong
    }

    // Konstruktor untuk memudahkan pembuatan objek (bisa disesuaikan)
    @Ignore // Room akan mengabaikan konstruktor ini untuk pembuatan objek dari DB
    public ActivityLog(int userId, String title, String activityType, long timestampMs, long durationMs, double distanceKm, String privacySetting, String status) {
        this.userId = userId;
        this.title = title;
        this.activityType = activityType;
        this.timestampMs = timestampMs;
        this.durationMs = durationMs;
        this.distanceKm = distanceKm;
        this.privacySetting = privacySetting;
        this.status = status;
        this.kudosCount = 0; // Default kudos
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

    public String getActivityType() {
        return activityType;
    }

    public void setActivityType(String activityType) {
        this.activityType = activityType;
    }

    public long getTimestampMs() {
        return timestampMs;
    }

    public void setTimestampMs(long timestampMs) {
        this.timestampMs = timestampMs;
    }

    public long getDurationMs() {
        return durationMs;
    }

    public void setDurationMs(long durationMs) {
        this.durationMs = durationMs;
    }

    public double getDistanceKm() {
        return distanceKm;
    }

    public void setDistanceKm(double distanceKm) {
        this.distanceKm = distanceKm;
    }

    public int getCaloriesKcal() {
        return caloriesKcal;
    }

    public void setCaloriesKcal(int caloriesKcal) {
        this.caloriesKcal = caloriesKcal;
    }

    public int getAvgHeartRateBpm() {
        return avgHeartRateBpm;
    }

    public void setAvgHeartRateBpm(int avgHeartRateBpm) {
        this.avgHeartRateBpm = avgHeartRateBpm;
    }

    public int getMaxHeartRateBpm() {
        return maxHeartRateBpm;
    }

    public void setMaxHeartRateBpm(int maxHeartRateBpm) {
        this.maxHeartRateBpm = maxHeartRateBpm;
    }

    public int getElevationGainM() {
        return elevationGainM;
    }

    public void setElevationGainM(int elevationGainM) {
        this.elevationGainM = elevationGainM;
    }

    public String getRouteDataJson() {
        return routeDataJson;
    }

    public void setRouteDataJson(String routeDataJson) {
        this.routeDataJson = routeDataJson;
    }

    public int getKudosCount() {
        return kudosCount;
    }

    public void setKudosCount(int kudosCount) {
        this.kudosCount = kudosCount;
    }

    public String getPrivacySetting() {
        return privacySetting;
    }

    public void setPrivacySetting(String privacySetting) {
        this.privacySetting = privacySetting;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    // --- Metode Utilitas (Opsional, untuk tampilan) ---

    @Ignore // Room akan mengabaikan metode ini
    public String getFormattedTimestamp(String formatPattern) {
        if (formatPattern == null || formatPattern.isEmpty()) {
            formatPattern = "dd MMM yyyy, HH:mm"; // Default format
        }
        SimpleDateFormat sdf = new SimpleDateFormat(formatPattern, Locale.getDefault());
        return sdf.format(new Date(timestampMs));
    }

    @Ignore
    public String getFormattedDuration() {
        long hours = TimeUnit.MILLISECONDS.toHours(durationMs);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(durationMs) % 60;
        long seconds = TimeUnit.MILLISECONDS.toSeconds(durationMs) % 60;

        if (hours > 0) {
            return String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds);
        } else {
            return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
        }
    }

    @Ignore
    public String getFormattedDistance() {
        return String.format(Locale.getDefault(), "%.2f km", distanceKm);
    }

    @Ignore
    public String getFormattedPace() {
        if (distanceKm <= 0 || durationMs <= 0) {
            return "-:-- /km";
        }
        // Pace dalam detik per kilometer
        double paceSecondsPerKm = (double) durationMs / 1000.0 / distanceKm;
        long minutes = (long) (paceSecondsPerKm / 60);
        long seconds = (long) (paceSecondsPerKm % 60);
        return String.format(Locale.getDefault(), "%d:%02d /km", minutes, seconds);
    }
}
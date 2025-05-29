package com.example.fraga.db.entity;

import androidx.annotation.NonNull; // Impor untuk @NonNull
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "users",
        indices = {@Index(value = {"email"}, unique = true)}) // Pastikan email unik
public class User {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int id;

    @NonNull // Nama pengguna seharusnya tidak null
    @ColumnInfo(name = "name")
    private String name;

    @NonNull // Email seharusnya tidak null
    @ColumnInfo(name = "email")
    private String email;

    // Mengganti passwordHash dengan field untuk hash dan salt yang sebenarnya
    @NonNull // Hash kata sandi tidak boleh null setelah pengguna dibuat
    @ColumnInfo(name = "hashed_password_hex")
    private String hashedPasswordHex;

    @NonNull // Salt tidak boleh null setelah pengguna dibuat
    @ColumnInfo(name = "salt_hex")
    private String saltHex;

    @ColumnInfo(name = "location")
    private String location; // Boleh null

    @ColumnInfo(name = "profile_image_url")
    private String profileImageUrl; // Boleh null

    @ColumnInfo(name = "join_date_ms")
    private long joinDateMs;

    @NonNull
    @ColumnInfo(name = "distance_unit_pref", defaultValue = "Kilometers (km)")
    private String distanceUnitPreference;

    @NonNull
    @ColumnInfo(name = "weight_unit_pref", defaultValue = "Kilograms (kg)")
    private String weightUnitPreference;

    // Room memerlukan konstruktor kosong
    public User() {
        // Inisialisasi field NonNull dengan nilai default yang aman jika diperlukan,
        // meskipun Room akan mengisinya. Namun, untuk konsistensi:
        this.name = "";
        this.email = "";
        this.hashedPasswordHex = "";
        this.saltHex = "";
        this.distanceUnitPreference = "Kilometers (km)";
        this.weightUnitPreference = "Kilograms (kg)";
    }

    // Konstruktor yang lebih lengkap dan aman untuk membuat objek User baru
    @Ignore
    public User(@NonNull String name, @NonNull String email, @NonNull String hashedPasswordHex, @NonNull String saltHex, long joinDateMs) {
        this.name = name;
        this.email = email;
        this.hashedPasswordHex = hashedPasswordHex;
        this.saltHex = saltHex;
        this.joinDateMs = joinDateMs;
        this.distanceUnitPreference = "Kilometers (km)"; // Default
        this.weightUnitPreference = "Kilograms (kg)";  // Default
    }

    // --- Getter dan Setter ---
    // (Pastikan semua getter dan setter ada dan konsisten dengan perubahan nama field jika ada)

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @NonNull
    public String getName() {
        return name;
    }

    public void setName(@NonNull String name) {
        this.name = name;
    }

    @NonNull
    public String getEmail() {
        return email;
    }

    public void setEmail(@NonNull String email) {
        this.email = email;
    }

    @NonNull
    public String getHashedPasswordHex() {
        return hashedPasswordHex;
    }

    public void setHashedPasswordHex(@NonNull String hashedPasswordHex) {
        this.hashedPasswordHex = hashedPasswordHex;
    }

    @NonNull
    public String getSaltHex() {
        return saltHex;
    }

    public void setSaltHex(@NonNull String saltHex) {
        this.saltHex = saltHex;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public long getJoinDateMs() {
        return joinDateMs;
    }

    public void setJoinDateMs(long joinDateMs) {
        this.joinDateMs = joinDateMs;
    }

    @NonNull
    public String getDistanceUnitPreference() {
        return distanceUnitPreference;
    }

    public void setDistanceUnitPreference(@NonNull String distanceUnitPreference) {
        this.distanceUnitPreference = distanceUnitPreference;
    }

    @NonNull
    public String getWeightUnitPreference() {
        return weightUnitPreference;
    }

    public void setWeightUnitPreference(@NonNull String weightUnitPreference) {
        this.weightUnitPreference = weightUnitPreference;
    }
}
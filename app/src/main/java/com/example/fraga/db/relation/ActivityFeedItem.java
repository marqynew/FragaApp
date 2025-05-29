package com.example.fraga.db.relation;

import androidx.room.Embedded;
import androidx.room.ColumnInfo;
import com.example.fraga.db.entity.ActivityLog;
// Tidak perlu import User di sini jika kita hanya mengambil beberapa kolomnya

import java.util.Objects;

public class ActivityFeedItem {

    @Embedded // Menyertakan semua field dari ActivityLog
    public ActivityLog activityLog;

    // Kolom tambahan dari tabel User yang di-JOIN
    @ColumnInfo(name = "user_name") // Nama alias dari kueri JOIN
    public String userName;

    @ColumnInfo(name = "user_profile_image_url") // Nama alias dari kueri JOIN
    public String userProfileImageUrl;

    // Anda bisa menambahkan field lain dari User jika perlu

    // Room memerlukan konstruktor publik, atau semua field harus publik.
    // Jika menggunakan @Embedded, Room akan menangani konstruksi ActivityLog.
    // Untuk field tambahan, Room akan mengisinya jika nama kolom di kueri cocok.

    // Override equals() dan hashCode() jika Anda menggunakan DiffUtil di Adapter
    // dan ingin perbandingan konten yang akurat.
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ActivityFeedItem that = (ActivityFeedItem) o;
        return Objects.equals(activityLog, that.activityLog) &&
                Objects.equals(userName, that.userName) &&
                Objects.equals(userProfileImageUrl, that.userProfileImageUrl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(activityLog, userName, userProfileImageUrl);
    }
}
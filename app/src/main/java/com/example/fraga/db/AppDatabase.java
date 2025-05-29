package com.example.fraga.db;

import android.content.Context;
import android.os.AsyncTask; // Digunakan untuk contoh pre-populate, bisa diganti dengan Executor atau Coroutine

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase; // Untuk Callback

// Impor semua DAO Anda
import com.example.fraga.db.dao.ActivityLogDao;
import com.example.fraga.db.dao.FriendRequestDao;
import com.example.fraga.db.dao.FriendshipDao;
import com.example.fraga.db.dao.MonthlyChallengeDao;
import com.example.fraga.db.dao.PersonalGoalDao;
import com.example.fraga.db.dao.UserChallengeStatusDao;
import com.example.fraga.db.dao.UserDao;

// Impor semua Entitas Anda
import com.example.fraga.db.entity.ActivityLog;
import com.example.fraga.db.entity.FriendRequest;
import com.example.fraga.db.entity.Friendship;
import com.example.fraga.db.entity.MonthlyChallenge;
import com.example.fraga.db.entity.PersonalGoal;
import com.example.fraga.db.entity.User;
import com.example.fraga.db.entity.UserChallengeStatus;

// Impor TypeConverter jika ada
// import com.example.fraga.db.converter.DateConverter;

@Database(entities = {
        User.class,
        ActivityLog.class,
        PersonalGoal.class,
        FriendRequest.class,
        Friendship.class,
        MonthlyChallenge.class,
        UserChallengeStatus.class
        // Tambahkan entitas lain di sini jika ada
},
        version = 1, // Naikkan versi jika Anda mengubah skema database
        exportSchema = false) // Sangat direkomendasikan untuk menyimpan histori skema
// @TypeConverters({DateConverter.class}) // Aktifkan jika Anda memiliki TypeConverter
public abstract class AppDatabase extends RoomDatabase {

    // Definisikan metode abstrak untuk setiap DAO
    public abstract UserDao userDao();
    public abstract ActivityLogDao activityLogDao();
    public abstract PersonalGoalDao personalGoalDao();
    public abstract FriendRequestDao friendRequestDao();
    public abstract FriendshipDao friendshipDao();
    public abstract MonthlyChallengeDao monthlyChallengeDao();
    public abstract UserChallengeStatusDao userChallengeStatusDao();
    // Tambahkan DAO lain di sini

    private static volatile AppDatabase INSTANCE;
    private static final String DATABASE_NAME = "fraga_app_db"; // Nama file database

    public static AppDatabase getInstance(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, DATABASE_NAME)
                            // PERHATIAN: fallbackToDestructiveMigration akan menghapus data lama saat versi berubah
                            // Gunakan ini hanya selama pengembangan awal atau jika Anda tidak peduli kehilangan data.
                            // Untuk produksi, Anda HARUS menyediakan implementasi Migration.
                            // .fallbackToDestructiveMigration()

                            // Callback untuk pre-populate data saat database pertama kali dibuat (opsional)
                            .addCallback(roomDatabaseCallback)

                            // Mengizinkan kueri di main thread (TIDAK DIREKOMENDASIKAN untuk produksi)
                            // .allowMainThreadQueries()
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    // Callback untuk pre-populate data (opsional)
    private static RoomDatabase.Callback roomDatabaseCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            // Lakukan operasi pre-populate di background thread
            // Anda bisa menggunakan ExecutorService atau AsyncTask (meskipun deprecated)
            // atau jika Anda menggunakan Kotlin, Coroutines.
            // Contoh sederhana menggunakan AsyncTask:
            // new PopulateDbAsyncTask(INSTANCE).execute();
            // Atau langsung jika Anda punya Executor:
            // AppExecutors.getInstance().diskIO().execute(() -> {
            //    // Kode pre-populate di sini
            //    UserDao userDao = INSTANCE.userDao();
            //    // Contoh: Buat user default untuk testing, pastikan password di-hash dengan benar
            //    // userDao.insertUser(new User("Test User", "test@example.com", "hashed_password", System.currentTimeMillis()));
            // });
        }

        @Override
        public void onOpen(@NonNull SupportSQLiteDatabase db) {
            super.onOpen(db);
            // Bisa digunakan untuk melakukan sesuatu saat database dibuka
        }
    };

    // Contoh AsyncTask untuk pre-populate (lebih baik gunakan ExecutorService atau Coroutines)
    /*
    private static class PopulateDbAsyncTask extends AsyncTask<Void, Void, Void> {
        private final UserDao userDao;
        private final MonthlyChallengeDao monthlyChallengeDao;
        // Tambahkan DAO lain jika perlu

        PopulateDbAsyncTask(AppDatabase instance) {
            userDao = instance.userDao();
            monthlyChallengeDao = instance.monthlyChallengeDao();
        }

        @Override
        protected Void doInBackground(final Void... params) {
            // Hanya untuk contoh, jangan simpan password plain text di produksi!
            // Anda perlu mekanisme hashing yang aman.
            // userDao.insertUserIfNotExists(new User("Admin User", "admin@fraga.com", "admin123", System.currentTimeMillis()));
            // userDao.insertUserIfNotExists(new User("Test User 1", "test1@fraga.com", "password123", System.currentTimeMillis()));

            // Contoh pre-populate tantangan bulanan
            // Calendar cal = Calendar.getInstance();
            // cal.set(Calendar.DAY_OF_MONTH, 1);
            // long startDate = cal.getTimeInMillis();
            // cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
            // long endDate = cal.getTimeInMillis();
            // String currentMonthYear = new SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(cal.getTime());

            // monthlyChallengeDao.insertChallenge(new MonthlyChallenge(
            //     "May Distance Run",
            //     "Run a total of 50km this May!",
            //     currentMonthYear,
            //     "TOTAL_DISTANCE_KM",
            //     50.0, 0, "km", null, startDate, endDate, true
            // ));
            return null;
        }
    }
    */
}
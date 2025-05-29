package com.example.fraga;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider; // Tambahkan ini jika Anda akan menggunakan ViewModel

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

// Asumsikan Anda akan membuat kelas-kelas ini nanti:
// import com.example.fraga.db.AppDatabase;
// import com.example.fraga.db.entity.ActivityLog; // Asumsi nama entitas Anda
// import com.example.fraga.db.dao.ActivityLogDao; // Asumsi nama DAO Anda

public class ActivityDetailActivity extends AppCompatActivity {

    private ImageView imageViewDetailMap;
    private ImageView imageViewDetailUserAvatar;
    private TextView textViewDetailUsername;
    private TextView textViewDetailActivityTime;
    private TextView textViewDetailActivityTitle;
    private TextView textViewDetailActivityDescription;
    private TextView textViewDetailDistance;
    private TextView textViewDetailDuration;
    private TextView textViewDetailPace;
    private TextView textViewDetailElevation;
    private TextView textViewDetailCalories;
    private TextView textViewDetailAvgHR;
    private TextView textViewDetailMaxHR;
    private EditText editTextComment;
    private ImageButton buttonSendComment;
    private FloatingActionButton fabBack;
    private ExtendedFloatingActionButton fabShare;

    // Tambahkan variabel untuk ID aktivitas dan objek data aktivitas
    public static final String EXTRA_ACTIVITY_ID = "activity_id";
    private int activityId;
    // private ActivityLog currentActivityLog; // Akan digunakan setelah entitas ActivityLog dibuat

    // private AppDatabase appDb; // Akan digunakan setelah AppDatabase dibuat

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        // Initialize views
        initializeViews();

        // Dapatkan instance database
        // appDb = AppDatabase.getInstance(getApplicationContext()); // Akan diaktifkan nanti

        // Dapatkan activityId dari Intent
        activityId = getIntent().getIntExtra(EXTRA_ACTIVITY_ID, -1);

        if (activityId == -1) {
            // Handle kasus jika ID tidak valid atau tidak ditemukan
            Toast.makeText(this, "Error: Activity ID not found.", Toast.LENGTH_LONG).show();
            finish(); // Tutup activity jika tidak ada ID
            return;
        }

        // Set up data (akan mengambil dari database)
        setupActivityData();

        // Set up click listeners
        setupListeners();
    }

    private void initializeViews() {
        imageViewDetailMap = findViewById(R.id.imageViewDetailMap);
        imageViewDetailUserAvatar = findViewById(R.id.imageViewDetailUserAvatar);
        textViewDetailUsername = findViewById(R.id.textViewDetailUsername);
        textViewDetailActivityTime = findViewById(R.id.textViewDetailActivityTime);
        textViewDetailActivityTitle = findViewById(R.id.textViewDetailActivityTitle);
        textViewDetailActivityDescription = findViewById(R.id.textViewDetailActivityDescription);
        textViewDetailDistance = findViewById(R.id.textViewDetailDistance);
        textViewDetailDuration = findViewById(R.id.textViewDetailDuration);
        textViewDetailPace = findViewById(R.id.textViewDetailPace);
        textViewDetailElevation = findViewById(R.id.textViewDetailElevation);
        textViewDetailCalories = findViewById(R.id.textViewDetailCalories);
        textViewDetailAvgHR = findViewById(R.id.textViewDetailAvgHR);
        textViewDetailMaxHR = findViewById(R.id.textViewDetailMaxHR);
        editTextComment = findViewById(R.id.editTextComment);
        buttonSendComment = findViewById(R.id.buttonSendComment);
        fabBack = findViewById(R.id.fabBack);
        fabShare = findViewById(R.id.fabShare);
    }

    private void setupActivityData() {
        // Di aplikasi nyata, Anda akan mengambil data dari database di sini.
        // Ini adalah contoh bagaimana Anda AKAN melakukannya setelah DAO dan Entitas dibuat.
        // Untuk sekarang, kita akan membiarkannya kosong atau menggunakan data placeholder.

        /*
        // Contoh pengambilan data dari database (AKAN DIAKTIFKAN NANTI)
        // Jalankan ini di background thread atau gunakan LiveData/ViewModel
        new Thread(() -> {
            currentActivityLog = appDb.activityLogDao().getActivityById(activityId);
            if (currentActivityLog != null) {
                runOnUiThread(() -> {
                    populateUI(currentActivityLog);
                });
            } else {
                runOnUiThread(() -> {
                    Toast.makeText(ActivityDetailActivity.this, "Activity not found.", Toast.LENGTH_SHORT).show();
                });
            }
        }).start();
        */

        // Untuk sekarang, kita bisa tampilkan ID yang diterima (opsional, untuk debugging)
        // Toast.makeText(this, "Loading data for activity ID: " + activityId, Toast.LENGTH_SHORT).show();

        // Jika Anda ingin tetap menampilkan data contoh dari layout untuk sementara:
        // Biarkan bagian ini kosong, karena data sudah di-set di XML atau
        // Anda bisa set data placeholder di sini jika XML tidak memiliki nilai default.
        // textViewDetailUsername.setText("Contoh User");
        // textViewDetailActivityTime.setText("Contoh Waktu");
        // ... dan seterusnya untuk field lainnya
    }

    // Metode untuk mengisi UI dengan data dari objek ActivityLog (AKAN DIGUNAKAN NANTI)
    /*
    private void populateUI(ActivityLog activityLog) {
        // Asumsikan ActivityLog memiliki getter untuk setiap field
        textViewDetailUsername.setText(activityLog.getUsername()); // Atau dari tabel user terpisah
        textViewDetailActivityTime.setText(activityLog.getFormattedTimestamp()); // Anda perlu format timestamp
        textViewDetailActivityTitle.setText(activityLog.getTitle());
        textViewDetailActivityDescription.setText(activityLog.getDescription());
        textViewDetailDistance.setText(String.format("%.2f km", activityLog.getDistance()));
        textViewDetailDuration.setText(activityLog.getFormattedDuration()); // Anda perlu format durasi
        textViewDetailPace.setText(activityLog.getFormattedPace()); // Anda perlu format pace
        textViewDetailElevation.setText(String.format("%d m", activityLog.getElevation()));
        textViewDetailCalories.setText(String.format("%d kcal", activityLog.getCalories()));
        textViewDetailAvgHR.setText(String.format("%d bpm", activityLog.getAvgHeartRate()));
        textViewDetailMaxHR.setText(String.format("%d bpm", activityLog.getMaxHeartRate()));

        // Mungkin juga mengatur gambar peta atau avatar pengguna jika disimpan/di-referensikan
        // Glide.with(this).load(activityLog.getMapImageUrl()).into(imageViewDetailMap);
        // Glide.with(this).load(activityLog.getUserAvatarUrl()).into(imageViewDetailUserAvatar);
    }
    */

    private void setupListeners() {
        fabBack.setOnClickListener(v -> finish());

        buttonSendComment.setOnClickListener(v -> {
            String commentText = editTextComment.getText().toString().trim();
            if (!commentText.isEmpty()) {
                // Di aplikasi nyata, komentar ini akan disimpan ke database,
                // mungkin terkait dengan activityId.
                Toast.makeText(this, "Comment added (locally): " + commentText + " for activity ID: " + activityId, Toast.LENGTH_SHORT).show();
                editTextComment.setText("");
                // TODO: Implementasi penyimpanan komentar ke database
            }
        });

        fabShare.setOnClickListener(v -> {
            // Create share intent
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Check out my activity on Fraga!");

            // Ambil data dari TextView untuk sekarang, idealnya dari currentActivityLog
            String distance = textViewDetailDistance.getText().toString();
            String duration = textViewDetailDuration.getText().toString();
            String title = textViewDetailActivityTitle.getText().toString();

            // String shareText;
            // if (currentActivityLog != null) {
            //     shareText = "I just completed '" + currentActivityLog.getTitle() + "' (" +
            //                   currentActivityLog.getFormattedDistance() + " in " +
            //                   currentActivityLog.getFormattedDuration() +
            //                   ") with Fraga! Check it out: http://fraga.app/activity/" + activityId;
            // } else {
            //     shareText = "Check out this activity on Fraga! http://fraga.app/activity/" + activityId;
            // }
            // shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);


            // Versi sementara menggunakan data dari UI:
            shareIntent.putExtra(Intent.EXTRA_TEXT,
                    "I just completed a " + title + " (" + distance +
                            " in " + duration +
                            ") with Fraga! Check it out: http://fraga.app/activity/" + activityId); // Gunakan activityId

            startActivity(Intent.createChooser(shareIntent, "Share via"));
        });
    }
}
package com.example.brainhub;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.TypedValue;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class Notifications extends AppCompatActivity {

    private LinearLayout notificationsContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);
        notificationsContainer = findViewById(R.id.notificationsContainer);

        // Setup navigation
        setupNavigation();

        // Load notifications
        loadNotifications();
    }

    private void setupNavigation() {
        // Get the bottom navigation icons
        ImageView homeBtn = findViewById(R.id.notif_home_button);
        ImageView savedBtn = findViewById(R.id.notif_saved_button);
        ImageView createBtn = findViewById(R.id.notif_create_button);
        ImageView notificationsBtn = findViewById(R.id.notif_notifications_button);
        ImageView profileBtn = findViewById(R.id.notif_profile_button);

        // Set onClick listeners for each icon
        homeBtn.setOnClickListener(view -> navigateToHome());
        savedBtn.setOnClickListener(view -> navigateToSavedPosts());
        createBtn.setOnClickListener(view -> navigateToCreatePost());
        notificationsBtn.setOnClickListener(view -> {
            // Prevent navigating to current screen (Notifications)
            Toast.makeText(this, "You are already in Notifications", Toast.LENGTH_SHORT).show();
        });
        profileBtn.setOnClickListener(view -> navigateToProfile());
    }

    private void loadNotifications() {
        SharedPreferences prefs = getSharedPreferences("PostsPrefs", MODE_PRIVATE);
        int postCount = prefs.getInt("postCount", 0);

        if (postCount > 0) {

            LinearLayout todayContainer = new LinearLayout(this);
            todayContainer.setOrientation(LinearLayout.VERTICAL);
            todayContainer.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

            LinearLayout earlierContainer = new LinearLayout(this);
            earlierContainer.setOrientation(LinearLayout.VERTICAL);
            earlierContainer.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));


            TextView todayHeader = new TextView(this);
            todayHeader.setText("Today");
            todayHeader.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
            todayHeader.setTypeface(null, Typeface.BOLD);
            todayHeader.setPadding(dpToPx(2), dpToPx(16), dpToPx(16), dpToPx(8));
            todayContainer.addView(todayHeader);

            TextView earlierHeader = new TextView(this);
            earlierHeader.setText("Earlier");
            earlierHeader.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
            earlierHeader.setTypeface(null, Typeface.BOLD);
            earlierHeader.setPadding(dpToPx(2), dpToPx(16), dpToPx(16), dpToPx(8));
            earlierContainer.addView(earlierHeader);

            for (int i = postCount - 1; i >= 0; i--) {
                String username = prefs.getString("post_" + i + "_username", "");
                long timestamp = prefs.getLong("post_" + i + "_timestamp", 0);

                if (!username.isEmpty()) {

                    String timeCategory = getTimeCategory(timestamp);
                    TextView notification = createNotificationView(username, timestamp);

                    if ("Today".equals(timeCategory)) {
                        todayContainer.addView(notification);
                    } else {
                        earlierContainer.addView(notification);
                    }
                }
            }

            notificationsContainer.addView(todayContainer);
            notificationsContainer.addView(earlierContainer);
        }
    }

    private String getTimeCategory(long timestamp) {
        long now = System.currentTimeMillis();
        long diff = now - timestamp;

        if (diff < 86400000) {
            return "Today";
        } else {
            return "Earlier";
        }
    }

    private TextView createNotificationView(String username, long timestamp) {
        TextView notificationView = new TextView(this);
        String timeAgo = getTimeAgo(timestamp);
        notificationView.setText(username + " posted a new update " + timeAgo);
        notificationView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        layoutParams.setMargins(dpToPx(0), dpToPx(8), dpToPx(0), dpToPx(8));
        notificationView.setLayoutParams(layoutParams);
        return notificationView;
    }

    private String getTimeAgo(long timestamp) {
        long now = System.currentTimeMillis();
        long diff = now - timestamp;

        if (diff < 60000) return "Just now";
        if (diff < 3600000) return (diff / 60000) + "m";
        if (diff < 86400000) return (diff / 3600000) + "h";
        return (diff / 86400000) + "d";
    }

    private int dpToPx(int dp) {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                getResources().getDisplayMetrics()
        );
    }

    private void navigateToHome() {
        Intent intent = new Intent(Notifications.this, HomeActivity.class);
        startActivity(intent);
    }

    private void navigateToSavedPosts() {
        Intent intent = new Intent(Notifications.this, SavedPostPage.class);
        startActivity(intent);
    }

    private void navigateToCreatePost() {
        Intent intent = new Intent(Notifications.this, CreatePost.class);
        startActivity(intent);
    }

    private void navigateToProfile() {
        Intent intent = new Intent(Notifications.this, Profile.class);
        startActivity(intent);
    }
}

package com.example.brainhub;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import android.view.ViewGroup;
import android.util.TypedValue;

public class FeedActivity extends AppCompatActivity {
    private LinearLayout postsContainer;
    private TextView emptyStateText;
    private ActivityResultLauncher<Intent> createPostLauncher;
    private NavigationHelper navigationHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        initializeViews();
        setupCreatePostLauncher();
        setupNavigation();
        refreshFeed();
        handleIncomingIntent(getIntent());
    }

    private void setupNavigation() {
        navigationHelper = new NavigationHelper(this, createPostLauncher);
        navigationHelper.setupNavigation();
    }

    private void handleIncomingIntent(Intent intent) {
        if (intent != null && intent.hasExtra("title")) {
            String title = intent.getStringExtra("title");
            String content = intent.getStringExtra("content");
            String username = intent.getStringExtra("username");
            long timestamp = intent.getLongExtra("timestamp", System.currentTimeMillis());
            hideEmptyState();
            savePost(username, title, content, timestamp);
            refreshFeed();
        }
    }

    private void refreshFeed() {
        postsContainer.removeAllViews();
        loadSavedPosts();
    }

    private void initializeViews() {
        postsContainer = findViewById(R.id.postsContainer);
        emptyStateText = new TextView(this);
        emptyStateText.setText("No posts yet. Be the first to share!");
        emptyStateText.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

        if (postsContainer != null) {
            postsContainer.removeAllViews();
            postsContainer.addView(emptyStateText);
        }
    }

    private void setupClickListeners() {
        ImageView homeBtn = findViewById(R.id.navHome);
        if (homeBtn != null) {
            homeBtn.setOnClickListener(v -> refreshFeed());
        }
    }

    private void loadSavedPosts() {
        SharedPreferences postsPrefs = getSharedPreferences("PostsPrefs", MODE_PRIVATE);
        int postCount = postsPrefs.getInt("postCount", 0);

        if (postCount > 0) {
            hideEmptyState();
            for (int i = postCount - 1; i >= 0; i--) {
                String title = postsPrefs.getString("post_" + i + "_title", "");
                String content = postsPrefs.getString("post_" + i + "_content", "");
                String username = postsPrefs.getString("post_" + i + "_username", "");
                long timestamp = postsPrefs.getLong("post_" + i + "_timestamp", 0);

                if (!title.isEmpty() && !content.isEmpty()) {
                    addPostToFeed(username, title, content, timestamp, false);
                }
            }
        } else {
            showEmptyState();
        }
    }

    private void setupCreatePostLauncher() {
        createPostLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        String title = result.getData().getStringExtra("title");
                        String content = result.getData().getStringExtra("content");
                        String username = result.getData().getStringExtra("username");
                        long timestamp = result.getData().getLongExtra("timestamp", System.currentTimeMillis());
                        hideEmptyState();
                        savePost(username, title, content, timestamp);
                        refreshFeed();
                    }
                }
        );
    }

    private void showEmptyState() {
        emptyStateText.setVisibility(View.VISIBLE);
    }

    private void hideEmptyState() {
        emptyStateText.setVisibility(View.GONE);
    }

    private void savePost(String username, String title, String content, long timestamp) {
        SharedPreferences postsPrefs = getSharedPreferences("PostsPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = postsPrefs.edit();
        int postCount = postsPrefs.getInt("postCount", 0);

        editor.putString("post_" + postCount + "_title", title);
        editor.putString("post_" + postCount + "_content", content);
        editor.putString("post_" + postCount + "_username", username);
        editor.putLong("post_" + postCount + "_timestamp", timestamp);
        editor.putInt("postCount", postCount + 1);
        editor.apply();
    }

    private void addPostToFeed(String username, String title, String content, long timestamp, boolean save) {
        LinearLayout postView = new LinearLayout(this);
        postView.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams postParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        postParams.setMargins(0, 0, 0, dpToPx(15));
        postView.setLayoutParams(postParams);

        LinearLayout userInfoLayout = new LinearLayout(this);
        userInfoLayout.setOrientation(LinearLayout.HORIZONTAL);
        userInfoLayout.setGravity(android.view.Gravity.CENTER_VERTICAL);
        userInfoLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));

        ImageView profilePic = new ImageView(this);
        LinearLayout.LayoutParams profilePicParams = new LinearLayout.LayoutParams(dpToPx(40), dpToPx(40));
        profilePicParams.setMargins(dpToPx(20), 0, 0, 0);
        profilePic.setLayoutParams(profilePicParams);

        TextView usernameView = new TextView(this);
        LinearLayout.LayoutParams usernameParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        usernameParams.setMargins(dpToPx(10), 0, dpToPx(10), 0);
        usernameView.setLayoutParams(usernameParams);
        usernameView.setText(username);

        TextView timeView = new TextView(this);
        timeView.setText(getTimeAgo(timestamp));

        LinearLayout contentContainer = new LinearLayout(this);
        contentContainer.setOrientation(LinearLayout.VERTICAL);
        contentContainer.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));

        TextView titleView = new TextView(this);
        LinearLayout.LayoutParams titleParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        titleParams.setMargins(dpToPx(20), dpToPx(5), dpToPx(20), dpToPx(5));
        titleView.setLayoutParams(titleParams);
        titleView.setText(title);
        titleView.setTypeface(null, android.graphics.Typeface.BOLD);

        TextView contentView = new TextView(this);
        LinearLayout.LayoutParams contentParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        contentParams.setMargins(dpToPx(20), 0, dpToPx(20), 0);
        contentView.setLayoutParams(contentParams);
        contentView.setText(content);

        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String encodedImage = prefs.getString("USER_PHOTO", null);
        if (encodedImage != null) {
            byte[] decodedBytes = Base64.decode(encodedImage, Base64.DEFAULT);
            Bitmap photo = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
            profilePic.setImageBitmap(photo);
        }

        userInfoLayout.addView(profilePic);
        userInfoLayout.addView(usernameView);
        userInfoLayout.addView(timeView);

        contentContainer.addView(titleView);
        contentContainer.addView(contentView);

        postView.addView(userInfoLayout);
        postView.addView(contentContainer);

        if (save) {
            savePost(username, title, content, timestamp);
        }

        postsContainer.addView(postView, 0);
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

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIncomingIntent(intent);
    }
}
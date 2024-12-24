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
    private ImageView createBtn;
    private TextView emptyStateText;
    private ActivityResultLauncher<Intent> createPostLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        initializeViews();
        setupCreatePostLauncher();
        setupClickListeners();
        loadSavedPosts();

        // Handle incoming post data if any
        handleIncomingIntent(getIntent());
    }

    private void handleIncomingIntent(Intent intent) {
        if (intent != null && intent.hasExtra("title")) {
            String title = intent.getStringExtra("title");
            String content = intent.getStringExtra("content");
            String username = intent.getStringExtra("username");
            long timestamp = intent.getLongExtra("timestamp", System.currentTimeMillis());
            hideEmptyState();
            addPostToFeed(username, title, content, timestamp);
        }
    }

    private void initializeViews() {
        postsContainer = findViewById(R.id.postsContainer);
        createBtn = findViewById(R.id.navPost);  // Changed from ivCreateBtn to navPost
        emptyStateText = new TextView(this);
        emptyStateText.setText("No posts yet. Be the first to share!");
        emptyStateText.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

        // Clear example post content first
        if (postsContainer != null) {
            postsContainer.removeAllViews();
            postsContainer.addView(emptyStateText);
        }
    }

    private void setupClickListeners() {
        createBtn.setOnClickListener(v -> {
            Intent intent = new Intent(this, CreatePost.class);
            createPostLauncher.launch(intent);
        });

        ImageView homeBtn = findViewById(R.id.navHome);  // Changed from ivHomeBtn to navHome
        if (homeBtn != null) {
            homeBtn.setOnClickListener(v -> {
                postsContainer.removeAllViews();
                postsContainer.addView(emptyStateText);
                loadSavedPosts();
            });
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
                    addPostToFeed(username, title, content, timestamp);
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
                        addPostToFeed(username, title, content, timestamp);
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

    private void addPostToFeed(String username, String title, String content, long timestamp) {
        LinearLayout postView = new LinearLayout(this);
        postView.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams postParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        postParams.setMargins(0, 0, 0, dpToPx(15));
        postView.setLayoutParams(postParams);

        // User info layout
        LinearLayout userInfoLayout = new LinearLayout(this);
        userInfoLayout.setOrientation(LinearLayout.HORIZONTAL);
        userInfoLayout.setGravity(android.view.Gravity.CENTER_VERTICAL);
        userInfoLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));

        // Profile picture
        ImageView profilePic = new ImageView(this);
        LinearLayout.LayoutParams profilePicParams = new LinearLayout.LayoutParams(dpToPx(40), dpToPx(40));
        profilePicParams.setMargins(dpToPx(20), 0, 0, 0);
        profilePic.setLayoutParams(profilePicParams);

        // Username
        TextView usernameView = new TextView(this);
        LinearLayout.LayoutParams usernameParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        usernameParams.setMargins(dpToPx(10), 0, dpToPx(10), 0);
        usernameView.setLayoutParams(usernameParams);
        usernameView.setText(username);

        // Time
        TextView timeView = new TextView(this);
        timeView.setText(getTimeAgo(timestamp));

        // Content container
        LinearLayout contentContainer = new LinearLayout(this);
        contentContainer.setOrientation(LinearLayout.VERTICAL);
        contentContainer.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));

        // Title
        TextView titleView = new TextView(this);
        LinearLayout.LayoutParams titleParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        titleParams.setMargins(dpToPx(20), dpToPx(5), dpToPx(20), dpToPx(5));
        titleView.setLayoutParams(titleParams);
        titleView.setText(title);
        titleView.setTypeface(null, android.graphics.Typeface.BOLD);

        // Content
        TextView contentView = new TextView(this);
        LinearLayout.LayoutParams contentParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        contentParams.setMargins(dpToPx(20), 0, dpToPx(20), 0);
        contentView.setLayoutParams(contentParams);
        contentView.setText(content);

        // Set profile picture from SharedPreferences
        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String encodedImage = prefs.getString("USER_PHOTO", null);
        if (encodedImage != null) {
            byte[] decodedBytes = Base64.decode(encodedImage, Base64.DEFAULT);
            Bitmap photo = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
            profilePic.setImageBitmap(photo);
        }

        // Assemble the post view
        userInfoLayout.addView(profilePic);
        userInfoLayout.addView(usernameView);
        userInfoLayout.addView(timeView);

        contentContainer.addView(titleView);
        contentContainer.addView(contentView);

        postView.addView(userInfoLayout);
        postView.addView(contentContainer);

        // Save post to SharedPreferences
        SharedPreferences postsPrefs = getSharedPreferences("PostsPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = postsPrefs.edit();
        int postCount = postsPrefs.getInt("postCount", 0);

        editor.putString("post_" + postCount + "_title", title);
        editor.putString("post_" + postCount + "_content", content);
        editor.putString("post_" + postCount + "_username", username);
        editor.putLong("post_" + postCount + "_timestamp", timestamp);
        editor.putInt("postCount", postCount + 1);
        editor.apply();

        // Add the post to the top of the feed
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
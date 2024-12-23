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

        Intent intent = getIntent();
        if (intent.hasExtra("title")) {
            String title = intent.getStringExtra("title");
            String content = intent.getStringExtra("content");
            String username = intent.getStringExtra("username");
            long timestamp = intent.getLongExtra("timestamp", System.currentTimeMillis());
            hideEmptyState();
            addPostToFeed(username, title, content, timestamp);
        } else {
            showEmptyState();
        }
    }

    private void initializeViews() {
        postsContainer = findViewById(R.id.postsContainer);
        createBtn = findViewById(R.id.ivCreateBtn);
        emptyStateText = new TextView(this);
        emptyStateText.setText("No posts yet. Be the first to share!");
        emptyStateText.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        postsContainer.removeAllViews();
        postsContainer.addView(emptyStateText);
    }

    private void setupClickListeners() {
        createBtn.setOnClickListener(v -> {
            Intent intent = new Intent(this, CreatePost.class);
            createPostLauncher.launch(intent);
        });

        ImageView homeBtn = findViewById(R.id.ivHomeBtn);
        if (homeBtn != null) {
            homeBtn.setOnClickListener(v -> recreate());
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

        SharedPreferences postsPrefs = getSharedPreferences("PostsPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = postsPrefs.edit();
        int postCount = postsPrefs.getInt("postCount", 0);

        editor.putString("post_" + postCount + "_title", title);
        editor.putString("post_" + postCount + "_content", content);
        editor.putString("post_" + postCount + "_username", username);
        editor.putLong("post_" + postCount + "_timestamp", timestamp);
        editor.putInt("postCount", postCount + 1);
        editor.apply();

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
}
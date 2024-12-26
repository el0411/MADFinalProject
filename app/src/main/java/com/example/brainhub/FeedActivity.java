package com.example.brainhub;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Base64;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import android.util.TypedValue;
import android.widget.Toast;

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
        CardView cardView = new CardView(this);
        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        cardParams.setMargins(dpToPx(16), dpToPx(8), dpToPx(16), dpToPx(8));
        cardView.setLayoutParams(cardParams);
        cardView.setCardElevation(dpToPx(2));
        cardView.setRadius(dpToPx(12));

        LinearLayout postView = new LinearLayout(this);
        postView.setOrientation(LinearLayout.VERTICAL);
        postView.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        postView.setPadding(dpToPx(16), dpToPx(12), dpToPx(16), dpToPx(12));

        LinearLayout headerLayout = new LinearLayout(this);
        headerLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        headerLayout.setOrientation(LinearLayout.HORIZONTAL);
        headerLayout.setGravity(Gravity.CENTER_VERTICAL);

        ImageView profilePic = new ImageView(this);
        int profileSize = dpToPx(40);
        profilePic.setLayoutParams(new LinearLayout.LayoutParams(profileSize, profileSize));
        profilePic.setScaleType(ImageView.ScaleType.CENTER_CROP);
        profilePic.setBackgroundResource(android.R.drawable.ic_menu_gallery);

        LinearLayout userInfoLayout = new LinearLayout(this);
        LinearLayout.LayoutParams userInfoParams = new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1.0f
        );
        userInfoParams.setMargins(dpToPx(12), 0, 0, 0);
        userInfoLayout.setLayoutParams(userInfoParams);
        userInfoLayout.setOrientation(LinearLayout.VERTICAL);

        TextView usernameView = new TextView(this);
        usernameView.setText(username);
        usernameView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        usernameView.setTypeface(null, Typeface.BOLD);

        TextView timeView = new TextView(this);
        timeView.setText(getTimeAgo(timestamp));
        timeView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        timeView.setAlpha(0.7f);

        Button followButton = new Button(this);
        LinearLayout.LayoutParams followParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                dpToPx(37)
        );
        followParams.setMargins(dpToPx(16), 0, dpToPx(16), 0);
        followButton.setLayoutParams(followParams);
        followButton.setText("Follow");
        followButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        followButton.setBackgroundColor(getResources().getColor(R.color.black));
        followButton.setTextColor(getResources().getColor(R.color.white));
        followButton.setOnClickListener(v -> {
            Toast.makeText(this, username + " followed!", Toast.LENGTH_SHORT).show();
        });

        ImageView saveButton = new ImageView(this);
        LinearLayout.LayoutParams saveParams = new LinearLayout.LayoutParams(dpToPx(25), dpToPx(25));
        saveParams.setMargins(0, 0, dpToPx(10), 0);
        saveButton.setLayoutParams(saveParams);
        saveButton.setImageResource(R.drawable.icon_save);
        saveButton.setScaleType(ImageView.ScaleType.FIT_CENTER);
        saveButton.setOnClickListener(v -> {
            Toast.makeText(this, "Post saved!", Toast.LENGTH_SHORT).show();
        });

        ImageView likeButton = new ImageView(this);
        likeButton.setLayoutParams(new LinearLayout.LayoutParams(dpToPx(24), dpToPx(24)));
        likeButton.setImageResource(R.drawable.icon_like);
        likeButton.setPadding(dpToPx(2), dpToPx(2), dpToPx(2), dpToPx(2));
        likeButton.setOnClickListener(v -> {
            Toast.makeText(this, "Liked!", Toast.LENGTH_SHORT).show();
        });

        ImageView shareButton = new ImageView(this);
        LinearLayout.LayoutParams shareParams = new LinearLayout.LayoutParams(dpToPx(24), dpToPx(24));
        shareParams.setMargins(dpToPx(16), 0, 0, 0);
        shareButton.setLayoutParams(shareParams);
        shareButton.setImageResource(R.drawable.icon_share);
        shareButton.setPadding(dpToPx(2), dpToPx(2), dpToPx(2), dpToPx(2));
        shareButton.setOnClickListener(v -> {
            Toast.makeText(this, "Shared!", Toast.LENGTH_SHORT).show();
        });

        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String encodedImage = prefs.getString("USER_PHOTO", null);
        if (encodedImage != null) {
            byte[] decodedBytes = Base64.decode(encodedImage, Base64.DEFAULT);
            Bitmap photo = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
            profilePic.setImageBitmap(photo);
        }

        userInfoLayout.addView(usernameView);
        userInfoLayout.addView(timeView);

        headerLayout.addView(profilePic);
        headerLayout.addView(userInfoLayout);
        headerLayout.addView(followButton);
        headerLayout.addView(saveButton);

        LinearLayout contentContainer = new LinearLayout(this);
        contentContainer.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams contentContainerParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        contentContainerParams.setMargins(0, dpToPx(12), 0, dpToPx(12));
        contentContainer.setLayoutParams(contentContainerParams);

        TextView titleView = new TextView(this);
        titleView.setText(title);
        titleView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        titleView.setTypeface(null, Typeface.BOLD);
        LinearLayout.LayoutParams titleParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        titleParams.setMargins(0, 0, 0, dpToPx(4));
        titleView.setLayoutParams(titleParams);

        TextView contentView = new TextView(this);
        contentView.setText(content);
        contentView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);

        contentContainer.addView(titleView);
        contentContainer.addView(contentView);

        LinearLayout interactionLayout = new LinearLayout(this);
        interactionLayout.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams interactionParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        interactionParams.setMargins(0, dpToPx(8), 0, 0);
        interactionLayout.setLayoutParams(interactionParams);

        interactionLayout.addView(likeButton);
        interactionLayout.addView(shareButton);

        postView.addView(headerLayout);
        postView.addView(contentContainer);
        postView.addView(interactionLayout);

        cardView.addView(postView);

        if (save) {
            savePost(username, title, content, timestamp);
        }

        postsContainer.addView(cardView, 0);
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

package com.example.brainhub;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

public class FeedActivity extends AppCompatActivity {
    private ImageView homeBtn, savedBtn, createBtn, notificationsBtn, profileBtn;
    private ArrayList<Bundle> posts = new ArrayList<>();
    private LinearLayout postsContainer;

    private ActivityResultLauncher<Intent> createPostLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Bundle postData = result.getData().getExtras();
                    if (postData != null) {
                        posts.add(0, postData);  // Add new post at the beginning
                        updateFeedUI();
                    }
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);

        initializeViews();
        setupClickListeners();
        updateFeedUI();
    }

    private void initializeViews() {
        homeBtn = findViewById(R.id.ivHomeBtn);
        savedBtn = findViewById(R.id.ivSavedBtn);
        createBtn = findViewById(R.id.ivCreateBtn);
        notificationsBtn = findViewById(R.id.ivNotifications);
        profileBtn = findViewById(R.id.ivProfileBtn);
        postsContainer = findViewById(R.id.postsContainer);  // Add this LinearLayout inside your ScrollView
    }

    private void setupClickListeners() {
        createBtn.setOnClickListener(v -> {
            Intent intent = new Intent(FeedActivity.this, CreatePost.class);
            createPostLauncher.launch(intent);
        });

        homeBtn.setOnClickListener(v -> updateFeedUI());
        savedBtn.setOnClickListener(v -> {/* TODO */});
        notificationsBtn.setOnClickListener(v -> {/* TODO */});
        profileBtn.setOnClickListener(v -> {/* TODO */});
    }

    private void updateFeedUI() {
        if (postsContainer != null) {
            postsContainer.removeAllViews();

            for (Bundle postData : posts) {
                // Inflate the post layout structure directly from activity_feed.xml
                LinearLayout postLayout = new LinearLayout(this);
                postLayout.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                ));
                postLayout.setOrientation(LinearLayout.VERTICAL);
                postLayout.setPadding(0, 0, 0, dpToPx(15)); // 15dp bottom margin

                // User info layout
                LinearLayout userInfoLayout = new LinearLayout(this);
                userInfoLayout.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                ));
                userInfoLayout.setGravity(android.view.Gravity.CENTER_VERTICAL);
                userInfoLayout.setOrientation(LinearLayout.HORIZONTAL);

                // Profile picture
                ImageView profilePic = new ImageView(this);
                LinearLayout.LayoutParams picParams = new LinearLayout.LayoutParams(dpToPx(40), dpToPx(40));
                picParams.setMarginStart(dpToPx(20));
                profilePic.setLayoutParams(picParams);
                profilePic.setImageResource(android.R.drawable.sym_def_app_icon); // placeholder

                // Username
                TextView username = new TextView(this);
                LinearLayout.LayoutParams usernameParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                usernameParams.setMargins(dpToPx(10), 0, dpToPx(10), 0);
                username.setLayoutParams(usernameParams);
                username.setText(postData.getString("username", "Anonymous"));

                // Time
                TextView time = new TextView(this);
                time.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                ));
                time.setText("Just now");

                userInfoLayout.addView(profilePic);
                userInfoLayout.addView(username);
                userInfoLayout.addView(time);

                // Post content layout
                LinearLayout contentLayout = new LinearLayout(this);
                contentLayout.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                ));
                contentLayout.setOrientation(LinearLayout.VERTICAL);

                // Title
                TextView title = new TextView(this);
                LinearLayout.LayoutParams titleParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                titleParams.setMargins(dpToPx(20), dpToPx(5), dpToPx(10), dpToPx(5));
                title.setLayoutParams(titleParams);
                title.setText(postData.getString("title", ""));
                //title.setTextStyle(android.graphics.Typeface.BOLD);

                // Content
                TextView content = new TextView(this);
                LinearLayout.LayoutParams contentParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                contentParams.setMargins(dpToPx(20), 0, dpToPx(10), 0);
                content.setLayoutParams(contentParams);
                content.setText(postData.getString("content", ""));

                contentLayout.addView(title);
                contentLayout.addView(content);

                // Add all views to post layout
                postLayout.addView(userInfoLayout);
                postLayout.addView(contentLayout);

                // Add post to container
                postsContainer.addView(postLayout);
            }
        }
    }

    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }

    private void setTextStyle(TextView textView, int style) {
        textView.setTypeface(textView.getTypeface(), style);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("posts", posts);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        ArrayList<Bundle> savedPosts = savedInstanceState.getParcelableArrayList("posts");
        if (savedPosts != null) {
            posts = savedPosts;
            updateFeedUI();
        }
    }
}
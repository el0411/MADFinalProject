package com.example.brainhub;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.Toast;
import java.util.ArrayList;

public class SavedPostPage extends AppCompatActivity {
    private LinearLayout postContainer;
    private ArrayList<Bundle> savedPosts;
    private LayoutInflater layoutInflater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_post_page);

        layoutInflater = LayoutInflater.from(this);
        postContainer = findViewById(R.id.post_container);
        loadSavedPosts();
        displaySavedPosts();

        // Setup bottom navigation
        setupNavigation();
    }

    private void loadSavedPosts() {
        savedPosts = new ArrayList<>();

        // Retrieve the saved post passed from the FeedActivity
        Bundle savedPost = getIntent().getBundleExtra("savedPost");
        if (savedPost != null) {
            savedPosts.add(savedPost); // Add the saved post to the list
        }
    }

    private void displaySavedPosts() {
        postContainer.removeAllViews();

        // Display each saved post in the container
        for (Bundle post : savedPosts) {
            View postView = layoutInflater.inflate(R.layout.saved_post_item, null);

            ImageView profilePic = postView.findViewById(R.id.profile_image);
            TextView username = postView.findViewById(R.id.username);
            TextView timestamp = postView.findViewById(R.id.timestamp);
            TextView title = postView.findViewById(R.id.post_title);
            TextView content = postView.findViewById(R.id.post_content);

            username.setText(post.getString("username", "Anonymous"));
            timestamp.setText(post.getString("timestamp", ""));
            title.setText(post.getString("title", ""));
            content.setText(post.getString("content", ""));

            postContainer.addView(postView);
        }
    }

    private void setupNavigation() {
        // Get the bottom navigation icons
        ImageView homeBtn = findViewById(R.id.home_button);
        ImageView savedBtn = findViewById(R.id.saved_button);
        ImageView createBtn = findViewById(R.id.create_button);
        ImageView notificationsBtn = findViewById(R.id.notifications_button);
        ImageView profileBtn = findViewById(R.id.profile_button);

        // Set onClick listeners for each icon
        homeBtn.setOnClickListener(view -> navigateToHome());
        savedBtn.setOnClickListener(view -> navigateToSavedPosts());
        createBtn.setOnClickListener(view -> navigateToCreatePost());
        notificationsBtn.setOnClickListener(view -> navigateToNotifications());
        profileBtn.setOnClickListener(view -> navigateToProfile());
    }

    private void navigateToHome() {
        // Example navigation to Home activity
        Intent intent = new Intent(SavedPostPage.this, HomeActivity.class);
        startActivity(intent);
    }

    private void navigateToSavedPosts() {
        // You are already on the Saved Post page, so show a message or navigate somewhere else
        Toast.makeText(this, "You are already in Saved Posts", Toast.LENGTH_SHORT).show();
    }

    private void navigateToCreatePost() {
        Intent intent = new Intent(SavedPostPage.this, CreatePost.class);
        startActivity(intent);
    }

    private void navigateToNotifications() {
        Intent intent = new Intent(SavedPostPage.this, Notifications.class);
        startActivity(intent);
    }

    private void navigateToProfile() {
        Intent intent = new Intent(SavedPostPage.this, Profile.class);
        startActivity(intent);
    }
}

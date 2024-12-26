package com.example.brainhub;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class SavedPostPage extends AppCompatActivity {
    private LinearLayout postContainer;
    private ArrayList<Bundle> savedPosts;
    private LayoutInflater layoutInflater;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_post_page);

        layoutInflater = LayoutInflater.from(this);
        postContainer = findViewById(R.id.post_container);
        prefs = getSharedPreferences("SavedPostsPrefs", MODE_PRIVATE);

        setupNavigation();
        loadSavedPosts();
        displaySavedPosts();
    }

    private void loadSavedPosts() {
        savedPosts = new ArrayList<>();

        // Load existing saved posts from SharedPreferences
        Set<String> savedPostIds = prefs.getStringSet("saved_post_ids", new HashSet<>());

        // Retrieve the new saved post passed from the FeedActivity
        Bundle newSavedPost = getIntent().getBundleExtra("savedPost");
        if (newSavedPost != null) {
            String postId = newSavedPost.getString("postId");
            if (postId != null && !savedPostIds.contains(postId)) {
                savedPosts.add(newSavedPost);

                // Update SharedPreferences with new post
                savedPostIds.add(postId);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putStringSet("saved_post_ids", savedPostIds);
                editor.apply();
            }
        }

        // Load all saved posts from SharedPreferences
        for (String postId : savedPostIds) {
            Bundle post = loadPostFromPrefs(postId);
            if (post != null) {
                savedPosts.add(post);
            }
        }
    }

    private Bundle loadPostFromPrefs(String postId) {
        Bundle post = new Bundle();
        String prefix = "post_" + postId + "_";

        String username = prefs.getString(prefix + "username", "");
        String title = prefs.getString(prefix + "title", "");
        String content = prefs.getString(prefix + "content", "");
        String timestamp = prefs.getString(prefix + "timestamp", "");
        String profilePicture = prefs.getString(prefix + "profile_picture", "");

        if (!username.isEmpty()) {
            post.putString("postId", postId);
            post.putString("username", username);
            post.putString("title", title);
            post.putString("content", content);
            post.putString("timestamp", timestamp);
            post.putString("profile_picture", profilePicture);
            return post;
        }
        return null;
    }

    private void displaySavedPosts() {
        postContainer.removeAllViews();

        for (Bundle post : savedPosts) {
            View postView = layoutInflater.inflate(R.layout.saved_post_item, null);

            ImageView profilePic = postView.findViewById(R.id.profile_image);
            TextView username = postView.findViewById(R.id.username);
            TextView timestamp = postView.findViewById(R.id.timestamp);
            TextView title = postView.findViewById(R.id.post_title);
            TextView content = postView.findViewById(R.id.post_content);
            Button unsaveButton = postView.findViewById(R.id.unsave_button);

            // Set profile picture
            String encodedImage = post.getString("profile_picture");
            if (encodedImage != null && !encodedImage.isEmpty()) {
                Bitmap profileBitmap = decodeBase64ToBitmap(encodedImage);
                if (profileBitmap != null) {
                    profilePic.setImageBitmap(profileBitmap);
                }
            }

            username.setText(post.getString("username", "Anonymous"));
            timestamp.setText(post.getString("timestamp", ""));
            title.setText(post.getString("title", ""));
            content.setText(post.getString("content", ""));

            // Setup unsave button
            final String postId = post.getString("postId");
            unsaveButton.setOnClickListener(v -> unsavePost(postId));

            postContainer.addView(postView);
        }
    }

    private void unsavePost(String postId) {
        // Remove from SharedPreferences
        Set<String> savedPostIds = new HashSet<>(prefs.getStringSet("saved_post_ids", new HashSet<>()));
        savedPostIds.remove(postId);

        SharedPreferences.Editor editor = prefs.edit();
        editor.putStringSet("saved_post_ids", savedPostIds);

        // Remove post details
        String prefix = "post_" + postId + "_";
        editor.remove(prefix + "username");
        editor.remove(prefix + "title");
        editor.remove(prefix + "content");
        editor.remove(prefix + "timestamp");
        editor.remove(prefix + "profile_picture");
        editor.apply();

        // Remove from current list and refresh UI
        savedPosts.removeIf(post -> postId.equals(post.getString("postId")));
        displaySavedPosts();

        Toast.makeText(this, "Post removed from saved posts", Toast.LENGTH_SHORT).show();
    }

    private Bitmap decodeBase64ToBitmap(String encodedImage) {
        try {
            byte[] decodedBytes = Base64.decode(encodedImage, Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void setupNavigation() {
        ImageView homeBtn = findViewById(R.id.home_button);
        ImageView savedBtn = findViewById(R.id.saved_button);
        ImageView createBtn = findViewById(R.id.create_button);
        ImageView notificationsBtn = findViewById(R.id.notifications_button);
        ImageView profileBtn = findViewById(R.id.profile_button);

        homeBtn.setOnClickListener(v -> startActivity(new Intent(this, HomeActivity.class)));
        savedBtn.setOnClickListener(v ->
                Toast.makeText(this, "You are already in Saved Posts", Toast.LENGTH_SHORT).show());
        createBtn.setOnClickListener(v -> startActivity(new Intent(this, CreatePost.class)));
        notificationsBtn.setOnClickListener(v -> startActivity(new Intent(this, Notifications.class)));
        profileBtn.setOnClickListener(v -> startActivity(new Intent(this, Profile.class)));
    }
}
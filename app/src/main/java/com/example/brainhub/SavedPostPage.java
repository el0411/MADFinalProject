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
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class SavedPostPage extends AppCompatActivity {
    private LinearLayout postContainer;
    private ArrayList<Bundle> savedPosts;
    private LayoutInflater layoutInflater;
    private SharedPreferences prefs;
    private ActivityResultLauncher<Intent> createPostLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_post_page);

        layoutInflater = LayoutInflater.from(this);
        postContainer = findViewById(R.id.post_container);
        prefs = getSharedPreferences("SavedPostsPrefs", MODE_PRIVATE);
        savedPosts = new ArrayList<>();

        setupCreatePostLauncher();
        setupNavigation();
        handleIncomingPost();
        loadSavedPosts();
        displaySavedPosts();
    }

    private void handleIncomingPost() {
        Bundle newSavedPost = getIntent().getBundleExtra("savedPost");
        if (newSavedPost != null) {
            // Generate a unique ID for the post if it doesn't have one
            String postId = newSavedPost.getString("postId", UUID.randomUUID().toString());
            newSavedPost.putString("postId", postId);

            Set<String> savedPostIds = new HashSet<>(prefs.getStringSet("saved_post_ids", new HashSet<>()));
            if (!savedPostIds.contains(postId)) {
                savedPostIds.add(postId);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putStringSet("saved_post_ids", savedPostIds);
                savePostToPrefs(postId, newSavedPost);
                editor.apply();
            }
        }
    }

    private void savePostToPrefs(String postId, Bundle post) {
        SharedPreferences.Editor editor = prefs.edit();
        String prefix = "post_" + postId + "_";

        editor.putString(prefix + "username", post.getString("username"));
        editor.putString(prefix + "title", post.getString("title"));
        editor.putString(prefix + "content", post.getString("content"));
        editor.putString(prefix + "timestamp", post.getString("timestamp"));
        editor.putString(prefix + "profile_picture", post.getString("profile_picture", ""));

        editor.apply();
    }

    private void loadSavedPosts() {
        savedPosts.clear();
        Set<String> savedPostIds = prefs.getStringSet("saved_post_ids", new HashSet<>());

        for (String postId : savedPostIds) {
            Bundle post = loadPostFromPrefs(postId);
            if (post != null) {
                savedPosts.add(post);
            }
        }
    }

    private Bundle loadPostFromPrefs(String postId) {
        String prefix = "post_" + postId + "_";

        String username = prefs.getString(prefix + "username", "");
        String title = prefs.getString(prefix + "title", "");
        String content = prefs.getString(prefix + "content", "");
        String timestamp = prefs.getString(prefix + "timestamp", "");
        String profilePicture = prefs.getString(prefix + "profile_picture", "");

        if (!username.isEmpty()) {
            Bundle post = new Bundle();
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

        if (savedPosts.isEmpty()) {
            displayEmptyState();
            return;
        }

        for (Bundle post : savedPosts) {
            View postView = createPostView(post);
            postContainer.addView(postView);
        }
    }

    private View createPostView(Bundle post) {
        View postView = layoutInflater.inflate(R.layout.saved_post_item, null);

        ImageView profilePic = postView.findViewById(R.id.profile_image);
        TextView username = postView.findViewById(R.id.username);
        TextView timestamp = postView.findViewById(R.id.timestamp);
        TextView title = postView.findViewById(R.id.post_title);
        TextView content = postView.findViewById(R.id.post_content);
        Button unsaveButton = postView.findViewById(R.id.unsave_button);

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

        final String postId = post.getString("postId");
        unsaveButton.setOnClickListener(v -> unsavePost(postId));

        return postView;
    }

    private void displayEmptyState() {
        TextView emptyStateText = new TextView(this);
        emptyStateText.setText("No saved posts yet");
        emptyStateText.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        emptyStateText.setPadding(0, 50, 0, 0);
        postContainer.addView(emptyStateText);
    }

    private void setupNavigation() {
        ImageView homeBtn = findViewById(R.id.home_button);
        ImageView savedBtn = findViewById(R.id.saved_button);
        ImageView createBtn = findViewById(R.id.create_button);
        ImageView notificationsBtn = findViewById(R.id.notifications_button);
        ImageView profileBtn = findViewById(R.id.profile_button);

        homeBtn.setOnClickListener(v -> navigateToHome());
        savedBtn.setOnClickListener(v -> Toast.makeText(this, "You are already in Saved Posts", Toast.LENGTH_SHORT).show());
        createBtn.setOnClickListener(v -> navigateToCreate());
        notificationsBtn.setOnClickListener(v -> navigateToNotifications());
        profileBtn.setOnClickListener(v -> navigateToProfile());
    }

    private void navigateToHome() {
        Intent intent = new Intent(this, FeedActivity.class); // Changed from HomeActivity to FeedActivity
        startActivity(intent);
        finish();
    }

    private void navigateToCreate() {
        Intent intent = new Intent(this, CreatePost.class);
        createPostLauncher.launch(intent);
    }

    private void navigateToNotifications() {
        Intent intent = new Intent(this, Notifications.class);
        startActivity(intent);
        finish();
    }

    private void navigateToProfile() {
        Intent intent = new Intent(this, Profile.class);
        startActivity(intent);
        finish();
    }

    private void unsavePost(String postId) {
        Set<String> savedPostIds = new HashSet<>(prefs.getStringSet("saved_post_ids", new HashSet<>()));
        savedPostIds.remove(postId);

        SharedPreferences.Editor editor = prefs.edit();
        editor.putStringSet("saved_post_ids", savedPostIds);

        String prefix = "post_" + postId + "_";
        editor.remove(prefix + "username");
        editor.remove(prefix + "title");
        editor.remove(prefix + "content");
        editor.remove(prefix + "timestamp");
        editor.remove(prefix + "profile_picture");
        editor.apply();

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

    private void setupCreatePostLauncher() {
        createPostLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        loadSavedPosts();
                        displaySavedPosts();
                    }
                }
        );
    }
}
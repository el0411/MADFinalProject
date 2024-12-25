// SavedPostPage.java
package com.example.brainhub;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

public class SavedPostPage extends AppCompatActivity {
    private LinearLayout postContainer;
    private ArrayList<Bundle> savedPosts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_post_page);

        postContainer = findViewById(R.id.post_container);
        loadSavedPosts();
        displaySavedPosts();
    }

    private void loadSavedPosts() {
        // Get saved posts from SharedPreferences or database
        savedPosts = new ArrayList<>();
        // TODO: Implement actual data persistence
    }

    private void displaySavedPosts() {
        postContainer.removeAllViews();

        for (Bundle post : savedPosts) {
            View postView = getLayoutInflater().inflate(R.layout.saved_post_item, null);

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
}

// FeedActivity.java modifications:
// Add to class fields:
private ArrayList<Bundle> savedPosts = new ArrayList<>();

// Add method:
private void savePost(Bundle post) {
    savedPosts.add(post);
    // TODO: Implement persistence using SharedPreferences or database
    // Update SavedPostPage when implemented
}
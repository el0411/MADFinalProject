package com.example.brainhub;

import android.os.Bundle;
import android.view.View;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
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
    }

    private void loadSavedPosts() {
        savedPosts = new ArrayList<>();
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

            username.setText(post.getString("username", "Anonymous"));
            timestamp.setText(post.getString("timestamp", ""));
            title.setText(post.getString("title", ""));
            content.setText(post.getString("content", ""));

            postContainer.addView(postView);
        }
    }
}
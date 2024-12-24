package com.example.brainhub;

import android.app.Activity;
import android.content.Intent;
import android.widget.ImageView;
import androidx.activity.result.ActivityResultLauncher;

public class NavigationHelper {
    private final Activity activity;
    private final ActivityResultLauncher<Intent> launcher;

    public NavigationHelper(Activity activity, ActivityResultLauncher<Intent> launcher) {
        this.activity = activity;
        this.launcher = launcher;
    }

    public void setupNavigation() {
        setupHome();
        setupSaved();
        setupCreate();
        setupNotifications();
        setupProfile();
    }

    private void setupHome() {
        ImageView homeBtn = activity.findViewById(R.id.navHome);
        if (homeBtn != null && !(activity instanceof FeedActivity)) {
            homeBtn.setOnClickListener(v -> {
                activity.startActivity(new Intent(activity, FeedActivity.class));
                activity.finish();
            });
        }
    }

    private void setupSaved() {
        ImageView savedBtn = activity.findViewById(R.id.navSave);
        if (savedBtn != null && !(activity instanceof SavedPostPage)) {
            savedBtn.setOnClickListener(v -> {
                activity.startActivity(new Intent(activity, SavedPostPage.class));
                activity.finish();
            });
        }
    }

    private void setupCreate() {
        ImageView createBtn = activity.findViewById(R.id.navPost);
        if (createBtn != null) {
            createBtn.setOnClickListener(v -> {
                Intent intent = new Intent(activity, CreatePost.class);
                launcher.launch(intent);
            });
        }
    }

    private void setupNotifications() {
        ImageView notifBtn = activity.findViewById(R.id.navNotif);
        if (notifBtn != null && !(activity instanceof Notifications)) {
            notifBtn.setOnClickListener(v -> {
                activity.startActivity(new Intent(activity, Notifications.class));
                activity.finish();
            });
        }
    }

    private void setupProfile() {
        ImageView profBtn = activity.findViewById(R.id.navProf);
        if (profBtn != null && !(activity instanceof Profile)) {
            profBtn.setOnClickListener(v -> {
                Intent intent = new Intent(activity, Profile.class);
                launcher.launch(intent);
            });
        }
    }
}
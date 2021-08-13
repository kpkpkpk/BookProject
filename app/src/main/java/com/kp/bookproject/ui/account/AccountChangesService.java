package com.kp.bookproject.ui.account;

import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;

import com.kp.bookproject.Controller.DatabaseController;

public class AccountChangesService extends JobIntentService {


    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        Thread thread=new Thread(() -> {
            DatabaseController databaseController=new DatabaseController(true);
            databaseController.insertChanges(intent.getStringExtra("nickname"),intent.getStringExtra("filepath"));
        });
        thread.start();
        stopSelf();
    }
    public void onDestroy() {
        super.onDestroy();
        Log.d("ImportRatingService", "Intent service destroyed");
    }
}

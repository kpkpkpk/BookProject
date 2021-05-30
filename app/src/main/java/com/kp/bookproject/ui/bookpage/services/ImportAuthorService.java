package com.kp.bookproject.ui.bookpage.services;

import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;

import com.kp.bookproject.Controller.DatabaseController;

public class ImportAuthorService extends JobIntentService {
    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        Thread thread=new Thread(new Runnable() {
            @Override
            public void run() {
                DatabaseController databaseController=new DatabaseController();
                if(intent.getBooleanExtra("Like",false)){
                        databaseController.setLikedAuthor(intent.getStringExtra("author_name"));
                }else{
                    databaseController.deleteLikedAuthor(intent.getStringExtra("author_name"));
                }
            }
        });
        thread.start();
        stopSelf();
    }
    public void onDestroy() {
        super.onDestroy();
        Log.d("ImportRatingService", "Intent service destroyed");
    }
}

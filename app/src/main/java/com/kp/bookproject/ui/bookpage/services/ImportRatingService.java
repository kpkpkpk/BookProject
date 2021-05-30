package com.kp.bookproject.ui.bookpage.services;

import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;

import com.kp.bookproject.Controller.DatabaseController;

public class ImportRatingService extends JobIntentService {


    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        Thread thread=new Thread(new Runnable() {
            @Override
            public void run() {
                DatabaseController databaseController=new DatabaseController();
                databaseController.setRating(intent.getIntExtra("book_id",-1)
                        ,intent.getIntExtra("book_rating",-1));
                if(intent.getBooleanExtra("Like",false)){
                    databaseController.setLikedBookIntoFirebase(intent.getIntExtra("book_id",-1));
                    Log.d("started","started author");

                }else {
                    databaseController.deleteLikedBookIntoFirebase(intent.getIntExtra("book_id", -1));
                }
            }
        });
        Log.d("ImportRatingService","started");
        Log.d("ImportRatingService",intent.getIntExtra("book_id",-1)+"  "+intent.getIntExtra("book_rating",-1));
        thread.start();
        while (thread.isAlive()){
        }
        stopSelf();
    }
    public void onDestroy() {
        super.onDestroy();
        Log.d("ImportRatingService", "Intent service destroyed");
    }
}


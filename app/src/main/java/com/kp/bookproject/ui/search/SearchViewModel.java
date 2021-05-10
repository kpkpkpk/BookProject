package com.kp.bookproject.ui.search;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.kp.bookproject.Constants;
import com.kp.bookproject.FavouriteTagsActivity;
import com.kp.bookproject.PostgresControllers.DatabaseController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SearchViewModel extends AndroidViewModel {

    private MutableLiveData<ArrayList<String>> tags;

    public SearchViewModel(@NonNull Application application) {
        super(application);
//        ArrayList<String> arrayList=new ArrayList<>();
//            tags = new MutableLiveData<>();
//            SharedPreferences tagsBase=application.getSharedPreferences(Constants.SHARED_PREFERENCES_FAVORITE_TAGS_NAME,
//                    Context.MODE_PRIVATE);
//        for (int i = 0; i < tagsBase.getInt(Constants.SELECTED_TAGS_COUNT,0); i++) {
//            arrayList.add(tagsBase.getString(Constants.SELECTED_TAGS_KEY+i,null));
//        }
//        tags.setValue(arrayList);
        }
    
//    private DatabaseController databaseController=new DatabaseController(true);


    public MutableLiveData<ArrayList<String>> getTags() {
      return tags;
    }


}
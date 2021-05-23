package com.kp.bookproject.ui.search;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;

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
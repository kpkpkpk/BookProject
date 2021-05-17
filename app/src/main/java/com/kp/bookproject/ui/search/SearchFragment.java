package com.kp.bookproject.ui.search;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.flexbox.FlexboxLayout;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.kp.bookproject.PostgresControllers.DatabaseController;
import com.kp.bookproject.R;
import com.kp.bookproject.ui.bookpage.BookActivity;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;
import static com.kp.bookproject.Constants.SELECTED_TAGS_COUNT;
import static com.kp.bookproject.Constants.SELECTED_TAGS_KEY;
import static com.kp.bookproject.Constants.SHARED_PREFERENCES_FAVORITE_TAGS_NAME;

public class SearchFragment extends Fragment {
    private View root;
    private RecyclerView recyclerViewTags;
    private LinearLayout mainLayout;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

         root = inflater.inflate(R.layout.fragment_search, container, false);
        recyclerViewTags=root.findViewById(R.id.fragment_search_recycler_view);
         mainLayout=root.findViewById(R.id.main_layout_fragment_search);
         createRecyclerViewTags();
         return root;
    }
    //выношу работу с recyclerViewTags в отдельную функцию, чтобы не захламлять oncreateview
    public void createRecyclerViewTags(){
        recyclerViewTags.setLayoutManager(new GridLayoutManager(root.getContext(),2));
        ArrayList<String> arrayList=new ArrayList<>();
        recyclerViewTags.addItemDecoration(new DividerItemDecoration(root.getContext(), GridLayoutManager.VERTICAL));

        arrayList.add("jop");
        arrayList.add("jop");
        arrayList.add("jop");
        arrayList.add("jop");
        arrayList.add("jop");
        arrayList.add("jop");
        arrayList.add("jop");
        arrayList.add("jop");
        arrayList.add("jop");
        arrayList.add("jop");
        arrayList.add("jop");
        recyclerViewTags.setAdapter(new SearchFragmentAdapter(arrayList,root.getContext()));
    }

}
package com.kp.bookproject.ui.home;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kp.bookproject.Entity.Book;
import com.kp.bookproject.PostgresControllers.DatabaseController;
import com.kp.bookproject.R;
import com.kp.bookproject.RecyclerViewBooksAdapter;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;
import static com.kp.bookproject.Constants.SELECTED_TAGS_COUNT;
import static com.kp.bookproject.Constants.SELECTED_TAGS_KEY;
import static com.kp.bookproject.Constants.SHARED_PREFERENCES_FAVORITE_TAGS_NAME;

public class HomeFragment extends Fragment {
    private RecyclerViewBooksAdapter recyclerViewBooksAdapter;
    private HomeViewModel homeViewModel;
    private DatabaseController dbController = new DatabaseController(false);
    private View root;
    private LinearLayout mainLayout;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);
        root = inflater.inflate(R.layout.fragment_home, container, false);
        mainLayout = root.findViewById(R.id.main_layout_fragment_home);
        createBooksListView();

        return root;
    }

    private void createBooksListView() {
        SharedPreferences selectedTagsPreference = getActivity().getSharedPreferences(SHARED_PREFERENCES_FAVORITE_TAGS_NAME, MODE_PRIVATE);

//        Создаем необходимое к-во RecyclerView для отображения первых 10 книг сортированных по рейтингу
        for (int i = 0; i < selectedTagsPreference.getInt(SELECTED_TAGS_COUNT, 0); i++) {
//            получаем название тега с SharedPreference
            String tag = selectedTagsPreference.getString(SELECTED_TAGS_KEY + i, "null");
            Log.d("tagos",tag);
            //линейный лейаут нужен чтоб удобно закинуть туда текстовое поле
            LinearLayout recyclerContainer = new LinearLayout(root.getContext());
            recyclerContainer.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            recyclerContainer.setOrientation(LinearLayout.VERTICAL);
            recyclerContainer.setPadding(1, 1, 1, 1);
            //Добавляем текстовое поле
            TextView text = new TextView(root.getContext());
            text.setTextSize(15);
            text.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            //берем текст из образца и подставляем название тега
            text.setText(getResources().getString(R.string.recommendation_string) + tag);
            //докидываем на view

            //Создаем recyclerview, будем закидывать его на view
            RecyclerView booksRecyclerView = new RecyclerView(root.getContext());

            RecyclerView.LayoutParams booksRecyclerViewLayoutParams = new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT
                    , RecyclerView.LayoutParams.WRAP_CONTENT);
//
//
            booksRecyclerView.setLayoutParams(booksRecyclerViewLayoutParams);

            //LayoutManager отвечает за форму отображения элементов, поэтому сделаем обычный горизонтальный список
            LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(root.getContext(), LinearLayoutManager.HORIZONTAL, false);

            booksRecyclerView.setLayoutManager(horizontalLayoutManager);
            booksRecyclerView.addItemDecoration(new DividerItemDecoration(root.getContext(), LinearLayoutManager.HORIZONTAL));
            ArrayList<Book> test = dbController.getTenBooks(tag);


            recyclerViewBooksAdapter = new RecyclerViewBooksAdapter(test, root.getContext());
            booksRecyclerView.setAdapter(recyclerViewBooksAdapter);
            //добавляем наш контейнер на основной лейаут
            recyclerContainer.addView(text);
            recyclerContainer.addView(booksRecyclerView);
            mainLayout.addView(recyclerContainer);


        }
    }
}
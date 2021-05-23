package com.kp.bookproject.ui.search;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.kp.bookproject.Callback;
import com.kp.bookproject.Entity.Book;
import com.kp.bookproject.Controller.DatabaseController;
import com.kp.bookproject.R;
import com.kp.bookproject.ui.RecyclerViewBooksAdapter;
import com.kp.bookproject.ui.bookpage.BookFragment;

import java.util.ArrayList;
import java.util.List;

import static com.kp.bookproject.Constants.FRAGMENT_SEARCH_TAG;
import static com.kp.bookproject.Constants.SELECTED_BOOK;
import static com.kp.bookproject.Constants.SELECTED_TAG_FRAGMENT;

public class SelectedTagFragment extends Fragment {
    private View root;
    private TextView selectedTagText;
    private ChipGroup chipGroupForTags;
    private RecyclerView booksRecyclerView;
    private ArrayList<Book> books;
    private ArrayList<String> tags;
    private ProgressBar progressBar;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
      root=inflater.inflate(R.layout.fragment_selected_tags,container,false);
        selectedTagText=root.findViewById(R.id.selected_tag_text);
        selectedTagText.setText(getArguments().getString("tag"));
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
       Toolbar toolbar=getActivity().findViewById(R.id.tool_bar);
        toolbar.setTitle("");
        chipGroupForTags=root.findViewById(R.id.chip_group_for_tags);
        booksRecyclerView=root.findViewById(R.id.recycler_view_books_with_selected_tag);
        progressBar=root.findViewById(R.id.fragment_selected_tags_progressbar);
        progressBar.setVisibility(View.VISIBLE);
//        selectedTagText.setVisibility(View.GONE);
        booksRecyclerView.setVisibility(View.GONE);
        chipGroupForTags.setVisibility(View.GONE);
      return root;
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                addBooksToRecyclerView(new Callback() {
                    @Override
                    public void onStart() {

                    }

                    @Override
                    public void onComplete(ArrayList<LinearLayout> linearLayouts) {

                    }

                    //после завершения загрузки убираем прогресс бар и закидываем ресайклеры
                    @Override
                    public void onComplete() {
                        try {
                            getActivity().runOnUiThread(new Runnable() {

                                @Override
                                public void run() {
                                    prepareChip();
                                    progressBar.setVisibility(View.GONE);
                                    selectedTagText.setVisibility(View.VISIBLE);
                                    booksRecyclerView.setVisibility(View.VISIBLE);
                                    chipGroupForTags.setVisibility(View.VISIBLE);
                                }
                            });
                        }catch (NullPointerException e){
                            Log.d("selectedtagFragment",e.getMessage());
                        }

                    }
                    //not used
                    @Override
                    public void onComplete(Book book) {

                    }


                });
            }
        });
        thread.start();

        super.onActivityCreated(savedInstanceState);
    }
    public void prepareChip(){
        if(tags.size()!=0){
            for (String obj:
                 tags) {
                Chip chip=new Chip(root.getContext());
                chip.setText(obj);
                      chip.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              FragmentManager fragmentManager=getParentFragmentManager();
              FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
              List<Fragment> existingFragments = fragmentManager.getFragments();
              Log.d("isListExist",(existingFragments != null)+"");
              Fragment currentShownFragment = null;
              //проверяем, есть ли на экране отображаемые фрагменты
              if (existingFragments != null) {
                  for (Fragment fragment : existingFragments) {
                      if (fragment.isVisible()) {
                          currentShownFragment = fragment;
                          break;
                      }
                  }
              }
              StringBuilder stringForFB=new StringBuilder();
              stringForFB.append(getArguments().get("forFirebase")).append("/").append(obj);
              SelectedTagFragment fragment=new SelectedTagFragment();
              Bundle bundle=new Bundle();
              bundle.putString("tag",obj);
              bundle.putString("forFirebase",stringForFB.toString());
              fragment.setArguments(bundle);
              fragmentTransaction.add(R.id.nav_host_fragment,fragment,SELECTED_TAG_FRAGMENT);
              fragmentTransaction.addToBackStack("PREVIOUS");
              fragmentTransaction.hide(currentShownFragment);
              fragmentTransaction.show(fragment).commit();
          }
      });
                chipGroupForTags.addView(chip);
            }
        }
    }

    public void addBooksToRecyclerView(Callback callback){
        DatabaseController controller=new DatabaseController();
        callback.onStart();

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                books=controller.getBooksInSelectedTagsFragment(getArguments().getString("tag"),getArguments().getString("forFirebase"));
                tags=controller.getTags();
            }
        });
        thread.start();

        //дожидаемся загрузки данных
        while(thread.isAlive()){

        }
        Log.d("check","books is null"+books.isEmpty());


       RecyclerViewBooksAdapter recyclerViewBooksAdapter = new RecyclerViewBooksAdapter(books, root.getContext());
        recyclerViewBooksAdapter.setClickListener(new RecyclerViewBooksAdapter.ItemClickListener() {
            @Override
            public void onItemClick(int id) {
//                    Intent i = new Intent(root.getContext(), BookFragment.class);
//                    i.putExtra("id",id);
//                    startActivity(i);
                FragmentManager fragmentManager=getParentFragmentManager();
                FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
                List<Fragment> existingFragments = fragmentManager.getFragments();
                Log.d("isListExist",(existingFragments != null)+"");
                Fragment currentShownFragment = null;
                //проверяем, есть ли на экране отображаемые фрагменты
                if (existingFragments != null) {
                    for (Fragment fragment : existingFragments) {
                        if (fragment.isVisible()) {
                            currentShownFragment = fragment;
                            break;
                        }
                    }
                }
                BookFragment fragment=new BookFragment();
                Bundle bundle=new Bundle();
                bundle.putInt("id",id);
                fragment.setArguments(bundle);
                fragmentTransaction.add(R.id.nav_host_fragment,fragment,SELECTED_BOOK);
                fragmentTransaction.addToBackStack("PREVIOUS");
                fragmentTransaction.hide(currentShownFragment);
                fragmentTransaction.show(fragment).commit();
            }
        });
        try {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    booksRecyclerView.setAdapter(recyclerViewBooksAdapter);
                    booksRecyclerView.setLayoutManager(new GridLayoutManager(root.getContext(), 2));
                    booksRecyclerView.addItemDecoration(new DividerItemDecoration(root.getContext(), GridLayoutManager.VERTICAL));

                }
            });
        }catch (NullPointerException e){
            Log.d("selectedtagFragment",e.getMessage());
        }finally {
            callback.onComplete();
        }


    }


}

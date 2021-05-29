package com.kp.bookproject.ui.account;

import android.annotation.SuppressLint;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kp.bookproject.Callback;
import com.kp.bookproject.Controller.DatabaseController;
import com.kp.bookproject.Entity.Book;
import com.kp.bookproject.R;
import com.kp.bookproject.ui.bookpage.BookFragment;
import com.kp.bookproject.ui.recyclerViewAdapters.VerticalBookRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.List;

import static com.kp.bookproject.Constants.SELECTED_BOOK;

public class ShowLikedBooksFragment extends Fragment {
    private View root;
    private RecyclerView likedRecyclerView;
    private ProgressBar progressBar;
    private ArrayList<Book> books;

    @SuppressLint("SetTextI18n")
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_liked_books, container, false);
        likedRecyclerView=root.findViewById(R.id.fragment_liked_books_recycler_view);
        Toolbar toolbar=getActivity().findViewById(R.id.tool_bar);
        toolbar.setTitle("Понравившиеся произведения");
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        likedRecyclerView.setVisibility(View.GONE);
        progressBar=root.findViewById(R.id.fragment_liked_books_progress_bar);
        progressBar.setVisibility(View.VISIBLE);
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
                                   if(books.isEmpty()){
                                       TextView text = root.findViewById(R.id.fragment_liked_books_text);
                                       progressBar.setVisibility(View.GONE);
                                       text.setVisibility(View.VISIBLE);
                                   }else{
                                       progressBar.setVisibility(View.GONE);
                                       likedRecyclerView.setVisibility(View.VISIBLE);
                                   }
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
    public void addBooksToRecyclerView(Callback callback) {
        DatabaseController controller = new DatabaseController();
        callback.onStart();

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                books = controller.getLikedBooks();
            }
        });
        thread.start();

        //дожидаемся загрузки данных
        while (thread.isAlive()) {

        }
        Log.d("check", "books is null" + books.isEmpty());

        if (books.isEmpty()) {
           callback.onComplete();
        } else{
            VerticalBookRecyclerViewAdapter recyclerViewBooksAdapter = new VerticalBookRecyclerViewAdapter(books, root.getContext());
        recyclerViewBooksAdapter.setClickListener(new VerticalBookRecyclerViewAdapter.ItemClickListener() {
            @Override
            public void onItemClick(int id) {
//                    Intent i = new Intent(root.getContext(), BookFragment.class);
//                    i.putExtra("id",id);
//                    startActivity(i);
                FragmentManager fragmentManager = getParentFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                List<Fragment> existingFragments = fragmentManager.getFragments();
                Log.d("isListExist", (existingFragments != null) + "");
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
                BookFragment fragment = new BookFragment();
                Bundle bundle = new Bundle();
                bundle.putInt("id", id);
                fragment.setArguments(bundle);
                fragmentTransaction.add(R.id.nav_host_fragment, fragment, SELECTED_BOOK);
                fragmentTransaction.addToBackStack("PREVIOUS");
                fragmentTransaction.hide(currentShownFragment);
                fragmentTransaction.show(fragment).commit();
            }
        });
        try {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    likedRecyclerView.setAdapter(recyclerViewBooksAdapter);
                    likedRecyclerView.setLayoutManager(new LinearLayoutManager(root.getContext(), LinearLayoutManager.VERTICAL, false));
                    likedRecyclerView.addItemDecoration(new DividerItemDecoration(root.getContext(), LinearLayout.VERTICAL));

                }
            });
        } catch (NullPointerException e) {
            Log.d("selectedtagFragment", e.getMessage());
        } finally {
            callback.onComplete();
        }
    }
    }
}

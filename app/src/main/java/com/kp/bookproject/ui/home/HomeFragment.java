   package com.kp.bookproject.ui.home;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kp.bookproject.Callback;
import com.kp.bookproject.Entity.Book;
import com.kp.bookproject.Controller.DatabaseController;
import com.kp.bookproject.Entity.News;
import com.kp.bookproject.Entity.NewsApiAnswer;
import com.kp.bookproject.FavouriteTagsActivity;
import com.kp.bookproject.LoginActivity;
import com.kp.bookproject.MainActivity;
import com.kp.bookproject.R;
import com.kp.bookproject.ui.RecyclerViewBooksAdapter;
import com.kp.bookproject.ui.bookpage.BookFragment;
import com.kp.bookproject.ui.search.SelectedTagFragment;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.content.Context.MODE_PRIVATE;
import static com.kp.bookproject.Constants.FRAGMENT_HOME_TAG;
import static com.kp.bookproject.Constants.SELECTED_BOOK;
import static com.kp.bookproject.Constants.SELECTED_TAGS_COUNT;
import static com.kp.bookproject.Constants.SELECTED_TAGS_KEY;

import static com.kp.bookproject.Constants.SHARED_PREFERENCES_FAVORITE_TAGS_NAME;

public class HomeFragment extends Fragment {
    private RecyclerViewBooksAdapter recyclerViewBooksAdapter;
    private String name;
    private View root;
    private LinearLayout secondL;
    private RelativeLayout mainLayout;
    private TextView text,lastNews;
    private ProgressBar progressBar;
    private RecyclerView recyclerViewNews;
    private final String BASE_URL="https://newsapi.org/";
    private final String API_KEY="3b2651d3eb8445068640c622f7138762";
    @SuppressLint("SetTextI18n")
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_home, container, false);
        recyclerViewNews=root.findViewById(R.id.fragment_home_recycler_view_news);
        recyclerViewNews.setLayoutManager(new LinearLayoutManager(root.getContext(),RecyclerView.HORIZONTAL,false));
        recyclerViewNews.addItemDecoration(new DividerItemDecoration(root.getContext(), LinearLayoutManager.HORIZONTAL));

        mainLayout = root.findViewById(R.id.main_layout_fragment_home);
        secondL=root.findViewById(R.id.container_layout);
        progressBar=root.findViewById(R.id.progressBar);
        lastNews=root.findViewById(R.id.fragment_home_text_news);
        text =root.findViewById(R.id.text_home);

       //подготавливаем вью, делаем прогресс бар видимым
        recyclerViewNews.setVisibility(View.GONE);
        secondL.setVisibility(View.GONE);
        text.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        lastNews.setVisibility(View.GONE);
        return root;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {

            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    getNews();
                    createBooksListView(new Callback() {
                        @Override
                        public void onStart() {

                        }

                        //после завершения загрузки убираем прогресс бар и закидываем ресайклеры
                        @Override
                        public void onComplete(ArrayList<LinearLayout> linearLayouts) {

                            getActivity().runOnUiThread(new Runnable() {

                                @Override
                                public void run() {
                                    secondL.removeAllViews();
                                    for (LinearLayout l :
                                            linearLayouts) {

                                        secondL.addView(l);
                                    }

                                    progressBar.setVisibility(View.GONE);
                                    recyclerViewNews.setVisibility(View.VISIBLE);
                                    secondL.setVisibility(View.VISIBLE);
                                    lastNews.setVisibility(View.VISIBLE);
                                    text.setVisibility(View.VISIBLE);
                                }
                            });

                        }

                        //not used
                        @Override
                        public void onComplete(Book book) {

                        }

                        @Override
                        public void onComplete() {

                        }


                    });
                }
            });
            thread.start();

            super.onActivityCreated(savedInstanceState);
        }


    ArrayList<Book> books;

    @SuppressLint("SetTextI18n")
    private void createBooksListView(Callback callback) {
        callback.onStart();
        DatabaseController controller = new DatabaseController();
        ArrayList<LinearLayout> layoutL = new ArrayList<>();
        ArrayList<Integer> arrayList = controller.getFavoriteTags();
        if(arrayList.isEmpty()){
          getActivity().runOnUiThread(new Runnable() {
              @Override
              public void run() {
                  Toast.makeText(root.getContext(), getResources().getString(R.string.home_fragment_none_selected_tags_hint), Toast.LENGTH_SHORT).show();
                  startActivity(new Intent(getActivity(), FavouriteTagsActivity.class));
                  getActivity().finish();
              }
          });
        }else {
//        Создаем необходимое к-во RecyclerView для отображения первых 10 книг сортированных по рейтингу
            for (Integer i : arrayList) {
                StringBuilder tag = new StringBuilder();
//            получаем название тега с SharedPreference
                Thread t = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        tag.append(controller.getTag(i));
                    }
                });
                t.start();
                while (t.isAlive()) {

                }

                //линейный лейаут нужен чтоб удобно закинуть туда текстовое поле
                LinearLayout recyclerContainer = new LinearLayout(root.getContext());
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                layoutParams.setMargins(5, 10, 5, 0);
                recyclerContainer.setLayoutParams(layoutParams);
                recyclerContainer.setOrientation(LinearLayout.VERTICAL);
                recyclerContainer.setPadding(1, 20, 1, 1);
                View straight = new View(getContext());
                straight.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1));
                straight.setPadding(2, 0, 2, 3);
                straight.setBackgroundColor(Color.parseColor("#C0C0C0"));
                //Добавляем текстовое поле
                TextView text = new TextView(root.getContext());
                text.setTextSize(15);
                LinearLayout.LayoutParams textPar = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                textPar.setMargins(5, 1, 0, 2);
                text.setLayoutParams(textPar);
                //берем текст из образца и подставляем название тега
                text.setText(getResources().getString(R.string.recommendation_string) + " " + tag);

                //Создаем recyclerview, будем закидывать его на view
                RecyclerView booksRecyclerView = new RecyclerView(root.getContext());

                RecyclerView.LayoutParams booksRecyclerViewLayoutParams = new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT
                        , RecyclerView.LayoutParams.WRAP_CONTENT);

                booksRecyclerView.setLayoutParams(booksRecyclerViewLayoutParams);
                //LayoutManager отвечает за форму отображения элементов, поэтому сделаем обычный горизонтальный список
                LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(root.getContext(), LinearLayoutManager.HORIZONTAL, false);
                booksRecyclerView.setLayoutManager(horizontalLayoutManager);
                booksRecyclerView.addItemDecoration(new DividerItemDecoration(root.getContext(), LinearLayoutManager.HORIZONTAL));
                Log.d("checkn", tag.toString());

                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {


                        books = controller.getTenBooks(tag.toString());
                    }
                });
                thread.start();

                //дожидаемся загрузки данных
                while (thread.isAlive()) {

                }
                Log.d("check", "books is null" + books.isEmpty());


                recyclerViewBooksAdapter = new RecyclerViewBooksAdapter(books, root.getContext());
                recyclerViewBooksAdapter.setClickListener(new RecyclerViewBooksAdapter.ItemClickListener() {
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
                booksRecyclerView.setAdapter(recyclerViewBooksAdapter);
                //добавляем наш контейнер на основной лейаут
                recyclerContainer.addView(straight);
                recyclerContainer.addView(text);
                recyclerContainer.addView(booksRecyclerView);
                layoutL.add(recyclerContainer);
                callback.onComplete(layoutL);
            }
        }
    }
    public void getNews(){
        Retrofit retrofit= new Retrofit.Builder()
                                    .baseUrl(BASE_URL)
                                    .addConverterFactory(GsonConverterFactory.create())
                                    .build();
        NewsApiService apiService=retrofit.create(NewsApiService.class);
        Call<NewsApiAnswer> call=apiService.getNews(API_KEY,"technology");
        call.enqueue(new retrofit2.Callback<NewsApiAnswer>() {
            @Override
            public void onResponse(Call<NewsApiAnswer> call, Response<NewsApiAnswer> response) {
                assert response.body() != null;
                ArrayList<News> apiAnswers = new ArrayList<>(response.body().getSources());
                NewsRecyclerViewAdapter recyclerViewAdapter=new NewsRecyclerViewAdapter(apiAnswers,getContext());
                recyclerViewAdapter.setClickListener(new NewsRecyclerViewAdapter.ItemClickListener() {
                    @Override
                    public void onItemClick(String url) {
                        Intent intent=new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(url));
                        startActivity(intent);
                    }
                });
                recyclerViewNews.setAdapter(recyclerViewAdapter);
            }

            @Override
            public void onFailure(Call<NewsApiAnswer> call, Throwable t) {

            }
        });

    }

}

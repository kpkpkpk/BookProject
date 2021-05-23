package com.kp.bookproject.ui.bookpage;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.JobIntentService;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.kp.bookproject.Callback;
import com.kp.bookproject.Entity.Book;
import com.kp.bookproject.Controller.DatabaseController;
import com.kp.bookproject.R;

import java.util.ArrayList;

import static com.kp.bookproject.Constants.JOB_ID;

public class BookFragment extends Fragment {
    private TextView bookTitle,authorName,description,rating,genre,descriptionTitle;
    private ImageView bookImage;
    private ProgressBar progressBar;
    private FloatingActionButton likeButton;
    private View firstLine,thirdLine;
    //используется для изменения лайка
    private boolean isChanged=false;
    private View root;
    private Book book;
    private int id=-1;
    private Toolbar toolbar;
    @SuppressLint("ClickableViewAccessibility")

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
      root=inflater.inflate(R.layout.fragment_book,container,false);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
      id=getArguments().getInt("id");
        bookImage=root.findViewById(R.id.activity_book_image);
        firstLine=root.findViewById(R.id.first_view);
        thirdLine=root.findViewById(R.id.third_view);
        toolbar=getActivity().findViewById(R.id.tool_bar);
        toolbar.setTitle("Книга");
        descriptionTitle=root.findViewById(R.id.activity_book_description_title);
        genre=root.findViewById(R.id.activity_book_genre);
        bookTitle=root.findViewById(R.id.activity_book_title);
        progressBar=root.findViewById(R.id.activity_book_progressbar);
        authorName=root.findViewById(R.id.activity_book_author_name);
        rating=root.findViewById(R.id.activity_book_rating);
        description=root.findViewById(R.id.activity_book_description);
        likeButton=root.findViewById(R.id.activity_book_like_button);

        //кликер для кнопки
        likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isChanged) {
                    likeButton.setImageDrawable(ContextCompat.getDrawable(v.getContext(), R.drawable.like_button));
                    isChanged=true;
                    book.setRating(book.getRating()+1);
                    rating.setText(Integer.toString(book.getRating()));
                    Intent i=new Intent(root.getContext(),ImportRatingService.class);
                    i.putExtra("Like",true);
                    i.putExtra("book_id",id);
                    i.putExtra("book_rating",book.getRating());
                    JobIntentService.enqueueWork(root.getContext(),ImportRatingService.class,JOB_ID,i);
                    Intent secI=new Intent(root.getContext(),ImportAuthorService.class);
                    secI.putExtra("author_name",book.getAuthorName());
                    secI.putExtra("Like",true);
                    JobIntentService.enqueueWork(root.getContext(),ImportAuthorService.class,JOB_ID+1,secI);
                } else{
                    likeButton.setImageDrawable(ContextCompat.getDrawable(v.getContext(), R.drawable.unlike_button));
                    isChanged=false;
                    book.setRating(book.getRating()-1);
                    rating.setText(Integer.toString(book.getRating()));
                    Intent i=new Intent(root.getContext(),ImportRatingService.class);
                    i.putExtra("Like",false);
                    i.putExtra("book_id",id);
                    i.putExtra("book_rating",book.getRating());
                    i.putExtra("author_name",book.getAuthorName());

                    JobIntentService.enqueueWork(root.getContext(),ImportRatingService.class,JOB_ID,i);
                    Intent secI=new Intent(root.getContext(),ImportAuthorService.class);
                    secI.putExtra("author_name",book.getAuthorName());
                    secI.putExtra("Like",false);
                    JobIntentService.enqueueWork(root.getContext(),ImportAuthorService.class,JOB_ID+1,secI);

                }
            }
        });
        //кликер для описания
        description.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                description.setVisibility(View.GONE);
                description.setMaxLines(Integer.MAX_VALUE);
                description.setVisibility(View.VISIBLE);
            }
        });
        //Устанавливаем все в невидимку, чтоб был чистый прогрессбар
        setVisibilityForElements(false);
        return root;
    }



    @Override
    public void onStart() {
//        int id=getIntent().getIntExtra("id",-1);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                getBook(new Callback() {
                    @Override
                    public void onStart() {

                    }
                    //после завершения загрузки убираем прогресс бар и закидываем ресайклеры
                    @Override
                    public void onComplete(Book receivedBook) {
                        BookFragment.this.getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Glide.with(root.getContext()).load(receivedBook.getImage_url()).into(bookImage);
                                bookTitle.setText(receivedBook.getBook_name());
                                authorName.setText(receivedBook.getAuthorName());
                                rating.setText(Integer.toString(receivedBook.getRating()));
                                description.setText(receivedBook.getDescription());
                                genre.setText(receivedBook.getTag());
                                if(isChanged){
                                    likeButton.setImageDrawable(ContextCompat.getDrawable(root.getContext(), R.drawable.baseline_favorite_black_24));
                                }else{
                                    likeButton.setImageDrawable(ContextCompat.getDrawable(root.getContext(), R.drawable.baseline_favorite_border_black_24));
                                }
                                setVisibilityForElements(true);
                            }
                        });

                    }

                    @Override
                    public void onComplete() {

                    }

                    //not used
                    @Override
                    public void onComplete(ArrayList<LinearLayout> linearLayouts) {
                    }
                },id);
            }
        });
        thread.start();
        super.onStart();
    }
    private void getBook(Callback callback,int id){
        callback.onStart();
        Thread thread=new Thread(new Runnable() {
            @Override
            public void run() {
                DatabaseController databaseController=new DatabaseController();
//                databaseController.fillCheckBook(id);
                book=databaseController.getBook(id);
                isChanged=databaseController.isCheck();
                Log.d("isAll",isChanged+"");
            }
        });
        thread.start();
        //дожидаемся загрузки данных
        while(thread.isAlive()){

        }

        Log.d("isChanged,",isChanged+"");

        callback.onComplete(book);
    }
    //чтобы не засирать onCreate
    public void setVisibilityForElements(boolean done){
        if(!done){
            bookImage.setVisibility(View.GONE);
            firstLine.setVisibility(View.GONE);

            thirdLine.setVisibility(View.GONE);
            bookTitle.setVisibility(View.GONE);
            authorName.setVisibility(View.GONE);
            rating.setVisibility(View.GONE);
            genre.setVisibility(View.GONE);
            description.setVisibility(View.GONE);
            likeButton.setVisibility(View.GONE);
            descriptionTitle.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
        }else{
            progressBar.setVisibility(View.GONE);
            bookImage.setVisibility(View.VISIBLE);
            firstLine.setVisibility(View.VISIBLE);

            thirdLine.setVisibility(View.VISIBLE);
            bookTitle.setVisibility(View.VISIBLE);
            authorName.setVisibility(View.VISIBLE);
            rating.setVisibility(View.VISIBLE);
            genre.setVisibility(View.VISIBLE);
            descriptionTitle.setVisibility(View.VISIBLE);
            description.setVisibility(View.VISIBLE);
            likeButton.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        menu.clear();
        super.onCreateOptionsMenu(menu, inflater);
    }


}
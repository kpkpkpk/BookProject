package com.kp.bookproject.PostgresControllers;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.kp.bookproject.Entity.Book;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;

public class DatabaseController {
    //create connection
    private DatabaseConnection databaseConnection;
    private Connection connection;
    //statement
    private Statement statement;
    //resultset is cursor that used for statement elements
    private ResultSet resultSet;
    //firebase
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    //если требуется работа с несколькими базами сразу


    public DatabaseController() {
        firebaseAuth = FirebaseAuth.getInstance();
        databaseConnection = new DatabaseConnection();


    }

    //если требуется работа с одной из баз
    public DatabaseController(boolean isFirebase) {
        if (isFirebase) {
            firebaseAuth = FirebaseAuth.getInstance();
        } else {
            databaseConnection = new DatabaseConnection();
            connection = databaseConnection.returnConnection();
        }
    }

    //change to arraylist and query
    public void getAuthors() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    //connection забирает слишком много, его кидать в отдельный поток
                    connection = databaseConnection.returnConnection();
                    statement = connection.createStatement();
                    String sql = "SELECT * FROM Authors";
                    resultSet = statement.executeQuery(sql);
                    while (resultSet.next()) {
                        Log.d("DB", resultSet.getInt(1) + " " + resultSet.getString(2));

                    }
                } catch (Exception err) {
                    err.printStackTrace();
                }
            }
        });
        thread.start();
        try {
            thread.join();
        } catch (Exception err) {
            Log.d("DatabaseController", " getAuthors()Error:" + err.getMessage());
        }
    }

    ArrayList<Integer> tagsId;

    public void setFavouriteTagsIntoAccountFirebase(ArrayList<String> selectedTags) {
        tagsId = new ArrayList<>();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    connection = databaseConnection.returnConnection();
                    statement = connection.createStatement();
                    //получаем лист тегов, по которому будем проходиться и собирать айди
                    for (int i = 0; i < selectedTags.size(); i++) {
                        //создаем запрос
                        StringBuilder sql = new StringBuilder("SELECT id FROM tags where nametag like '%");
                        //добавляем название тега
                        sql.append(selectedTags.get(i)).append("%'");
                        Log.d("setF", sql.toString() + "");
                        resultSet = statement.executeQuery(sql.toString());
                        while (resultSet.next()) {
                            //заносим в tagsID и далее передадим в акк фаербейс
                            tagsId.add(resultSet.getInt(1));
                            Log.d("setF", resultSet.getInt(1) + "");
                        }
                    }

                } catch (Exception err) {
                    err.printStackTrace();
                    Log.d("setF", err.getMessage());
                }

                //продолжаем работу с фаербейзом
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                String userId = null;
                if (firebaseUser != null) {
                    userId = firebaseUser.getUid();
                    databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userId);
                    HashMap<String, Integer> likedMap = new HashMap<>();
                    //решил сразу добавить лайкнутых авторов, чтоб не было проблемс с этим
                    //сначала айдишник в кач-ве ключа, а второе значение - к-во лайков
                    likedMap.put(Integer.toString(9999), 0);

//                    databaseReference.child("liked_authors").setValue(likedMap);

                    databaseReference.child("tags_id").setValue(tagsId);
                    databaseReference.child("liked_authors").setValue(likedMap);
                } else {
                    Log.d("setF", "user is null");
                }
            }
        });
        thread.start();
        try {
            thread.join();
        } catch (Exception err) {
            Log.d("DatabaseController", "getBooks" + " Error:" + err.getMessage());
        }

    }

    ArrayList<Integer> favoriteTags;

    public int getTag(int id) {
        favoriteTags = new ArrayList<>();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                    String userId = null;

                    if (firebaseUser != null) {
                        userId = firebaseUser.getUid();
                        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userId).child("tags_id").child(id+"");
                        databaseReference.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DataSnapshot> task) {
                                if (task.isSuccessful()) {
                                    GenericTypeIndicator<Integer> arrayListGenericTypeIndicator =
                                            new GenericTypeIndicator<Integer>() {
                                            };
                                    try {

                                    } catch (NullPointerException ex) {
                                        Log.d("zxccl", "null err");
                                    }
                                    Log.d("setF", favoriteTags.toString() + "");
                                }
                            }
                        });
                    } else {
                        Log.d("zxccl", "user is null");

                    }
                } catch (Exception err) {
                    err.printStackTrace();

                }

            }


        });
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return id;
//        return favoriteTags;
    }
    CountDownLatch countDownLatch=new CountDownLatch(1);
    ArrayList<Book> books;
    public void fillTenBooks(String tagName) {
        books = new ArrayList<>();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    connection = databaseConnection.returnConnection();
                    statement = connection.createStatement();
//                    getTag(id);
//                    timer.await();
                    //получаем лист тегов, по которому будем проходиться и собирать айди
//                    Log.d("setFGet", favoriteTags.toString());

                        StringBuilder sql = new StringBuilder("select books.id,book_name,authors.author_name,book_image from books,authors where (select id from tags where tags.nametag like '%");
                        //добавляем название тега
                        sql.append(tagName).append("%')=any(tags_id) and authors.id=author_id order by rating,random() asc limit 10");
                        Log.d("setF", sql.toString() + "");
                        resultSet = statement.executeQuery(sql.toString());
                        while (resultSet.next()) {
                            Book book = new Book(resultSet.getInt("id")
                                    , resultSet.getString("book_name")
                                    , resultSet.getString("author_name")
                                    , resultSet.getString("book_image")
                            );
                            Log.d("getBooksLog", book.toString());
                            books.add(book);
                        }
                        countDownLatch.countDown();
                } catch (Exception err) {
                    err.printStackTrace();
                    Log.d("getBooksLog", err.getMessage());
                }
            }

        });
        thread.start();
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    public ArrayList<Book> getTenBooks(String tagName){
       fillTenBooks(tagName);
       if(books.isEmpty()){
           CountDownTimer timer=new CountDownTimer(8000,1000) {
               @Override
               public void onTick(long millisUntilFinished) {

               }

               @Override
               public void onFinish() {

               }
           };
           timer.start();
       }
        return books;
    }
}

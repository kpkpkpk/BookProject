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
import com.kp.bookproject.Callback;
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
                    databaseReference.child("tags_id").setValue(tagsId);

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


    public String getUsername() {
        StringBuilder name = new StringBuilder();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        String userId = null;
        if (firebaseUser != null) {
            userId = firebaseUser.getUid();
            databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userId).child("username");
            databaseReference.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    if (task.isSuccessful()) {
                        GenericTypeIndicator<String> arrayListGenericTypeIndicator =
                                new GenericTypeIndicator<String>() {
                                };
                        try {
                            name.append(task.getResult().getValue(arrayListGenericTypeIndicator));
                            Log.d("get_usr", name + "");
                        } catch (NullPointerException ex) {
                            Log.d("zxccl", "null err");
                        }

                    }
                }
            });
        } else {
            Log.d("zxccl", "user is null");

        }

        return name.toString();

    }


    /**
     * сделал не потоком потому что требовалось управлять в другом методе
     * {@see #com.kp.bookproject.ui.home.HomeFragment}
     **/
    public ArrayList<Book> getTenBooks(String tagName) {
        ArrayList<Book> books = new ArrayList<>();
        try {
            connection = databaseConnection.returnConnection();
            statement = connection.createStatement();

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

        } catch (Exception err) {
            Log.d("getBooksLog", err.getMessage());
        }


        return books;
    }

    Book book;
boolean isall=false;
    public Book getBook(int id) {
        fillCheckBook(id);
        Log.d("all","ISALL"+isall);
        while (!isall){
            Log.d("all","ISALL"+isall);
        }

        try {
            connection = databaseConnection.returnConnection();
            statement = connection.createStatement();
            StringBuilder sql = new StringBuilder("select tags.nametag,book_name,description,authors.author_name,book_image,rating from books,authors,tags where books.id=");

            sql.append(id).append(" and tags.id=tags_id[array_length(books.tags_id,1)] and authors.id=author_id");
            Log.d("setF", sql.toString() + "");
            resultSet = statement.executeQuery(sql.toString());
            while (resultSet.next()) {
                book = new Book(resultSet.getString("nametag")
                        , resultSet.getString("book_name")
                        , resultSet.getString("author_name")
                        , resultSet.getString("description")
                        , resultSet.getString("book_image")
                        , resultSet.getInt("rating")
                );
                Log.d("getBooksLog", book.toString());
            }

        } catch (Exception err) {
            Log.d("getBooksLog", err.getMessage());
        }


        return book;
    }

    public void setRating(int book_id, int rating) {
        try {
            connection = databaseConnection.returnConnection();
            statement = connection.createStatement();
            StringBuilder sql = new StringBuilder("update books set rating=");
            sql.append(rating).append(" where id=").append(book_id);
            statement.executeQuery(sql.toString());
        } catch (Exception err) {
            Log.d("setRating", err.getMessage());
        }
    }
    public void deleteLikedBookIntoFirebase(int id) {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        String userId = null;
        if (firebaseUser != null) {
            userId = firebaseUser.getUid();
            databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userId).child("liked_books");
            HashMap<String, Boolean> map = new HashMap<>();
            //решил сразу добавить лайкнутых авторов, чтоб не было проблемс с этим
            //сначала айдишник в кач-ве ключа, а второе значение - к-во лайков

            databaseReference.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    if (task.isSuccessful()) {
                        GenericTypeIndicator<HashMap<String,Boolean>> arrayListGenericTypeIndicator =
                                new GenericTypeIndicator<HashMap<String,Boolean>>() {
                                };
                        try {
                            map.putAll(Objects.requireNonNull(task.getResult().getValue(arrayListGenericTypeIndicator)));
                            Log.d("map", "onComplete: "+map.toString());

                        } catch (NullPointerException ex) {
                            Log.d("deletes", "null err");
                        }

                    }
                    map.remove(Integer.toString(id));
                    databaseReference.setValue(map);
                }
            });

        } else {
            Log.d("likedbooksfirebase", "err");
        }
    }

    public void setLikedBookIntoFirebase(int id) {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        String userId = null;
        HashMap<String, Boolean> map = new HashMap<>();
        if (firebaseUser != null) {
            userId = firebaseUser.getUid();
            databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userId).child("liked_books");

            databaseReference.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    if (task.isSuccessful()) {
                        GenericTypeIndicator<HashMap<String,Boolean>> arrayListGenericTypeIndicator =
                                new GenericTypeIndicator<HashMap<String,Boolean>>() {
                                };
                        try {
                           map.putAll(task.getResult().getValue(arrayListGenericTypeIndicator));

                           Log.d("mapCh",map.toString());
                        } catch (NullPointerException ex) {
                            Log.d("setLikedBooks", "null err");
                            map.put(Integer.toString(id), true);
                            databaseReference.setValue(map);

                        }

                    }
                    map.put(Integer.toString(id), true);
                    databaseReference.setValue(map);
                    databaseReference=null;
                }
            });

            Log.d("mapch",map.toString());

        } else {
            Log.d("likedbooksfirebase", "err");
        }
    }

boolean check=false;
    public void fillCheckBook(int id){
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String userId = null;
        if (firebaseUser != null) {
            userId = firebaseUser.getUid();
            databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userId).child("liked_books").child(Integer.toString(id));

            databaseReference.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    if (task.isSuccessful()) {
                        GenericTypeIndicator<Boolean> arrayListGenericTypeIndicator =
                                new GenericTypeIndicator<Boolean>() {
                                };
                        try {
                           check=task.getResult().getValue(arrayListGenericTypeIndicator);

                               isall=true;

                           Log.d("likedbooksfirebase",check+"");
                        } catch (NullPointerException ex) {
                            isall=true;
                            check=false;
                            Log.d("fillCheckBook", "null err"+check);
                        }

                    }
                }
            });

        } else {
            Log.d("likedbooksfirebase", "err");

        }

    }

    public boolean isCheck() {
        return check;
    }
    int author_id=-1;
    public void setLikedAuthor(String author_name) {
        try {
            connection = databaseConnection.returnConnection();
            statement = connection.createStatement();
            StringBuilder sql = new StringBuilder("select id from authors where author_name like '%");
            sql.append(author_name).append("%'");
            Log.d("setF", sql.toString() + "");
            resultSet = statement.executeQuery(sql.toString());
            Log.d("res",resultSet.toString());
            while (resultSet.next()) {
                author_id = resultSet.getInt("id");
                Log.d("getBooksLog", resultSet.getInt("id") + "");
            }

        } catch (Exception err) {
            Log.d("getBooksLog", err.getMessage());
        }
        Log.d("getBookLog",author_id+"");
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String userId = null;
        HashMap<String, Integer> map = new HashMap<>();
        if (firebaseUser != null) {
            userId = firebaseUser.getUid();
            databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userId).child("liked_authors");
            databaseReference.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    if (task.isSuccessful()) {
                        GenericTypeIndicator<HashMap<String, Integer>> arrayListGenericTypeIndicator =
                                new GenericTypeIndicator<HashMap<String, Integer>>() {
                                };
                        try {
                            map.putAll(task.getResult().getValue(arrayListGenericTypeIndicator));

                            Log.d("mapCh", map.toString());
                        } catch (NullPointerException ex) {
                            Log.d("setLiked", "null err");
                            map.put(Integer.toString(author_id), 1);
                            databaseReference.setValue(map);
                        }
                        if(map.containsKey(author_id)){
                            map.replace(Integer.toString(author_id),map.get(author_id)+1);
                            databaseReference.setValue(map);
                        }else{
                            map.put(Integer.toString(author_id), 1);
                            databaseReference.setValue(map);
                        }
                    }
                    Log.d("m", map.toString());

                }
            });
        }
    }


    public void deleteLikedAuthor(String author_name) {
        try {
            connection = databaseConnection.returnConnection();
            statement = connection.createStatement();
            StringBuilder sql = new StringBuilder("select id from authors where author_name like '%");
            sql.append(author_name).append("%'");
            Log.d("setF", sql.toString() + "");
            resultSet = statement.executeQuery(sql.toString());
            Log.d("res",resultSet.toString());
            while (resultSet.next()) {
                author_id = resultSet.getInt("id");
                Log.d("getBooksLog", resultSet.getInt("id") + "");
            }

        } catch (Exception err) {
            Log.d("getBooksLog", err.getMessage());
        }
        Log.d("getBookLog",author_id+"");
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String userId = null;
        HashMap<String, Integer> map = new HashMap<>();
        if (firebaseUser != null) {
            userId = firebaseUser.getUid();
            databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userId).child("liked_authors");
            databaseReference.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    if (task.isSuccessful()) {
                        GenericTypeIndicator<HashMap<String, Integer>> arrayListGenericTypeIndicator =
                                new GenericTypeIndicator<HashMap<String, Integer>>() {
                                };
                        try {
                            map.putAll(task.getResult().getValue(arrayListGenericTypeIndicator));

                            Log.d("mapCh", map.toString());
                        } catch (NullPointerException ex) {
                            Log.d("setLiked", "null err");

                        }
                       map.replace(Integer.toString(author_id),map.get(Integer.toString(author_id))-1);
                        Log.d("seekmap",map.toString());
                        if(map.get(Integer.toString(author_id)).equals(0)){
                            map.remove(Integer.toString(author_id));
                            Log.d("seekmapinif",map.toString());
                        }
                        databaseReference.setValue(map);
                        Log.d("seekmap",map.toString());
                    }
                    Log.d("m", map.toString());

                }
            });
        }
    }
}


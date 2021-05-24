package com.kp.bookproject.Controller;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.kp.bookproject.Callback;
import com.kp.bookproject.Entity.Account;
import com.kp.bookproject.Entity.Book;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

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

    boolean isall=false;

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
    private ArrayList<String> tags;

    public ArrayList<String> getTags() {
        return tags;
    }

    public void fillTagsFromFirebase(String tagName){
        tags=new ArrayList<>();
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if (firebaseUser != null) {

            Log.d("ISEXISTSTAGS",tagName);
            databaseReference = FirebaseDatabase.getInstance().getReference("Alltags/"+tagName);

            databaseReference.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    if (task.isSuccessful()) {
                       if(task.getResult().exists()){
                           try {
                               for (DataSnapshot d:
                                       task.getResult().getChildren()) {
                                   tags.add(d.getKey());
                               }

                               isall=true;
                               Log.d("likedbooksfirebase",check+"");
                           } catch (NullPointerException ex) {


                               Log.d("fillCheckBook", "null err"+check);
                           }

                       }else{
                           isall=true;
                       }
                        Log.d("ISEXISTSTAGS","empty?"+tags.isEmpty());
                    }
                }
            });

        } else {
            Log.d("likedbooksfirebase", "err");

        }
    }

    public ArrayList<Book> getBooksInSelectedTagsFragment(String tagName,String forFirebase) {
        ArrayList<Book> books = new ArrayList<>();
            fillTagsFromFirebase(forFirebase);
        while (!isall){
            Log.d("all","ISALL"+isall);
        }
        isall=false;
        try {
            connection = databaseConnection.returnConnection();
            statement = connection.createStatement();

            StringBuilder sql = new StringBuilder("select books.id,book_name,authors.author_name,book_image from books,authors where (select id from tags where tags.nametag like '%");
            //добавляем название тега
            sql.append(tagName).append("%')=any(tags_id) and authors.id=author_id order by rating desc limit 20");
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

private ArrayList<Integer> favoriteTags;
    public void getTagsId(){
        FirebaseUser firebaseUser=firebaseAuth.getCurrentUser();
        String userId=null;
        if (firebaseUser != null) {
            userId = firebaseUser.getUid();
            databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userId).child("tags_id");
            databaseReference.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    if (task.isSuccessful()) {
                        GenericTypeIndicator<ArrayList<Integer>> arrayListGenericTypeIndicator =
                                new GenericTypeIndicator<ArrayList<Integer>>() {
                                };

                        favoriteTags=task.getResult().getValue(arrayListGenericTypeIndicator);
                        Log.d("BOOKS",favoriteTags.toString());
                        isall=true;
                    }


                }
            });
        }
    }
    public String getTag(int id){
        StringBuilder str=new StringBuilder();
        try {
            connection = databaseConnection.returnConnection();
            statement = connection.createStatement();

            StringBuilder sql = new StringBuilder("select nametag from tags where id=");
            //добавляем  тег
            sql.append(id);
            Log.d("setF", sql.toString() + "");
            resultSet = statement.executeQuery(sql.toString());
            while (resultSet.next()) {
                str.append(resultSet.getString("nametag"));
                Log.d("getBooksLog", book.toString());
            }

        } catch (Exception err) {
            Log.d("getBooksLog", err.getMessage());
        }
        return str.toString();
    }
    public ArrayList<Integer> getFavoriteTags() {
        getTagsId();
        while (!isall){
            Log.d("all","ISALL"+isall);
        }
        isall=false;

        return favoriteTags;
    }

    /**
     * Books
     * @param tagName исползуется для получения книг по ранее выбранному пользователем тегу
     * @return возвращаем ArrayList для
     * @see com.kp.bookproject.ui.RecyclerViewBooksAdapter
     * Используется для получения десяти РАНДОМНЫХ книг отсортированных по рейтингу
     * Взаимодействовать с этим методом исключительно через отдельный поток, так как:
     * виснет UI
     * Можно воткнуть прогрессбар и т.п. вещи
     */

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

    /**
     * обратите внимане выше на isAll
     *это требуется для того, чтобы ждать завершение работы firebase и не получить ошибку
     * @param id - используется для получения книги по ее айди
     * @return соотв ввозвращает экземпляр класса
     * @see Book
     * Использовать в отдельном потоке для предотвращения ANR
     */
    public Book getBook(int id) {
        fillCheckBook(id);
        Log.d("all","ISALL"+isall);
        while (!isall){
            Log.d("all","ISALL"+isall);
        }
        isall=false;
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
                        Log.d("contains",map.containsKey(Integer.toString(author_id))+"");
                        if(map.containsKey(Integer.toString(author_id))){
                            map.replace(Integer.toString(author_id),map.get(Integer.toString(author_id))+1);
                        }else{
                            map.put(Integer.toString(author_id), 1);
                        }
                        databaseReference.setValue(map);
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
    private Account userAccount;
    public void fillUserInfo(){
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String userId = null;
        if(firebaseUser!=null){
             userId=firebaseUser.getUid();
            StringBuilder username=new StringBuilder();
            StringBuilder image=new StringBuilder();
            databaseReference=FirebaseDatabase.getInstance().getReference("Users").child(userId+"/username");
            String finalUserId = userId;
            databaseReference.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    GenericTypeIndicator<String> genericTypeIndicator=new GenericTypeIndicator<String>() {
                    };
                    if(task.isComplete()){
                      username.append(task.getResult().getValue(genericTypeIndicator));
                      Log.d("userAccountusername",username.toString());
                        databaseReference=FirebaseDatabase.getInstance().getReference("Users").child(finalUserId +"/image_url");
                        databaseReference.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DataSnapshot> task) {
                                GenericTypeIndicator<String> genericTypeIndicator=new GenericTypeIndicator<String>() {
                                };
                                if(task.isComplete()){
                                    image.append(task.getResult().getValue(genericTypeIndicator));
                                    userAccount=new Account(username.toString(),image.toString());
                                    isall=true;
                                }
                            }
                        });

                    }
                }
            });


        }

    }
    public void insertChanges(String newUserName,String pathFile){
        String userId=FirebaseAuth.getInstance().getCurrentUser().getUid();
        if(pathFile.length()!=0) {
            File file = new File(pathFile);
            Uri link = Uri.fromFile(file);
            FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
            StorageReference storageRef = firebaseStorage.getReference();
            StorageReference riversRef = storageRef.child("usersavatar/" +userId ).child("/avatar");

            UploadTask uploadTask = riversRef.putFile(link);
            Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }

                    // Continue with the task to get the download URL
                    return riversRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        databaseReference=FirebaseDatabase.getInstance().getReference("Users").child(userId+"/image_url");
                        databaseReference.setValue(downloadUri.toString());
                    } else {
                        // Handle failures
                        // ...
                    }
                }
            });
            databaseReference=FirebaseDatabase.getInstance().getReference("Users").child(userId+"/username");
            databaseReference.setValue(newUserName);

        }else{
            databaseReference=FirebaseDatabase.getInstance().getReference("Users").child(userId+"/username");
            databaseReference.setValue(newUserName);
        }
    }

    public Account getUserAccount() {
        fillUserInfo();
        while (!isall){
            Log.d("l","poop");
        }
        isall=false;
        Log.d("userAccount",userAccount.toString());
        return userAccount;
    }

    public ArrayList<Book> getLikedBooks(){
        ArrayList<Book> likedBooks=new ArrayList<>();
        ArrayList<Integer> likedBooksId=new ArrayList<>();
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
                            isall=true;
                        } catch (NullPointerException ex) {
                            Log.d("deletes", "null err");
                            isall=true;
                        }

                    }

                }
            });
            while (!isall){
            Log.d("sre","logaem");
            }
            isall=false;
            if(!map.isEmpty()) {
                for (String str :
                        map.keySet()) {
                    likedBooksId.add(Integer.valueOf(str));
                }
                try {
                    connection = databaseConnection.returnConnection();
                    statement = connection.createStatement();
                    for (Integer id:
                         likedBooksId) {
                        StringBuilder sql = new StringBuilder("select books.id,book_name,authors.author_name,book_image from books,authors where books.id=");
                        //добавляем название тега
                        sql.append(id).append(" and authors.id=author_id");
                        Log.d("setF", sql.toString() + "");
                        resultSet = statement.executeQuery(sql.toString());
                        while (resultSet.next()) {
                            Book book = new Book(resultSet.getInt("id")
                                    , resultSet.getString("book_name")
                                    , resultSet.getString("author_name")
                                    , resultSet.getString("book_image")
                            );
                            Log.d("getBooksLog", book.toString());
                            likedBooks.add(book);
                        }
                    }


                } catch (Exception err) {
                    Log.d("getBooksLog", err.getMessage());
                }


            }


        } else {
            Log.d("likedbooksfirebase", "err");
        }
        return likedBooks;
    }
}


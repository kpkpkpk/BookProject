package com.kp.bookproject.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.kp.bookproject.Entity.Book;
import com.kp.bookproject.PostgresControllers.DatabaseController;

public class HomeViewModel extends ViewModel {

    private MutableLiveData<Book> arr;
    private DatabaseController databaseController;

    public HomeViewModel() {
        arr = new MutableLiveData<>();
        databaseController=new DatabaseController(false);

    }

    public LiveData<Book> getBooks() {
        return arr;
    }
}
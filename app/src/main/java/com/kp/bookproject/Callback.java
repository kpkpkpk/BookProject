package com.kp.bookproject;

import android.widget.LinearLayout;

import com.kp.bookproject.Entity.Book;

import java.util.ArrayList;

public interface Callback {
    public void onStart();
    public void onComplete(ArrayList<LinearLayout> linearLayouts);
    public void onComplete(Book book);

}

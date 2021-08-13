package com.kp.bookproject.HelperClasses;

import android.widget.LinearLayout;

import com.kp.bookproject.Entity.Book;

import java.util.ArrayList;

public interface Callback {
     void onStart();
     void onComplete(ArrayList<LinearLayout> linearLayouts);
     void onComplete(Book book);
     void onComplete();

}

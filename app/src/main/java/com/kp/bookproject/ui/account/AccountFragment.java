package com.kp.bookproject.ui.account;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.imageview.ShapeableImageView;
import com.kp.bookproject.R;

public class AccountFragment extends Fragment {


    private ShapeableImageView userImage;
    private  View root;
    private TextView likedBooksButton,greetingUserTextView;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

      root= inflater.inflate(R.layout.fragment_account, container, false);
        userImage=root.findViewById(R.id.fragment_account_user_image);
        greetingUserTextView=root.findViewById(R.id.fragment_account_greeting_text);
        likedBooksButton=root.findViewById(R.id.account_liked_books_button);
        return root;
    }
}
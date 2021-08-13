package com.kp.bookproject.ui.account;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.kp.bookproject.HelperClasses.Callback;
import com.kp.bookproject.Controller.DatabaseController;
import com.kp.bookproject.Entity.Account;
import com.kp.bookproject.Entity.Book;
import com.kp.bookproject.ui.FavouriteTagsActivity;
import com.kp.bookproject.ui.startpages.LoginActivity;
import com.kp.bookproject.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;
import static com.kp.bookproject.HelperClasses.Constants.EDIT_PROFILE_TAG;
import static com.kp.bookproject.HelperClasses.Constants.REQUEST_CODE_FOR_ACCOUNT;
import static com.kp.bookproject.HelperClasses.Constants.SHOW_LIKED_TAG;

public class AccountFragment extends Fragment {

    private CardView editProfileCardView;
    private ShapeableImageView userImage;
    private  View root;
    private TextView likedBooksButton,greetingUserTextView;
    private Button exitButton,changeTagsButton;
    private ProgressBar progressBar;
    private Account userAccount;
    private View v1,v2;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

      root= inflater.inflate(R.layout.fragment_account, container, false);
        v1=root.findViewById(R.id.acc_v1);
        v2=root.findViewById(R.id.acc_v2);
        userImage=root.findViewById(R.id.fragment_account_user_image);
        greetingUserTextView=root.findViewById(R.id.fragment_account_greeting_text);
        editProfileCardView=root.findViewById(R.id.cardView);
        likedBooksButton=root.findViewById(R.id.account_liked_books_button);
        exitButton=root.findViewById(R.id.exit_account_button);
        progressBar=root.findViewById(R.id.fragment_account_progressbar);
        changeTagsButton=root.findViewById(R.id.edit_tags_account_button);
        changeTagsButton.setOnClickListener(v -> startActivity(new Intent(getActivity(), FavouriteTagsActivity.class)));
        exitButton.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            if(FirebaseAuth.getInstance().getCurrentUser()==null){
                startActivity(new Intent(root.getContext(), LoginActivity.class));
                try{
                    getActivity().finish();
                }catch (NullPointerException er){
                    Log.d("AccountFragment",er.getMessage());
                }
            }
        });
        editProfileCardView.setOnClickListener(v -> {
            FragmentManager fragmentManager=getParentFragmentManager();
            FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
            List<Fragment> existingFragments = fragmentManager.getFragments();
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

            EditProfileFragment fragment=new EditProfileFragment();
            Bundle bundle=new Bundle();
            bundle.putString("username",userAccount.getUsername());
            bundle.putString("imageUrl",userAccount.getImageUrl());
            fragment.setArguments(bundle);
            fragment.setTargetFragment(AccountFragment.this, REQUEST_CODE_FOR_ACCOUNT);
            fragmentTransaction.add(R.id.nav_host_fragment,fragment,EDIT_PROFILE_TAG);
            fragmentTransaction.addToBackStack("PREVIOUS");
            fragmentTransaction.hide(currentShownFragment);
            fragmentTransaction.show(fragment).commit();
        });
        likedBooksButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager=getParentFragmentManager();
                FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
                List<Fragment> existingFragments = fragmentManager.getFragments();
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

                ShowLikedBooksFragment fragment=new ShowLikedBooksFragment();
                fragmentTransaction.add(R.id.nav_host_fragment,fragment,SHOW_LIKED_TAG);
                fragmentTransaction.addToBackStack("PREVIOUS");
                fragmentTransaction.hide(currentShownFragment);
                fragmentTransaction.show(fragment).commit();
            }
        });
        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        progressBar.setVisibility(View.VISIBLE);
        editProfileCardView.setVisibility(View.GONE);
        userImage.setVisibility(View.GONE);
       v1.setVisibility(View.GONE);
        changeTagsButton.setVisibility(View.GONE);
       v2.setVisibility(View.GONE);
        greetingUserTextView.setVisibility(View.GONE);
        exitButton.setVisibility(View.GONE);
        likedBooksButton.setVisibility(View.GONE);
        Thread thread=new Thread(() -> getUser(new Callback() {
            @Override
            public void onStart() {

            }

            @Override
            public void onComplete(ArrayList<LinearLayout> linearLayouts) {

            }

            @Override
            public void onComplete(Book book) {

            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onComplete() {
                try {
                getActivity().runOnUiThread(() -> {
                    Glide.with(root.getContext()).load(userAccount.getImageUrl()).into(userImage);
                    greetingUserTextView.setText(getResources().getString(R.string.hello_account_fragment) +" "+ userAccount.getUsername());
                    progressBar.setVisibility(View.GONE);
                    editProfileCardView.setVisibility(View.VISIBLE);
                    greetingUserTextView.setVisibility(View.VISIBLE);
                    userImage.setVisibility(View.VISIBLE);
                    exitButton.setVisibility(View.VISIBLE);
                    changeTagsButton.setVisibility(View.VISIBLE);
                    v1.setVisibility(View.VISIBLE);
                    likedBooksButton.setVisibility(View.VISIBLE);
                    v2.setVisibility(View.VISIBLE);
                });
            }catch(NullPointerException e){
                    Log.d("AccountFragment",e.getMessage());
                }
            }
        }));
        thread.start();
        

        super.onActivityCreated(savedInstanceState);
    }
    private void getUser(Callback callback){
        callback.onStart();
        DatabaseController databaseController = new DatabaseController(true);
        userAccount=databaseController.getUserAccount();
        callback.onComplete();
    }
    @SuppressLint("SetTextI18n")
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode==REQUEST_CODE_FOR_ACCOUNT){
               greetingUserTextView.setText(getResources().getString(R.string.hello_account_fragment)+" "+data.getStringExtra("newNick"));
               String filepath=data.getStringExtra("newFilepath");
               if(filepath.length()!=0) {
                   File file = new File(filepath);
                   userImage.setImageURI(Uri.fromFile(file));
               }
            }
        }
    }
}
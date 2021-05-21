package com.kp.bookproject.ui.account;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.kp.bookproject.LoginActivity;
import com.kp.bookproject.R;
import com.kp.bookproject.ui.search.SelectedTagFragment;

import java.util.List;

import static com.kp.bookproject.Constants.SELECTED_TAG_FRAGMENT;

public class AccountFragment extends Fragment {

    private CardView editProfileCardView;
    private ShapeableImageView userImage;
    private  View root;
    private TextView likedBooksButton,greetingUserTextView,exitButton;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

      root= inflater.inflate(R.layout.fragment_account, container, false);
        userImage=root.findViewById(R.id.fragment_account_user_image);
        greetingUserTextView=root.findViewById(R.id.fragment_account_greeting_text);
        editProfileCardView=root.findViewById(R.id.cardView);
        likedBooksButton=root.findViewById(R.id.account_liked_books_button);
        exitButton=root.findViewById(R.id.exit_account_button);
        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(root.getContext(), LoginActivity.class));
                getActivity().finish();
            }
        });
        editProfileCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager=getParentFragmentManager();
                FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
                List<Fragment> existingFragments = fragmentManager.getFragments();
                Log.d("isListExist",(existingFragments != null)+"");
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
                fragmentTransaction.add(R.id.nav_host_fragment,fragment,SELECTED_TAG_FRAGMENT);
                fragmentTransaction.addToBackStack("PREVIOUS");
                fragmentTransaction.hide(currentShownFragment);
                fragmentTransaction.show(fragment).commit();
            }
        });
        return root;
    }
}
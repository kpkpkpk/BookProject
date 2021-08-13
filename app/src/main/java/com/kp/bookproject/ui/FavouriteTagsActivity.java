package com.kp.bookproject.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.kp.bookproject.Controller.DatabaseController;
import com.kp.bookproject.Entity.Book;
import com.kp.bookproject.HelperClasses.Callback;
import com.kp.bookproject.R;
import com.kp.bookproject.ui.startpages.RegistrationActivity;

import java.util.ArrayList;
import java.util.List;

public class FavouriteTagsActivity extends AppCompatActivity {
    private ChipGroup chipGroup;
    private DatabaseController databaseController;
    private TextView nextB;


    ArrayList<String> selectedTags;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.getDefaultNightMode());
        setContentView(R.layout.activity_favourite_tags);
        chipGroup=findViewById(R.id.chip_tags_group);
        databaseController=new DatabaseController();
        nextB=findViewById(R.id.clickable_text_to_next_activity);
        selectedTags=new ArrayList<>();
        nextB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Integer> ids = chipGroup.getCheckedChipIds();
                if (ids.isEmpty()) {
                    Toast.makeText(FavouriteTagsActivity.this, getResources().getString(R.string.select_tags_hint), Toast.LENGTH_SHORT).show();
                } else {
                    for (Integer id : ids) {
                        Chip chip = chipGroup.findViewById(id);
                        //добавляем текстовое значение чипа, чтобы найти его айди в ДБ
                        selectedTags.add(chip.getText().toString());
                    }
                    //добавляем данные в БД
                    databaseController.setFavouriteTagsIntoAccountFirebase(selectedTags, new Callback() {
                        @Override
                        public void onStart() {

                        }

                        @Override
                        public void onComplete(ArrayList<LinearLayout> linearLayouts) {

                        }

                        @Override
                        public void onComplete(Book book) {

                        }

                        @Override
                        public void onComplete() {
                            Intent intent=new Intent(FavouriteTagsActivity.this, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        }
                    });

                }
            }

        });
    }
    @Override
    public void onBackPressed(){
        if(getIntent().getExtras()!=null&&getIntent().getBooleanExtra("fromRegistration",false)){
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if(user!=null) {
                user.delete()
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Log.d("delete acc", "User account deleted.");
                                    FirebaseAuth.getInstance().signOut();
                                    Toast.makeText(FavouriteTagsActivity.this,
                                            getResources().getString(R.string.registration_rejected),
                                            Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(FavouriteTagsActivity.this, RegistrationActivity.class));
                                    finish();
                                }
                            }
                        });
            }
        }else {
            super.onBackPressed();
        }
    }



}
package com.kp.bookproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.kp.bookproject.Controller.DatabaseController;

import java.util.ArrayList;
import java.util.List;

import static com.kp.bookproject.Constants.SELECTED_TAGS_COUNT;
import static com.kp.bookproject.Constants.SELECTED_TAGS_KEY;
import static com.kp.bookproject.Constants.SHARED_PREFERENCES_FAVORITE_TAGS_NAME;

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
                Toast.makeText(FavouriteTagsActivity.this, ""+ids.toString(), Toast.LENGTH_SHORT).show();
                for (Integer id:ids){
                    Chip chip = chipGroup.findViewById(id);
                    //добавляем текстовое значение чипа, чтобы найти его айди в ДБ
                    selectedTags.add(chip.getText().toString());
                    Toast.makeText(FavouriteTagsActivity.this,chip.getText().toString(),Toast.LENGTH_LONG).show();
                }
                //добавляем данные в БД
                databaseController.setFavouriteTagsIntoAccountFirebase(selectedTags);

                startActivity(new Intent(FavouriteTagsActivity.this,MainActivity.class));
                finish();
            }
        });
    }




}
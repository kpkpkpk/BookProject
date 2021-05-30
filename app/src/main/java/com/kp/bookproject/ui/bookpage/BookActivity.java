package com.kp.bookproject.ui.bookpage;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.kp.bookproject.R;

import static com.kp.bookproject.HelperClasses.Constants.SELECTED_BOOK;

public class BookActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private FrameLayout containerForBookPage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.getDefaultNightMode());
        setContentView(R.layout.activity_book);
        toolbar=findViewById(R.id.tool_bar_activity_book);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        containerForBookPage=findViewById(R.id.frame_layout_for_book);
    }

    @Override
    protected void onStart() {
        super.onStart();
        BookFragment fragment = new BookFragment();
        Bundle bundle = getIntent().getBundleExtra("bundle");
        fragment.setArguments(bundle);
        FragmentTransaction fragmentTransaction=getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.frame_layout_for_book, fragment, SELECTED_BOOK);
        fragmentTransaction.show(fragment).commit();
    }
    @Override
    public void onBackPressed(){
        super.onBackPressed();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
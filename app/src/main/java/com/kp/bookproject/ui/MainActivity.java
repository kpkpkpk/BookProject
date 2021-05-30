package com.kp.bookproject.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.kp.bookproject.R;
import com.kp.bookproject.ui.home.HomeFragment;
import com.kp.bookproject.ui.account.AccountFragment;
import com.kp.bookproject.ui.search.SearchFragment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.util.List;

import static com.kp.bookproject.HelperClasses.Constants.FRAGMENT_ACCOUNT_TAG;
import static com.kp.bookproject.HelperClasses.Constants.FRAGMENT_HOME_TAG;

import static com.kp.bookproject.HelperClasses.Constants.FRAGMENT_SEARCH_TAG;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView navView;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        navView = findViewById(R.id.nav_view);
        toolbar=findViewById(R.id.tool_bar);
        toolbar.setTitle("Главная");
        setSupportActionBar(toolbar);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.getDefaultNightMode());
        navView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                int itemId = item.getItemId();
                switch (itemId) {
                    case R.id.navigation_home:
                        toolbar.setTitle(getResources().getString(R.string.title_home));
                        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                        changeFragment(FRAGMENT_HOME_TAG);
                        getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                        return true;
                    case R.id.navigation_search:
                        toolbar.setTitle(getResources().getString(R.string.title_search));
                        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                        changeFragment(FRAGMENT_SEARCH_TAG);

                        return true;
                    case R.id.navigation_account:
                        toolbar.setTitle(getResources().getString(R.string.title_acc));
                        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                        changeFragment(FRAGMENT_ACCOUNT_TAG);
                        return true;
                }

                return false;
            }
        });
        navView.setSelectedItemId(R.id.navigation_home);

    }

    private void changeFragment(String neededToShowFragmentTag) {

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        List<Fragment> existingFragments = fragmentManager.getFragments();
        Log.d("isListExist", (existingFragments != null) + "");
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
        Log.d("isShownFragment", (currentShownFragment == null) + "");

        //Если отображаемого фрагмента нет или выбирается другая вкладка, то

        if (currentShownFragment == null || !currentShownFragment.getTag().equals(neededToShowFragmentTag)) {
            //мы определяем, есть ли уже раннее созданный готовый фрагмент
            Fragment neededToShowFragment = fragmentManager.findFragmentByTag(neededToShowFragmentTag);
            //если нет, то берем с функции обозначаемый тег и проходимся по свитчу
            if (neededToShowFragment == null) {
                switch (neededToShowFragmentTag) {
                    case "fragment_home":
                        neededToShowFragment = new HomeFragment();
                        break;
                    case "fragment_search":
                        neededToShowFragment = new SearchFragment();
                        break;
                    case "fragment_account":
                        neededToShowFragment = new AccountFragment();
                }
                //после уже в fragment transaction будем добавлен необходимый фрагмент
                fragmentTransaction.add(R.id.nav_host_fragment, neededToShowFragment, neededToShowFragmentTag);
            }
            //если уже показывается фрагмент, то скроем его, но не удаляем
            if (currentShownFragment != null) {
                fragmentTransaction.hide(currentShownFragment);
                fragmentTransaction.addToBackStack("PREVIOUS");
            }

            //выводим необходимый фрагмент на экран
            fragmentTransaction.show(neededToShowFragment).commit();

        }


    }

    @Override
    public void onBackPressed() {

        FragmentManager manager=getSupportFragmentManager();
        int count = manager.getBackStackEntryCount();

        if (count == 0) {
            super.onBackPressed();
            //additional code
        } else {

            manager.popBackStackImmediate();

            List<Fragment> existingFragments =manager.getFragments();
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
            switch (currentShownFragment.getTag()) {
                case FRAGMENT_HOME_TAG:
                    if ((getSupportActionBar().getDisplayOptions() & ActionBar.DISPLAY_HOME_AS_UP) != 0) {
                        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                    }
                    navView.setSelectedItemId(R.id.navigation_home);
                    getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    break;
                case FRAGMENT_SEARCH_TAG:
                    if ((getSupportActionBar().getDisplayOptions() & ActionBar.DISPLAY_HOME_AS_UP) != 0) {
                        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                    }
                    navView.setSelectedItemId(R.id.navigation_search);
                    break;
                case FRAGMENT_ACCOUNT_TAG:
                    if ((getSupportActionBar().getDisplayOptions() & ActionBar.DISPLAY_HOME_AS_UP) != 0) {
                        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                    }
                    navView.setSelectedItemId(R.id.navigation_account);
                    break;
            }
        }
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //при использоавнии if не дает вывзывать onBackPressed
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();

                break;
        }
        return super.onOptionsItemSelected(item);
    }




}
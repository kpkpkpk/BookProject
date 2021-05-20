package com.kp.bookproject;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.kp.bookproject.ui.home.HomeFragment;
import com.kp.bookproject.ui.account.AccountFragment;
import com.kp.bookproject.ui.search.SearchFragment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.util.List;

import static com.kp.bookproject.Constants.FRAGMENT_ACCOUNT_TAG;
import static com.kp.bookproject.Constants.FRAGMENT_HOME_TAG;

import static com.kp.bookproject.Constants.FRAGMENT_SEARCH_TAG;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView navView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        navView = findViewById(R.id.nav_view);

        navView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                int itemId = item.getItemId();
                switch (itemId) {
                    case R.id.navigation_home:

                        changeFragment(FRAGMENT_HOME_TAG);

                        return true;
                    case R.id.navigation_search:

                        changeFragment(FRAGMENT_SEARCH_TAG);

                        return true;
                    case R.id.navigation_account:
                        changeFragment(FRAGMENT_ACCOUNT_TAG);

                        return true;
                }

                return false;
            }
        });
        navView.setSelectedItemId(R.id.navigation_home);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
//        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
//                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
//                .build();
//        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
//        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
//        NavigationUI.setupWithNavController(navView, navController);
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

            Toast.makeText(this, ""+currentShownFragment.getTag(), Toast.LENGTH_SHORT).show();
            switch (currentShownFragment.getTag()) {
                case FRAGMENT_HOME_TAG:
                    navView.setSelectedItemId(R.id.navigation_home);
                    break;
                case FRAGMENT_SEARCH_TAG:
                    navView.setSelectedItemId(R.id.navigation_search);
                    break;
                case FRAGMENT_ACCOUNT_TAG:
                    navView.setSelectedItemId(R.id.navigation_account);
                    break;
            }
        }
    }




}
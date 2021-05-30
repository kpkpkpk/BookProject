package com.kp.bookproject.ui.search;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kp.bookproject.R;
import com.kp.bookproject.ui.recyclerViewAdapters.SearchTagsFragmentAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.kp.bookproject.Constants.SELECTED_TAG_FRAGMENT;

public class SearchFragment extends Fragment {
    private View root;
    private RecyclerView recyclerViewTags;
    private LinearLayout mainLayout;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

         root = inflater.inflate(R.layout.fragment_search, container, false);
        recyclerViewTags=root.findViewById(R.id.fragment_search_recycler_view);
         mainLayout=root.findViewById(R.id.main_layout_fragment_search);
         createRecyclerViewTags();
         return root;
    }
    //выношу работу с recyclerViewTags в отдельную функцию, чтобы не захламлять oncreateview
    public void createRecyclerViewTags(){
        recyclerViewTags.setLayoutManager(new GridLayoutManager(root.getContext(),2));
        ArrayList<String> arrayList=new ArrayList<>();
        String[] tags=getResources().getStringArray(R.array.array_tags);
        Collections.addAll(arrayList,tags);
        recyclerViewTags.addItemDecoration(new DividerItemDecoration(root.getContext(), GridLayoutManager.VERTICAL));
        SearchTagsFragmentAdapter recyclerViewAdapter=new SearchTagsFragmentAdapter(root.getContext(),arrayList);
        recyclerViewAdapter.setSearchClickListener(new SearchTagsFragmentAdapter.SearchItemClickListener() {
            @Override
            public void onItemClick(String tagName) {
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
                SelectedTagFragment fragment=new SelectedTagFragment();
                Bundle bundle=new Bundle();
                bundle.putString("tag",tagName);
                bundle.putString("forFirebase",tagName);
                fragment.setArguments(bundle);
                fragmentTransaction.add(R.id.nav_host_fragment,fragment,SELECTED_TAG_FRAGMENT);
                fragmentTransaction.addToBackStack("PREVIOUS");
                fragmentTransaction.hide(currentShownFragment);
                fragmentTransaction.show(fragment).commit();

            }
        });
        recyclerViewTags.setAdapter(recyclerViewAdapter);
    }

}
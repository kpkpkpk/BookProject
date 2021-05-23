package com.kp.bookproject.ui.account;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.FileUtils;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.imageview.ShapeableImageView;
import com.kp.bookproject.Entity.Account;
import com.kp.bookproject.R;
import com.kp.bookproject.ui.home.HomeFragment;
import com.kp.bookproject.ui.search.SelectedTagFragment;

import java.util.List;

import static com.kp.bookproject.Constants.FRAGMENT_ACCOUNT_TAG;
import static com.kp.bookproject.Constants.FRAGMENT_HOME_TAG;
import static com.kp.bookproject.Constants.PICK_PHOTO_FROM_GALLERY;
import static com.kp.bookproject.Constants.SELECTED_TAG_FRAGMENT;

public class EditProfileFragment extends Fragment {
    private View root;
    private ShapeableImageView userImage;
    private TextView changePhotoClickableTextView;
    private EditText nickname;
    private StringBuilder filepath;
    private BottomSheetDialog imageBottomSheetDialog;
    private Toolbar toolbar;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_editprofile, container, false);
        setHasOptionsMenu(true);
        toolbar=getActivity().findViewById(R.id.tool_bar);
        changePhotoClickableTextView=root.findViewById(R.id.fragment_editprofile_change_photo_text_button);
        nickname=root.findViewById(R.id.fragment_editprofile_nickname_edittext);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        userImage=root.findViewById(R.id.fragment_editprofile_image_profile);
        toolbar.setTitle("Редактирование профиля");
        Glide.with(root.getContext()).load(getArguments().getString("imageUrl")).into(userImage);
        nickname.setText(getArguments().getString("username"));
        prepareBottomSheetDialog();
        changePhotoClickableTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageBottomSheetDialog.show();
            }
        });
        return root;
    }
    public void prepareBottomSheetDialog(){
        imageBottomSheetDialog=new BottomSheetDialog(getActivity(),R.style.SheetDialog);
        View sheetView=getActivity().getLayoutInflater().inflate(R.layout.select_image_bottom_sheet,null);
        imageBottomSheetDialog.setContentView(sheetView);
        LinearLayout useCamera =  sheetView.findViewById(R.id.fragment_editprofile_bottom_sheet_use_camera);
        LinearLayout useGallery = sheetView.findViewById(R.id.fragment_editprofile_bottom_sheet_use_gallery);
        useGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, PICK_PHOTO_FROM_GALLERY);
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.toolbar_editprofile_menu,menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                getActivity().onBackPressed();

                break;

                case R.id.check_mark:
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
                    AccountFragment fragment=new AccountFragment();
                    toolbar.setTitle("Профиль");
                    fragmentManager.popBackStack();
                    ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                Toast.makeText(getActivity(), "accepted", Toast.LENGTH_SHORT).show();
                break;
        }
         return super.onOptionsItemSelected(item);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_PHOTO_FROM_GALLERY && resultCode == Activity.RESULT_OK) {
            if (data == null) {
                return;
            }
            String string=getPath(data.getData());
            Toast.makeText(getActivity(), data.getData()+"toast"+string, Toast.LENGTH_SHORT).show();
            //Now you can do whatever you want with your inpustream, save it as file, upload to a server, decode a bitmap...
        }
    }
    //
    private String getPath(Uri uri){

        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        StringBuilder builder=new StringBuilder();
        Cursor cursor = root.getContext().getContentResolver().query(
                uri, filePathColumn, null, null, null);
        cursor.moveToFirst();

        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
       builder.append(cursor.getString(columnIndex));
        cursor.close();

        return builder.toString();
    }
}

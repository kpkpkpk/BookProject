package com.kp.bookproject.ui.account;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.JobIntentService;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.imageview.ShapeableImageView;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.kp.bookproject.R;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.File;
import java.util.List;

import static android.app.Activity.RESULT_OK;
import static com.kp.bookproject.Constants.JOB_ID_CHANGES;

public class EditProfileFragment extends Fragment {
    private View root;
    private ShapeableImageView userImage;
    private TextView changePhotoClickableTextView;
    private EditText nickname;
    private StringBuilder filepath;
    private BottomSheetDialog imageBottomSheetDialog;
    private Toolbar toolbar;
    private Fragment fragment;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_editprofile, container, false);
        setHasOptionsMenu(true);
        fragment=this;
        filepath=new StringBuilder();
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

        LinearLayout useGallery = sheetView.findViewById(R.id.fragment_editprofile_bottom_sheet_use_gallery);
        useGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dexter.withContext(root.getContext())
                        .withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        .withListener(new MultiplePermissionsListener() {
                            @Override
                            public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                                if(!multiplePermissionsReport.areAllPermissionsGranted()){
                                    Toast.makeText(getActivity(), getResources().getString(R.string.is_permission_denied), Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(Settings.ACTION_APPLICATION_SETTINGS));
                                }else{
                                    CropImage.activity().start(getContext(),fragment);
                                }
                            }

                            @Override
                            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                                permissionToken.continuePermissionRequest();
                            }
                        })
                        .check();
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
                    Intent i=new Intent(getContext(),AccountChangesService.class);
                    i.putExtra("filepath",filepath.toString());
                    i.putExtra("nickname",nickname.getText().toString());
                    JobIntentService.enqueueWork(root.getContext(), AccountChangesService.class,JOB_ID_CHANGES,i);
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
                    ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                    Intent intent = new Intent(getContext(), EditProfileFragment.class);
                    intent.putExtra("newNick", nickname.getText().toString());
                    intent.putExtra("newFilepath", filepath.toString());
                    getTargetFragment().onActivityResult(getTargetRequestCode(), RESULT_OK, intent);
                    toolbar.setTitle("Профиль");
                    getActivity().getSupportFragmentManager().popBackStack();


                break;
        }
         return super.onOptionsItemSelected(item);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                userImage.setImageURI(resultUri);
                imageBottomSheetDialog.dismiss();
                File file=new File(resultUri.getPath());

                filepath.append(file.getAbsolutePath());
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        }
    }
    //

}

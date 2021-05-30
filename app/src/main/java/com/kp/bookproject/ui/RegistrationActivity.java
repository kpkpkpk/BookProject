package com.kp.bookproject.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.kp.bookproject.R;

import java.util.HashMap;

public class RegistrationActivity extends AppCompatActivity {
    private Button regButton;
    private EditText loginField,passField,usernameField;
    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;
    private ProgressBar progressBar;
    private LinearLayout layoutWithFields;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.getDefaultNightMode());
        loginField=findViewById(R.id.loginActField);
        passField=findViewById(R.id.passwordActField);
        usernameField=findViewById(R.id.userActField);
        firebaseAuth=FirebaseAuth.getInstance();
        progressBar=findViewById(R.id.registration_activity_progressBar);
        layoutWithFields=findViewById(R.id.fieldsLayout);
        regButton=findViewById(R.id.regActButton);
        regButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username=usernameField.getText().toString();
                String email=loginField.getText().toString();
                String password=passField.getText().toString();
                if (TextUtils.isEmpty(username) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                    Toast.makeText(RegistrationActivity.this, getResources().getString(R.string.registration_hint), Toast.LENGTH_SHORT).show();
                }else if(password.length()<8){
                    Toast.makeText(RegistrationActivity.this, getResources().getString(R.string.registration_create_pass_hint),
                            Toast.LENGTH_SHORT).show();
                }else{
                    userRegister(username,email,password);

                }

            }

        });

    }

    private void userRegister(final String username, String email, String password) {
        firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    progressBar.setVisibility(View.VISIBLE);
                    layoutWithFields.setVisibility(View.GONE);
                    TextView textView=findViewById(R.id.activity_registration_text);
                    textView.setVisibility(View.GONE);
                    FirebaseUser firebaseUser=firebaseAuth.getCurrentUser();
                    String userId=null;
                    if(firebaseUser != null){
                        userId=firebaseUser.getUid();
                        databaseReference= FirebaseDatabase.getInstance().getReference("Users").child(userId);
                        HashMap<String,String> hashMap=new HashMap<>();
                        hashMap.put("id",userId);
                        hashMap.put("username",username);
                        //получаем объект
                        FirebaseStorage firebaseStorage=FirebaseStorage.getInstance();
                        //создаем ссылку
                        StorageReference storageReference=firebaseStorage.getReference().child("usersavatar/defaultavatar/default-user-image.png");
                        Log.d("getImage",storageReference.toString());
                        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                hashMap.put("image_url",uri.toString());
                                Log.d("getImage",hashMap.get("image_url"));
                                databaseReference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            progressBar.setVisibility(View.GONE);
                                            layoutWithFields.setVisibility(View.VISIBLE);
                                            textView.setVisibility(View.VISIBLE);
                                            Toast.makeText(RegistrationActivity.this,
                                                    getResources().getString(R.string.successful_registr),
                                                    Toast.LENGTH_SHORT).show();
                                            Intent i = new Intent(RegistrationActivity.this, FavouriteTagsActivity.class);
                                            i.putExtra("fromRegistration",true);
                                            startActivity(i);
                                            finish();
                                        }
                                    }

                                });

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d("getImage",e.getMessage());
                            }
                        });


                    }
                }else{
                    Toast.makeText(RegistrationActivity.this, getResources().getString(R.string.unsuccessful_login),
                            Toast.LENGTH_SHORT).show();

                }
            }
        });
    }


}
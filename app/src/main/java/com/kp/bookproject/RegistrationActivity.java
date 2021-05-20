package com.kp.bookproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.HashMap;

public class RegistrationActivity extends AppCompatActivity {
    private Button regButton;
    private MaterialEditText loginField,passField,usernameField;
    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        loginField=findViewById(R.id.loginActField);
        passField=findViewById(R.id.passwordActField);
        usernameField=findViewById(R.id.userActField);
        firebaseAuth=FirebaseAuth.getInstance();
        regButton=findViewById(R.id.regActButton);
        regButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username=usernameField.getText().toString();
                String email=loginField.getText().toString();
                String password=passField.getText().toString();
                if (TextUtils.isEmpty(username) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                    Toast.makeText(RegistrationActivity.this, "All fields are required", Toast.LENGTH_SHORT).show();
                }else if(password.length()<8){
                    Toast.makeText(RegistrationActivity.this, "Password should contain more than 8 symbols",
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
                    FirebaseUser firebaseUser=firebaseAuth.getCurrentUser();
                    String userId=null;
                    if(firebaseUser != null){
                        userId=firebaseUser.getUid();
                        databaseReference= FirebaseDatabase.getInstance().getReference("Users").child(userId);
                        HashMap<String,String> hashMap=new HashMap<>();
                        hashMap.put("id",userId);
                        hashMap.put("username",username);
                        hashMap.put("imageUrl","default");
                        databaseReference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {

                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    Toast.makeText(RegistrationActivity.this, "Вы успешно зарегестрировались, выбрать изображение профиля можно во вкладке Аккаунт", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(RegistrationActivity.this, FavouriteTagsActivity.class));
                                    finish();
                                }
                            }
                        });
                    }
                }else{
                    Toast.makeText(RegistrationActivity.this, "Try to input other login or password",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}
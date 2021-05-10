package com.kp.bookproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.rengwuxian.materialedittext.MaterialEditText;


public class LoginActivity extends AppCompatActivity {

    private Button loginButton,registrationButton;
    private MaterialEditText loginF,passF;
    FirebaseUser firebaseUser;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        loginButton=findViewById(R.id.loginButton);
        registrationButton=findViewById(R.id.registrationButton);
        loginF=findViewById(R.id.loginField);
        passF=findViewById(R.id.passwordField);
        firebaseAuth=FirebaseAuth.getInstance();
        View.OnClickListener onClickListener=new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.loginButton:
                        String emailStr=loginF.getText().toString();
                        String passStr=passF.getText().toString();
                        if(TextUtils.isEmpty(emailStr)||TextUtils.isEmpty(passStr)){
                            Toast.makeText(LoginActivity.this, "All fields are required", Toast.LENGTH_SHORT).show();
                        }else{
                            firebaseAuth.signInWithEmailAndPassword(emailStr,passStr).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(task.isSuccessful()){
                                        startActivity(new Intent(LoginActivity.this,MainActivity.class));
                                        finish();
                                    }else{
                                        Toast.makeText(LoginActivity.this, "Please check ur fields", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }

                        break;
                    case R.id.registrationButton:
                        startActivity(new Intent(LoginActivity.this,RegistrationActivity.class));
                        break;
                }
            }
        };
        loginButton.setOnClickListener(onClickListener);
        registrationButton.setOnClickListener(onClickListener);
    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseUser= firebaseAuth.getCurrentUser();
        if(firebaseUser !=null){
            startActivity(new Intent(this,MainActivity.class));
            finish();
        }
    }

}
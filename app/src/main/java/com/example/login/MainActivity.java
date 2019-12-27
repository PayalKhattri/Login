package com.example.login;

import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
FirebaseAuth mAuth;
    EditText editTextEmail,editTextPassword;
    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth=FirebaseAuth.getInstance() ;
        editTextEmail=(EditText)findViewById(R.id.editTextEmail);
        editTextPassword=(EditText)findViewById(R.id.editTextPassword);
        progressBar=(ProgressBar)findViewById(R.id.progressBar);
        findViewById(R.id.textViewSignUp).setOnClickListener(this);
        findViewById(R.id.buttonLogIn).setOnClickListener(this);
        findViewById(R.id.buttonsign).setOnClickListener(this);
    }
private void userLogin()
{

    final String email=editTextEmail.getText().toString().trim();
    final String password=editTextPassword.getText().toString().trim();
    if(email.isEmpty()){
        editTextEmail.setError("Email is required");
        editTextEmail.requestFocus();
        return;
    }
    if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
        editTextEmail.setError("Please enter a valid email");
        editTextEmail.requestFocus();
        return;
    }
    if(password.isEmpty()){
        editTextPassword.setError("Password is required");
        editTextPassword.requestFocus();
        return;
    }
    if(password.length()<6)
    {
        editTextPassword.setError("Minimum length of password should be 6");
        editTextPassword.requestFocus();
        return;
    }
    progressBar.setVisibility(View.VISIBLE);
    mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
        @Override
        public void onComplete(@NonNull Task<AuthResult> task) {
            progressBar.setVisibility(View.VISIBLE);


            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    progressBar.setVisibility(View.GONE);
                    if (task.isSuccessful()) {
                        finish();
                   Intent intent=new Intent(MainActivity.this,ProfileActivity.class);
                   intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                   startActivity(intent);
                    } else {
                        Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    });
}


    @Override
    protected void onStart() {
        super.onStart();
        if(mAuth.getCurrentUser()!=null){
            finish();
            startActivity(new Intent(this,ProfileActivity.class));
        }
    }

    @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.buttonsign:
                case R.id.textViewSignUp:
                    finish();
                    startActivity(new Intent(this, SignUpActivity.class));
                    break;
                case R.id.buttonLogIn:
                    userLogin();
                    break;
            }
        }
    }


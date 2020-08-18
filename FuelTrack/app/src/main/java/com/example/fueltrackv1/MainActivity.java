package com.example.fueltrackv1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "Hi";
    Button signUp,login;
    TextInputLayout mobile, password;
    ProgressBar progressBar;
    private int RC_SIGN_IN=1;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        login = findViewById(R.id.loginBtn);
        signUp = findViewById(R.id.signUp);
        mobile = findViewById(R.id.mobileNumber);
        password = findViewById(R.id.password);
        progressBar = findViewById(R.id.loginProgress);
        mAuth = FirebaseAuth.getInstance();

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                loginUser(v);
            }
        });

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), signup.class);
                startActivity(intent);
            }
        });


    }

    //Code to validate username and password
    private Boolean validateUsername(){

        String val = mobile.getEditText().getText().toString();

        if(val.isEmpty()) {
            mobile.setError("Username Name cannot be empty");
            return false;
        } else {
            mobile.setError(null);
            mobile.setErrorEnabled(false);
            return true;
        }
    }

    private Boolean validatePassword(){
        String val = password.getEditText().getText().toString();

        if (val.isEmpty()) {
            password.setError("Password cannot be empty");
            return true;
        } else {
            password.setError(null);
            password.setErrorEnabled(false);
            return true;
        }
    }

    //authenticating the user to SignIn
    public void loginUser(View v){
        if(!validateUsername() | !validatePassword()) {
            return;
        }
        else {
            checkUser();
        }

    }

    private void checkUser() {
        progressBar.setVisibility(View.VISIBLE);

        final String enteredUsername = mobile.getEditText().getText().toString().trim();
        final String enteredPassword = password.getEditText().getText().toString().trim();


        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
        Query checkUsername = reference.orderByChild("mobile").equalTo(enteredUsername);

        checkUsername.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    mobile.setError(null);
                    mobile.setErrorEnabled(false);

                    String DbPassword = dataSnapshot.child(enteredUsername).child("password").getValue(String.class);


                    assert DbPassword != null;
                    if(DbPassword.equals(enteredPassword)) {
                        password.setError(null);
                        password.setErrorEnabled(false);
                        progressBar.setVisibility(View.VISIBLE);

                        User user = new User(1, enteredUsername);


                        SessionMangement sessionMangement = new SessionMangement(MainActivity.this);
                        sessionMangement.saveSession(user);

                        SharedPreferences.Editor editor = getSharedPreferences("ID", MODE_PRIVATE).edit();
                        editor.putString("number", enteredUsername);
                        editor.putInt("idSet", 1);
                        editor.apply();
                        Intent intent = new Intent(getApplicationContext(), home.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra("username",enteredUsername);
                        startActivity(intent);
                        MainActivity.this.finish();
                    } else {
                        progressBar.setVisibility(View.INVISIBLE);
                        password.setError("Invalid Password");
                        password.requestFocus();
                    }
                }else{
                    //Toast.makeText(signup.this,"The number is already Exixts",Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.INVISIBLE);
                    mobile.setError("User not exist");
                    mobile.requestFocus();
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        SessionMangement sessionMangement = new SessionMangement((MainActivity.this));
        int userId = sessionMangement.getSession();
        String username = sessionMangement.getUserName();
        if(userId!=-1)
        {
            Intent intent = new Intent(getApplicationContext(), home.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("username",username);
            startActivity(intent);
        }
    }
}

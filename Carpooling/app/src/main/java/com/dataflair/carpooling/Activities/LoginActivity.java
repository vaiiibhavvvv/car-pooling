package com.dataflair.carpooling.Activities;

/**
 * In this Activity we make the user login
 * we will add the user details to firebase in this activity
 */

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.dataflair.carpooling.MainActivity;
import com.dataflair.carpooling.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {

    GoogleSignInClient mSignInClient;
    FirebaseAuth firebaseAuth;
    ProgressDialog progressBar;
    Button loginBtn;
    EditText adharNumberEditTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Assigned Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance();


        loginBtn = (Button) findViewById(R.id.loginBtn);
        adharNumberEditTxt = (EditText) findViewById(R.id.adharNumberEditTxt);

        //Assigned ProgressDialog To indicate Background Action
        progressBar = new ProgressDialog(this);
        progressBar.setTitle("Please Wait...");
        progressBar.setMessage("We are setting Everything for you...");
        progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);


        //Firebase SignOptions to Register User using Gmail
        GoogleSignInOptions signInOptions = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("919563026249-q4cq83ah2jbnmps9bqkbc8d59vev4vm6.apps.googleusercontent.com")
                .requestEmail()
                .build();

        mSignInClient = GoogleSignIn.getClient(getApplicationContext(), signInOptions);

        //implementation of OnClickListener  to Perform signIn operation on Button click
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Gets AdharNumber from the EditText
                String phoneNumber = adharNumberEditTxt.getText().toString().trim();
                if (phoneNumber.isEmpty()) {

                    //Shows the Below Toast if the editText is empty
                    Toast.makeText(getApplicationContext(), "Please,Enter your Adhar Number", Toast.LENGTH_SHORT).show();
                } else {

                    //Shows all the available mail ids in device
                    Intent intent = mSignInClient.getSignInIntent();
                    startActivityForResult(intent, 100);
                }

            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100) {
            Task<GoogleSignInAccount> googleSignInAccountTask = GoogleSignIn
                    .getSignedInAccountFromIntent(data);

            //Checks if SignIn is Success or not
            if (googleSignInAccountTask.isSuccessful()) {
                progressBar.show();
                try {
                    GoogleSignInAccount googleSignInAccount = googleSignInAccountTask.getResult(ApiException.class);

                    if (googleSignInAccount != null) {
                        AuthCredential authCredential = GoogleAuthProvider
                                .getCredential(googleSignInAccount.getIdToken(), null);

                        //Makes the user signin with the Registred Gmail
                        firebaseAuth.signInWithCredential(authCredential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {

                                    //Firebase database path to store user details
                                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                                    DatabaseReference myRef = database.getReference().child("users");
                                    HashMap<String, String> user_details = new HashMap<>();


                                    //Access the user details from the registered Gmail
                                    String id = googleSignInAccount.getId().toString();
                                    String name = googleSignInAccount.getDisplayName().toString();
                                    String mail = googleSignInAccount.getEmail().toString();
                                    String pic = googleSignInAccount.getPhotoUrl().toString();
                                    String adharNumber = adharNumberEditTxt.getText().toString().trim();


                                    //Stores the above details in a HasMap
                                    user_details.put("userId", id);
                                    user_details.put("userName", name);
                                    user_details.put("mail", mail);
                                    user_details.put("profilePic", pic);
                                    user_details.put("adharNumber", adharNumber);


                                    //Add the Details HashMap to the Firebase database
                                    myRef.child(id).setValue(user_details).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {

                                                //Navigates to MainActivity if userDetails inserted to firebase Database
                                                progressBar.cancel();
                                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                startActivity(intent);
                                            }
                                        }
                                    });
                                }
                            }
                        });
                    }

                } catch (ApiException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
package com.h5200006_burak_oztas_yonlendirilmiscalisma;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SettingsActivity extends AppCompatActivity {

    EditText userPassword, userConfPassword, userEmail;
    Button btnSaveNewPass, btnSendNewEmail;
    private Toolbar settingsToolbar;

    private FirebaseUser firebaseUser;
    private FirebaseAuth auth;


    private String newPassword = "", confNewPassword = "", userNewEmail = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // init firebase auth
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();


        // Toolbar'a özellikler verdim.
        settingsToolbar = findViewById(R.id.settings_toolbar);
        setSupportActionBar(settingsToolbar);
        getSupportActionBar().setTitle("Account Settings");

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);

        userPassword = findViewById(R.id.edtNewPassword);
        userConfPassword = findViewById(R.id.edtConfNewPassword);
        userEmail = findViewById(R.id.edtNewMail);


        btnSaveNewPass = findViewById(R.id.btnSaveNewPass);
        btnSendNewEmail = findViewById(R.id.btnSendUpdateEmail);

        btnSaveNewPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                changePassword();
            }
        });


        btnSendNewEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                updateEmail();
            }
        });
    }

    private void updateEmail() {

        userNewEmail = userEmail.getText().toString().trim();

        if (TextUtils.isEmpty(userNewEmail)) {
            userEmail.setError("Required Field");
            return;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(userNewEmail).matches()) {
            userEmail.setError("Invalid email format");
            return;
        } else {
            updateNewEmail();
        }


    }

    private void updateNewEmail() {


        firebaseUser.updateEmail(userNewEmail).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(SettingsActivity.this, "Email updated", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(SettingsActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });

    }


    private void changePassword() {

        //get Data
        newPassword = userPassword.getText().toString().trim();
        confNewPassword = userConfPassword.getText().toString().trim();

        // validate Data

        if (TextUtils.isEmpty(newPassword)) {
            // password is empty
            userPassword.setError("Required field. Please enter your new password");
            return;
        } else if (TextUtils.isEmpty(confNewPassword)) {
            // Confirm password is empty
            userConfPassword.setError("Required field. Please confirm your new password");
            return;
        } else if (!newPassword.equals(confNewPassword)) {
            // Passwords are not same
            userConfPassword.setError("Password does not match");
            return;
        } else {
            updateUserPassword();
        }

    }

    private void updateUserPassword() {
        firebaseUser.updatePassword(newPassword).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(SettingsActivity.this, "Password Updated", Toast.LENGTH_SHORT).show();

                // user must go LoginActivity after update password
                Intent loginPage = new Intent(SettingsActivity.this, LoginActivity.class);
                startActivity(loginPage);
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Toast.makeText(SettingsActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }
}
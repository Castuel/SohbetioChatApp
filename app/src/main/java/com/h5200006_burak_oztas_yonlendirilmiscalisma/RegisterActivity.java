package com.h5200006_burak_oztas_yonlendirilmiscalisma;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    public static final String TAG = "TAG";

    // Bu kısımda componentlerimi tanımladım.
    TextInputEditText username, email, password, confPassword;
    Button btnRegister;
    TextView txtGoToSignIn;

    private String userID;

    // FirebaseAuth classsını tanımladım
    private FirebaseAuth firebaseAuth;


    // Databasereference classını kullanarak rootref değişkenini tanımladım.
    private DatabaseReference rootRef;


    // progress dialog
    private ProgressDialog progressDialog;


    // Kullanıcının email ve şifresini kaydedebilmek için değişkenleri tanımladım.
    private String regUsername = "", regEmail = "", regPassword = "", regConfPassword = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // init firebase auth
        firebaseAuth = FirebaseAuth.getInstance();
        rootRef = FirebaseDatabase.getInstance().getReference();

        // progress dialog changes
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setMessage("Please wait");
        progressDialog.setMessage("Creating your account...");
        progressDialog.setCanceledOnTouchOutside(false);

        initalizeFields();

        // SignIn textview'ine tıklama özelliği verdim.
        txtGoToSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //SignIn'e tıklandığında LoginActivity'e yönlendiren fonksiyonu çağırdım.
                sendUserToLoginScreen();

            }
        });


        // Register butonuno tıklama özelliği verdim.
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Register butonuna tıklandığına dataları valide eden fonksiyonu çağırdım.
                validateData();
            }
        });
    }


    // Geri tuşuna basıldığına önceki activity'ye geri dönmemesini sağladım.
    @Override
    public void onBackPressed() {

    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    private void validateData() {
        // Kullanıcının verilerini çektim.

        regEmail = email.getText().toString().trim();
        regPassword = password.getText().toString().trim();
        regConfPassword = confPassword.getText().toString().trim();


        if (TextUtils.isEmpty(regEmail)) {
            // Email alanı boş olamaz.
            email.setError("Enter your email address");
            return;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(regEmail).matches()) {
            // Email formatı geçersiz.
            email.setError("Invalid email format");
            return;
        } else if (TextUtils.isEmpty(regPassword)) {
            // Şifre alanı boş olamaz.
            password.setError("Enter your password");
            return;
        } else if (regPassword.length() < 6) {
            // Şifre uzunluğu 6 karakterden kısa olamaz.
            password.setError("Password must be atleast 6 character long");
            return;
        } else if (TextUtils.isEmpty(regConfPassword)) {
            // Şifre doğrulama alanı boş geçilemez.
            confPassword.setError("Please confirm your password");
            return;
        } else if (!regPassword.equals(regConfPassword)) {
            // Şifreler uyuşmuyor.
            confPassword.setError("Password does not match");
            return;
        } else {
            // Doğrulama başarılı. Kayıt olma fonksiyonunu çağırdım.
            firebaseSignUp();
        }
    }

    // Valide başatılı olduğunda firebase ile kayıt olma fonksiyonunu oluşturdum.
    private void firebaseSignUp() {

        progressDialog.show();

        firebaseAuth.createUserWithEmailAndPassword(regEmail, regPassword)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {

                        // Kayıt başarılı.
                        progressDialog.dismiss();

                        // Mevcut kullanıcının veritabanında bulunan Unique id'sini değişkene atadım.
                        userID = firebaseAuth.getCurrentUser().getUid();

                        // Veritabanında Users adlı tabloyu oluşturdum.
                        rootRef.child("Users").child(userID).setValue("");

                        // Kullanıcyı uygulama ana ekranına gönderen fonksiyonu çağırdım.

                        sendUserToHomeScreen();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        // sign up failed
                        progressDialog.dismiss();
                        Toast.makeText(RegisterActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });

    }

    private void initalizeFields() {
        // Layout dosyasındaki componentleri tanımladığım değişkenlerle eşleştirdim
        username = findViewById(R.id.edtSignEmail);
        email = findViewById(R.id.edtSignPassword);
        password = findViewById(R.id.edtRegisterPassword);
        confPassword = findViewById(R.id.edtRegisterConfPass);
        txtGoToSignIn = findViewById(R.id.txtGoToSignIn);
        btnRegister = findViewById(R.id.btnRegister);
    }

    private void sendUserToHomeScreen() {
        Intent homeScreenIntent = new Intent(RegisterActivity.this, HomeScreenActivity.class);
        homeScreenIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(homeScreenIntent);
        finish();
    }

    private void sendUserToLoginScreen() {
        Intent loginIntent = new Intent(RegisterActivity.this, LoginActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        finish();
    }
}
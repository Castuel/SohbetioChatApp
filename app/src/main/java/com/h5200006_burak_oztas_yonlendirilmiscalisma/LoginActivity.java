package com.h5200006_burak_oztas_yonlendirilmiscalisma;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
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

public class LoginActivity extends AppCompatActivity {

    // Bu kısımda componentlerimi tanımladım.
    TextInputEditText userEmail, userPassword;
    TextView txtCreateAcc, txtForgotPass;
    Button btnLogin;

    // FirebaseAuth classsını tanımladım
    private FirebaseAuth firebaseAuth;

    // Kullanıcının email ve şifresini kaydedebilmek için değişkenleri tanımladım.
    private String email = "", password = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // FirebaseAuth nesnesini initilaize ettim.
        firebaseAuth = FirebaseAuth.getInstance();

        initializeFields();

        // Kullanıcının giriş yapıp yapmadığını kontrol eden fonksiyonu çağırdım
        checkUser();

        //  ForgotPass textviewine tıklama özelliği verdim
        txtForgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // ForgotPass'e tıklandığında ResetActivity ekranını açan intent tanımladım.
                Intent resetScreen = new Intent(LoginActivity.this, ResetActivity.class);
                startActivity(resetScreen);
            }
        });


        // Login butonuna tıklama özelliği verdim.
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Login butonuna tıklandığında girilen değerleri valide eden fonksiyonu çağırdım.
                validateData();

            }
        });

        // CreateAcc textviewine tıklama özelliği verdim.
        txtCreateAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Create Acc'ye tıklandığında SignUpActivity ekranını açan intent tanımladım.
                Intent registerScreen = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(registerScreen);
            }
        });

    }


    // Geri tuşuna basıldığında hiçbirşey yapmamasını sağladım.
    @Override
    public void onBackPressed() {

    }


    // Kullanıcının girdiği verileri valide eden fonksiyon
    private void validateData() {

        // Get Data
        email = userEmail.getText().toString().trim();
        password = userPassword.getText().toString().trim();

        // Validate Data
        if (TextUtils.isEmpty(email)) {
            // email can not be empty
            userEmail.setError("Enter your email address");
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            // email format is invalid , don't proceed further
            userEmail.setError("Invalid email format");
        } else if (TextUtils.isEmpty(password)) {
            // password is empty
            userPassword.setError("Enter your password");
        } else {
            // validate success
            firebaseLogin();
        }
    }

    // Validate işlemi tamamlandığında kullanıcının uygulamaya firebase ile giriş yapabileceği fonksiyonu tanımladım.
    private void firebaseLogin() {

        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        // Giriş başarılı.

                        // Kullanıcı giriş yaptığında anasayfaya yönlendiren fonksiyonu çağırdım.

                        sendUserToHomeScreen();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Giriş başarısız.
                        Toast.makeText(LoginActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });


    }

    private void initializeFields() {
        // Layout dosyasındaki componentleri tanımladığım değişkenlerle eşleştirdim
        btnLogin = findViewById(R.id.btnLogin);
        txtCreateAcc = findViewById(R.id.txtCreateAcc);
        txtForgotPass = findViewById(R.id.txtForgotPass);

        userEmail = findViewById(R.id.edtSignEmail);
        userPassword = findViewById(R.id.edtSignPassword);
    }

    // Kullanıcın uygulamaya giriş yapıp yapmadığını kontrol eden fonksiyon.
    private void checkUser() {
        // check user is logged in , if logged in open HomeScreen

        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser != null) {
            // user logged in , start HomeScreen activity

            sendUserToHomeScreen();
        }
    }

    // Kullanıcıyı uygulama ana sayfasına gönderen fonksiyon.
    private void sendUserToHomeScreen() {
        Intent homeScreenIntent = new Intent(LoginActivity.this, HomeScreenActivity.class);

        // Kullanıcı geri tuşuna bastığında  önceki activity'ye geri dönmesini engelledim

        homeScreenIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(homeScreenIntent);
        finish();
    }
}
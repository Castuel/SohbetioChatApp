package com.h5200006_burak_oztas_yonlendirilmiscalisma;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

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

public class ResetActivity extends AppCompatActivity {

    // Bu kısımda componentlerimi tanımladım.
    EditText resetPassword;
    Button btnSendResetMail;

    private String email = "";
    private Toolbar resetToolBar;

    // FirebaseAuth classsını tanımladım
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset);

        // FirebaseAuth nesnesini initilaize ettim.
        firebaseAuth = FirebaseAuth.getInstance();

        // Layout dosyasındaki componentleri tanımladığım değişkenlerle eşleştirdim
        resetPassword = findViewById(R.id.edtResetPassword);
        btnSendResetMail = findViewById(R.id.btnSendResetMail);

        resetToolBar = findViewById(R.id.reset_toolbar);
        setSupportActionBar(resetToolBar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        getSupportActionBar().setTitle("Forgot Password");


        // Şifre sıfırlama bağlantısı gönder butonuna tıklama özelliği verdim.
        btnSendResetMail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateEmail();
            }
        });
    }

    private void validateEmail() {
        // Kullanıcının girdiği email'i bir değişkene atadım.
        email = resetPassword.getText().toString().trim();

        // Kullanıcının girdiği verileri doğrulayan kod.

        if (TextUtils.isEmpty(email)) {
            // Email boş geçilemez.
            resetPassword.setError("Email is required");
            return;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            // Email formatı hatalı.
            resetPassword.setError("Invalid email format");
            return;
        } else {
            // Şifre sıfırlama bağlantısı gönderen fonksiyonu çağırdım.
            sendResetLink();
        }

    }

    // Şifre sıfırlama bağlantısı gönderen fonksiyonu tanımladım.
    private void sendResetLink() {

        firebaseAuth.sendPasswordResetEmail(email).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                // Sıfırlama bağlantısı başarıyla gönderildi.
                Toast.makeText(ResetActivity.this, "Reset Email Sent", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                // Şifre sıfırlama bağlantısı gönderilemedi.
                Toast.makeText(ResetActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });


    }
}
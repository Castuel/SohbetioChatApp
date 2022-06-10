package com.h5200006_burak_oztas_yonlendirilmiscalisma;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Bu kısımda CountDownTimer sınıfını kullanarak uygulama ilk açıldığında ekranda uygulama logosunun 3 saniye gözükmesini sağladım.

        CountDownTimer countDownTimer = new CountDownTimer(3000, 1000) {
            @Override
            public void onTick(long l) {

            }

            @Override
            public void onFinish() {

                // Bu kısımda splash ekran sonlandığında LoginActivity classına yönlendiren bir intent tanımladım.

                Intent loginIntent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(loginIntent);
                finish();

            }
        }.start();
    }
}
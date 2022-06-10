package com.h5200006_burak_oztas_yonlendirilmiscalisma;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import com.h5200006_burak_oztas_yonlendirilmiscalisma.Fragments.ChatsFragment;
import com.h5200006_burak_oztas_yonlendirilmiscalisma.Fragments.FriendsFragment;
import com.h5200006_burak_oztas_yonlendirilmiscalisma.Fragments.GroupsFragment;
import com.h5200006_burak_oztas_yonlendirilmiscalisma.Fragments.RequestsFragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class HomeScreenActivity extends AppCompatActivity {

    // Bu kısımda componentlerimi tanımladım.
    private Toolbar mToolbar;
    private ViewPager viewPager;
    private TabLayout mTabLayout;
    private TabsAccessorAdapter mTabsAccessorAdapter;

    private String currentUserID;

    // FirebaseAuth classsını tanımladım.
    private FirebaseAuth firebaseAuth;
    // Veritabanı referans classını tanımladım.
    private DatabaseReference rootRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        // Toolbar'a özellikler verdim.
        mToolbar = findViewById(R.id.home_screen_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Sohbetio");

        viewPager = findViewById(R.id.home_screen_view_pager);
        mTabsAccessorAdapter = new TabsAccessorAdapter(getSupportFragmentManager());
        viewPager.setAdapter(mTabsAccessorAdapter);

        mTabLayout = findViewById(R.id.tab_layout);
        mTabLayout.setupWithViewPager(viewPager);


        rootRef = FirebaseDatabase.getInstance().getReference();
        // FirebaseAuth nesnesini initilaize ettim.
        firebaseAuth = FirebaseAuth.getInstance();
        // Mevcut kullanıcının eşsiz id'sini çektim.
        currentUserID = firebaseAuth.getCurrentUser().getUid();


    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        if (firebaseUser == null) {
            // Kullanıcı boşsa kullanıcıyı giriş ekranına yönlendiren fonksiyonu çağpırdım.
            sendUserToLoginActivity();
        } else {

            // Kullanıcının durumunu online olarak değiştirdim.
            updateUserStatus("Online");
            // Kullanıcı giriş yapmışsa , kullanıcı adı olup olmadığını kontrol eden fonksiyonu çağırdım.
            verifyUserExistance();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        // FirebaseUser classını kullanarak mevcut kullanıcıyı çektim.
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        if (firebaseUser != null) {
            // Uygulama alta alındığında kullanıcının durumunu offline olarak güncellledim.
            updateUserStatus("Offline");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // FirebaseUser classını kullanarak mevcut kullanıcıyı çektim.
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        // Uygulama kapatıldığında kullanıcının durumunu offline olarak güncellledim.
        if (firebaseUser != null) {
            updateUserStatus("Offline");
        }
    }

    @Override
    public void onBackPressed() {

    }


    // Kullanıcı adı olup olmadığını kontrol eden fonksiyonu tanımladım.

    private void verifyUserExistance() {

        String currentUserId = firebaseAuth.getCurrentUser().getUid();
        rootRef.child("Users").child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if ((snapshot.child("username").exists())) {
                    //
                } else {
                    // Kullanıcı adı olmayan kullanıcıyı , kullanıcı adını belirlemesi için profil ekranına yönlendiren fonksiyon.
                    sendUserToProfileActivity();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    // Kullanıcıyı giriş yapma ekranına yönlendiren fonksiyonu tanımladım.
    private void sendUserToLoginActivity() {

        Intent loginScreen = new Intent(HomeScreenActivity.this, LoginActivity.class);
        // Kullanıcı geri tuşuna bastığında  önceki activity'ye geri dönmesini engelledim
        loginScreen.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginScreen);
        finish();

    }

    // Kullanıcıyı profil ekranına yönlendiren fonksiyonu tanımladım.
    private void sendUserToProfileActivity() {
        Intent profileScreen = new Intent(HomeScreenActivity.this, ProfileActivity.class);
        startActivity(profileScreen);

    }


    // Kullanıcıyı ayarlar ekranına yönlendiren fonksiyonu tanımladım.
    private void sendUserToSettingsActivity() {
        Intent settingsScreen = new Intent(HomeScreenActivity.this, SettingsActivity.class);
        startActivity(settingsScreen);
    }

    // Kullanıcıyı yeni kişiler bul ekranına yönlendiren fonksiyonu tanımladım.
    private void sendUserToFindFriendsActivity() {
        Intent findFriendsScreen = new Intent(HomeScreenActivity.this, FindFriendsActivity.class);
        startActivity(findFriendsScreen);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_option, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        super.onOptionsItemSelected(item);

        if (item.getItemId() == R.id.menu_profile) {
            // Profil butonuna tıklandığında kullanıcıyı Profil sayfasına yönlendiren kod.
            sendUserToProfileActivity();
        }
        if (item.getItemId() == R.id.menu_settings) {
            // Ayarlar butonuna tıklandığında kullanıcıyı Ayarlar sayfasına yönlendiren kod.
            sendUserToSettingsActivity();
        }
        if (item.getItemId() == R.id.menu_logout) {
            // Logout butonuna tıklandığında kullanıcıya uygulamadan çıkış yapmak isteyip istemediğini soran dialog.

            Dialog dialog = new Dialog(HomeScreenActivity.this, R.style.Dialoge);
            dialog.setContentView(R.layout.dialog_layout);

            TextView btnYes, btnNo;

            btnYes = dialog.findViewById(R.id.btnPositiveLogout);
            btnNo = dialog.findViewById(R.id.btnNegativeLogout);

            btnYes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Yes butonuna tıklandığın kullanıcının oturumunu kapatan ve kullanıcıyı LoginActivity'ye yönlendiren fonksiyonları tanımladım.

                    updateUserStatus("Offline");
                    firebaseAuth.signOut();
                    sendUserToLoginActivity();

                }
            });


            btnNo.setOnClickListener(new View.OnClickListener() {
                @Override
                // No butonuna tıklandığına dialog'u kapatan kod bloğu.
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });
            dialog.show();

        }
        if (item.getItemId() == R.id.menu_search) {
            Toast.makeText(this, "Action Search", Toast.LENGTH_SHORT).show();
        }
        if (item.getItemId() == R.id.menu_createGroup) {
            // Yeni grup oluştur butonuna tıklandığında kullanıcının yeni grup oluşturabilmesi için gerekli olan fonksiyonu çağırdım.
            requestNewGroup();
        }

        if (item.getItemId() == R.id.menu_findFriends) {
            // Arkadaş bul butonuna tıklandığında kullanıcıyı Arkadaş Bul sayfasına yönlendiren kod.
            sendUserToFindFriendsActivity();
        }
        return true;
    }


    // Yeni grup oluşturmaya yarayan fonksiyonu tanımladım.
    private void requestNewGroup() {
        // Alertdialog nesnesi oluşturdum.
        AlertDialog.Builder builder = new AlertDialog.Builder(HomeScreenActivity.this, R.style.AlertDialog);
        builder.setTitle("Enter Group Name :");

        final EditText groupNameField = new EditText(HomeScreenActivity.this);
        groupNameField.setHint("e.g Sohbetio Friends");
        builder.setView(groupNameField);

        // Oluştur butonuna tıklandığında çalışacak kod bloğu.
        builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Kullanıcının girdiği grup adını bir değişkene atadım.
                String groupName = groupNameField.getText().toString();

                if (TextUtils.isEmpty(groupName)) {
                    // Grup adı boş geçilemez.
                    Toast.makeText(HomeScreenActivity.this, "Please write Group Name...", Toast.LENGTH_SHORT).show();
                } else {
                    // Kullanıcının girdiği grup adı ile yeni grup oluşturan fonksiyonu çağırdım.
                    createNewGroup(groupName);
                }
            }
        });

        // İptal butonuna tıklandığına çalışacak kod bloğu.
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });

        builder.show();
    }


    // Yeni grup oluşturmaya yarayan fonksiyonu tanımladım.
    private void createNewGroup(final String groupName) {
        // Veritabanında gruplar adlı tabloyu oluşturdum.
        rootRef.child("Groups").child(groupName).setValue("")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // Grup oluşturma başarılı olduğunda çalışacak kod.
                            Toast.makeText(HomeScreenActivity.this, groupName + " group is Created Successfully...", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    // Kullanıcının durumunu güncelleyen fonksiyonu tanımladım.
    private void updateUserStatus(String state) {
        String saveCurrentTime, saveCurrentDate;
        // Calendar classını tanımladım.
        Calendar calendar = Calendar.getInstance();

        // Mevcut tarihi kaydetmek için oluşturulan nesne.
        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());

        // Mevcut zamanı kaydetmek için oluşturulan nesne.
        SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm a");
        saveCurrentTime = currentTime.format(calendar.getTime());

        // Zaman , tarih ve durum değişkenlerini mapledim.
        HashMap<String, Object> onlineStateMap = new HashMap<>();
        onlineStateMap.put("time", saveCurrentTime);
        onlineStateMap.put("date", saveCurrentDate);
        onlineStateMap.put("state", state);

        // Veritabanında kullanıcılar tablosunun altında kullanıcıDurumu adlı tabloyu tanımladım.
        rootRef.child("Users").child(currentUserID).child("userState")
                .updateChildren(onlineStateMap);

    }
}
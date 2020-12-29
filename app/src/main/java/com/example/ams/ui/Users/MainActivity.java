package com.example.ams.ui.Users;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.ams.ui.LoginActivity;
import com.example.ams.Model.Users;
import com.example.ams.Prevalent.Prevalent;
import com.example.ams.R;
import com.example.ams.ui.RegisterActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity {

    private Button joinButton;
    private Button loginButton;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        joinButton = (Button) findViewById(R.id.main_join_btn);
        loginButton = (Button) findViewById(R.id.main_login_btn);
        loadingBar = new ProgressDialog(this);

        Paper.init(this);


        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(loginIntent);
            }
        });
        joinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent registerIntent = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(registerIntent);

            }
        });

        String UserPhoneKey = Paper.book().read(Prevalent.UserPhoneKey);
        String UserPasswordKey = Paper.book().read(Prevalent.UserPasswordKey);

        if (UserPhoneKey != "" && UserPasswordKey != "") {

            if (!TextUtils.isEmpty(UserPhoneKey) && !TextUtils.isEmpty(UserPasswordKey)) {
                ValidateUser(UserPhoneKey, UserPasswordKey);
                loadingBar.setTitle("Вход в приложение");
                loadingBar.setMessage("Пожалуйста, подождите...");
                loadingBar.setCanceledOnTouchOutside(false);
                loadingBar.show();

            }
        }
    }

    private void ValidateUser(final String phone, String password) {
        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();

        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("Users").child(phone).exists()) {
                    Users usersData = dataSnapshot.child("Users").child(phone).getValue(Users.class);
                    if (usersData.getPhone().equals(phone)) {
                        if (usersData.getPassword().equals(phone)) {
                            loadingBar.dismiss();
                            Toast.makeText(MainActivity.this, "Успешный вход!", Toast.LENGTH_SHORT).show();

                            Intent HomeIntent = new Intent(MainActivity.this, HomeActivity.class);
                            startActivity(HomeIntent);
                        } else {
                            loadingBar.dismiss();


                        }
                    }
                } else {
                    loadingBar.dismiss();
                    Toast.makeText(MainActivity.this, "Аккаунт с номером" + phone + "Не существует", Toast.LENGTH_SHORT).show();
                    Intent registerIntent = new Intent(MainActivity.this, RegisterActivity.class);
                    startActivity(registerIntent);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}


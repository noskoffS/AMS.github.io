package com.example.ams.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.ams.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {
    private Button registerBtn;
    private EditText usernameInput, phoneInput, passwordInput;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        registerBtn=(Button) findViewById(R.id.registern_btn);
        usernameInput=(EditText) findViewById(R.id.register_username_input);
        passwordInput=(EditText) findViewById(R.id.register_password_input);
        phoneInput=(EditText) findViewById(R.id.register_phone_input);
        loadingBar = new ProgressDialog(this);

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateAccount();
            }
        });
    }

    private void CreateAccount() {
        String username = usernameInput.getText().toString();
        String phone = phoneInput.getText().toString();
        String password = passwordInput.getText().toString();

        if (TextUtils.isEmpty(username))
        {
            Toast.makeText(this,"Введите имя", Toast.LENGTH_SHORT).show();
        }

       else if (TextUtils.isEmpty(phone))
        {
            Toast.makeText(this,"Введите номер телефона", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(password))
        {
            Toast.makeText(this,"Введите пароль", Toast.LENGTH_SHORT).show();
        }
        else
        {
            loadingBar.setTitle("Создание аккаунта");
            loadingBar.setMessage("Пожалуйста, подождите...");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();
            
            ValidatePhone(username,phone,password);
        }
    }

    private void ValidatePhone(final String username, final String phone, final String password) {
        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();

        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!(dataSnapshot.child("Users").child(phone).exists()))
                {
                    HashMap<String, Object> userDatamap = new HashMap<>();
                    userDatamap.put("phone", phone);
                    userDatamap.put("name", username);
                    userDatamap.put("password", password);

                    RootRef.child("Users").child(phone).updateChildren(userDatamap)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        loadingBar.dismiss();
                                        Toast.makeText(RegisterActivity.this, "Регистрация прошла успешно.", Toast.LENGTH_SHORT).show();
                                        Intent loginIntent = new Intent(RegisterActivity.this, LoginActivity.class);
                                        startActivity(loginIntent);
                                    }
                                    else {

                                        loadingBar.dismiss();
                                        Toast.makeText(RegisterActivity.this, "Ошибка", Toast.LENGTH_SHORT).show();
                                    }

                                }
                            });
                }
                else {
                    Toast.makeText(RegisterActivity.this, "Номер"+phone+"Уже зарегестрирован", Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                    Intent loginIntent = new Intent(RegisterActivity.this, LoginActivity.class);
                    startActivity(loginIntent);
            }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}
package com.gamiro.covidjournal;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.gamiro.covidjournal.adapters.IntroViewPagerAdapter;
import com.gamiro.covidjournal.helpers.AppUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class StartupActivity extends AppCompatActivity {

    private Button btnLogin;
    private Button btnRegister;
    private TextView textIntro;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startup);

        btnLogin = findViewById(R.id.btn_to_login);
        btnRegister = findViewById(R.id.btn_to_register);
        textIntro = findViewById(R.id.tv_to_intro);

        mAuth = FirebaseAuth.getInstance();

        btnLogin.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
        });

        btnRegister.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
            startActivity(intent);
        });

        textIntro.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), IntroActivity.class);
            intent.putExtra(AppUtil.COMES_FROM_STARTUP, true);
            startActivity(intent);
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
            startActivity(intent);
        }
    }
}

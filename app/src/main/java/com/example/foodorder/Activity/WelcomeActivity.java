package com.example.foodorder.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.example.foodorder.Common.Common;
import com.example.foodorder.MainActivity;
import com.example.foodorder.R;
import com.example.foodorder.Service.ListenOrder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class WelcomeActivity extends AppCompatActivity {
    private static final int SPLASH_DURATION = 2000;
    FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        Intent service = new Intent(WelcomeActivity.this, ListenOrder.class);
        startService(service);

        // Hiển thị màn hình Splash trong khoảng thời gian SPLASH_DURATION
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Chuyển đến màn hình chính

                if (currentUser != null && (currentUser.isEmailVerified() || isPhoneVerified())) {
                    if (getIntent() != null) {
                        String fragment = getIntent().getStringExtra("openFragment");
                        Common.saveUserInfor(WelcomeActivity.this, MainActivity.class, fragment);
                    }
                } else {
                    startActivity(new Intent(WelcomeActivity.this, LoginActivity.class));
                    finish();
                }
                // Đóng SplashActivity để ngăn người dùng quay lại màn hình Splash bằng nút Back
            }


        }, SPLASH_DURATION);
    }

    protected boolean isPhoneVerified() {
        return !FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber().isEmpty();
    }


}
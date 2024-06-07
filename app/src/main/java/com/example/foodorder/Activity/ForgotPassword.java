package com.example.foodorder.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.foodorder.R;
import com.example.foodorder.ReusableCodeForAll;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPassword extends AppCompatActivity {

    EditText editTextValue;
    Button btnChange, btnSend;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        editTextValue = findViewById(R.id.value);
        btnChange = findViewById(R.id.another);
        btnSend = findViewById(R.id.send);
        firebaseAuth = FirebaseAuth.getInstance();
    }

    public void otherWay(View view) {
        startActivity(new Intent(ForgotPassword.this, LoginActivity.class));
        finish();
    }

    public void send(View view) {
        final ProgressDialog mDialog = new ProgressDialog(ForgotPassword.this);
        mDialog.setCancelable(false);
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.setMessage("Đang gửi mail ...");
        mDialog.show();
        firebaseAuth.sendPasswordResetEmail(editTextValue.getText().toString().trim())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            mDialog.dismiss();
                            Toast.makeText(ForgotPassword.this, "Đã gửi mail thành công", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(ForgotPassword.this, LoginActivity.class));
                            finish();
                        } else {
                            mDialog.dismiss();
                            ReusableCodeForAll.showAlert(ForgotPassword.this, "Error", task.getException().getMessage());
                        }
                    }
                });
    }
}
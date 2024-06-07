package com.example.foodorder.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.foodorder.R;
import com.example.foodorder.ReusableCodeForAll;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.List;

public class RegistrationActivity extends AppCompatActivity {
    // Username không dấu, không có 2 ký tự ._- liên tiếp, độ dài từ 5-21 ký tự
    // Username bắt đầu và kết thúc là ký tự trong a-zA-Z0-9
    public static final String USERNAME_PATTERN =
            "^[a-zA-Z0-9]([._-](?![._-])|[a-zA-Z0-9]){3,18}[a-zA-Z0-9]$";
    public static final String PHONE_PATTERN = "(03|05|07|08|09|01[2689])([0-9]{8})\\b";

    public static final String EMAIL_PATTERN = "[a-z0-9A-Z._-]+@[a-z]+\\.+[a-z]+";

    public static final String PASS_PATTERN = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{6,}$";
    EditText editTextPhone, editTextPassword, editTextUsername, editTextRepass, editTextEmail;
    Drawable security, visibibility;
    public Boolean isPassVisible, isRePassVisible;
    FirebaseAuth FAuth = FirebaseAuth.getInstance();
    DatabaseReference databaseReference = FirebaseDatabase.getInstance()
            .getReferenceFromUrl("https://food-order-6f686-default-rtdb.firebaseio.com/");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        isPassVisible = false;
        isRePassVisible = false;
        editTextPhone = findViewById(R.id.phone);
        editTextPassword = findViewById(R.id.password);
        editTextRepass = findViewById(R.id.repass);
        editTextUsername = findViewById(R.id.username);
        editTextEmail = findViewById(R.id.email);

        security = getResources().getDrawable(R.drawable.baseline_security_24);
        handleVisible();
    }

    private void handleVisible() {
        editTextPassword.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (editTextPassword.getRight() - editTextPassword.getCompoundDrawables()[2].getBounds().width())) {
                        // Do something here
                        isPassVisible = !isPassVisible;

                        if (isPassVisible) {
                            visibibility = getResources().getDrawable(R.drawable.baseline_remove_red_eye_24);
                            editTextPassword.setTransformationMethod(null);
                        } else {
                            visibibility = getResources().getDrawable(R.drawable.baseline_visibility_off_24);
                            editTextPassword.setTransformationMethod(new PasswordTransformationMethod());
                        }

                        editTextPassword.setCompoundDrawablesRelativeWithIntrinsicBounds(security, null, visibibility, null);
                        editTextPassword.setSelection(editTextPassword.getText().length());
                        return true;
                    }
                }
                return false;
            }
        });
        editTextRepass.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (editTextRepass.getRight() - editTextRepass.getCompoundDrawables()[2].getBounds().width())) {
                        // Do something here
                        isRePassVisible = !isRePassVisible;

                        if (isRePassVisible) {
                            visibibility = getResources().getDrawable(R.drawable.baseline_remove_red_eye_24);
                            editTextRepass.setTransformationMethod(null);
                        } else {
                            visibibility = getResources().getDrawable(R.drawable.baseline_visibility_off_24);
                            editTextRepass.setTransformationMethod(new PasswordTransformationMethod());
                        }

                        editTextRepass.setCompoundDrawablesRelativeWithIntrinsicBounds(security, null, visibibility, null);
                        editTextRepass.setSelection(editTextRepass.getText().length());
                        return true;
                    }
                }
                return false;
            }
        });
    }

    public void login(View view) {
        startActivity(new Intent(RegistrationActivity.this, LoginActivity.class));
    }

    public void register(View view) {
        String phone, password, repass, username, email;
        phone = editTextPhone.getText() != null ? editTextPhone.getText().toString() : null;
        password = editTextPassword.getText().toString();
        username = editTextUsername.getText().toString();
        repass = editTextRepass.getText().toString();
        email = editTextEmail.getText().toString();

        validateInput(phone, username, password, repass, email);

    }

    public void validateInput(String phone, String username, String password, String repass, String email) {
        if (password.isEmpty() || repass.isEmpty() || username.isEmpty()) {
            Toast.makeText(RegistrationActivity.this, "Điền đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        } else if (!password.equals(repass)) {
            Toast.makeText(RegistrationActivity.this, "Nhập lại mật khẩu không chính xác", Toast.LENGTH_SHORT).show();
            return;
        } else if (!phone.matches(PHONE_PATTERN)) {
            System.out.println(!phone.matches(PHONE_PATTERN));
            Toast.makeText(RegistrationActivity.this, "Số điện thoại không hợp lệ", Toast.LENGTH_SHORT).show();
            return;
        } else if (!email.matches(EMAIL_PATTERN)) {
            Toast.makeText(RegistrationActivity.this, "Email không hợp lệ", Toast.LENGTH_SHORT).show();
            return;
        } else if (!username.matches(USERNAME_PATTERN)) {
            Toast.makeText(RegistrationActivity.this, "Tên người dùng không hợp lệ", Toast.LENGTH_SHORT).show();
            return;
        } else if (!password.matches(PASS_PATTERN)) {
            Toast.makeText(RegistrationActivity.this, "Mật khẩu không hợp lệ", Toast.LENGTH_SHORT).show();
            return;
        } else {
            final ProgressDialog mDialog = new ProgressDialog(RegistrationActivity.this);
            mDialog.setCancelable(false);
            mDialog.setCanceledOnTouchOutside(false);
            mDialog.setMessage("Đang xử lý, vui lòng đợi ...");
            mDialog.show();
            FAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        mDialog.dismiss();
                        String userUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                        HashMap<String,Object> hashMap = new HashMap<>();
                        hashMap.put("username", username);
                        hashMap.put("phone","+84" + phone.substring(1));
                        hashMap.put("email", email);
                        hashMap.put("isPhoneVerified", false);
                        FirebaseUser user = FAuth.getCurrentUser();
                        user.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(RegistrationActivity.this);
                                builder.setCancelable(false);
                                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                dialogInterface.dismiss();
                                                if (!phone.isEmpty()) {
                                                    String phoneNumber = "+84" + phone.substring(1);
                                                    Intent intent = new Intent(RegistrationActivity.this, VerifyPhone.class);
                                                    intent.putExtra("phoneNumber", phoneNumber);
                                                    intent.putExtra("require", "linkWith");
                                                    startActivity(intent);
                                                    finish();
                                                } else {
                                                    startActivity(new Intent(RegistrationActivity.this, LoginActivity.class));
                                                    finish();
                                                }
                                            }
                                        }).setTitle("Kiểm tra hòm thư")
                                        .setMessage("Xác thực email để có thể đăng nhập bằng email").show();
                                databaseReference.child("User").child(userUID).setValue(hashMap);
                            }
                        });
                    } else {
                        ReusableCodeForAll.showAlert(RegistrationActivity.this, "Error", task.getException().getMessage());
                    }
                }

            });

        }
    }
}
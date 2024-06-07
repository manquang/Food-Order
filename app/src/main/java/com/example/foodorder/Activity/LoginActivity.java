package com.example.foodorder.Activity;

import static com.example.foodorder.Activity.RegistrationActivity.EMAIL_PATTERN;
import static com.example.foodorder.Activity.RegistrationActivity.PASS_PATTERN;
import static com.example.foodorder.Activity.RegistrationActivity.PHONE_PATTERN;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.foodorder.Common.Common;
import com.example.foodorder.MainActivity;
import com.example.foodorder.Model.User;
import com.example.foodorder.R;
import com.example.foodorder.ReusableCodeForAll;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    Boolean method, isPassVisible;
    EditText editTextValue, editTextPassword;
    Drawable email, phone, security, visibibility;
    Button anotherLogin;
    FirebaseAuth FAuth;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance()
            .getReferenceFromUrl("https://food-order-6f686-default-rtdb.firebaseio.com/");



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        FirebaseAuth.getInstance().signOut();

        method = true;
        isPassVisible = false;
        editTextValue = findViewById(R.id.value);
        editTextPassword = findViewById(R.id.password);
        editTextPassword.setVisibility(View.INVISIBLE);
        anotherLogin = findViewById(R.id.anotherLogin);
        FAuth = FirebaseAuth.getInstance();

        security = getResources().getDrawable(R.drawable.baseline_security_24);
        email = getResources().getDrawable(R.drawable.baseline_email_24);
        phone = getResources().getDrawable(R.drawable.baseline_local_phone_24);

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
    }

    public void register(View view) {
        startActivity(new Intent(LoginActivity.this, RegistrationActivity.class));
        finish();
    }

    public void login(View view) {
        String value, password;
        value = String.valueOf(editTextValue.getText()).trim();
        password = String.valueOf(editTextPassword.getText()).trim();

        if (method) {
            validatePhoneInput(value);
        } else {
            validateEmailInput(value, password);
        }
    }

    public void otherWay(View view) {
        method = !method;
        if (method) {
            editTextValue.setCompoundDrawablesRelativeWithIntrinsicBounds(phone, null, null, null);
            editTextValue.setHint("Số điện thoại");
            editTextPassword.setVisibility(View.INVISIBLE);

            anotherLogin.setText("Email");
        } else {
            editTextValue.setCompoundDrawablesRelativeWithIntrinsicBounds(email, null, null, null);
            editTextValue.setHint("Email");
            editTextPassword.setVisibility(View.VISIBLE);

            anotherLogin.setText("Số điện thoại");
        }
    }

    public void validateEmailInput(String email, String password) {
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(LoginActivity.this, "Điền đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        } else if (!email.matches(EMAIL_PATTERN)) {
            Toast.makeText(LoginActivity.this, "Email không hợp lệ", Toast.LENGTH_SHORT).show();
            return;
        } else if (!password.matches(PASS_PATTERN)) {
            Toast.makeText(LoginActivity.this, "Mật khẩu không hợp lệ", Toast.LENGTH_SHORT).show();
            return;
        } else {
            final ProgressDialog mDialog = new ProgressDialog(LoginActivity.this);
            mDialog.setCancelable(false);
            mDialog.setCanceledOnTouchOutside(false);
            mDialog.setMessage("Đang xử lý, vui lòng đợi ...");
            mDialog.show();
            FAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        mDialog.dismiss();
                        if (FAuth.getCurrentUser().isEmailVerified()) {
                            mDialog.dismiss();
                            Common.saveUserInfor(LoginActivity.this, MainActivity.class);
                        } else {
                            mDialog.dismiss();
                            ReusableCodeForAll.showAlert(LoginActivity.this, "Xác thực lỗi", "Bạn phải xác thực email. Hãy kiểm tra hòm thư");
                        }
                    } else {
                        mDialog.dismiss();
                        ReusableCodeForAll.showAlert(LoginActivity.this, "Error", task.getException().getMessage());
                    }
                }
            });
        }
    }

    public void validatePhoneInput(String phone) {
        if (phone.isEmpty()) {
            Toast.makeText(LoginActivity.this, "Hãy nhập số điện thoại", Toast.LENGTH_SHORT).show();
            return;
        } else if (!phone.matches(PHONE_PATTERN)) {
            Toast.makeText(LoginActivity.this, "Số điện thoại không hợp lệ", Toast.LENGTH_SHORT).show();
            return;
        } else {
            String phoneNumber = "+84" + phone.substring(1);
            databaseReference.child("User").orderByChild("phone").equalTo(phoneNumber)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {

                                for (DataSnapshot user : snapshot.getChildren()) {
                                    if (user.child("isPhoneVerified").getValue(Boolean.class)) {
                                        Intent intent = new Intent(LoginActivity.this, VerifyPhone.class);
                                        intent.putExtra("phoneNumber", phoneNumber);
                                        intent.putExtra("require", "signIn");
                                        startActivity(intent);
                                    } else {
                                        Toast.makeText(LoginActivity.this, "Số điện thoại chưa được xác thực", Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                }

                            } else {
                                Toast.makeText(LoginActivity.this, "Số điện thoại chưa đăng ký", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
        }
    }

    public void forgotPassWord(View view) {
        startActivity(new Intent(LoginActivity.this, ForgotPassword.class));
    }
}
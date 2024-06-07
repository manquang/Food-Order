package com.example.foodorder.Common;


import android.app.Activity;
import android.content.Intent;

import androidx.annotation.NonNull;


import com.example.foodorder.Model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Common {
    public static User currentUser;
    public static final String DELETE = "Delete";
    public static String convertCodeToStatus(String status) {
        if(status.equals("0")) {
            return "Đã đặt";
        } else if (status.equals("1")) {
            return "Đang giao";
        } else {
            return "Đã giao";
        }
    }
    public static void saveUserInfor(Activity resourceActivity, Class<?> destinationActivity, String startFragment) {
        String Uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseDatabase.getInstance().getReference("User")
                .child(Uid).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Common.currentUser = snapshot.getValue(User.class);
                        Intent intent = new Intent(resourceActivity, destinationActivity);
                        intent.putExtra("openFragment", startFragment);
                        resourceActivity.startActivity(intent);
                        resourceActivity.finish();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    public static void saveUserInfor(Activity resourceActivity, Class<?> destinationActivity) {
        String Uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseDatabase.getInstance().getReference("User")
                .child(Uid).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Common.currentUser = snapshot.getValue(User.class);
                        Intent intent = new Intent(resourceActivity, destinationActivity);
                        resourceActivity.startActivity(intent);
                        resourceActivity.finish();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}

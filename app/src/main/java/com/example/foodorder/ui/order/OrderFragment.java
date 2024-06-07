package com.example.foodorder.ui.order;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodorder.Adapter.OrderAdapter;
import com.example.foodorder.Common.Common;
import com.example.foodorder.Model.Order;
import com.example.foodorder.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class OrderFragment extends Fragment {
    public RecyclerView orderRec;
    OrderAdapter orderAdapter;
    ArrayList<Order> orderList;
    DatabaseReference orderRef;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_order, container, false);

        orderList = new ArrayList<>();

        //Firebase
        orderRef = FirebaseDatabase.getInstance().getReference("Order");
        //View
        orderRec = root.findViewById(R.id.order_list);
        orderAdapter = new OrderAdapter(orderList, getContext());
        orderRec.setAdapter(orderAdapter);
        orderRec.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false));
        orderRec.setHasFixedSize(true);
        orderRec.setNestedScrollingEnabled(false);



        loadOrder(Common.currentUser.getPhone());

        return root;
    }

    private void loadOrder(String phone) {
        orderRef.orderByChild("phone").equalTo(phone)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            Order order = dataSnapshot.getValue(Order.class);
                            orderList.add(order);
                            orderAdapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

}
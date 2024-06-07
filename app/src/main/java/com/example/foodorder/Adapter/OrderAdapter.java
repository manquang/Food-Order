package com.example.foodorder.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodorder.Model.Order;
import com.example.foodorder.R;

import java.util.ArrayList;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder> {
    private ArrayList<Order> listData;
    private Context context;

    public OrderAdapter(ArrayList<Order> listData, Context context) {
        this.listData = listData;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.order_item, parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull OrderAdapter.ViewHolder holder, int position) {
        holder.orderPhone.setText(listData.get(position).getPhone());
        holder.orderAddress.setText(listData.get(position).getAddress());
        holder.orderId.setText(listData.get(position).getOrderId());
        holder.orderStatus.setText(convertCodeToStatus(listData.get(position).getStatus()));
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView orderId, orderStatus, orderPhone, orderAddress;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            orderId = itemView.findViewById(R.id.order_id);
            orderAddress = itemView.findViewById(R.id.order_address);
            orderPhone = itemView.findViewById(R.id.order_phone);
            orderStatus = itemView.findViewById(R.id.order_status);
        }
    }

    public String convertCodeToStatus(String status) {
        if(status.equals("0")) {
            return "Đã đặt";
        } else if (status.equals("1")) {
            return "Đang giao";
        } else {
            return "Đã giao";
        }
    }
}

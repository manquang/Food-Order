package com.example.foodorder.Adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodorder.Model.Category;
import com.example.foodorder.Model.Rating;
import com.example.foodorder.Model.User;
import com.example.foodorder.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class FeedbackAdapter extends RecyclerView.Adapter<FeedbackAdapter.ViewHolder> {

    DatabaseReference ratingRef;
    DatabaseReference userRef;
    Context context;
    private ArrayList<Rating> list;

    public FeedbackAdapter(Context context, ArrayList<Rating> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public FeedbackAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ratingRef = FirebaseDatabase.getInstance().getReference("Rating");
        userRef = FirebaseDatabase.getInstance().getReference("User");
        return new FeedbackAdapter.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull FeedbackAdapter.ViewHolder holder, int position) {
        holder.username.setText(list.get(position).getUserName());
        holder.cmt.setText(list.get(position).getComment());
        holder.ratingBar.setRating(Float.parseFloat(list.get(position).getRateValue()));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView cmt;
        TextView username;

        RatingBar ratingBar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            cmt = itemView.findViewById(R.id.textComment);
            username = itemView.findViewById(R.id.userNameRating);
            ratingBar = itemView.findViewById(R.id.singleRatingBar);
        }
    }
}

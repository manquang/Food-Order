package com.example.foodorder.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodorder.Database.Database;
import com.example.foodorder.Model.Food;
import com.example.foodorder.R;
import com.example.foodorder.ui.home.FoodDetail;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class FoodListAdapter extends RecyclerView.Adapter<FoodListAdapter.ViewHolder> {
    Context context;
    ArrayList<Food> list;
    Database localDB;
    public FoodListAdapter(Context context, ArrayList<Food> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public FoodListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //Local
        localDB = new Database(context);
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.home_vertical_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull FoodListAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        String currentImg = list.get(position).getImage().isEmpty()
                ? "android.resource://" + context.getPackageName() + "/" + R.drawable.default_image
                : list.get(position).getImage();
        Picasso.get()
                .load(currentImg)
                .into(holder.imgView);
        //Favourite
        String foodId = list.get(position).getFoodId();
        if(localDB.isFav(foodId)) {
            holder.fav.setImageResource(R.drawable.baseline_favorite_24);
        }
        //Click to change state of Favourite
        holder.fav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!localDB.isFav(foodId)) {
                    localDB.addFav(foodId);
                    holder.fav.setImageResource(R.drawable.baseline_favorite_24);
                    Toast.makeText(context, list.get(position).getName()+" đã thêm vào Yêu thích", Toast.LENGTH_SHORT).show();
                } else {
                    localDB.removeFav(foodId);
                    holder.fav.setImageResource(R.drawable.baseline_favorite_border_24);
                    Toast.makeText(context, list.get(position).getName()+" đã xóa khỏi Yêu thích", Toast.LENGTH_SHORT).show();
                }
            }
        });
        holder.name.setText(list.get(position).getName());
        holder.price.setText(list.get(position).getPrice() + " đ");
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent foodDetail = new Intent(context, FoodDetail.class);
                foodDetail.putExtra("foodId", list.get(position).getFoodId());
                context.startActivity(foodDetail);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgView, fav;
        TextView name, price;
        CardView cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgView = itemView.findViewById(R.id.verImg);
            name = itemView.findViewById(R.id.name);
            price = itemView.findViewById(R.id.price);
            fav = itemView.findViewById(R.id.favVote);
            cardView = itemView.findViewById(R.id.cardFood);

        }
    }
}

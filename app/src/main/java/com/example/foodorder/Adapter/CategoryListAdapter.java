package com.example.foodorder.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodorder.Model.Category;
import com.example.foodorder.Model.Food;
import com.example.foodorder.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class CategoryListAdapter extends RecyclerView.Adapter<CategoryListAdapter.ViewHolder> {
    UpdateRec updateVerRec;
    Activity activity;
    private ArrayList<Category> list;

    DatabaseReference foodList;

    StorageReference storageReference;

    boolean check = true;
    boolean selected = false;
    int row_index = -1;

    public CategoryListAdapter(UpdateRec updateVerRec, Activity activity, ArrayList<Category> list) {
        this.updateVerRec = updateVerRec;
        this.activity = activity;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        foodList = FirebaseDatabase.getInstance().getReference("Food");
        storageReference = FirebaseStorage.getInstance().getReference("Food");
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.home_horizontal_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Picasso.get().load(list.get(position).getImgUrl()).into(holder.imageView);
        System.out.println(list.get(position).getFileName());
        holder.name.setText(list.get(position).getName());

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<Food> foods = new ArrayList<>();
                foodList.orderByChild("categoryId").equalTo(list.get(position).getFileName())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot foodSnapshot : snapshot.getChildren()) {
                                    Food food = foodSnapshot.getValue(Food.class);
                                    if (food.getImage().isEmpty()) {
                                        storageReference.child(food.getFoodId() + ".png").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {
                                                food.setImage(uri.toString());
                                                updateVerRec.callBack(foods);
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                System.out.println("lỗi" + e);
                                            }
                                        });

                                    }
                                    foods.add(food);
                                }
                                updateVerRec.callBack(foods);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                selected = true;
                row_index = position;
                notifyDataSetChanged();
            }
        });

        if (row_index == position) {
            holder.cardView.setBackgroundResource(R.drawable.change_bg);
        } else {
            holder.cardView.setBackgroundResource(R.drawable.default_bg);
        }
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView name;
        CardView cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.horImg);
            name = itemView.findViewById(R.id.horText);
            cardView = itemView.findViewById(R.id.cardCategory);

        }
    }
}

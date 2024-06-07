package com.example.foodorder.ui.home;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.foodorder.Adapter.FeedbackAdapter;
import com.example.foodorder.Common.Common;
import com.example.foodorder.Model.CartItem;
import com.example.foodorder.Model.Food;
import com.example.foodorder.Model.Rating;
import com.example.foodorder.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import com.stepstone.apprating.AppRatingDialog;
import com.stepstone.apprating.listener.RatingDialogListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class FoodDetail extends AppCompatActivity implements RatingDialogListener {
    TextView foodName, foodPrice, foodDescription, quantityRating;
    ImageView foodImage;
    CollapsingToolbarLayout collapsingToolbarLayout;
    FloatingActionButton btnCart, btnRating;
    RatingBar ratingBar;
    EditText quantityText;
    int quantity;
    String foodId = "";
    Food currentFood;
    DatabaseReference food, ratingdb, cart;
    StorageReference storageReference;

    RecyclerView commentRec;
    FeedbackAdapter feedbackAdapter;
    ArrayList<Rating> rating;
    private boolean isCommentVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_detail);

        //Firebase
        food = FirebaseDatabase.getInstance().getReference("Food");
        ratingdb = FirebaseDatabase.getInstance().getReference("Rating");
        cart = FirebaseDatabase.getInstance().getReference("Cart");
        storageReference = FirebaseStorage.getInstance().getReference("Food");
        //Views
        foodName = findViewById(R.id.food_name);
        foodImage = findViewById(R.id.food_img);
        foodPrice = findViewById(R.id.food_price);
        quantityText = findViewById(R.id.quantity);
        foodDescription = findViewById(R.id.food_description);
        quantityRating = findViewById(R.id.quantityRating);
        quantity = Integer.parseInt(quantityText.getText().toString());

        //Ràng buộc số lượng
        quantityText.setOnEditorActionListener((v, actionId, event) -> {
            // Handle the Enter key press on the keyboard
            if (actionId == EditorInfo.IME_ACTION_DONE || event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                String input = quantityText.getText().toString();
                try {
                    int newNumber = Integer.parseInt(input);
                    if (newNumber >= 1) {
                        quantity = newNumber;
                    } else {
                        quantity = 1;
                        quantityText.setText(String.valueOf(1));
                        Toast.makeText(this, "Số lượng lớn hơn 0", Toast.LENGTH_SHORT).show();
                    }
                } catch (NumberFormatException e) {
                    quantity = 1;
                    quantityText.setText(String.valueOf(1));
                    Toast.makeText(this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                }
                return true;
            }
            return false;
        });
        //Add to cart
        btnCart = findViewById(R.id.btnCart);
        btnCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cart.child(Common.currentUser.getPhone()).child(foodId)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    Integer currentQuantity = snapshot.child("quantity").getValue(Integer.class);
                                    if (currentQuantity != null) {
                                        int newQuantity = currentQuantity + quantity;
                                        cart.child(Common.currentUser.getPhone()).child(foodId)
                                                .child("quantity").setValue(newQuantity);
                                    }
                                } else {
                                    CartItem newItem = new CartItem(
                                            foodId,
                                            currentFood.getName(),
                                            quantity,
                                            currentFood.getPrice(),
                                            currentFood.getDiscount());
                                    cart.child(Common.currentUser.getPhone()).child(foodId)
                                            .setValue(newItem);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                Toast.makeText(FoodDetail.this, "Thêm vào giỏ hàng thành công", Toast.LENGTH_SHORT).show();
            }
        });
        //Rating
        btnRating = (FloatingActionButton) findViewById(R.id.btnRating);
        ratingBar = (RatingBar) findViewById(R.id.ratingBar);
        btnRating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showRatingDialog();
            }
        });


        collapsingToolbarLayout = findViewById(R.id.collapsing);
        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ExpandedAppbar);
        collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.CollapseAppbar);

        if (getIntent() != null) {
            foodId = getIntent().getStringExtra("foodId");
        }
        if (!foodId.isEmpty()) {
            getDetailFood(foodId);
            getRatingFood(foodId);
        }

        //Comment
        commentRec = findViewById(R.id.commentRec);
        rating = new ArrayList<>();
        getRatingList();
        feedbackAdapter = new FeedbackAdapter(getBaseContext(), rating);
        commentRec.setAdapter(feedbackAdapter);
        commentRec.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        commentRec.setHasFixedSize(true);
        commentRec.setNestedScrollingEnabled(false);
        commentRec.setVisibility(View.GONE);

        quantityRating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isCommentVisible = !isCommentVisible;
                if (isCommentVisible) {
                    commentRec.setVisibility(View.VISIBLE);
                } else {
                    commentRec.setVisibility(View.GONE);
                }
            }
        });
    }

    private void getRatingList() {
        Query foodRating = ratingdb.orderByChild("foodId").equalTo(foodId);
        foodRating.addValueEventListener(new ValueEventListener() {
            int count = 0, sum = 0;

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Rating item = postSnapshot.getValue(Rating.class);

                    rating.add(item);
                    feedbackAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void getRatingFood(String foodId) {
        Query foodRating = ratingdb.orderByChild("foodId").equalTo(foodId);
        foodRating.addValueEventListener(new ValueEventListener() {
            int count = 0, sum = 0;

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Rating item = postSnapshot.getValue(Rating.class);
                    sum += Integer.parseInt(item.getRateValue());
                    count++;
                }
                if (count != 0) {
                    float avg = sum / count;
                    quantityRating.setText(count + " lượt đánh giá (" + avg + "/5.0).");
                    ratingBar.setRating(avg);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void showRatingDialog() {
        new AppRatingDialog.Builder()
                .setPositiveButtonText("Gửi")
                .setNegativeButtonText("Hủy")
                .setNoteDescriptions(Arrays.asList("Rất tệ", "Tệ", "Ổn", "Ngon", "Rất Ngon"))
                .setDefaultRating(1)
                .setTitle("Đánh giá món ăn")
                .setDescription("Chọn sao và gửi phản hồi")
                .setTitleTextColor(R.color.DarkSlateGray)
                .setDescriptionTextColor(R.color.DarkSlateGray)
                .setHint("Để lại bình luận ở đây ...")
                .setHintTextColor(R.color.teal_700)
                .setCommentTextColor(R.color.white)
                .setCommentBackgroundColor(R.color.DarkSlateGray)
                .setWindowAnimation(R.style.RatingDialogFateAnim)
                .create(FoodDetail.this)
                .show();
    }

    private void getDetailFood(String foodId) {
        food.child(foodId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                currentFood = snapshot.getValue(Food.class);
                if (currentFood.getImage().isEmpty()) {
                    storageReference.child(foodId + ".png").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            currentFood.setImage(uri.toString());
                            Picasso.get().load(currentFood.getImage()).into(foodImage);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Picasso.get().load("android.resource://"
                                    + FoodDetail.this.getPackageName() + "/"
                                    + R.drawable.default_image).into(foodImage);
                        }
                    });
                } else {
                    Picasso.get().load(currentFood.getImage()).into(foodImage);
                }
                collapsingToolbarLayout.setTitle(currentFood.getName());
                foodName.setText(currentFood.getName());
                foodPrice.setText(currentFood.getPrice());
                if (!currentFood.getDescription().isEmpty()) {
                    foodDescription.setText(currentFood.getDescription());
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void increase(View view) {
        quantity++;
        quantityText.setText(String.valueOf(quantity));
    }

    public void decrease(View view) {
        if (quantity > 1) {
            quantity--;
            quantityText.setText(String.valueOf(quantity));
        }
    }

    @Override
    public void onNegativeButtonClicked() {

    }

    @Override
    public void onPositiveButtonClicked(int i, @NonNull String s) {
        Rating rating = new Rating(Common.currentUser.getPhone(), Common.currentUser.getUsername(),
                foodId, String.valueOf(i), s
        );
        ratingdb.push()
                .setValue(rating)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(FoodDetail.this, "Cảm ơn vì đã đánh giá", Toast.LENGTH_SHORT).show();
                    }
                });
//        ratingdb.child(Common.currentUser.getPhone()).addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                if (snapshot.child(Common.currentUser.getPhone()).exists()) {
//                    //Update rating value
//                    ratingdb.child(Common.currentUser.getPhone()).removeValue();
//                    ratingdb.child(Common.currentUser.getPhone()).setValue(rating);
//                } else {
//                    ratingdb.child(Common.currentUser.getPhone()).setValue(rating);
//                }
//                Toast.makeText(FoodDetail.this, "Cảm ơn vì đã đánh giá", Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
    }

    @Override
    public void onNeutralButtonClicked() {

    }
}
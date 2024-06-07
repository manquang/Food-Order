package com.example.foodorder.ui.home;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;

import com.example.foodorder.Adapter.FoodListAdapter;
import com.example.foodorder.Model.Food;
import com.example.foodorder.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mancj.materialsearchbar.MaterialSearchBar;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {
    RecyclerView recycleViewFood;
    MaterialSearchBar searchBar;
    FoodListAdapter foodListAdapter;
    DatabaseReference foodRef;
    DatabaseReference categoryRef;
    ArrayList<String> suggestList;
    ArrayList<Food> searchList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        //Firebase
        categoryRef = FirebaseDatabase.getInstance().getReference("Category");
        foodRef = FirebaseDatabase.getInstance().getReference("Food");
        //View
        recycleViewFood = findViewById(R.id.foodList);
        searchBar = findViewById(R.id.searchBar);
        // Adapter
        suggestList = new ArrayList<>();
        searchList = new ArrayList<>();
        foodListAdapter = new FoodListAdapter(this, searchList);
        recycleViewFood.setAdapter(foodListAdapter);
        recycleViewFood.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        recycleViewFood.setHasFixedSize(true);
        loadSuggest();

        // SearchBar
        searchBar.setLastSuggestions(suggestList);
        searchBar.setCardViewElevation(4);
        searchBar.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                System.out.println(suggestList.size());
                ArrayList<String> suggest = new ArrayList<>();
                for (String search : suggestList) {
                    if (search.toLowerCase().contains(charSequence.toString().toLowerCase())) {
                        suggest.add(search);
                    }
                }
                searchBar.updateLastSuggestions(suggest);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        searchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {
            }

            @Override
            public void onSearchConfirmed(CharSequence text) {
                startSearch(text);
            }

            @Override
            public void onButtonClicked(int buttonCode) {

            }
        });

    }

    private void startSearch(CharSequence text) {
        searchList = new ArrayList<>();

        foodRef.orderByChild("name").equalTo(text.toString())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot item : snapshot.getChildren()) {
                            searchList.add(item.getValue(Food.class));
                        }
                        foodListAdapter = new FoodListAdapter(getBaseContext(), searchList);
                        recycleViewFood.setAdapter(foodListAdapter);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void loadSuggest() {
        foodRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot item : snapshot.getChildren()) {
                    suggestList.add(item.child("name").getValue(String.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
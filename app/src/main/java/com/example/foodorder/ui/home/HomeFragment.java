package com.example.foodorder.ui.home;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodorder.Adapter.CategoryListAdapter;
import com.example.foodorder.Adapter.FoodListAdapter;
import com.example.foodorder.Adapter.UpdateRec;
import com.example.foodorder.Model.Category;
import com.example.foodorder.Model.Food;
import com.example.foodorder.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mancj.materialsearchbar.MaterialSearchBar;

import java.util.ArrayList;

public class HomeFragment extends Fragment implements UpdateRec<Food> {

    RecyclerView homeHorRec, homeVerRec;
    DatabaseReference categoryRef;
    StorageReference storageReference;

    // Horizontal
    ArrayList<Category> categories;
    CategoryListAdapter categoryListAdapter;
    //Vertical
    ArrayList<Food> foods;
    //SearchBar
    FoodListAdapter foodListAdapter;
    MaterialSearchBar searchBar;


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        homeHorRec = root.findViewById(R.id.home_hor_rec);
        homeVerRec = root.findViewById(R.id.home_ver_rec);
        //Firebase
        categoryRef = FirebaseDatabase.getInstance().getReference("Category");
        storageReference = FirebaseStorage.getInstance().getReference("Category");

        categories = new ArrayList<>();
        getHorModelList();
        categoryListAdapter = new CategoryListAdapter(this, getActivity(), categories);
        homeHorRec.setAdapter(categoryListAdapter);
        homeHorRec.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL, false));
        homeHorRec.setHasFixedSize(true);
        homeHorRec.setNestedScrollingEnabled(false);

        // Vertical
        foods = new ArrayList<>();
        foodListAdapter = new FoodListAdapter(getActivity(), foods);
        homeVerRec.setAdapter(foodListAdapter);
        homeVerRec.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false));

        //searchBar
        searchBar = root.findViewById(R.id.searchBar);
        searchBar.setHint("Nhập đồ ăn");
        searchBar.setSpeechMode(false);
        searchBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), SearchActivity.class);
                startActivity(intent);
            }
        });

        return root;
    }

    private void getHorModelList() {
        categories = new ArrayList<>();
        categoryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot categorySnapshot : snapshot.getChildren()) {

                    Category category = new Category();
                    category = categorySnapshot.getValue(Category.class);

                    Category finalCategory = category;
                    System.out.println(category.getFileName());
                    storageReference.child(category.getFileName()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            finalCategory.setImgUrl(uri.toString());
                            categoryListAdapter.notifyDataSetChanged();
                        }
                    });
                    categories.add(category);
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

    @Override
    public void callBack(ArrayList<Food> list) {
        foodListAdapter = new FoodListAdapter(getContext(), list);
        foodListAdapter.notifyDataSetChanged();
        homeVerRec.setAdapter(foodListAdapter);
    }
}
package com.example.foodorder.ui.cart;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodorder.Adapter.CartAdapter;
import com.example.foodorder.Adapter.UpdateRec;
import com.example.foodorder.Common.Common;
import com.example.foodorder.Database.Database;
import com.example.foodorder.Model.CartItem;
import com.example.foodorder.Model.Order;
import com.example.foodorder.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class CartFragment extends Fragment implements UpdateRec<CartItem> {

    RecyclerView cartRec;
    TextView totalPrice;
    Button makeOrder;
    List<CartItem> cartList = new ArrayList<>();
    CartAdapter adapter;
    LinearLayout noFood;
    DatabaseReference orderRef, cartRef,  foodRef;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_cart, container, false);
        //Firebase
        orderRef = FirebaseDatabase.getInstance().getReference("Order");
        cartRef = FirebaseDatabase.getInstance().getReference("Cart");
        foodRef = FirebaseDatabase.getInstance().getReference("Food");
        //Views
        cartRec = root.findViewById(R.id.cart_rec);
        makeOrder = root.findViewById(R.id.makeOrder);
        totalPrice = root.findViewById(R.id.total);
        registerForContextMenu(cartRec);
        loadlistFood(root.getContext());

        makeOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAlertDialog(root.getContext());
            }
        });


        return root;
    }

    private void showAlertDialog(Context context) {
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setTitle("Thêm 1 bước nữa");
        alert.setMessage("Địa chỉ nhận hàng: ");
        final EditText addressEdt = new EditText(context);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        );
        addressEdt.setLayoutParams(lp);
        alert.setView(addressEdt);
        alert.setIcon(R.drawable.baseline_shopping_cart_24);

        alert.setPositiveButton("Tiếp", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Tạo request
                Order order = new Order(
                        Common.currentUser.getPhone(),
                        Common.currentUser.getUsername(),
                        addressEdt.getText().toString().trim(),
                        totalPrice.getText().toString().trim(),
                        "0",
                        String.valueOf(System.currentTimeMillis()),
                        cartList
                );
                //Submit to Firebase
                orderRef.child(order.getOrderId()).setValue(order);
                cleanCart();
                Toast.makeText(context, "Đặt hàng thành công", Toast.LENGTH_SHORT).show();
                dialogInterface.dismiss();
                loadlistFood(getContext());
            }

        });
        alert.setNegativeButton("Dừng", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        alert.show();
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        if (item.getTitle().equals(Common.DELETE)) {
            deleteCartItem(adapter.getItemAtPosition(item.getOrder()));
        }
        return super.onContextItemSelected(item);
    }

    private void deleteCartItem(CartItem item) {
        cartList.remove(item);
        cartRef.child(Common.currentUser.getPhone()).child(item.getFoodId()).removeValue();
        callBack((ArrayList)cartList);
        adapter.notifyDataSetChanged();
    }

    private void loadlistFood(Context context) {
        // Get Cart
        cartList = getCart();

        adapter = new CartAdapter(cartList, this, this);
        cartRec.setAdapter(adapter);
        cartRec.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false));
        cartRec.setHasFixedSize(true);
        cartRec.setNestedScrollingEnabled(false);
    }

    private List<CartItem> getCart() {
        final List<CartItem> result = new ArrayList<>();
        cartRef.child(Common.currentUser.getPhone())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (DataSnapshot item : snapshot.getChildren()) {
                                CartItem cnt = item.getValue(CartItem.class);
                                result.add(cnt);
                                adapter.notifyDataSetChanged();
                            }
                            callBack((ArrayList)result);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
        return result;
    }

    public void updateCart(CartItem cartItem) {
        cartRef.child(Common.currentUser.getPhone()).child(cartItem.getFoodId())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            Integer currentQuantity = snapshot.child("quantity").getValue(Integer.class);
                            if (currentQuantity != null) {
                                int newQuantity = cartItem.getQuantity();
                                cartRef.child(Common.currentUser.getPhone()).child(cartItem.getFoodId())
                                        .child("quantity").setValue(newQuantity);
                            }
                        } else {
                            cartRef.child(Common.currentUser.getPhone()).child(cartItem.getFoodId())
                                    .setValue(cartItem);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void cleanCart() {
        cartRef.child(Common.currentUser.getPhone()).setValue(null);
        loadlistFood(getContext());
        callBack((ArrayList<CartItem>) cartList);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void callBack(ArrayList<CartItem> list) {
        int total = 0;
        for (CartItem cartItem : list) {
            total += (Integer.parseInt(cartItem.getPrice()) * cartItem.getQuantity());
        }
        Locale locale = new Locale("vi", "VN");
        NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);
        totalPrice.setText(fmt.format(total));
    }

}
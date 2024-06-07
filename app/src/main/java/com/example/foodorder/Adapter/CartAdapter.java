package com.example.foodorder.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodorder.Common.Common;
import com.example.foodorder.Database.Database;
import com.example.foodorder.Model.CartItem;
import com.example.foodorder.R;
import com.example.foodorder.ui.cart.CartFragment;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder>  {
    private List<CartItem> listData;
    private CartFragment fragment;
    UpdateRec updateRec;

    public CartAdapter(List<CartItem> listData, UpdateRec updateRec, CartFragment fragment) {
        this.listData = listData;
        this.fragment = fragment;
        this.updateRec = updateRec;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.card_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CartAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.cartCount.setText(String.valueOf(listData.get(position).getQuantity()));
        Locale locale = new Locale("vi", "VN");
        NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);
        int price = Integer.parseInt(listData.get(position).getPrice())
                * listData.get(position).getQuantity();
        holder.cartNameTxt.setText(listData.get(position).getFoodName());
        holder.cardPriceTxt.setText(fmt.format(price));
        holder.quantity = listData.get(position).getQuantity();

        holder.increaseDB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.quantity++;
                listData.get(position).setQuantity(Integer.parseInt(String.valueOf(holder.quantity)));
                holder.cartCount.setText(String.valueOf(holder.quantity));
                fragment.updateCart(new CartItem(
                        listData.get(position).getFoodId(),
                        listData.get(position).getFoodName(),
                        Integer.parseInt(holder.cartCount.getText().toString()),
                        listData.get(position).getPrice(),
                        listData.get(position).getDiscount()
                ));
                notifyDataSetChanged();
                updateRec.callBack((ArrayList) listData);
            }
        });
        holder.decreaseDB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(holder.quantity > 1) {
                    holder.quantity--;
                    listData.get(position).setQuantity(Integer.parseInt(String.valueOf(holder.quantity)));
                    holder.cartCount.setText(String.valueOf(holder.quantity));
                    fragment.updateCart(new CartItem(
                            listData.get(position).getFoodId(),
                            listData.get(position).getFoodName(),
                            Integer.parseInt(holder.cartCount.getText().toString()),
                            listData.get(position).getPrice(),
                            listData.get(position).getDiscount()
                    ));
                    notifyDataSetChanged();
                    updateRec.callBack((ArrayList) listData);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    public CartItem getItemAtPosition(int position) {
        if (position >= 0 && position < listData.size()) {
            return listData.get(position);
        }
        return null;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener{
        TextView cartNameTxt, cardPriceTxt;
        EditText cartCount;
        Button increaseDB, decreaseDB;

        int quantity;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnCreateContextMenuListener(this);
            cartNameTxt = itemView.findViewById(R.id.item_name);
            cartCount = itemView.findViewById(R.id.item_count);
            cardPriceTxt = itemView.findViewById(R.id.item_price);
            increaseDB = itemView.findViewById(R.id.increaseDB);
            decreaseDB = itemView.findViewById(R.id.decreaseDB);
            quantity = Integer.parseInt(cartCount.getText().toString());

            cartCount.setOnEditorActionListener((v, actionId, event) -> {
                // Handle the Enter key press on the keyboard
                if (actionId == EditorInfo.IME_ACTION_DONE || event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    String input = cartCount.getText().toString();
                    try {
                        int newNumber = Integer.parseInt(input);
                        if (newNumber >= 1) {
                            quantity = newNumber;
                        } else {
                            quantity = 1;
                            cartCount.setText(String.valueOf(1));
                            Toast.makeText(v.getContext(), "Số lượng lớn hơn 0", Toast.LENGTH_SHORT).show();
                        }
                    } catch (NumberFormatException e) {
                        quantity = 1;
                        cartCount.setText(String.valueOf(1));
                        Toast.makeText(v.getContext(), e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                    }
                    return true;
                }
                return false;
            });
        }

        @Override
        public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
//            contextMenu.setHeaderTitle("Select the action");
            contextMenu.add(0,0,getAdapterPosition(), Common.DELETE);
        }
    }
}

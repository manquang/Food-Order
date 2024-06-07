package com.example.foodorder.Database;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;

import com.example.foodorder.Model.CartItem;
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.util.ArrayList;
import java.util.List;

public class Database extends SQLiteAssetHelper {
    private static String DB_NAME = "FoodOrderDB.db";
    private static int DB_VER = 1;

    public Database(Context context) {
        super(context, DB_NAME, null, DB_VER);
    }

    @SuppressLint("Range")
    public List<CartItem> getCart() {
        SQLiteDatabase db = getReadableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        String[] sqlSelect = {"ProductName", "ProductId", "Quantity", "Price", "Discount"};
        String sqlTable = "OrderDetail";

        qb.setTables(sqlTable);
        Cursor c = qb.query(db, sqlSelect, null, null, null, null, null);
        final List<CartItem> result = new ArrayList<>();
        if (c.moveToFirst()) {
            do {
                result.add(new CartItem(c.getString(c.getColumnIndex("ProductId")),
                        c.getString(c.getColumnIndex("ProductName")),
                        Integer.parseInt(c.getString(c.getColumnIndex("Quantity"))),
                        c.getString(c.getColumnIndex("Price")),
                        c.getString(c.getColumnIndex("Discount"))
                ));
            } while (c.moveToNext());
        }
        return result;
    }

    public void addCart(CartItem cartItem) {
        SQLiteDatabase db = getReadableDatabase();
        //Check exist
        String checkQuery = String.format("SELECT * FROM OrderDetail WHERE ProductId = '%s';", cartItem.getFoodId());
        Cursor cursor = db.rawQuery(checkQuery, null);
        if (cursor.getCount() > 0) {
            // Update quantity if productId already exists
            String updateQuery = String.format("UPDATE OrderDetail SET Quantity = %s WHERE ProductId = '%s';", cartItem.getQuantity(), cartItem.getFoodId());
            db.execSQL(updateQuery);
        } else {
            //Add new
            String query = String.format("INSERT INTO OrderDetail(ProductId,ProductName, Quantity, Price, Discount) VALUES('%s','%s','%s','%s','%s');",
                    cartItem.getFoodId(),
                    cartItem.getFoodName(),
                    cartItem.getQuantity(),
                    cartItem.getPrice(),
                    cartItem.getDiscount());
            db.execSQL(query);
        }
    }

    public void cleanCart() {
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("Delete from OrderDetail;");
        db.execSQL(query);
    }

    public void addFav(String foodId) {
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("INSERT INTO Favourites(FoodId) VALUES('%s');", foodId);
        db.execSQL(query);
    }

    public void removeFav(String foodId) {
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("DELETE FROM Favourites WHERE FoodId = '%s';", foodId);
        db.execSQL(query);
    }

    public boolean isFav(String foodId) {
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("SELECT * FROM Favourites WHERE FoodId = '%s';", foodId);
        Cursor cursor = db.rawQuery(query,null);
        if (cursor.getCount() <= 0) {
            cursor.close();
            return false;
        } else {
            cursor.close();
            return true;
        }
    }
}

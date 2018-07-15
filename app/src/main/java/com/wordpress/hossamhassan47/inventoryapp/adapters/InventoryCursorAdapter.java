package com.wordpress.hossamhassan47.inventoryapp.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.wordpress.hossamhassan47.inventoryapp.R;
import com.wordpress.hossamhassan47.inventoryapp.data.InventoryContract;

public class InventoryCursorAdapter extends CursorAdapter {

    public InventoryCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 /* flags */);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.inventory_list_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // Find views
        TextView txtProductName = (TextView) view.findViewById(R.id.text_view_product_name);
        TextView txtPrice = (TextView) view.findViewById(R.id.text_view_price);
        TextView txtQuantity = (TextView) view.findViewById(R.id.text_view_quantity);

        // Get indexes
        int indexName = cursor.getColumnIndex(InventoryContract.ProductEntry.COLUMN_PRODUCT_NAME);
        int indexPrice = cursor.getColumnIndex(InventoryContract.ProductEntry.COLUMN_PRICE);
        int indexQuantity = cursor.getColumnIndex(InventoryContract.ProductEntry.COLUMN_QUANTITY);

        // Read product attributes from cursor
        String productName = cursor.getString(indexName);
        Integer price = cursor.getInt(indexPrice);
        Integer quantity = cursor.getInt(indexQuantity);

        // Update views
        txtProductName.setText(productName);
        txtPrice.setText(price + "");
        txtQuantity.setText(quantity + "");
    }
}

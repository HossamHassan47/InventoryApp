package com.wordpress.hossamhassan47.inventoryapp.adapters;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
    public void bindView(View view, final Context context, Cursor cursor) {
        // Find views
        TextView txtProductName = (TextView) view.findViewById(R.id.text_view_product_name);
        TextView txtPrice = (TextView) view.findViewById(R.id.text_view_price);
        TextView txtQuantity = (TextView) view.findViewById(R.id.text_view_quantity);
        ImageView btnSale = (ImageView) view.findViewById(R.id.image_view_sale);

        // Get indexes
        int indexId = cursor.getColumnIndex(InventoryContract.ProductEntry._ID);
        int indexName = cursor.getColumnIndex(InventoryContract.ProductEntry.COLUMN_PRODUCT_NAME);
        int indexPrice = cursor.getColumnIndex(InventoryContract.ProductEntry.COLUMN_PRICE);
        int indexQuantity = cursor.getColumnIndex(InventoryContract.ProductEntry.COLUMN_QUANTITY);

        // Read product attributes from cursor
        final long id = cursor.getLong(indexId);
        String productName = cursor.getString(indexName);
        Integer price = cursor.getInt(indexPrice);
        final Integer quantity = cursor.getInt(indexQuantity);

        // Update views
        txtProductName.setText(productName);
        txtPrice.setText(context.getString(R.string.list_item_price) + " " + context.getString(R.string.list_item_currency) + price);
        txtQuantity.setText(context.getString(R.string.list_item_quantity) + " " + quantity);

        btnSale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (quantity <= 0) {
                    return;
                }

                int newQuantity = quantity - 1;

                Uri productUri = ContentUris.withAppendedId(InventoryContract.ProductEntry.CONTENT_URI, id);

                // Create a ContentValues object
                ContentValues values = new ContentValues();
                values.put(InventoryContract.ProductEntry.COLUMN_QUANTITY, newQuantity);

                // Edit Mode
                int rowsAffected = context.getContentResolver().update(productUri, values, null, null);

                // Show a toast message depending on whether or not the update was successful.
                if (rowsAffected == 0) {
                    Toast.makeText(context, context.getString(R.string.sale_product_failed), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, context.getString(R.string.sale_product_successful),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}

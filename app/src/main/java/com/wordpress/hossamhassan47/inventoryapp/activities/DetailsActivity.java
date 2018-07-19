package com.wordpress.hossamhassan47.inventoryapp.activities;

import android.Manifest;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.wordpress.hossamhassan47.inventoryapp.R;
import com.wordpress.hossamhassan47.inventoryapp.data.InventoryContract;

public class DetailsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    //region Variables and Members
    private static final int MY_PERMISSIONS_REQUEST_CALL_PHONE = 1101;

    private static final int EXISTING_Product_LOADER = 0;

    private Uri mCurrentProductUri;

    private TextView mTextViewProductName;
    private TextView mTextViewPrice;
    private TextView mTextViewQuantity;
    private TextView mTextViewSupplierName;
    private TextView mTextViewSupplierPhoneNumber;

    private ImageView btnDecreaseQuantity;
    private ImageView btnIncreaseQuantity;
    private ImageView btnOrder;

    private int quantity;
    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Get Current Product Uri
        mCurrentProductUri = getIntent().getData();

        // Initialize a loader to read the product data from the database
        getLoaderManager().initLoader(EXISTING_Product_LOADER, null, this);

        // Get reference to product views
        mTextViewProductName = findViewById(R.id.text_view_product_name);
        mTextViewPrice = findViewById(R.id.text_view_price);
        mTextViewQuantity = findViewById(R.id.text_view_quantity);
        mTextViewSupplierName = findViewById(R.id.text_view_supplier_name);
        mTextViewSupplierPhoneNumber = findViewById(R.id.text_view_supplier_phone);

        btnDecreaseQuantity = findViewById(R.id.image_view_decrease_quantity);
        btnDecreaseQuantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (quantity <= 0) {
                    return;
                }

                quantity--;
                saveQuantity(quantity);
            }
        });

        btnIncreaseQuantity = findViewById(R.id.image_view_increase_quantity);
        btnIncreaseQuantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                quantity++;
                saveQuantity(quantity);
            }
        });

        btnOrder = findViewById(R.id.image_view_order);
        btnOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + mTextViewSupplierPhoneNumber.getText()));

                if (ContextCompat.checkSelfPermission(getApplicationContext(),
                        Manifest.permission.CALL_PHONE)
                        != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(DetailsActivity.this,
                            new String[]{Manifest.permission.CALL_PHONE},
                            MY_PERMISSIONS_REQUEST_CALL_PHONE);
                } else {
                    // Already have permission
                    try {
                        startActivity(callIntent);
                    } catch (SecurityException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CALL_PHONE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the phone call
                    Toast.makeText(getApplicationContext(),
                            "Access granted, try again now please.",
                            Toast.LENGTH_SHORT).show();
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(getApplicationContext(),
                            "Oops, Access denied.",
                            Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

    //region Cursor Loader
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // Since the editor shows all product attributes, define a projection that contains
        // all columns from the products table
        String[] projection = {
                InventoryContract.ProductEntry._ID,
                InventoryContract.ProductEntry.COLUMN_PRODUCT_NAME,
                InventoryContract.ProductEntry.COLUMN_PRICE,
                InventoryContract.ProductEntry.COLUMN_QUANTITY,
                InventoryContract.ProductEntry.COLUMN_SUPPLIER_NAME,
                InventoryContract.ProductEntry.COLUMN_SUPPLIER_PHONE_NUMBER};

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,       // Parent activity context
                mCurrentProductUri,                 // Query the content URI for the current product
                projection,                         // Columns to include in the resulting Cursor
                null,                      // No selection clause
                null,                   // No selection arguments
                null);                     // Default sort order
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // Bail early if the cursor is null or there is less than 1 row in the cursor
        if (data == null || data.getCount() < 1) {
            return;
        }

        if (data.moveToFirst()) {
            // Find the columns of product attributes that we're interested in
            int indexProductName = data.getColumnIndex(InventoryContract.ProductEntry.COLUMN_PRODUCT_NAME);
            int indexPrice = data.getColumnIndex(InventoryContract.ProductEntry.COLUMN_PRICE);
            int indexQuantity = data.getColumnIndex(InventoryContract.ProductEntry.COLUMN_QUANTITY);
            int indexSupplierName = data.getColumnIndex(InventoryContract.ProductEntry.COLUMN_SUPPLIER_NAME);
            int indexPhoneNumber = data.getColumnIndex(InventoryContract.ProductEntry.COLUMN_SUPPLIER_PHONE_NUMBER);

            // Extract out the value from the Cursor for the given column index
            String productName = data.getString(indexProductName);
            int price = data.getInt(indexPrice);
            quantity = data.getInt(indexQuantity);
            String supplierName = data.getString(indexSupplierName);
            String supplierPhone = data.getString(indexPhoneNumber);

            // Update the views on the screen with the values from the database
            mTextViewProductName.setText(productName);
            mTextViewPrice.setText(getString(R.string.list_item_currency) + Integer.toString(price));
            mTextViewQuantity.setText(Integer.toString(quantity));
            mTextViewSupplierName.setText(supplierName);
            mTextViewSupplierPhoneNumber.setText(supplierPhone);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // If the loader is invalidated, clear out all the data from the input fields.
        mTextViewProductName.setText("");
        mTextViewPrice.setText("");
        mTextViewQuantity.setText("");
        mTextViewSupplierName.setText("");
        mTextViewSupplierPhoneNumber.setText("");
    }
    //endregion

    //region Options Menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_delete_product) {
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setTitle(R.string.alert_confirm_title);
            alert.setMessage(R.string.alert_confirm_message);
            alert.setCancelable(false);
            alert.setPositiveButton(R.string.alert_confirm_yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    deleteProduct();

                    // Exit activity
                    finish();
                }
            });

            alert.setNegativeButton(R.string.alert_confirm_cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // close dialog
                    dialog.cancel();
                }
            });

            alert.show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    //endregion

    //region Helper Methods
    private void saveQuantity(int quantity) {
        // Create a ContentValues object
        ContentValues values = new ContentValues();
        values.put(InventoryContract.ProductEntry.COLUMN_QUANTITY, quantity);

        // Edit Mode
        int rowsAffected = getContentResolver().update(mCurrentProductUri, values, null, null);

        // Show a toast message depending on whether or not the update was successful.
        if (rowsAffected == 0) {
            Toast.makeText(this, getString(R.string.editor_save_product_failed),
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteProduct() {
        if (mCurrentProductUri == null) {
            return;
        }

        // Defines a variable to contain the number of rows deleted
        int rowsAffected = getContentResolver().delete(mCurrentProductUri, null, null);

        // Show a toast message depending on whether or not the delete was successful.
        if (rowsAffected == 0) {
            Toast.makeText(this, getString(R.string.editor_delete_product_failed),
                    Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, getString(R.string.editor_delete_product_successful),
                    Toast.LENGTH_SHORT).show();
        }
    }
    //endregion
}

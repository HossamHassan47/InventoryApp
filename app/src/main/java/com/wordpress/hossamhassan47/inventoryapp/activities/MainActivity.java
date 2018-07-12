package com.wordpress.hossamhassan47.inventoryapp.activities;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.wordpress.hossamhassan47.inventoryapp.R;
import com.wordpress.hossamhassan47.inventoryapp.data.InventoryContract;
import com.wordpress.hossamhassan47.inventoryapp.data.InventoryDbHelper;

public class MainActivity extends AppCompatActivity {

    /**
     * Database helper that will provide us access to the database
     */
    private InventoryDbHelper mDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: Open product editor
                Snackbar.make(view, "TODO: Open product editor", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        // Instantiate our inventory db helper
        mDbHelper = new InventoryDbHelper(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        // TODO: Display products list
        displayDatabaseInfo();
    }

    /**
     * Temporary helper method to display information in the onscreen TextView about the state of
     * the product database.
     */
    private void displayDatabaseInfo() {

        // Create and/or open a database to read from it
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                InventoryContract.ProductEntry._ID,
                InventoryContract.ProductEntry.COLUMN_PRODUCT_NAME,
                InventoryContract.ProductEntry.COLUMN_PRICE,
                InventoryContract.ProductEntry.COLUMN_QUANTITY,
                InventoryContract.ProductEntry.COLUMN_SUPPLIER_NAME,
                InventoryContract.ProductEntry.COLUMN_SUPPLIER_PHONE_NUMBER};

        // Perform a query on the products table
        Cursor cursor = getContentResolver().query(InventoryContract.ProductEntry.CONTENT_URI,
                projection, null, null, null);

        TextView displayView = (TextView) findViewById(R.id.text_view_products);

        try {
            // Create a header in the Text View that looks like this:
            //
            // The product table contains <number of rows in Cursor> products.
            // _id - name - quantity - price - supplier name - phone
            //
            // In the while loop below, iterate through the rows of the cursor and display
            // the information from each column in this order.
            displayView.setText("The products table contains " + cursor.getCount() + " products.\n\n");
            displayView.append(InventoryContract.ProductEntry._ID + " - " +
                    InventoryContract.ProductEntry.COLUMN_PRODUCT_NAME + " - " +
                    InventoryContract.ProductEntry.COLUMN_PRICE + " - " +
                    InventoryContract.ProductEntry.COLUMN_QUANTITY + " - " +
                    InventoryContract.ProductEntry.COLUMN_SUPPLIER_NAME + " - " +
                    InventoryContract.ProductEntry.COLUMN_SUPPLIER_PHONE_NUMBER + "\n");

            // Figure out the index of each column
            int indexId = cursor.getColumnIndex(InventoryContract.ProductEntry._ID);
            int indexProuctName = cursor.getColumnIndex(InventoryContract.ProductEntry.COLUMN_PRODUCT_NAME);
            int indexPrice = cursor.getColumnIndex(InventoryContract.ProductEntry.COLUMN_PRICE);
            int indexQuantity = cursor.getColumnIndex(InventoryContract.ProductEntry.COLUMN_QUANTITY);
            int indexSupplierName = cursor.getColumnIndex(InventoryContract.ProductEntry.COLUMN_SUPPLIER_NAME);
            int indexSupplierPhoneNumber = cursor.getColumnIndex(InventoryContract.ProductEntry.COLUMN_SUPPLIER_PHONE_NUMBER);

            // Iterate through all the returned rows in the cursor
            while (cursor.moveToNext()) {
                // Use that index to extract the String or Int value of the word
                // at the current row the cursor is on.
                int currentID = cursor.getInt(indexId);
                String currentProductName = cursor.getString(indexProuctName);
                int currentPrice = cursor.getInt(indexPrice);
                int currentQuantity = cursor.getInt(indexQuantity);
                String currentSupplierName = cursor.getString(indexSupplierName);
                String currentSupplierPhoneNumber = cursor.getString(indexSupplierPhoneNumber);

                // Display the values from each column of the current row in the cursor in the TextView
                displayView.append(("\n" + currentID + " - " +
                        currentProductName + " - " +
                        currentPrice + " - " +
                        currentQuantity + " - " +
                        currentSupplierName + " - " +
                        currentSupplierPhoneNumber));
            }
        } finally {
            // Close cursor
            cursor.close();
        }
    }

    /**
     * Helper method to insert hardcoded product data into the database. For debugging purposes only.
     */
    private void insertProduct() {
        // Create a ContentValues object
        ContentValues values = new ContentValues();
        values.put(InventoryContract.ProductEntry.COLUMN_PRODUCT_NAME, "Test Product");
        values.put(InventoryContract.ProductEntry.COLUMN_PRICE, 2);
        values.put(InventoryContract.ProductEntry.COLUMN_QUANTITY, 7);
        values.put(InventoryContract.ProductEntry.COLUMN_SUPPLIER_NAME, "Test Supplier");
        values.put(InventoryContract.ProductEntry.COLUMN_SUPPLIER_PHONE_NUMBER, "+2012345678910");

        //Cursor cursor = getContentResolver().query(InventoryContract.ProductEntry.CONTENT_URI,
        //        projection, null, null, null);

        // Insert a new row & returning the ID.
        Uri newUri = getContentResolver().insert(InventoryContract.ProductEntry.CONTENT_URI, values);

        // Show a toast message depending on whether or not the insertion was successful
        if (newUri == null) {
            // If the new content URI is null, then there was an error with insertion.
            Toast.makeText(this, getString(R.string.editor_insert_product_failed),
                    Toast.LENGTH_SHORT).show();
        } else {
            // Otherwise, the insertion was successful and we can display a toast.
            Toast.makeText(this, getString(R.string.editor_insert_product_successful),
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.action_insert_dummy_data) {
            insertProduct();
            displayDatabaseInfo();
            return true;
        } else if (id == R.id.action_delete_all_products) {
            // Do nothing for now
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

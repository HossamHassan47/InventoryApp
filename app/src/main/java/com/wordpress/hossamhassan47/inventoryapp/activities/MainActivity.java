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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.wordpress.hossamhassan47.inventoryapp.R;
import com.wordpress.hossamhassan47.inventoryapp.adapters.InventoryCursorAdapter;
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

        displayProductList();
    }

    /**
     * Display all inventory product list
     */
    private void displayProductList() {
        // Define projection for Id, Name, Price, and Quantity
        String[] projection = {
                InventoryContract.ProductEntry._ID,
                InventoryContract.ProductEntry.COLUMN_PRODUCT_NAME,
                InventoryContract.ProductEntry.COLUMN_PRICE,
                InventoryContract.ProductEntry.COLUMN_QUANTITY};

        // Perform a query on the products table
        Cursor cursor = getContentResolver().query(InventoryContract.ProductEntry.CONTENT_URI,
                projection, null, null, null);

        // Find the ListView which will be populated with the pet data
        ListView lstvwInventory = (ListView) findViewById(R.id.list_view_inventory);

        // Find and set empty view on the ListView, so that it only shows when the list has 0 items.
        View emptyView = findViewById(R.id.empty_view);
        lstvwInventory.setEmptyView(emptyView);

        // Setup an Adapter to create a list item for each row of pet data in the Cursor.
        InventoryCursorAdapter adapter = new InventoryCursorAdapter(this, cursor);

        // Attach the adapter to the ListView.
        lstvwInventory.setAdapter(adapter);
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
            displayProductList();
            return true;
        } else if (id == R.id.action_delete_all_products) {
            // Do nothing for now
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

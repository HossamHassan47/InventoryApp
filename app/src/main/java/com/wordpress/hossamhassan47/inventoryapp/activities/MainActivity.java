package com.wordpress.hossamhassan47.inventoryapp.activities;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.wordpress.hossamhassan47.inventoryapp.R;
import com.wordpress.hossamhassan47.inventoryapp.adapters.InventoryCursorAdapter;
import com.wordpress.hossamhassan47.inventoryapp.data.InventoryContract;

/**
 * Main Activity
 * Activity that used to display all product list
 */
public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    //region Variables and Members
    private static final int INVENTORY_LOADER = 0;

    InventoryCursorAdapter mCursorAdapter;
    ListView lstInventory;
    View emptyView;
    //endregion

    //region On Create
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

        // Find the ListView which will be populated with the pet data
        lstInventory = findViewById(R.id.list_view_inventory);

        // Find and set empty view on the ListView, so that it only shows when the list has 0 items.
        emptyView = findViewById(R.id.empty_view);
        lstInventory.setEmptyView(emptyView);

        // Attach the adapter to the ListView.
        mCursorAdapter  = new InventoryCursorAdapter(this, null);
        lstInventory.setAdapter(mCursorAdapter);

        lstInventory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, DetailsActivity.class);

                Uri currentProductUri = ContentUris.withAppendedId(InventoryContract.ProductEntry.CONTENT_URI, id);

                intent.setData(currentProductUri);

                startActivity(intent);
            }
        });

        // Kick off the loader
        getLoaderManager().initLoader(INVENTORY_LOADER, null, this);
    }
    //endregion

    //region Options Menu
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
        }

        return super.onOptionsItemSelected(item);
    }
    //endregion

    //region Cursor
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // Define projection for Id, Name, Price, and Quantity
        String[] projection = {
                InventoryContract.ProductEntry._ID,
                InventoryContract.ProductEntry.COLUMN_PRODUCT_NAME,
                InventoryContract.ProductEntry.COLUMN_PRICE,
                InventoryContract.ProductEntry.COLUMN_QUANTITY};

        return new CursorLoader(this,
                InventoryContract.ProductEntry.CONTENT_URI,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);
    }
    //endregion
}

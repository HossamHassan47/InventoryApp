package com.wordpress.hossamhassan47.inventoryapp.activities;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.wordpress.hossamhassan47.inventoryapp.R;
import com.wordpress.hossamhassan47.inventoryapp.data.InventoryContract;

/**
 * Editor Activity
 * Activity that used to add or edit product
 */
public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    //region Variables and Members
    private static final int EXISTING_Product_LOADER = 0;

    private Uri mCurrentProductUri;
    private boolean mProductHasChanged = false;

    private EditText mEditTextProductName;
    private EditText mEditTextPrice;
    private EditText mEditTextQuantity;
    private EditText mEditTextSupplierName;
    private EditText mEditTextSupplierPhoneNumber;
    //endregion

    //region On Create
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_editor);

        // Set action bar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Set back navigation button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Get Current Product Uri
        mCurrentProductUri = getIntent().getData();

        // Set title
        setActivityTitle();

        // Get reference to product views
        mEditTextProductName = findViewById(R.id.edit_text_product_name);
        mEditTextPrice = findViewById(R.id.edit_text_price);
        mEditTextQuantity = findViewById(R.id.edit_text_quantity);
        mEditTextSupplierName = findViewById(R.id.edit_text_supplier_name);
        mEditTextSupplierPhoneNumber = findViewById(R.id.edit_text_supplier_phone);

        // Attach on touch listener
        mEditTextProductName.setOnTouchListener(mTouchListener);
        mEditTextPrice.setOnTouchListener(mTouchListener);
        mEditTextQuantity.setOnTouchListener(mTouchListener);
        mEditTextSupplierName.setOnTouchListener(mTouchListener);
        mEditTextSupplierPhoneNumber.setOnTouchListener(mTouchListener);
    }

    // OnTouchListener that listens for any user touches on a View, implying that they are modifying
    // the view, and we change the mProductHasChanged boolean to true.
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mProductHasChanged = true;
            return false;
        }
    };

    private void setActivityTitle() {
        if (mCurrentProductUri == null) {
            setTitle(getString(R.string.add_product));
        } else {
            setTitle(getString(R.string.edit_product));

            // Initialize a loader to read the product data from the database
            getLoaderManager().initLoader(EXISTING_Product_LOADER, null, this);
        }
    }
    //endregion

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
            int quantity = data.getInt(indexQuantity);
            String supplierName = data.getString(indexSupplierName);
            String supplierPhone = data.getString(indexPhoneNumber);

            // Update the views on the screen with the values from the database
            mEditTextProductName.setText(productName);
            mEditTextPrice.setText(String.format("%s", Integer.toString(price)));
            mEditTextQuantity.setText(String.format("%s", Integer.toString(quantity)));
            mEditTextSupplierName.setText(supplierName);
            mEditTextSupplierPhoneNumber.setText(supplierPhone);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // If the loader is invalidated, clear out all the data from the input fields.
        mEditTextProductName.setText("");
        mEditTextPrice.setText("");
        mEditTextQuantity.setText("");
        mEditTextSupplierName.setText("");
        mEditTextSupplierPhoneNumber.setText("");
    }
    //endregion

    //region Options Menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_editor, menu);

        menu.findItem(R.id.action_delete_product).setVisible(mCurrentProductUri != null);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_save_product) {
            saveProduct();
            return true;
        } else if (id == R.id.action_delete_product) {
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
        } else if (id == android.R.id.home) {
            // If the pet hasn't changed, continue with navigating up to parent activity
            // which is the {@link CatalogActivity}.
            if (!mProductHasChanged) {
                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                return true;
            }

            // Otherwise if there are unsaved changes, setup a dialog to warn the user.
            // Create a click listener to handle the user confirming that
            // changes should be discarded.
            DialogInterface.OnClickListener discardButtonClickListener =
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            // User clicked "Discard" button, navigate to parent activity.
                            NavUtils.navigateUpFromSameTask(EditorActivity.this);
                        }
                    };

            // Show a dialog that notifies the user they have unsaved changes
            showUnsavedChangesDialog(discardButtonClickListener);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        // If the pet hasn't changed, continue with handling back button press
        if (!mProductHasChanged) {
            super.onBackPressed();
            return;
        }

        // Otherwise if there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that changes should be discarded.
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };

        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }
    //endregion

    //region Helper Methods
    private void saveProduct() {
        String productName = mEditTextProductName.getText().toString().trim();
        String price = mEditTextPrice.getText().toString().trim();
        String quantity = mEditTextQuantity.getText().toString().trim();
        String supplierName = mEditTextSupplierName.getText().toString().trim();
        String supplierPhone = mEditTextSupplierPhoneNumber.getText().toString().trim();

        if (TextUtils.isEmpty(productName)) {
            Toast.makeText(this, R.string.validation_empty_product_name, Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(price)) {
            Toast.makeText(this, R.string.validation_invalid_price, Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(quantity)) {
            Toast.makeText(this, R.string.validation_invalid_quantity, Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(supplierName)) {
            Toast.makeText(this, R.string.validation_empty_supplier_name, Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(supplierPhone)) {
            Toast.makeText(this, R.string.validation_empty_supplier_phone, Toast.LENGTH_SHORT).show();
            return;
        }

        // Create a ContentValues object
        ContentValues values = new ContentValues();
        values.put(InventoryContract.ProductEntry.COLUMN_PRODUCT_NAME, productName);
        values.put(InventoryContract.ProductEntry.COLUMN_PRICE, Integer.parseInt(price));
        values.put(InventoryContract.ProductEntry.COLUMN_QUANTITY, Integer.parseInt(quantity));
        values.put(InventoryContract.ProductEntry.COLUMN_SUPPLIER_NAME, supplierName);
        values.put(InventoryContract.ProductEntry.COLUMN_SUPPLIER_PHONE_NUMBER, supplierPhone);

        boolean result;
        if (mCurrentProductUri == null) {
            // Insert a new row & returning the ID.
            Uri newUri = getContentResolver().insert(InventoryContract.ProductEntry.CONTENT_URI, values);

            result = newUri != null;
        } else {
            // Edit Mode
            int rowsAffected = getContentResolver().update(mCurrentProductUri, values, null, null);

            result = rowsAffected > 0;
        }

        // Show a toast message depending on whether or not the update was successful.
        if (result) {
            Toast.makeText(this, getString(R.string.editor_save_product_successful),
                    Toast.LENGTH_SHORT).show();

            // Exit activity
            finish();
        } else {
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

    private void showUnsavedChangesDialog(DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing the product.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
    //endregion
}

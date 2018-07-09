package com.wordpress.hossamhassan47.inventoryapp.data;

import android.provider.BaseColumns;

public final class InventoryContract {

    private InventoryContract() {}

    /**
     * Inner class that defines constant values for the product database table.
     * Each entry in the table represents a single pet.
     */
    public static final class ProductEntry implements BaseColumns {

        /** Name of database table for products */
        public final static String TABLE_NAME = "products";

        /**
         * Unique ID number for the pet (only for use in the database table).
         *
         * Type: INTEGER
         */
        public final static String _ID = BaseColumns._ID;

        /**
         * Name of the product.
         *
         * Type: TEXT
         */
        public final static String COLUMN_PRODUCT_NAME ="productName";

        /**
         * Price of the product.
         *
         * Type: INTEGER
         */
        public final static String COLUMN_PRICE = "price";

        /**
         * Quantity of the product.
         *
         * Type: INTEGER
         */
        public final static String COLUMN_QUANTITY = "quantity";

        /**
         * Supplier name of the product.
         *
         * Type: TEXT
         */
        public final static String COLUMN_SUPPLIER_NAME = "supplierName";

        /**
         * Supplier phone number of the product.
         *
         * Type: TEXT
         */
        public final static String COLUMN_SUPPLIER_PHONE_NUMBER = "supplierPhoneNumber";
    }
}

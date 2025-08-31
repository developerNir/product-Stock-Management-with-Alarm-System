package com.example.inventorymanager.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.sql.Blob;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DB_NAME = "product_app.db";
    public static final int DB_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE MainCategory(id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT)");
        db.execSQL("CREATE TABLE SubCategory(id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, mainCatId INTEGER, FOREIGN KEY(mainCatId) REFERENCES MainCategory(id))");
        db.execSQL("CREATE TABLE Products(id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, image BLOB, description TEXT, price REAL, quantity INTEGER, stock TEXT, subCatId INTEGER, FOREIGN KEY(subCatId) REFERENCES SubCategory(id))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS Products");
        db.execSQL("DROP TABLE IF EXISTS SubCategory");
        db.execSQL("DROP TABLE IF EXISTS MainCategory");
        onCreate(db);
    }




    // CRUD for MainCategory
    public boolean insertMainCategory(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("name", name);
        return db.insert("MainCategory", null, cv) != -1;
    }

    public Cursor getAllMainCategories() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM MainCategory", null);
    }

    public boolean updateMainCategory(int id, String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("name", name);
        return db.update("MainCategory", cv, "id=?", new String[]{String.valueOf(id)}) > 0;
    }

    public boolean deleteMainCategory(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete("MainCategory", "id=?", new String[]{String.valueOf(id)}) > 0;
    }

    // CRUD for SubCategory
    public boolean insertSubCategory(String name, int mainCatId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("name", name);
        cv.put("mainCatId", mainCatId);
        return db.insert("SubCategory", null, cv) != -1;
    }

    public Cursor getSubCategoriesByMainId(int mainCatId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM SubCategory WHERE mainCatId=?", new String[]{String.valueOf(mainCatId)});
    }

    public Cursor getAllSubCategories() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM SubCategory", null);
    }

    public boolean updateSubCategory(int id, String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("name", name);
        return db.update("SubCategory", cv, "id=?", new String[]{String.valueOf(id)}) > 0;
    }

    public boolean deleteSubCategory(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete("SubCategory", "id=?", new String[]{String.valueOf(id)}) > 0;
    }

    // CRUD for Product
    public boolean insertProduct(String name, byte[] image, String description, double price, int quantity, String stock, int subCatId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("name", name);
        cv.put("image", image); // byte[] works fine for BLOB
        cv.put("description", description);
        cv.put("price", price);
        cv.put("quantity", quantity);
        cv.put("stock", stock);
        cv.put("subCatId", subCatId);
        return db.insert("Products", null, cv) != -1;
    }


    public Cursor getAllProducts() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM Products", null);
    }

    public Cursor getProductsBySubCat(int subCatId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM Products WHERE subCatId=?", new String[]{String.valueOf(subCatId)});
    }

    // Get all products with quantity <= threshold (low stock)
    public Cursor getLowStockProducts(int threshold) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(
                "SELECT * FROM Products WHERE quantity <= ?",
                new String[]{String.valueOf(threshold)}
        );
    }


    public boolean updateProduct(int id, String name, byte[] image, String description, double price, int quantity, String stock) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("name", name);
        if (image != null) {
            cv.put("image", image); // byte[] for BLOB
        }
        cv.put("description", description);
        cv.put("price", price);
        cv.put("quantity", quantity);
        cv.put("stock", stock);

        return db.update("Products", cv, "id=?", new String[]{String.valueOf(id)}) > 0;
    }


    public boolean deleteProduct(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete("Products", "id=?", new String[]{String.valueOf(id)}) > 0;
    }

    public boolean updateProductQuantity(int id, int quantity) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("quantity", quantity);
        return db.update("Products", cv, "id=?", new String[]{String.valueOf(id)}) > 0;
    }



    // Optional methods
    public void clearMainCategories() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("main_category", null, null);
    }

    public void clearSubCategories() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("sub_category", null, null);
    }



}
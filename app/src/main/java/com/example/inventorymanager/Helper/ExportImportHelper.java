//package com.example.inventorymanager.Helper;
//
//import android.content.ContentValues;
//import android.content.Context;
//import android.database.Cursor;
//import android.database.sqlite.SQLiteDatabase;
//
//import com.example.inventorymanager.Model.MainCategory;
//import com.example.inventorymanager.Model.SubCategory;
//
//import org.apache.poi.ss.usermodel.*;
//import org.apache.poi.xssf.usermodel.XSSFWorkbook;
//
//import java.io.*;
//import java.util.List;
//
//public class ExportImportHelper {
//
//
//    SQLiteDatabase db;
//
//
//    public static void exportProductsToExcel(SQLiteDatabase db, File file) throws IOException {
//        Workbook workbook = new XSSFWorkbook();
//        Sheet sheet = workbook.createSheet("Products");
//
//        // Header Row
//        Row header = sheet.createRow(0);
//        String[] headers = {"ID", "Name", "Image", "Description", "Price", "Quantity", "Stock", "SubCategoryID"};
//        for (int i = 0; i < headers.length; i++) {
//            header.createCell(i).setCellValue(headers[i]);
//        }
//
//        Cursor cursor = db.rawQuery("SELECT * FROM Products", null);
//        int rowIndex = 1;
//        while (cursor.moveToNext()) {
//            Row row = sheet.createRow(rowIndex++);
//            for (int i = 0; i < cursor.getColumnCount(); i++) {
//                row.createCell(i).setCellValue(cursor.getString(i));
//            }
//        }
//        cursor.close();
//
//        FileOutputStream fos = new FileOutputStream(file);
//        workbook.write(fos);
//        fos.close();
//        workbook.close();
//    }
//
//    public static void importProductsFromExcel(SQLiteDatabase db, File file) throws IOException {
//        FileInputStream fis = new FileInputStream(file);
//        Workbook workbook = new XSSFWorkbook(fis);
//        Sheet sheet = workbook.getSheetAt(0);
//
//        db.beginTransaction();
//        try {
//            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
//                Row row = sheet.getRow(i);
//                if (row == null) continue;
//
//                String name = row.getCell(1).getStringCellValue();
//                String image = row.getCell(2).getStringCellValue();
//                String description = row.getCell(3).getStringCellValue();
//                double price = row.getCell(4).getNumericCellValue();
//                int quantity = (int) row.getCell(5).getNumericCellValue();
//                String stock = row.getCell(6).getStringCellValue();
//                int subCatId = (int) row.getCell(7).getNumericCellValue();
//
//                ContentValues cv = new ContentValues();
//                cv.put("name", name);
//                cv.put("image", image);
//                cv.put("description", description);
//                cv.put("price", price);
//                cv.put("quantity", quantity);
//                cv.put("stock", stock);
//                cv.put("subCatId", subCatId);
//
//                db.insert("Products", null, cv);
//            }
//            db.setTransactionSuccessful();
//        } finally {
//            db.endTransaction();
//            workbook.close();
//            fis.close();
//        }
//    }
//
//
//
//    public boolean exportMainCategoriesToExcel( String filePath) {
//        List<MainCategory> list = db.getAllMainCategories();
//        Workbook workbook = new XSSFWorkbook();
//        Sheet sheet = workbook.createSheet("MainCategories");
//
//        Row header = sheet.createRow(0);
//        header.createCell(0).setCellValue("ID");
//        header.createCell(1).setCellValue("Name");
//
//        for (int i = 0; i < list.size(); i++) {
//            Row row = sheet.createRow(i + 1);
//            row.createCell(0).setCellValue(list.get(i).getId());
//            row.createCell(1).setCellValue(list.get(i).getName());
//        }
//
//        try (FileOutputStream fos = new FileOutputStream(filePath)) {
//            workbook.write(fos);
//            workbook.close();
//            return true;
//        } catch (IOException e) {
//            e.printStackTrace();
//            return false;
//        }
//    }
//
//
//    public boolean importMainCategoriesFromExcel(String filePath) {
//        try (FileInputStream fis = new FileInputStream(filePath)) {
//            Workbook workbook = new XSSFWorkbook(fis);
//            Sheet sheet = workbook.getSheetAt(0);
//            db.clearMainCategories(); // Optional: avoid duplicates
//
//            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
//                Row row = sheet.getRow(i);
//                if (row == null) continue;
//                String name = row.getCell(1).getStringCellValue();
//                db.insertMainCategory(new MainCategory(0, name));
//            }
//
//            workbook.close();
//            return true;
//        } catch (IOException e) {
//            e.printStackTrace();
//            return false;
//        }
//    }
//
//
//
//    public boolean exportSubCategoriesToExcel(String filePath) {
//        List<SubCategory> list = db.getAllSubCategories();
//        Workbook workbook = new XSSFWorkbook();
//        Sheet sheet = workbook.createSheet("SubCategories");
//
//        Row header = sheet.createRow(0);
//        header.createCell(0).setCellValue("ID");
//        header.createCell(1).setCellValue("Name");
//        header.createCell(2).setCellValue("MainCategoryID");
//
//        for (int i = 0; i < list.size(); i++) {
//            Row row = sheet.createRow(i + 1);
//            row.createCell(0).setCellValue(list.get(i).getId());
//            row.createCell(1).setCellValue(list.get(i).getName());
//            row.createCell(2).setCellValue(list.get(i).getMainCategoryId());
//        }
//
//        try (FileOutputStream fos = new FileOutputStream(filePath)) {
//            workbook.write(fos);
//            workbook.close();
//            return true;
//        } catch (IOException e) {
//            e.printStackTrace();
//            return false;
//        }
//    }
//
//
//    public boolean importSubCategoriesFromExcel(String filePath) {
//        try (FileInputStream fis = new FileInputStream(filePath)) {
//            Workbook workbook = new XSSFWorkbook(fis);
//            Sheet sheet = workbook.getSheetAt(0);
//            db.clearSubCategories(); // Optional
//
//            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
//                Row row = sheet.getRow(i);
//                if (row == null) continue;
//                String name = row.getCell(1).getStringCellValue();
//                int mainCategoryId = (int) row.getCell(2).getNumericCellValue();
//                db.insertSubCategory(new SubCategory(0, name, mainCategoryId));
//            }
//
//            workbook.close();
//            return true;
//        } catch (IOException e) {
//            e.printStackTrace();
//            return false;
//        }
//    }
//
//
//
//}



//===================================ui ========================================================================================

//package com.example.inventorymanager;
//
//import android.content.Intent;
//import android.content.pm.PackageManager;
//import android.os.Build;
//import android.os.Bundle;
//import android.os.Environment;
//import android.provider.Settings;
//import android.widget.Button;
//import android.widget.Toast;
//
//import androidx.activity.EdgeToEdge;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.core.app.ActivityCompat;
//import androidx.core.content.ContextCompat;
//import androidx.core.graphics.Insets;
//import androidx.core.view.ViewCompat;
//import androidx.core.view.WindowInsetsCompat;
//
//import com.example.inventorymanager.Database.DatabaseHelper;
//import com.example.inventorymanager.Helper.ExportImportHelper;
//
//public class MainActivity extends AppCompatActivity {
//
//    private static final int STORAGE_PERMISSION_CODE = 101;
//    DatabaseHelper dbHelper;
//    ExportImportHelper helper;
//
//
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
//        setContentView(R.layout.activity_main);
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });
//
//
//
//        Button btnExportMain = findViewById(R.id.btn_export_main_category);
//        Button btnImportMain = findViewById(R.id.btn_import_main_category);
//        Button btnExportSub = findViewById(R.id.btn_export_sub_category);
//        Button btnImportSub = findViewById(R.id.btn_import_sub_category);
//
//        btnExportMain.setOnClickListener(v -> {
//            String path = Environment.getExternalStorageDirectory() + "/main_categories.xlsx";
//            if (helper.exportMainCategoriesToExcel(path)) {
//                Toast.makeText(this, "Main Categories exported", Toast.LENGTH_SHORT).show();
//            }
//        });
//
//        btnImportMain.setOnClickListener(v -> {
//            String path = Environment.getExternalStorageDirectory() + "/main_categories.xlsx";
//            if (helper.importMainCategoriesFromExcel(path)) {
//                Toast.makeText(this, "Main Categories imported", Toast.LENGTH_SHORT).show();
//            }
//        });
//
//        btnExportSub.setOnClickListener(v -> {
//            String path = Environment.getExternalStorageDirectory() + "/sub_categories.xlsx";
//            if (helper.exportSubCategoriesToExcel(path)) {
//                Toast.makeText(this, "Sub Categories exported", Toast.LENGTH_SHORT).show();
//            }
//        });
//
//        btnImportSub.setOnClickListener(v -> {
//            String path = Environment.getExternalStorageDirectory() + "/sub_categories.xlsx";
//            if (helper.importSubCategoriesFromExcel(path)) {
//                Toast.makeText(this, "Sub Categories imported", Toast.LENGTH_SHORT).show();
//            }
//        });
//
//
//
//
//
//
//        dbHelper = new DatabaseHelper(this);
//        helper = new ExportImportHelper();
//
//        checkPermission(); // Ensure permissions
//
//        Button btnExport = findViewById(R.id.btn_export_excel);
//        Button btnImport = findViewById(R.id.btn_import_excel);
//
//        btnExport.setOnClickListener(v -> {
//            String filePath = Environment.getExternalStorageDirectory() + "/products_export.xlsx";
//            if (helper.exportProductsToExcel()) {
//                Toast.makeText(this, "Exported to " + filePath, Toast.LENGTH_LONG).show();
//            } else {
//                Toast.makeText(this, "Export failed", Toast.LENGTH_SHORT).show();
//            }
//        });
//
//        btnImport.setOnClickListener(v -> {
//            String filePath = Environment.getExternalStorageDirectory() + "/products_export.xlsx";
//            if (helper.importProductsFromExcel()) {
//                Toast.makeText(this, "Imported from " + filePath, Toast.LENGTH_LONG).show();
//            } else {
//                Toast.makeText(this, "Import failed", Toast.LENGTH_SHORT).show();
//            }
//        });
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//    }
//
//
//
//
//
//    private void checkPermission() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//            if (!Environment.isExternalStorageManager()) {
//                Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
//                startActivity(intent);
//            }
//        } else {
//            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
//                    != PackageManager.PERMISSION_GRANTED) {
//                ActivityCompat.requestPermissions(this,
//                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
//                                Manifest.permission.READ_EXTERNAL_STORAGE},
//                        STORAGE_PERMISSION_CODE);
//            }
//        }
//    }
//
//
//
//
//
//}


//=================================================================xml ====================================================================================


//<?xml version="1.0" encoding="utf-8"?>
//<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
//xmlns:app="http://schemas.android.com/apk/res-auto"
//xmlns:tools="http://schemas.android.com/tools"
//android:id="@+id/main"
//android:layout_width="match_parent"
//android:layout_height="match_parent"
//tools:context=".MainActivity"
//android:orientation="vertical"
//        >
//
//    <Button
//android:id="@+id/btn_export_main_category"
//android:layout_width="match_parent"
//android:layout_height="wrap_content"
//android:text="Export Main Categories" />
//
//    <Button
//android:id="@+id/btn_import_main_category"
//android:layout_width="match_parent"
//android:layout_height="wrap_content"
//android:text="Import Main Categories" />
//
//    <Button
//android:id="@+id/btn_export_sub_category"
//android:layout_width="match_parent"
//android:layout_height="wrap_content"
//android:text="Export Sub Categories" />
//
//    <Button
//android:id="@+id/btn_import_sub_category"
//android:layout_width="match_parent"
//android:layout_height="wrap_content"
//android:text="Import Sub Categories" />
//
//    <Button
//android:id="@+id/btn_export_excel"
//android:layout_width="match_parent"
//android:layout_height="wrap_content"
//android:text="Export to Excel"
//app:layout_constraintBottom_toBottomOf="parent" />
//
//    <Button
//android:id="@+id/btn_import_excel"
//android:layout_width="match_parent"
//android:layout_height="wrap_content"
//android:text="Import from Excel"
//app:layout_constraintBottom_toTopOf="@+id/textView" />
//
//
//</LinearLayout>
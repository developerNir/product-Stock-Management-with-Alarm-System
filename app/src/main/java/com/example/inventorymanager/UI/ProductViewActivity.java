package com.example.inventorymanager.UI;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.inventorymanager.Adapter.ProductAdapter;
import com.example.inventorymanager.Database.DatabaseHelper;
import com.example.inventorymanager.Model.ProductModel;
import com.example.inventorymanager.Model.SubCategory;
import com.example.inventorymanager.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ProductViewActivity extends AppCompatActivity {


    private static final int PICK_IMAGE_REQUEST = 4 ;
    RecyclerView recyclerProducts;
    FloatingActionButton btnAddProduct, showSettings;


    List<ProductModel> productList = new ArrayList<>();
    ProductAdapter adapter;
    DatabaseHelper db;
    int subCatId; // from intent or default


    private ImageView dialogImageView;
    private final Bitmap[] selectedImage = {null};
    private Bitmap selectedImageBitmap = null; // field in activity

    Bitmap selectedBitmap;






    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_product_view);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        db = new DatabaseHelper(this);
        subCatId = getIntent().getIntExtra("subId", -1);
        showSettings = findViewById(R.id.showSettings);

        recyclerProducts = findViewById(R.id.recyclerProducts);
        btnAddProduct = findViewById(R.id.btnAddProduct);
        recyclerProducts.setLayoutManager(new LinearLayoutManager(this));



        SearchView searchView = findViewById(R.id.searchViewProduct);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.getFilter().filter(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return true;
            }
        });



        showSettings.setOnClickListener(v -> {
            showSettingsDialog();
        });


        adapter = new ProductAdapter(this, productList, new ProductAdapter.OnProductClickListener() {
            @Override
            public void onEdit(ProductModel product) {
                showProductDialog(true, product);
            }

            @Override
            public void onDelete(ProductModel product) {
//                db.deleteProduct(product.getId());
                onDeletePupupDialog(db, product);
//                loadProducts();
            }

            @Override
            public void onView(ProductModel product) {

            }

        });

        recyclerProducts.setAdapter(adapter);

        btnAddProduct.setOnClickListener(v -> showProductDialog(false, null));

        loadProducts();
    }           // this is end ====================================================================================================




    public void onDeletePupupDialog(DatabaseHelper db, ProductModel product) {
        new AlertDialog.Builder(ProductViewActivity.this)
                .setTitle("Warning!")
                .setMessage("Are you sure you want to delete \"" + product.getName() + "\"?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton("Yes", (dialog, which) -> {
                    db.deleteProduct(product.getId());
                    loadProducts();
                    Toast.makeText(ProductViewActivity.this, "Deleted!", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .show();
    }




//    private void loadProducts() {
//        Cursor cursor = db.getProductsBySubCat(subCatId);
//        List<ProductModel> tempList = new ArrayList<>();
//        if (cursor.moveToFirst()) {
//            do {
//                @SuppressLint("Range") int id = cursor.getInt(cursor.getColumnIndex("id"));
//                @SuppressLint("Range") String name = cursor.getString(cursor.getColumnIndex("name"));
//                @SuppressLint("Range") String img = cursor.getString(cursor.getColumnIndex("image"));
//
//
//
//                @SuppressLint("Range") String desc = cursor.getString(cursor.getColumnIndex("description"));
//                @SuppressLint("Range") double price = cursor.getDouble(cursor.getColumnIndex("price"));
//                @SuppressLint("Range") int qty = cursor.getInt(cursor.getColumnIndex("quantity"));
//                @SuppressLint("Range") String stock = cursor.getString(cursor.getColumnIndex("stock"));
//                tempList.add(new ProductModel(id, name, img, desc, price, qty, stock, subCatId));
//
//            } while (cursor.moveToNext());
//        }
//        cursor.close();
//
//        adapter.updateData(tempList); // refresh both fullList & filteredList
//    }


    private void loadProducts() {
        Cursor cursor = db.getProductsBySubCat(subCatId);
        List<ProductModel> tempList = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                byte[] imgBytes = cursor.getBlob(cursor.getColumnIndexOrThrow("image"));

                Bitmap image = (imgBytes != null) ? BitmapFactory.decodeByteArray(imgBytes, 0, imgBytes.length) : null;
                String desc = cursor.getString(cursor.getColumnIndexOrThrow("description"));
                double price = cursor.getDouble(cursor.getColumnIndexOrThrow("price"));
                int qty = cursor.getInt(cursor.getColumnIndexOrThrow("quantity"));
                String stock = cursor.getString(cursor.getColumnIndexOrThrow("stock"));

                tempList.add(new ProductModel(id, name, imgBytes, desc, price, qty, stock, subCatId));
            } while (cursor.moveToNext());
        }
        cursor.close();
        adapter.updateData(tempList);
    }



//    private void showProductDialog(boolean isEdit, ProductModel product) {
//    AlertDialog.Builder builder = new AlertDialog.Builder(this);
//    View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_product, null);
//    builder.setView(view);
//    AlertDialog dialog = builder.create();
//
//    // Initialize DatabaseHelper
//    DatabaseHelper db = new DatabaseHelper(this);
//
//    // Initialize views from dialog's layout
//    ImageView btnSelectImage = view.findViewById(R.id.btnSelectImage);
//    TextInputEditText etName = view.findViewById(R.id.etName);
//    TextInputEditText etDescription = view.findViewById(R.id.etDescription);
//    TextInputEditText etPrice = view.findViewById(R.id.etPrice);
//    TextInputEditText etQuantity = view.findViewById(R.id.etQuantity);
//    TextInputEditText etStock = view.findViewById(R.id.etStock);
//    MaterialButton btnSave = view.findViewById(R.id.btnSave);
//
//    // Bitmap for selected image
//    final Bitmap[] selectedImage = {null};
//
//    // Handle image selection
//    btnSelectImage.setOnClickListener(v -> {
//        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//        startActivityForResult(intent, 101); // 101 is requestCode for gallery
//    });
//
//    // Pre-fill fields if editing
//    if (isEdit && product != null) {
//        etName.setText(product.getName());
//        etDescription.setText(product.getDescription());
//        etPrice.setText(String.valueOf(product.getPrice()));
//        etQuantity.setText(String.valueOf(product.getQuantity()));
//        etStock.setText(product.getStock());
//        // Load image into ImageView if using URL or bitmap
//        // Picasso.get().load(product.getImage()).into(btnSelectImage);
//    }
//
//    btnSave.setOnClickListener(v -> {
//        String name = etName.getText().toString().trim();
//        String desc = etDescription.getText().toString().trim();
//        String priceStr = etPrice.getText().toString().trim();
//        String qtyStr = etQuantity.getText().toString().trim();
//        String stock = etStock.getText().toString().trim();
//
//        if (name.isEmpty()) {
//            etName.setError("Name required");
//            return;
//        }
//
//        double price = 0;
//        int qty = 0;
//
//        try {
//            price = Double.parseDouble(priceStr.isEmpty() ? "0" : priceStr);
//        } catch (NumberFormatException e) {
//            etPrice.setError("Invalid price");
//            return;
//        }
//
//        try {
//            qty = Integer.parseInt(qtyStr.isEmpty() ? "0" : qtyStr);
//        } catch (NumberFormatException e) {
//            etQuantity.setError("Invalid quantity");
//            return;
//        }
//
//        // Insert or update
//        if (isEdit && product != null) {
////            db.updateProduct(product.getId(), name, desc, price, qty, stock, selectedImage != null ? selectedImage[0] : null);
//            db.updateProduct(product.getId(), name, "image", desc, price, qty, stock);
//        } else {
////            db.insertProduct(name, desc, price, qty, stock, selectedImage != null ? selectedImage[0] : null, subCatId);
//            db.insertProduct(name, "image", desc, price, qty, stock, subCatId);
//        }
//
//        loadProducts(); // refresh RecyclerView
//        dialog.dismiss();
//    });
//
//    dialog.show();
//}

private void showProductDialog(boolean isEdit, ProductModel product) {
    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_product, null);
    builder.setView(view);
    AlertDialog dialog = builder.create();

    DatabaseHelper db = new DatabaseHelper(this);

    int PICK_IMAGE_REQUEST = 1;



//    ImageView btnSelectImage = view.findViewById(R.id.btnSelectImage);
    dialogImageView = view.findViewById(R.id.btnSelectImage);


    TextInputEditText etName = view.findViewById(R.id.etName);
    TextInputEditText etDescription = view.findViewById(R.id.etDescription);
    TextInputEditText etPrice = view.findViewById(R.id.etPrice);
    TextInputEditText etQuantity = view.findViewById(R.id.etQuantity);
    TextInputEditText etStock = view.findViewById(R.id.etStock);
    MaterialButton btnSave = view.findViewById(R.id.btnSave);


    final Bitmap[] selectedImage = {selectedImageBitmap}; // reference the field

    dialogImageView.setOnClickListener(v -> {
//        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//        startActivityForResult(intent, 101);
//        openGallery();
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);

    });


    // Prefill when editing
    if (isEdit && product != null) {
        etName.setText(product.getName());
        etDescription.setText(product.getDescription());
        etPrice.setText(String.valueOf(product.getPrice()));
        etQuantity.setText(String.valueOf(product.getQuantity()));
        etStock.setText(product.getStock());


        if (product.getImage() != null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(product.getImage(), 0, product.getImage().length);
            dialogImageView.setImageBitmap(bitmap);
            selectedImage[0] = bitmap;
        }

    }

    btnSave.setOnClickListener(v -> {
        String name = etName.getText().toString().trim();
        String desc = etDescription.getText().toString().trim();
        String priceStr = etPrice.getText().toString().trim();
        String qtyStr = etQuantity.getText().toString().trim();
        String stock = etStock.getText().toString().trim();

        if (name.isEmpty()) {
            etName.setError("Name required");
            return;
        }

        double price;
        int qty;
        try {
            price = Double.parseDouble(priceStr.isEmpty() ? "0" : priceStr);
        } catch (NumberFormatException e) {
            etPrice.setError("Invalid price");
            return;
        }

        try {
            qty = Integer.parseInt(qtyStr.isEmpty() ? "0" : qtyStr);
        } catch (NumberFormatException e) {
            etQuantity.setError("Invalid quantity");
            return;
        }

//        if (isEdit && product != null) {
//            db.updateProduct(product.getId(), name, selectedImage[0], desc, price, qty, stock);
//        } else {
//            db.insertProduct(name, selectedImage[0], desc, price, qty, stock, subCatId);
//        }

//        if (isEdit && product != null) {
//            db.updateProduct(product.getId(), name,
//                    Arrays.toString(selectedImage[0] != null ? bitmapToBytes(selectedImage[0]) : product.getImage()),
//                    desc, price, qty, stock);
//        } else {
//            db.insertProduct(name,
//                    selectedImage[0] != null ? Arrays.toString(bitmapToBytes(selectedImage[0])) : null,
//                    desc, price, qty, stock, subCatId);
//        }

        if (isEdit && product != null) {
            db.updateProduct(
                    product.getId(),
                    name,
                    selectedImage[0] != null ? bitmapToBytes(selectedImage[0]) : product.getImage(), // keep it byte[]
                    desc,
                    price,
                    qty,
                    stock
            );
        } else {
            db.insertProduct(
                    name,
                    selectedImage[0] != null ? bitmapToBytes(selectedImage[0]) : null, // keep it byte[]
//                    bitmapToBytes(selectedImage[0]),
                    desc,
                    price,
                    qty,
                    stock,
                    subCatId
            );
        }



        loadProducts();
        dialog.dismiss();
    });

    dialog.show();
}


    // Convert Bitmap to byte[]
    private byte[] bitmapToBytes(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }

    // Example usage while saving





    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.settings_dialog, null);
        builder.setView(view);
        AlertDialog dialog = builder.create();

        EditText etThreshold = view.findViewById(R.id.etThreshold);
        Spinner spinnerFrequency = view.findViewById(R.id.spinnerFrequency);
        Button btnSave = view.findViewById(R.id.btnSaveSettings);

        // Frequency options
//        String[] options = {"Once", "Every Hour", "Every Day"};
//        String[] options = {"Once", "Every Hour", "Every Day", "Every 10 Minutes"};
        String[] options = {"Once", "Every Minute", "Every 10 Minutes", "Every Hour", "Every Day"};


        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, options);
        spinnerFrequency.setAdapter(adapter);

        // Load saved preferences
        SharedPreferences prefs = getSharedPreferences("settings", MODE_PRIVATE);
        int savedThreshold = prefs.getInt("low_stock_threshold", 5);
        int savedFreq = prefs.getInt("low_stock_frequency", 0);

        etThreshold.setText(String.valueOf(savedThreshold));
        spinnerFrequency.setSelection(savedFreq);



        btnSave.setOnClickListener(v -> {
            String thresholdStr = etThreshold.getText().toString().trim();
            if (thresholdStr.isEmpty()) {
                etThreshold.setError("Enter a valid threshold");
                return;
            }

            int threshold = Integer.parseInt(thresholdStr);
            int freq = spinnerFrequency.getSelectedItemPosition();

            prefs.edit()
                    .putInt("low_stock_threshold", threshold)
                    .putInt("low_stock_frequency", freq)
                    .apply();

            Toast.makeText(this, "Settings Saved", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });


        dialog.show();
    }





    // Open gallery to select image
    private void openGallery() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                    4);
        } else {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, 4);
        }
    }



    public byte[] getBytesFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream); // can use JPEG
        return stream.toByteArray();
    }

    public Bitmap getBitmapFromBytes(byte[] imageBytes) {
        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_settings) {
            showSettingsDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (requestCode == 101 && resultCode == RESULT_OK && data != null) {
//            Uri imageUri = data.getData();
//            try {
//                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
//
//                // Store bitmap in field
//                selectedImageBitmap = bitmap;
//
//                // Update ImageView in dialog
//                ImageView btnSelectImage = findViewById(R.id.btnSelectImage);
//                if (btnSelectImage != null) {
//                    btnSelectImage.setImageBitmap(bitmap);
//                }
//
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            try {
                if (Build.VERSION.SDK_INT >= 29) {
                    ImageDecoder.Source source = ImageDecoder.createSource(getContentResolver(), imageUri);
                    selectedBitmap = ImageDecoder.decodeBitmap(source);
                } else {
                    selectedBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                }

                // show image inside the dialog's ImageView ✅
                if (dialogImageView != null) {
                    dialogImageView.setImageBitmap(selectedBitmap);
                }

                // also save to field so you can insert into DB
                selectedImageBitmap = selectedBitmap;

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}
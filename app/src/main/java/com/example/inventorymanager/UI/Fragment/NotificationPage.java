package com.example.inventorymanager.UI.Fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.example.inventorymanager.Adapter.ProductAdapter;
import com.example.inventorymanager.Database.DatabaseHelper;
import com.example.inventorymanager.Model.ProductModel;
import com.example.inventorymanager.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;


public class NotificationPage extends Fragment {

    RecyclerView recyclerProducts;
    FloatingActionButton btnAddProduct, showSettings;


    List<ProductModel> productList = new ArrayList<>();
    ProductAdapter adapter;
    DatabaseHelper db;
    LottieAnimationView lottieAnimationView;


    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_notification_page, container, false);


        recyclerProducts = view.findViewById(R.id.recyclerProducts);
        btnAddProduct = view.findViewById(R.id.btnAddProduct);
        recyclerProducts.setLayoutManager(new LinearLayoutManager(getContext()));
        lottieAnimationView = view.findViewById(R.id.lottieAnimation);


        db= new DatabaseHelper(getContext());

        SearchView searchView = view.findViewById(R.id.searchViewProduct);
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



        adapter = new ProductAdapter(getContext(), productList, new ProductAdapter.OnProductClickListener() {
            @Override
            public void onEdit(ProductModel product) {
//                showProductDialog(true, product);
                Toast.makeText(getContext(), "Please Go To Main product page\n then Edit The Product", Toast.LENGTH_SHORT).show();
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


        loadProducts(db);



    return view;
    }





    public void onDeletePupupDialog(DatabaseHelper db, ProductModel product) {
        new AlertDialog.Builder(getContext())
                .setTitle("Warning!")
                .setMessage("Are you sure you want to delete \"" + product.getName() + "\"?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton("Yes", (dialog, which) -> {
                    db.deleteProduct(product.getId());
                    loadProducts(db);
                    Toast.makeText(getContext(), "Deleted!", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .show();
    }



    private void loadProducts(DatabaseHelper db) {

        List<ProductModel> productList = new ArrayList<>();
        // Get products with quantity <= 5
        Cursor cursor = db.getLowStockProducts(7);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                byte[] image = cursor.getBlob(cursor.getColumnIndexOrThrow("image"));
                String description = cursor.getString(cursor.getColumnIndexOrThrow("description"));
                double price = cursor.getDouble(cursor.getColumnIndexOrThrow("price"));
                int quantity = cursor.getInt(cursor.getColumnIndexOrThrow("quantity"));
                String stock = cursor.getString(cursor.getColumnIndexOrThrow("stock"));
                int subCatId = cursor.getInt(cursor.getColumnIndexOrThrow("subCatId"));

                productList.add(new ProductModel(id, name, image, description, price, quantity, stock, subCatId));
            } while (cursor.moveToNext());
        }
        cursor.close();



        // ✅ Toggle visibility
        if (productList.isEmpty()) {
            recyclerProducts.setVisibility(View.GONE);
            lottieAnimationView.setVisibility(View.VISIBLE);
            lottieAnimationView.playAnimation(); // start animation
        } else {
            recyclerProducts.setVisibility(View.VISIBLE);
            lottieAnimationView.setVisibility(View.GONE);
            adapter.updateData(productList);
        }

    }







}
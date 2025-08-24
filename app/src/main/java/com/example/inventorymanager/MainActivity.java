package com.example.inventorymanager;



import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.Toast;


import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.inventorymanager.Adapter.MainCategoryAdapter;

import com.example.inventorymanager.Database.DatabaseHelper;
import com.example.inventorymanager.Model.MainCategory;
import com.example.inventorymanager.Model.SubCategory;
import com.example.inventorymanager.UI.SubCatagoryActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;


import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {


    RecyclerView recyclerView;
    DatabaseHelper dbHelper;
    List<MainCategory> list;
    MainCategoryAdapter adapter;
    SearchView searchView;
    FloatingActionButton btnAddCatagory;
    int updateId = -1;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });



        dbHelper = new DatabaseHelper(this);



        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        searchView = findViewById(R.id.searchViewMainCatagory);
        btnAddCatagory = findViewById(R.id.btnAddCatagory);

        loadCategories();

        btnAddCatagory.setOnClickListener(v -> {
            showDialog(null);
        });


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



    }

    private void loadCategories() {
        list = new ArrayList<>();
        Cursor cursor = dbHelper.getAllMainCategories();
        while (cursor.moveToNext()) {
            list.add(new MainCategory(cursor.getInt(0), cursor.getString(1)));
        }
        cursor.close();

        adapter = new MainCategoryAdapter(this, list, new MainCategoryAdapter.OnMainCategoryClickListener() {
            @Override
            public void onEdit(MainCategory category) {
//                editName.setText(category.getName());
                showDialog(category);
                updateId = category.getId();
            }

            @Override
            public void onDelete(MainCategory category) {
                onDeletePupupDialog(dbHelper, category);
                loadCategories();
            }

            @Override
            public void activityChange(MainCategory category) {
                int iditme = category.getId();
                Intent myIntent = new Intent(MainActivity.this, SubCatagoryActivity.class);
                myIntent.putExtra("id", iditme);
                startActivity(myIntent);
            }
        });

        recyclerView.setAdapter(adapter);
    }


    private void showDialog(@Nullable MainCategory mainCategory) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(mainCategory == null ? "Add Category" : "Edit Category");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        if (mainCategory != null) input.setText(mainCategory.getName());
        builder.setView(input);

        builder.setPositiveButton("Save", (dialog, which) -> {
            String name = input.getText().toString().trim();
            if (name.isEmpty()) return;
            boolean result;
            if (updateId == -1) {
                result = dbHelper.insertMainCategory(name);
            } else {
                result = dbHelper.updateMainCategory(updateId, name);
                updateId = -1;
            }
            if (result) {
                input.setText("");
                loadCategories();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }



    public void onDeletePupupDialog(DatabaseHelper db, MainCategory mainCategory) {
        new AlertDialog.Builder(MainActivity.this)
                .setTitle("Warning!")
                .setMessage("Are you sure you want to delete \"" + mainCategory.getName() + "\"?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton("Yes", (dialog, which) -> {
                    db.deleteMainCategory(mainCategory.getId());
                    loadCategories();
                    Toast.makeText(MainActivity.this, "Deleted!", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .show();
    }


}
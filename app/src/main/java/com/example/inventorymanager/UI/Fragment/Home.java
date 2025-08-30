package com.example.inventorymanager.UI.Fragment;

import static com.example.inventorymanager.R.drawable.button_bg_orange;

import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.inventorymanager.Adapter.MainCategoryAdapter;
import com.example.inventorymanager.Database.DatabaseHelper;
import com.example.inventorymanager.MainActivity;
import com.example.inventorymanager.Model.MainCategory;
import com.example.inventorymanager.R;
import com.example.inventorymanager.UI.SubCatagoryActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;


public class Home extends Fragment {



    RecyclerView recyclerView;
    DatabaseHelper dbHelper;
    List<MainCategory> list;
    MainCategoryAdapter adapter;
    SearchView searchView;
    FloatingActionButton btnAddCatagory;
    int updateId = -1;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);



        dbHelper = new DatabaseHelper(getContext());



        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        searchView = view.findViewById(R.id.searchViewMainCatagory);
        btnAddCatagory =view.findViewById(R.id.btnAddCatagory);


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








        return view;
    }// the end ========================================//// ===========================================================

    private void loadCategories() {
        list = new ArrayList<>();
        Cursor cursor = dbHelper.getAllMainCategories();
        while (cursor.moveToNext()) {
            list.add(new MainCategory(cursor.getInt(0), cursor.getString(1)));
        }
        cursor.close();

        adapter = new MainCategoryAdapter(getContext(), list, new MainCategoryAdapter.OnMainCategoryClickListener() {
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
                Intent myIntent = new Intent(getContext(), SubCatagoryActivity.class);
                myIntent.putExtra("id", iditme);
                startActivity(myIntent);
            }
        });

        recyclerView.setAdapter(adapter);
    }


    private void showDialog(@Nullable MainCategory mainCategory) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(mainCategory == null ? "Add Category" : "Edit Category");

        final EditText input = new EditText(getContext());
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
        AlertDialog dialog = builder.create();
        dialog.show();

        dialog.getWindow().setBackgroundDrawableResource(R.drawable.background);
        Button positive = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        Button negative = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);

        positive.setTextColor(Color.WHITE);
        input.setTextColor(Color.WHITE);

        negative.setTextColor(Color.WHITE);

    }



    public void onDeletePupupDialog(DatabaseHelper db, MainCategory mainCategory) {
        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setTitle("Warning!")
                .setMessage("Are you sure you want to delete \"" + mainCategory.getName() + "\"?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton("Yes", (d, which) -> {
                    db.deleteMainCategory(mainCategory.getId());
                    loadCategories();
                    Toast.makeText(getContext(), "Deleted!", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("No", (d, which) -> d.dismiss())
                .create();

        dialog.show();

// set custom background
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.background);


        Button positive = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        Button negative = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);

        positive.setBackgroundResource(button_bg_orange);
        positive.setTextColor(Color.WHITE);

        negative.setBackgroundResource(button_bg_orange);
        negative.setTextColor(Color.WHITE);
    }










}
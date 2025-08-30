package com.example.inventorymanager.UI.Fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.inventorymanager.Adapter.SubCategoryAdapter;
import com.example.inventorymanager.Database.DatabaseHelper;
import com.example.inventorymanager.Model.SubCategory;
import com.example.inventorymanager.R;
import com.example.inventorymanager.UI.ProductViewActivity;
import com.example.inventorymanager.UI.SubCatagoryActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;


public class SubCatagoryItem extends Fragment {


    RecyclerView recyclerView;

    FloatingActionButton btnAdd;
    List<SubCategory> subCategoryList = new ArrayList<>();
    SubCategoryAdapter adapter;
    DatabaseHelper db;
    SearchView searchView;
//    int mainCatId;


    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sub_catagory_item, container, false);


        db = new DatabaseHelper(getContext());
//        mainCatId = view.getId().("id", -1);

        recyclerView = view.findViewById(R.id.recyclerViewSubCategory);
        btnAdd = view.findViewById(R.id.btnAddSubCategory);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        searchView = view.findViewById(R.id.searchViewsubCata);


        adapter = new SubCategoryAdapter(getContext(), subCategoryList, new SubCategoryAdapter.OnItemActionListener() {
            @Override
            public void onEdit(SubCategory subCategory) {
                showDialog(subCategory);
            }

            @Override
            public void onDelete(SubCategory subCategory) {

                onDeletePupupDialog(db,subCategory);
            }

            @Override
            public void changeActivity(SubCategory subCategory) {
                int subId = subCategory.getId();
                Intent myIntent = new Intent(getContext(), ProductViewActivity.class);
                myIntent.putExtra("subId", subId);
                startActivity(myIntent);
            }
        });


//        search functionality =====================
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






        recyclerView.setAdapter(adapter);

        btnAdd.setOnClickListener(v -> showDialog(null));

        loadSubCategories();




        return view;
    }



    private void loadSubCategories() {


        Cursor cursor = db.getAllSubCategories();
        List<SubCategory> list = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                int mainCatId = cursor.getInt(cursor.getColumnIndexOrThrow("mainCatId"));
                list.add(new SubCategory(id, name, id));
                Log.d("SubCategory", "ID: " + id + " Name: " + name + " MainCatId: " + mainCatId);
            } while (cursor.moveToNext());
        }
        cursor.close();


        adapter.updateList(list); // update adapter, this keeps filteredList synced
    }




    private void showDialog(@Nullable SubCategory subCategory) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(subCategory == null ? "Add SubCategory" : "Edit SubCategory");

        final EditText input = new EditText(getContext());
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        if (subCategory != null) input.setText(subCategory.getName());
        builder.setView(input);

        builder.setPositiveButton("Save", (dialog, which) -> {
            String name = input.getText().toString().trim();
            if (name.isEmpty()) return;

            if (subCategory == null) {
                db.insertSubCategory(name, getId());
            } else {
                db.updateSubCategory(subCategory.getId(), name);
            }
            loadSubCategories();
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }



    public void onDeletePupupDialog(DatabaseHelper db, SubCategory subCategory) {
        new AlertDialog.Builder(getContext())
                .setTitle("Warning!")
                .setMessage("Are you sure you want to delete \"" + subCategory.getName() + "\"?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton("Yes", (dialog, which) -> {
                    db.deleteSubCategory(subCategory.getId());
                    loadSubCategories();
                    Toast.makeText(getContext(), "Deleted!", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .show();
    }




}
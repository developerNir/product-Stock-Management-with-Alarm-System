package com.example.inventorymanager.UI;

import static android.view.View.GONE;
import static com.example.inventorymanager.R.drawable.button_bg_orange;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.example.inventorymanager.Adapter.SubCategoryAdapter;
import com.example.inventorymanager.Database.DatabaseHelper;
import com.example.inventorymanager.MainActivity;
import com.example.inventorymanager.Model.MainCategory;
import com.example.inventorymanager.Model.SubCategory;
import com.example.inventorymanager.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class SubCatagoryActivity extends AppCompatActivity {


    RecyclerView recyclerView;

    FloatingActionButton btnAdd;
    List<SubCategory> subCategoryList = new ArrayList<>();
    SubCategoryAdapter adapter;
    DatabaseHelper db;
    SearchView searchView;
    int mainCatId;

    TextView text_notfound;
    LottieAnimationView lottieAnimation;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sub_catagory);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.orange)); // Your color
        }





        db = new DatabaseHelper(this);
        mainCatId = getIntent().getIntExtra("id", -1);

        recyclerView = findViewById(R.id.recyclerViewSubCategory);
        btnAdd = findViewById(R.id.btnAddSubCategory);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        searchView = findViewById(R.id.searchViewsubCata);

        text_notfound = findViewById(R.id.text_notfound);
        lottieAnimation = findViewById(R.id.lottieAnimation);

        adapter = new SubCategoryAdapter(this, subCategoryList, new SubCategoryAdapter.OnItemActionListener() {
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
                Intent myIntent = new Intent(SubCatagoryActivity.this, ProductViewActivity.class);
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




    }



    private void loadSubCategories() {
        Cursor cursor = db.getSubCategoriesByMainId(mainCatId);
        List<SubCategory> list = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") int id = cursor.getInt(cursor.getColumnIndex("id"));
                @SuppressLint("Range") String name = cursor.getString(cursor.getColumnIndex("name"));
                @SuppressLint("Range") int mainId = cursor.getInt(cursor.getColumnIndex("mainCatId"));
                list.add(new SubCategory(id, name, mainId));
            } while (cursor.moveToNext());
        }
        cursor.close();



        // ✅ Toggle visibility
        if (list.isEmpty()) {
            recyclerView.setVisibility(GONE);
            lottieAnimation.setVisibility(View.VISIBLE);
            text_notfound.setVisibility(View.VISIBLE);
            lottieAnimation.playAnimation(); // start animation
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            lottieAnimation.setVisibility(GONE);
            text_notfound.setVisibility(GONE);
            adapter.updateList(list);
        }


    }




    private void showDialog(@Nullable SubCategory subCategory) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(subCategory == null ? "Add SubCategory" : "Edit SubCategory");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        if (subCategory != null) input.setText(subCategory.getName());
        builder.setView(input);

        builder.setPositiveButton("Save", (dialog, which) -> {
            String name = input.getText().toString().trim();
            if (name.isEmpty()) return;

            if (subCategory == null) {
                db.insertSubCategory(name, mainCatId);
            } else {
                db.updateSubCategory(subCategory.getId(), name);
            }
            loadSubCategories();
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



    public void onDeletePupupDialog(DatabaseHelper db, SubCategory subCategory) {
        AlertDialog dialog = new AlertDialog.Builder(SubCatagoryActivity.this)
                .setTitle("Warning!")
                .setMessage("Are you sure you want to delete \"" + subCategory.getName() + "\"?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton("Yes", (dialogg, which) -> {
                    db.deleteSubCategory(subCategory.getId());
                    loadSubCategories();
                    Toast.makeText(SubCatagoryActivity.this, "Deleted!", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("No", (dialogg, which) -> dialogg.dismiss())
                .show();



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
package com.example.inventorymanager.UI;

import static androidx.core.view.GravityCompat.START;

import static com.example.inventorymanager.R.id.action_search;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;

import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.Toast;


import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;

import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.inventorymanager.R;
import com.example.inventorymanager.UI.Fragment.AllProudct;
import com.example.inventorymanager.UI.Fragment.Home;
import com.example.inventorymanager.UI.Fragment.NotificationPage;
import com.example.inventorymanager.UI.Fragment.Settings;
import com.example.inventorymanager.UI.Fragment.SubCatagoryItem;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;


public class AppBody extends AppCompatActivity {




    NavigationView navigationView;
    MaterialToolbar toolbar;
    FrameLayout frameLayout;

    DrawerLayout drawerLayout;






    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_app_body);
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.orange)); // Your color
        }



        navigationView = findViewById(R.id.navigationManu);
        toolbar = findViewById(R.id.toolbarApp);
        frameLayout =findViewById(R.id.frameLayout);
        drawerLayout = findViewById(R.id.main);
        // header image and button introduce ------------------------
        setSupportActionBar(toolbar);

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(
                AppBody.this,
                drawerLayout,
                toolbar,
                R.string.close,
                R.string.open
        );



        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();



        fragmentReplace(new Home(), "Main Category");
// navigation item selected ======================================
      navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
          @Override
          public boolean onNavigationItemSelected(@NonNull MenuItem item) {


      if (item.getItemId() == R.id.main_catagory) {
                fragmentReplace(new Home(), "Main Category");
            } else if (item.getItemId() == R.id.sub_catagory) {
                fragmentReplace(new SubCatagoryItem(), "Sub Category");
            } else if (item.getItemId() == R.id.all_product) {
                fragmentReplace(new AllProudct(), "All Products");
            } else if (item.getItemId() == R.id.settings) {
                fragmentReplace(new Settings(), "Settings");
            } else if (item.getItemId() == R.id.notification) {
                fragmentReplace(new NotificationPage(), "Low Stock Product");
            }
            drawerLayout.closeDrawer(START);
            return true;
          }
      });



//        navigationView.setNavigationItemSelectedListener(item -> {
//            if (item.getItemId() == R.id.main_catagory) {
//                fragmentReplace(new Home(), "Main Category");
//            } else if (item.getItemId() == R.id.sub_catagory) {
//                fragmentReplace(new SubCatagoryItem(), "Sub Category");
//            } else if (item.getItemId() == R.id.all_product) {
//                fragmentReplace(new AllProudct(), "All Products");
//            } else if (item.getItemId() == R.id.settings) {
//                fragmentReplace(new Settings(), "Settings");
//            } else if (item.getItemId() == R.id.notification) {
//                fragmentReplace(new NotificationPage(), "Low Stock Product");
//            }
//            drawerLayout.closeDrawer(START);
//            return true;
//        });

// app bar or tool bar icon select and click =======================================================

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {


                if (item.getItemId() == R.id.main_catagory){
                    fragmentReplace(new Home(), "Main Category");
                    drawerLayout.closeDrawer(START);
                } else if (item.getItemId()== R.id.sub_catagory) {
                    fragmentReplace(new SubCatagoryItem(), "Sub Category");
                    drawerLayout.closeDrawer(START);
                }else if (item.getItemId() == R.id.all_product) {
                    fragmentReplace(new AllProudct(), "All Products");
                    drawerLayout.closeDrawer(START);
                } else if (item.getItemId() == R.id.settings) {
                    fragmentReplace(new Settings(), "Settings");
                    drawerLayout.closeDrawer(START);
                } else if (item.getItemId() == R.id.notification) {
                    fragmentReplace(new NotificationPage(), "Low Stock Product");
                    drawerLayout.closeDrawer(START);
                }




                return true;



            }
        });










        setUpOnBackPressed();


    }//end ================================================================================================================================

    // fragment replace ----------------------------------
    private void fragmentReplace(Fragment fragment, String title){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, fragment);
        fragmentTransaction.commit();
        getSupportActionBar().setTitle(title); // now works because of setSupportActionBar(toolbar)
    }



    public void setUpOnBackPressed(){
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                new AlertDialog.Builder(AppBody.this)
                        .setTitle("Confirm Exit")
                        .setMessage("Are you sure you want to exit?")
                        .setIcon(R.drawable.exit_icon)
                        .setNegativeButton("No thanks", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // No click and Worked this Code ===============
                                dialogInterface.dismiss();
                            }
                        })
                        .setPositiveButton("Yes, Exit", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // Yes click and Worked this Code ===============
                                dialogInterface.dismiss();
                                finishAndRemoveTask();
                            }
                        })
                        .show();


            }
        });
    }
    // OnBack passed end ===============================


    // ✅ Inflate Toolbar Menu (so search + three dots appear)
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.toolbar_item, menu);
//        return true;
//    }

//     ✅ Handle Toolbar Menu Clicks
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_search) {
            Toast.makeText(this, "Search clicked", Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.action_settings) {
            Toast.makeText(this, "Settings clicked", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_item, menu);

        MenuItem searchItem = menu.findItem(action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();

        // Listen for query text change
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Called when user presses search/enter
                Toast.makeText(AppBody.this, "Searching: " + query, Toast.LENGTH_SHORT).show();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Called every time text changes
                // e.g. filter list / RecyclerView dynamically
                return false;
            }
        });

        return true;
    }


//    @SuppressLint("NonConstantResourceId")
//    @Override
//    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
//        switch (item.getItemId()) {
//            case android.R.id.home: // drawer menu icon
//                drawerLayout.openDrawer(GravityCompat.START);
//                return true;
//
//            case action_search:
//                // already handled by SearchView
//                return true;
//
//            case R.id.action_settings:
//                Toast.makeText(this, "Settings clicked", Toast.LENGTH_SHORT).show();
//                return true;
//
//
//        }
//        return super.onOptionsItemSelected(item);
//    }


}
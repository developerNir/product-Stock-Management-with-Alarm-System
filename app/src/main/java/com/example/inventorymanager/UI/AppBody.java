package com.example.inventorymanager.UI;

import static androidx.core.view.GravityCompat.START;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.FrameLayout;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.inventorymanager.R;
import com.example.inventorymanager.UI.Fragment.Home;
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


        navigationView = findViewById(R.id.navigationManu);
        toolbar = findViewById(R.id.toolbarApp);
        frameLayout =findViewById(R.id.frameLayout);
        drawerLayout = findViewById(R.id.main);
        // header image and button introduce ------------------------


        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(
                AppBody.this, drawerLayout, toolbar, R.string.close, R.string.open
        );

        drawerLayout.addDrawerListener(actionBarDrawerToggle);


        fragmentReplace(new Home());
// navigation item selected ======================================
      navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
          @Override
          public boolean onNavigationItemSelected(@NonNull MenuItem item) {

              if (item.getItemId() == R.id.main_catagory){

                  fragmentReplace(new Home());
                  drawerLayout.closeDrawer(START);
              } else if (item.getItemId()== R.id.sub_catagory) {
                  fragmentReplace(new Home());
                  drawerLayout.closeDrawer(START);
              }
              return true;
          }
      });

// app bar or tool bar icon select and click =======================================================

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {


                if (item.getItemId() == R.id.main_catagory){
                    fragmentReplace(new Home());
                    drawerLayout.closeDrawer(START);
                } else if (item.getItemId()== R.id.sub_catagory) {
                    fragmentReplace(new Home());
                    drawerLayout.closeDrawer(START);
                }




                return true;



            }
        });












    }//end ================================================================================================================================

    // fragment replace ----------------------------------
    private void fragmentReplace(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, fragment);
        fragmentTransaction.commit();
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



}
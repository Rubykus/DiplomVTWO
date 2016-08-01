package com.rubykus.realmvtwo.ui.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.rubykus.realmvtwo.R;
import com.rubykus.realmvtwo.model.Category;
import com.rubykus.realmvtwo.ui.adapters.CategoriesRecyclerViewAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;

public class CategoriesActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public Realm realm;

    @BindView(R.id.recycler_view)RecyclerView recyclerView;
    @BindView(R.id.toolbar)Toolbar toolbar;
    @BindView(R.id.fab)FloatingActionButton fab;
    @BindView(R.id.drawer_layout)DrawerLayout drawer;
    @BindView(R.id.nav_view)NavigationView navigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.categories);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createCategories();
            }
        });

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        realm = Realm.getDefaultInstance();
        registerForContextMenu(recyclerView);
        setUpRecyclerView();
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.categories) {
            onBackPressed();
        } else if (id == R.id.goods) {
            Intent intent = new Intent(this, GoodsActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.card) {
            Intent intent = new Intent(this, BasketActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.sales) {
            Intent intent = new Intent(this, SaleActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.about_us) {
            Intent intent = new Intent(this, AboutUsActivity.class);
            startActivity(intent);
        } else if (id == R.id.exit) {
            finish();
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        realm.close();
    }

    /**
     * MY METHODS
     */
    private void setUpRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new CategoriesRecyclerViewAdapter(this, realm.where(Category.class).findAllAsync().sort("id")));
        recyclerView.setHasFixedSize(true);
    }

    public void createCategories(){
        View viewDialog = LayoutInflater.from(CategoriesActivity.this).inflate(R.layout.dialog_cat, null);
        final EditText nameCat = ButterKnife.findById(viewDialog, R.id.nameAddCat);
        AlertDialog.Builder builder = new AlertDialog.Builder(CategoriesActivity.this);
        builder.setTitle(R.string.new_cat)
                .setView(viewDialog)
                .setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final String textNameCat = nameCat.getText().toString();
                        try {
                            if (textNameCat.equals("")) {
                                throw new Exception();
                            }
                            realm.executeTransactionAsync(new Realm.Transaction() {
                                @Override
                                public void execute(Realm realm) {
                                    Category cat = realm.createObject(Category.class);
                                    cat.setId(realm.where(Category.class).max("id").intValue()+1);
                                    cat.setName(textNameCat);
                                }
                            });
                        } catch (Exception e){
                            Toast.makeText(CategoriesActivity.this, R.string.error_validations, Toast.LENGTH_LONG).show();
                        }
                    }
                })
                .setNegativeButton(R.string.cancle, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {}
                });
        AlertDialog dialog_check = builder.create();
        dialog_check.show();
    }

    public void deleteCategory(Category item) {
        final int id = item.getId();
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.where(Category.class).equalTo("id", id)
                        .findAll()
                        .deleteAllFromRealm();
            }
        });
    }

    public void editCategory(Category item){
        final int id = item.getId();
        final String name = item.getName();
        View viewDialog = LayoutInflater.from(CategoriesActivity.this).inflate(R.layout.dialog_cat, null);
        final EditText nameCat = ButterKnife.findById(viewDialog, R.id.nameAddCat);
        nameCat.setText(name);
        AlertDialog.Builder builder = new AlertDialog.Builder(CategoriesActivity.this);
        builder.setTitle(R.string.new_cat)
                .setView(viewDialog)
                .setPositiveButton(R.string.update, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final String textNameCat = nameCat.getText().toString();
                        try {
                            if (textNameCat.equals("")) {
                                throw new Exception();
                            }
                            realm.executeTransactionAsync(new Realm.Transaction() {
                                @Override
                                public void execute(Realm realm) {
                                    realm.where(Category.class).equalTo("id", id)
                                            .findFirst().setName(nameCat.getText().toString());
                                }
                            });
                        } catch (Exception e){
                            Toast.makeText(CategoriesActivity.this, R.string.error_validations, Toast.LENGTH_LONG).show();
                        }
                    }
                })
                .setNegativeButton(R.string.cancle, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {}
                });
        AlertDialog dialog_check = builder.create();
        dialog_check.show();
    }
}

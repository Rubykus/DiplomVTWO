package com.rubykus.realmvtwo.ui.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.rubykus.realmvtwo.R;
import com.rubykus.realmvtwo.model.Sale;
import com.rubykus.realmvtwo.ui.GridSpacingItemDecoration;
import com.rubykus.realmvtwo.ui.adapters.SaleRecyclerViewAdapter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by Rubykus on 31.07.2016.
 */
public class SaleActivity extends AppCompatActivity
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
        getSupportActionBar().setTitle(R.string.sales);

        fab.setVisibility(View.GONE);

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
            Intent intent = new Intent(this, CategoriesActivity.class);
            startActivity(intent);
        } else if (id == R.id.goods) {
            Intent intent = new Intent(this, GoodsActivity.class);
            startActivity(intent);
        } else if (id == R.id.card) {
            Intent intent = new Intent(this, BasketActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.sales) {
            onBackPressed();
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
        recyclerView.setAdapter(new SaleRecyclerViewAdapter(this, realm.where(Sale.class).findAllAsync().sort("id")));
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(1, 50, false));
        recyclerView.setHasFixedSize(true);
    }

    public void deleteSale(Sale item) {
        final int id = item.getId();
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.where(Sale.class).equalTo("id", id)
                        .findAll()
                        .deleteAllFromRealm();
            }
        });
    }

    public void createCheck(Sale item) {

        final String sFileName = "sale-" + item.getId() + ".pdf";
        String sBody = "Код продажи: " + item.getId()
                + "\n\nДата: " + item.getDate()
                + "\n\nТовары:\n" + item.getInfo()
                + "\n\nСумма: " + item.getSum();


        try {
            File root = new File(Environment.getExternalStorageDirectory(), "Check");
            if (!root.exists()) {
                root.mkdirs();
            }
            final File gpxfile = new File(root, sFileName);
            FileWriter writer = new FileWriter(gpxfile);
            writer.write(sBody);
            writer.flush();
            writer.close();

            AlertDialog.Builder builder = new AlertDialog.Builder(SaleActivity.this);
            builder.setMessage("Показать чек?")
                    .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent i = new Intent();
                            i.setAction(android.content.Intent.ACTION_VIEW);
                            i.setDataAndType(Uri.fromFile(gpxfile), "text/plain");
                            startActivity(i);
                        }
                    })
                    .setNegativeButton("Нет", null);
            builder.show();
        } catch (IOException e) {
            Toast.makeText(this, "Ошибка.", Toast.LENGTH_SHORT).show();
        }
    }

}

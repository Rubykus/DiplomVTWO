package com.rubykus.realmvtwo.ui.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import com.rubykus.realmvtwo.R;
import com.rubykus.realmvtwo.model.Basket;
import com.rubykus.realmvtwo.model.Good;
import com.rubykus.realmvtwo.model.Sale;
import com.rubykus.realmvtwo.ui.adapters.BasketRecyclerViewAdapter;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by Rubykus on 31.07.2016.
 */
public class BasketActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public Realm realm;

    @BindView(R.id.recycler_view)RecyclerView recyclerView;
    @BindView(R.id.toolbar)Toolbar toolbar;
    @BindView(R.id.drawer_layout)DrawerLayout drawer;
    @BindView(R.id.nav_view)NavigationView navigationView;
    @BindView(R.id.clearCard)Button clear;
    @BindView(R.id.toOrder)Button add;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basket);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        realm = Realm.getDefaultInstance();
        registerForContextMenu(recyclerView);
        setUpRecyclerView();

        RealmResults<Basket> results = realm.where(Basket.class).findAllAsync().sort("idGood");
        Basket[] baskets = results.toArray(new Basket[results.size()]);
        if (baskets.length == 0){
            getSupportActionBar().setTitle(R.string.empty_basket);
        }else {
            getSupportActionBar().setTitle(R.string.basket);
        }

        // buttons
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(BasketActivity.this);
                builder.setMessage("Вы уверены что хотите удалить ВСЕ товары из корзины?")
                        .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                RealmResults<Basket> results = realm.where(Basket.class).findAllAsync().sort("idGood");
                                final Basket[] baskets = results.toArray(new Basket[results.size()]);
                                for (int i = 0; i < baskets.length; i++){
                                    final int id = baskets[i].getIdGood();
                                    final int count = baskets[i].getCount();
                                    realm.executeTransactionAsync(new Realm.Transaction() {
                                        @Override
                                        public void execute(Realm realm) {
                                            Good good = realm.where(Good.class).equalTo("id", id).findFirst();
                                            good.setQuantity(good.getQuantity()+ count);
                                        }
                                    });
                                }

                                realm.executeTransactionAsync(new Realm.Transaction() {
                                    @Override
                                    public void execute(Realm realm) {
                                        realm.where(Basket.class).findAll().deleteAllFromRealm();
                                    }
                                });

                            }
                        })
                        .setNegativeButton("Нет", null);
                builder.show();
            }
        });
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RealmResults<Basket> results = realm.where(Basket.class).findAllAsync().sort("idGood");
                Basket[] arrBasket = results.toArray(new Basket[results.size()]);
                if (arrBasket.length != 0) {
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                    String date = sdf.format(new Date());
                    double sum = 0;
                    String info = "";
                    final DecimalFormat twoDForm = new DecimalFormat("#.##");
                    for (int i = 0; i < arrBasket.length; i++) {
                        sum += arrBasket[i].getSum();
                        info += arrBasket[i].getName() + "     " +
                                arrBasket[i].getCount()+"X"+
                                arrBasket[i].getPrice()+ "      " +
                                arrBasket[i].getSum()+"\n";
                    }
                    final String finalDate = date;
                    final String finalInfo = info;
                    final double finalSum = sum;
                    AlertDialog.Builder builder = new AlertDialog.Builder(BasketActivity.this);
                    builder.setTitle("Приобрести")
                            .setMessage("Вы действительно желаете приобрести все товары на суму "
                                    +Double.valueOf(twoDForm.format(sum))+" грн.")
                            .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    realm.executeTransactionAsync(new Realm.Transaction() {
                                        @Override
                                        public void execute(Realm realm) {
                                            Sale sale = realm.createObject(Sale.class);
                                            sale.setId(realm.where(Sale.class).max("id").intValue()+1);
                                            sale.setDate(finalDate);
                                            sale.setInfo(finalInfo);
                                            sale.setSum(Double.valueOf(twoDForm.format(finalSum)));
                                        }
                                    });
                                    realm.executeTransactionAsync(new Realm.Transaction() {
                                        @Override
                                        public void execute(Realm realm) {
                                            realm.where(Basket.class).findAll().deleteAllFromRealm();
                                        }
                                    });
                                }
                            })
                            .setNegativeButton("Нет", null);
                    builder.show();
                } else {
                    Toast.makeText(BasketActivity.this, "Ваша корзина пуста.", Toast.LENGTH_LONG).show();
                }
            }
        });
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
            finish();
        } else if (id == R.id.goods) {
            Intent intent = new Intent(this, GoodsActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.card) {
            onBackPressed();
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
        recyclerView.setAdapter(new BasketRecyclerViewAdapter(this, realm.where(Basket.class).findAllAsync()));
        recyclerView.setHasFixedSize(true);
    }

    public void deleteBasketItem(Basket item) {
        final int id = item.getIdGood();
        final int count = item.getCount();

        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Good good = realm.where(Good.class).equalTo("id", id).findFirst();
                good.setQuantity(good.getQuantity()+ count);
            }
        });

        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.where(Basket.class).equalTo("idGood", id)
                        .findAll()
                        .deleteAllFromRealm();
            }
        });
    }


}

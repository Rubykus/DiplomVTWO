package com.rubykus.realmvtwo.ui.activities;

import android.content.ActivityNotFoundException;
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
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.rubykus.realmvtwo.R;
import com.rubykus.realmvtwo.model.Category;
import com.rubykus.realmvtwo.model.Good;
import com.rubykus.realmvtwo.ui.DecimalDigitsInputFilter;
import com.rubykus.realmvtwo.ui.GridSpacingItemDecoration;
import com.rubykus.realmvtwo.ui.adapters.GoodsRecyclerViewAdapter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by Rubykus on 29.07.2016.
 */
public class GoodsActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static Realm realm;
    int index_cat;

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
        getSupportActionBar().setTitle(R.string.goods);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createGood();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        menu.add(0, 3,0, R.string.price_list);
        menu.add(0, 1,0, R.string.categories);
        menu.add(0, 2,0, R.string.all_good);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == 1){
            showListCat(null, 1);
        } else if (id == 2){
            setUpRecyclerView();
        } else if (id == 3) {
            RealmResults<Good> results = realm.where(Good.class).findAll().sort("id");
            Good[] goods = results.toArray(new Good[results.size()]);
            final String sFileName = "price-list.pdf";
            int rowCount = goods.length;

            PdfPTable table = new PdfPTable(3);

            PdfPCell cell = new PdfPCell(new Phrase("Good"));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("Quantity"));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("Price"));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
            table.setHeaderRows(1);

            for (int i= 0; i<rowCount; i++){
                table.addCell(goods[i].getName());
                table.addCell(String.valueOf(goods[i].getQuantity()));
                table.addCell(String.valueOf(goods[i].getPrice())+" UAH");
            }

            Document doc = new Document();

            try {

                File root = new File(Environment.getExternalStorageDirectory(), "Price-list");
                if (!root.exists()) {
                    root.mkdirs();
                }

                File priceList = new File(root, sFileName);
                FileOutputStream fOut = new FileOutputStream(priceList);

                PdfWriter.getInstance(doc, fOut);

                doc.open();

                Paragraph p1 = new Paragraph("Price-list");
                p1.setAlignment(Paragraph.ALIGN_CENTER);
                p1.setSpacingAfter(10);

                doc.add(p1);
                doc.add(table);

            } catch (DocumentException de) {
                Log.e("PDFCreator", "DocumentException:" + de);
            } catch (IOException e) {
                Log.e("PDFCreator", "ioException:" + e);
            } finally {
                doc.close();
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(GoodsActivity.this);
            builder.setMessage("Открыть прайс-лист?")
                    .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            File pdfFile = new File(Environment.getExternalStorageDirectory() + "/Price-list/" + sFileName);
                            Uri path = Uri.fromFile(pdfFile);

                            Intent pdfIntent = new Intent(Intent.ACTION_VIEW);
                            pdfIntent.setDataAndType(path, "application/pdf");
                            pdfIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                            try {
                                startActivity(pdfIntent);
                            } catch (ActivityNotFoundException e) {
                                Toast.makeText(GoodsActivity.this, "Can't read pdf file", Toast.LENGTH_SHORT).show();
                            }
                        }
                    })
                    .setNegativeButton("Нет", null);
            builder.show();

        }


        return super.onOptionsItemSelected(item);
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
            onBackPressed();
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
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.setAdapter(new GoodsRecyclerViewAdapter(this, realm.where(Good.class).findAllAsync().sort("id")));
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, 4, false));
    }

    // show list of categories
    public void showListCat(final TextView tv, final int id){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        RealmResults<Category> results = realm.where(Category.class).findAllAsync().sort("id");
        Category[] catList = results.toArray(new Category[results.size()]);
        int countRow = catList.length;
        final String[] cat_name = new String[countRow];
        final int[] cat_id = new int[countRow];
        for (int i = 0; i < countRow; i++) {
            cat_name[i] = catList[i].getName();
            cat_id[i] = catList[i].getId();
        }
        builder.setTitle("Выберите категорию")
                .setItems(cat_name, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (id == 0) {
                            tv.setText(cat_name[which]);
                            index_cat = cat_id[which];
                        } else if (id == 1) {
                            recyclerView.setAdapter(new GoodsRecyclerViewAdapter(GoodsActivity.this,
                                    realm.where(Good.class).equalTo("idCat", cat_id[which]).findAll().sort("id")));
                            recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, 4, false));
                        }
                    }
                });
        AlertDialog dialog_choose = builder.create();
        dialog_choose.show();
    }

    // return name category
    public static String getNameCat(int idCat){
        RealmResults<Category> results = realm.where(Category.class).findAllAsync().sort("id");
        Category[] catList = results.toArray(new Category[results.size()]);
        HashMap<Integer,String> collectionCat = new HashMap<>();
        for (int i = 0; i < catList.length; i++){
            collectionCat.put(catList[i].getId(), catList[i].getName());
        }
        return collectionCat.get(idCat);
    }

    // open activity with single good
    public void openSingleGood(Good item){

        Intent intent = new Intent(this, SingleGoodActivity.class);
        intent.putExtra("id_good", item.getId());
        intent.putExtra("name_good", item.getName());
        intent.putExtra("cat_good", item.getIdCat());
        intent.putExtra("color_good", item.getColor());
        intent.putExtra("sex_good", item.getSex());
        intent.putExtra("firm_good", item.getFirm());
        intent.putExtra("quantity_good", item.getQuantity());
        intent.putExtra("price_good", item.getPrice());
        intent.putExtra("img_good", item.getImg());
        startActivity(intent);

    }

    // CRUD
    public void deleteGood(Good item) {
        final int id = item.getId();
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.where(Good.class).equalTo("id", id)
                        .findAll()
                        .deleteAllFromRealm();
            }
        });
    }

    public void createGood() {
        // initialize view component
        View view = LayoutInflater.from(GoodsActivity.this).inflate(R.layout.dialog_good, null);
        final EditText goodName = ButterKnife.findById(view, R.id.goodAddName);
        final TextView goodIdCat = ButterKnife.findById(view, R.id.goodAddCat);
        goodIdCat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showListCat(goodIdCat, 0);
            }
        });
        final EditText goodColor = ButterKnife.findById(view, R.id.goodAddColor);
        final EditText goodSex = ButterKnife.findById(view, R.id.goodAddSex);
        final EditText goodFirm = ButterKnife.findById(view, R.id.goodAddFirm);
        final EditText goodQuantity = ButterKnife.findById(view, R.id.goodAddQuantity);
        final EditText goodPrice = ButterKnife.findById(view, R.id.goodAddPrice);
        goodPrice.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(7,2)});
        final EditText goodImg = ButterKnife.findById(view, R.id.goodAddImg);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.new_good)
                .setView(view)
                .setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            final String textName = goodName.getText().toString();
                            final int idCat = index_cat;
                            final String textColor = goodColor.getText().toString();
                            final String textSex = goodSex.getText().toString();
                            final String textFirm = goodFirm.getText().toString();
                            final int textQuantity = Integer.parseInt(goodQuantity.getText().toString());
                            final double textPrice = Double.parseDouble(goodPrice.getText().toString());
                            final String textImg = goodImg.getText().toString();
                            // validation
                            if ( index_cat == 0 || textName.isEmpty() || textColor.isEmpty() || textSex.isEmpty() ||
                                    textFirm.isEmpty() || textImg.isEmpty()) {
                                throw new Exception();
                            }
                            // create
                            realm.executeTransactionAsync(new Realm.Transaction() {
                                @Override
                                public void execute(Realm realm) {
                                    Good good = realm.createObject(Good.class);
                                    good.setId(realm.where(Good.class).max("id").intValue()+1);
                                    good.setName(textName);
                                    good.setIdCat(idCat);
                                    good.setColor(textColor);
                                    good.setSex(textSex);
                                    good.setFirm(textFirm);
                                    good.setQuantity(textQuantity);
                                    good.setPrice(textPrice);
                                    good.setImg(textImg);
                                }
                            });
                        } catch (Exception e) {
                            Toast.makeText(GoodsActivity.this, R.string.error_validations, Toast.LENGTH_LONG).show();
                        }
                        index_cat = 0;
                    }
                })
                .setNegativeButton(R.string.cancle, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        index_cat = 0;
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void editGood(Good item){
        // initialize view component
        View view = LayoutInflater.from(GoodsActivity.this).inflate(R.layout.dialog_good, null);
        final EditText goodName = ButterKnife.findById(view, R.id.goodAddName);
        final TextView goodIdCat = ButterKnife.findById(view, R.id.goodAddCat);
        goodIdCat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showListCat(goodIdCat, 0);
            }
        });
        final EditText goodColor = ButterKnife.findById(view, R.id.goodAddColor);
        final EditText goodSex = ButterKnife.findById(view, R.id.goodAddSex);
        final EditText goodFirm = ButterKnife.findById(view, R.id.goodAddFirm);
        final EditText goodQuantity = ButterKnife.findById(view, R.id.goodAddQuantity);
        final EditText goodPrice = ButterKnife.findById(view, R.id.goodAddPrice);
        goodPrice.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(7,2)});
        final EditText goodImg = ButterKnife.findById(view, R.id.goodAddImg);
        // set text in view components
        final int id = item.getId();
        goodName.setText(item.getName());
        final int idCat = item.getIdCat();
        goodIdCat.setText(getNameCat(idCat));
        goodIdCat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showListCat(goodIdCat, 0);
            }
        });
        goodColor.setText(item.getColor());
        goodSex.setText(item.getSex());
        goodFirm.setText(item.getFirm());
        goodQuantity.setText(String.valueOf(item.getQuantity()));
        goodPrice.setText(String.valueOf(item.getPrice()));
        goodImg.setText(item.getImg());
        // create alert dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.update)
                .setView(view)
                .setPositiveButton(R.string.update, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            // check changing in id_cat
                            final int currentIdCat = index_cat == 0 ? idCat : index_cat;
                            // get text
                            final String textName = goodName.getText().toString();
                            final String textColor = goodColor.getText().toString();
                            final String textSex = goodSex.getText().toString();
                            final String textFirm = goodFirm.getText().toString();
                            final int textQuantity = Integer.parseInt(goodQuantity.getText().toString());
                            final double textPrice = Double.parseDouble(goodPrice.getText().toString());
                            final String textImg = goodImg.getText().toString();
                            // validation
                            if ( textName.isEmpty() || textColor.isEmpty() || textSex.isEmpty() ||
                                    textFirm.isEmpty() || textImg.isEmpty()) {
                                throw new Exception();
                            }
                            //update
                            realm.executeTransactionAsync(new Realm.Transaction() {
                                @Override
                                public void execute(Realm realm) {
                                    Good good = realm.where(Good.class).equalTo("id", id)
                                            .findFirst();
                                    good.setName(textName);
                                    good.setIdCat(currentIdCat);
                                    good.setColor(textColor);
                                    good.setSex(textSex);
                                    good.setFirm(textFirm);
                                    good.setQuantity(textQuantity);
                                    good.setPrice(textPrice);
                                    good.setImg(textImg);

                                }
                            });

                        } catch (Exception e) {
                            Toast.makeText(GoodsActivity.this, R.string.error_validations, Toast.LENGTH_LONG).show();
                        }
                        index_cat = 0;
                    }
                })
                .setNegativeButton(R.string.cancle, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        index_cat = 0;
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}

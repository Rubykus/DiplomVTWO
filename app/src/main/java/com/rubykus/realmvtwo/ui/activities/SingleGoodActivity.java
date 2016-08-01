package com.rubykus.realmvtwo.ui.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.rubykus.realmvtwo.R;
import com.rubykus.realmvtwo.model.Basket;
import com.rubykus.realmvtwo.model.Good;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmResults;
import static com.rubykus.realmvtwo.ui.activities.GoodsActivity.getNameCat;
import static com.rubykus.realmvtwo.ui.activities.GoodsActivity.realm;

/**
 * Created by Rubykus on 31.07.2016.
 */
public class SingleGoodActivity extends AppCompatActivity {

    public Realm realm;

    public int varId, varQuantity, varCount;
    public String varName, textImg;
    public double varPrice;

    @BindView(R.id.toolbar)Toolbar toolbar;
    @BindView(R.id.idGood)TextView idGood;
    @BindView(R.id.nameGood)TextView nameGood;
    @BindView(R.id.catGood)TextView catGood;
    @BindView(R.id.colorGood)TextView colorGood;
    @BindView(R.id.sexGood)TextView sexGood;
    @BindView(R.id.firmGood)TextView firmGood;
    @BindView(R.id.quantityGood)TextView quantityGood;
    @BindView(R.id.priceGood)TextView priceGood;
    @BindView(R.id.imgGood)ImageView imgGood;



    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_good);

        ButterKnife.bind(this);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        realm = Realm.getDefaultInstance();

        varId = extras.getInt("id_good");
        varName = extras.getString("name_good");
        varPrice = extras.getDouble("price_good");
        varQuantity = extras.getInt("quantity_good");

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(String.valueOf(varName));

        idGood.setText(String.valueOf(varId));
        nameGood.setText(varName);
        catGood.setText(getNameCat(extras.getInt("cat_good")));
        colorGood.setText(extras.getString("color_good"));
        sexGood.setText(extras.getString("sex_good"));
        firmGood.setText(extras.getString("firm_good"));
        quantityGood.setText(String.valueOf(varQuantity));
        priceGood.setText(String.valueOf(varPrice));
        textImg = extras.getString("img_good");

        Uri path = Uri.parse(Environment.getExternalStorageDirectory().toString()+"/"+textImg);
        File file = new File(String.valueOf(path));
        if (file.exists()){
            imgGood.setImageURI(path);
        }else{
            imgGood.setImageResource(R.drawable.not_img);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 1, 0, R.string.add_to_basket);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == 1) {
            final EditText count = new EditText(this);
            count.setInputType(InputType.TYPE_CLASS_NUMBER);
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.add_to_basket)
                    .setMessage("Введите количество товара.")
                    .setView(count)
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            try {
                                varCount = Integer.parseInt(count.getText().toString());

                                if (varCount > varQuantity) {
                                    throw new Exception();
                                } else {
                                    varQuantity -= varCount;
                                    quantityGood.setText(String.valueOf(varQuantity));
                                    realm.executeTransactionAsync(new Realm.Transaction() {
                                        @Override
                                        public void execute(Realm realm) {
                                            Good good = realm.where(Good.class).equalTo("id", varId).findFirst();
                                            good.setQuantity(varQuantity);
                                        }
                                    });
                                }

                                final double sum = varCount * varPrice;
                                int counter = 0;

                                RealmResults<Basket> results = realm.where(Basket.class).findAllAsync().sort("idGood");
                                Basket[] arrBasket = results.toArray(new Basket[results.size()]);

                                // if this good is exist in basket
                                for (int i=0; i<arrBasket.length; i++) {
                                    if (varId == arrBasket[i].getIdGood()) {

                                        realm.executeTransactionAsync(new Realm.Transaction() {
                                            @Override
                                            public void execute(Realm realm) {
                                                Basket basket = realm.where(Basket.class).equalTo("idGood", varId).findFirst();
                                                basket.setCount(basket.getCount() + varCount);
                                                basket.setSum(basket.getSum() + sum);
                                            }
                                        });
                                    } else {
                                        counter++;
                                    }
                                }
                                // if this good is not exist in basket
                                if (counter == arrBasket.length) {
                                    realm.executeTransactionAsync(new Realm.Transaction() {
                                        @Override
                                        public void execute(Realm realm) {
                                            Basket basket = realm.createObject(Basket.class);
                                            basket.setIdGood(varId);
                                            basket.setName(varName);
                                            basket.setCount(varCount);
                                            basket.setPrice(varPrice);
                                            basket.setSum(sum);
                                            basket.setImg(textImg);
                                        }
                                    });
                                    Toast.makeText(SingleGoodActivity.this, "Товар добавлен.", Toast.LENGTH_LONG).show();
                                    finish();
                                } else {
                                    Toast.makeText(SingleGoodActivity.this, "Товар обновлен.", Toast.LENGTH_LONG).show();
                                    finish();
                                }
                            } catch (Exception e){
                                Toast.makeText(SingleGoodActivity.this, "Некорректные данные.", Toast.LENGTH_LONG).show();
                            }
                        }
                    })
                    .setNegativeButton(R.string.cancle, null);
            AlertDialog dialog = builder.create();
            dialog.show();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        realm.close();
    }
}
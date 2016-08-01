package com.rubykus.realmvtwo.ui.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.rubykus.realmvtwo.R;
import com.rubykus.realmvtwo.ui.RoundedImageView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Rubykus on 01.08.2016.
 */
public class StartActivity  extends Activity {

    @BindView(R.id.imageCat) RoundedImageView ivCat;
    @BindView(R.id.imageGood) RoundedImageView ivGood;
    @BindView(R.id.imageSale) RoundedImageView ivSale;
    @BindView(R.id.imageCard) RoundedImageView ivCard;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_activity);

        ButterKnife.bind(this);

        ivCat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StartActivity.this, CategoriesActivity.class);
                startActivity(intent);
                finish();
            }
        });
        ivGood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StartActivity.this, GoodsActivity.class);
                startActivity(intent);
                finish();
            }
        });
        ivSale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StartActivity.this, SaleActivity.class);
                startActivity(intent);
                finish();
            }
        });
        ivCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StartActivity.this, BasketActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}

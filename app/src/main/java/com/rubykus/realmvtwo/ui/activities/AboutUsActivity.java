package com.rubykus.realmvtwo.ui.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.rubykus.realmvtwo.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Rubykus on 01.08.2016.
 */
public class AboutUsActivity extends AppCompatActivity {

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.about) TextView about;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_us_activity);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.about_us);
        String text = "Проэкт разработан в рамках дипломной работы, студентам курса РПЗ 12 1/9 Ераком Егором Валерьевичем.";
        about.setText(text);
    }

}
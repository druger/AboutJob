package com.druger.aboutwork.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.druger.aboutwork.R;

public class CompanyDetailActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView site;
    private TextView description;
    private ImageView downDrop;
    private ImageView upDrop;
    private ImageView imgToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsingToolbar);

        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        site = (TextView) findViewById(R.id.site);
        description = (TextView) findViewById(R.id.content_description);
        downDrop = (ImageView) findViewById(R.id.down_drop);
        upDrop = (ImageView) findViewById(R.id.up_drop);
        imgToolbar = (ImageView) findViewById(R.id.img_toolbar);

        description.setVisibility(View.GONE);
        downDrop.setOnClickListener(this);
        upDrop.setOnClickListener(this);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        Intent intent = getIntent();

        collapsingToolbar.setTitle(intent.getStringExtra("name"));
        collapsingToolbar.setExpandedTitleColor(ContextCompat.getColor(this, android.R.color.transparent));

        String iSite = intent.getStringExtra("site");
        String iDescription = intent.getStringExtra("description");
        if (iSite != null) {
            site.setText(iSite);
        }
        if (iDescription != null) {
            description.setText(Html.fromHtml(iDescription));
        }

        Glide.with(this)
                .load(intent.getStringExtra("logo"))
                .placeholder(R.drawable.default_company)
                .error(R.drawable.default_company)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imgToolbar);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.down_drop:
                downDrop.setVisibility(View.INVISIBLE);
                upDrop.setVisibility(View.VISIBLE);
                description.setVisibility(View.VISIBLE);
                break;
            case R.id.up_drop:
                upDrop.setVisibility(View.INVISIBLE);
                downDrop.setVisibility(View.VISIBLE);
                description.setVisibility(View.GONE);
                break;
        }
    }
}

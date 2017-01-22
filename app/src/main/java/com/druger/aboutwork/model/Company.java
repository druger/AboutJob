package com.druger.aboutwork.model;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.druger.aboutwork.R;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.gson.annotations.SerializedName;
import com.mikepenz.fastadapter.items.AbstractItem;

import java.util.List;

/**
 * Created by druger on 24.07.2016.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Company extends AbstractItem<Company, Company.CompanyVH> {

    private String id;
    private String name;
    @JsonIgnore
    @SerializedName("logo_urls")
    private Logo logo;

    public Company() {
    }

    public Company(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Logo getLogo() {
        return logo;
    }

    public void setLogo(Logo logo) {
        this.logo = logo;
    }

    static class Logo {
        @SerializedName("90")
        String logo_90;
        @SerializedName("240")
        String logo_240;
        String original;

        public String getLogo_90() {
            return logo_90;
        }

        public void setLogo_90(String logo_90) {
            this.logo_90 = logo_90;
        }

        public String getLogo_240() {
            return logo_240;
        }

        public void setLogo_240(String logo_240) {
            this.logo_240 = logo_240;
        }

        public String getOriginal() {
            return original;
        }

        public void setOriginal(String original) {
            this.original = original;
        }
    }

    @Override
    public int getType() {
        return R.id.item_company;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.item_company;
    }

    @Override
    public void bindView(CompanyVH holder, List<Object> payloads) {
        super.bindView(holder, payloads);
        holder.name.setText(name);

        Glide.with(holder.itemView.getContext())
                .load(logo.logo_90)
                .placeholder(R.drawable.default_company)
                .error(R.drawable.default_company)
                .fitCenter()
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.logo);
    }

    @Override
    public void unbindView(CompanyVH holder) {
        super.unbindView(holder);
        holder.name.setText(null);
    }

    protected static class CompanyVH extends RecyclerView.ViewHolder {

        ImageView logo;
        TextView name;

        public CompanyVH(View itemView) {
            super(itemView);
            logo = (ImageView) itemView.findViewById(R.id.logo_comapny);
            name = (TextView) itemView.findViewById(R.id.name_company);
        }
    }
}

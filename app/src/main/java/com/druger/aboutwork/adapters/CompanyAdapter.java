package com.druger.aboutwork.adapters;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.druger.aboutwork.R;
import com.druger.aboutwork.model.Company;
import com.druger.aboutwork.model.Logo;

/**
 * Created by druger on 28.01.2017.
 */

public class CompanyAdapter extends BaseRecyclerViewAdapter<Company, RecyclerView.ViewHolder> {
    // TODO Не отображается progress bar
    private static final int TYPE_COMPANY = 0;
    private static final int TYPE_LOADING = 1;

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_COMPANY) {
            View view = inflate(R.layout.item_company, parent);
            return new CompanyVH(view);
        } else if (viewType == TYPE_LOADING) {
            View view = inflate(R.layout.item_load, parent);
            return new LoadVH(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof CompanyVH) {
            CompanyVH companyVH = (CompanyVH) holder;
            Company company = getItem(position);
            Logo logo = company.getLogo();
            companyVH.tvName.setText(company.getName());
//            companyVH.tvCity.setText(company.getCity());

            Glide.with(holder.itemView.getContext())
                    .load(logo != null ? logo.getLogo90() : "")
                    .placeholder(R.drawable.ic_default_company)
                    .error(R.drawable.ic_default_company)
                    .fitCenter()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(companyVH.ivLogo);

            holder.itemView.setOnClickListener(v -> {
                if (clickListener != null) {
                    int pos = holder.getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        clickListener.onClick(company, pos);
                    }
                }
            });
        } else if (holder instanceof LoadVH) {
            LoadVH loadVH = (LoadVH) holder;
            loadVH.progressBar.setVisibility(View.VISIBLE);
            loadVH.progressBar.setIndeterminate(true);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return getItem(position) == null ? TYPE_LOADING : TYPE_COMPANY;
    }

    private static class CompanyVH extends BaseViewHolder {
        ImageView ivLogo;
        TextView tvName;
//        TextView tvCity;

        CompanyVH(View itemView) {
            super(itemView);
            ivLogo = bindView(R.id.ivLogoCompany);
            tvName = bindView(R.id.tvNameCompany);
//            tvCity = bindView(R.id.tvCity);
        }
    }

    private static class LoadVH extends BaseViewHolder {
        ProgressBar progressBar;

        LoadVH(View itemView) {
            super(itemView);
            progressBar = bindView(R.id.progress_bar);
        }
    }
}

package com.druger.aboutwork.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.druger.aboutwork.R;
import com.druger.aboutwork.model.Company;

/**
 * Created by druger on 28.01.2017.
 */

public class CompanyAdapter extends BaseRecyclerViewAdapter<Company, RecyclerView.ViewHolder> {
    // TODO Не отображается progress bar
    private final int TYPE_COMPANY = 0;
    private final int TYPE_LOADING = 1;

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == TYPE_COMPANY) {
            View view = inflater.inflate(R.layout.item_company, parent, false);
            return new CompanyVH(view);
        } else if (viewType == TYPE_LOADING) {
            View view = inflater.inflate(R.layout.item_load, parent, false);
            return new LoadVH(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof CompanyVH) {
            CompanyVH companyVH = (CompanyVH) holder;
            Company company = getItem(position);
            companyVH.tvName.setText(company.getName());

            Glide.with(holder.itemView.getContext())
                    .load(company.getLogo().getLogo_90())
                    .placeholder(R.drawable.default_company)
                    .error(R.drawable.default_company)
                    .fitCenter()
                    .crossFade()
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

        CompanyVH(View itemView) {
            super(itemView);
            ivLogo = bindView(R.id.ivLogoComapny);
            tvName = bindView(R.id.tvNameCompany);
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

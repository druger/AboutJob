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
import com.druger.aboutwork.recyclerview_helper.OnItemClickListener;

import java.util.List;

/**
 * Created by druger on 28.01.2017.
 */

public class CompanyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    // TODO Не отображается progress bar
    private final int TYPE_COMPANY = 0;
    private final int TYPE_LOADING = 1;

    private List<Company> companies;

    private OnItemClickListener clickListener;

    public CompanyAdapter(List<Company> companies) {
        this.companies = companies;
    }

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
            Company company = companies.get(position);
            companyVH.tvName.setText(company.getName());

            Glide.with(holder.itemView.getContext())
                    .load(company.getLogo().getLogo_90())
                    .placeholder(R.drawable.default_company)
                    .error(R.drawable.default_company)
                    .fitCenter()
                    .crossFade()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(companyVH.ivLogo);
        } else if (holder instanceof LoadVH) {
            LoadVH loadVH = (LoadVH) holder;
            loadVH.progressBar.setVisibility(View.VISIBLE);
            loadVH.progressBar.setIndeterminate(true);
        }
    }

    @Override
    public int getItemCount() {
        return companies.size();
    }

    @Override
    public int getItemViewType(int position) {
        return companies.get(position) == null ? TYPE_LOADING : TYPE_COMPANY;
    }

    public class CompanyVH extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView ivLogo;
        TextView tvName;

        public CompanyVH(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            ivLogo = (ImageView) itemView.findViewById(R.id.ivLogoComapny);
            tvName = (TextView) itemView.findViewById(R.id.tvNameCompany);
        }

        @Override
        public void onClick(View v) {
            if (clickListener != null) {
                clickListener.onClick(v, getAdapterPosition());
            }
        }
    }

    class LoadVH extends RecyclerView.ViewHolder {
        ProgressBar progressBar;

        public LoadVH(View itemView) {
            super(itemView);
            progressBar = (ProgressBar) itemView.findViewById(R.id.progress_bar);
        }
    }

    public void setOnItemClickListener(OnItemClickListener clickListener) {
        this.clickListener = clickListener;
    }
}

package com.druger.aboutwork.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.druger.aboutwork.R;
import com.druger.aboutwork.model.CatalogCompanies;

import java.util.List;

/**
 * Created by druger on 24.07.2016.
 */
public class CatalogAdapter extends RecyclerView.Adapter<CatalogAdapter.CatalogVH> {

    private List<CatalogCompanies> companies;

    public CatalogAdapter(List<CatalogCompanies> companies) {
        this.companies = companies;
    }

    public class CatalogVH extends RecyclerView.ViewHolder {

        TextView name;

        public CatalogVH(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.name_industry);
        }
    }

    @Override
    public CatalogVH onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_catalog_companies, parent, false);
        return new CatalogVH(itemView);
    }

    @Override
    public void onBindViewHolder(CatalogVH holder, int position) {
        CatalogCompanies company = companies.get(position);
        holder.name.setText(company.getName());
    }

    @Override
    public int getItemCount() {
        return companies.size();
    }

}

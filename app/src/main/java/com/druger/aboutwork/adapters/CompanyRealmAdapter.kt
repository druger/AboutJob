package com.druger.aboutwork.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.druger.aboutwork.R
import com.druger.aboutwork.interfaces.OnItemClickListener
import com.druger.aboutwork.model.realm.CompanyRealm
import io.realm.OrderedRealmCollection
import io.realm.RealmRecyclerViewAdapter
import kotlinx.android.synthetic.main.item_company.view.*


class CompanyRealmAdapter(data: OrderedRealmCollection<CompanyRealm>, var clickListener: OnItemClickListener<CompanyRealm>)
    : RealmRecyclerViewAdapter<CompanyRealm, CompanyRealmAdapter.CompanyVH>(data, true) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CompanyVH {
        val inflater = LayoutInflater.from(parent.context)
        val itemView = inflater.inflate(R.layout.item_company, parent, false)
        return CompanyVH(itemView)
    }

    override fun onBindViewHolder(holder: CompanyVH, position: Int) {
        val company = data?.get(position)
        holder.tvName.text = company!!.name

        Glide.with(holder.itemView.context)
                .load(company.logo)
                .placeholder(R.drawable.ic_default_company)
                .error(R.drawable.ic_default_company)
                .fitCenter()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.ivLogo)

        holder.itemView.setOnClickListener {
            val position = holder.adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                clickListener.onClick(company, position)
            }
        }
    }

    inner class CompanyVH (itemView: View) : RecyclerView.ViewHolder(itemView){
        val ivLogo = itemView.ivLogoCompany
        val tvName = itemView.tvNameCompany
    }
}
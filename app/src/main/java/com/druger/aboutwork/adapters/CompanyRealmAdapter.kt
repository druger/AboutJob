package com.druger.aboutwork.adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.druger.aboutwork.R
import com.druger.aboutwork.model.Company
import io.realm.OrderedRealmCollection
import io.realm.RealmRecyclerViewAdapter

class CompanyRealmAdapter(context: Context, data: OrderedRealmCollection<Company>)
    : RealmRecyclerViewAdapter<Company, CompanyRealmAdapter.CompanyVH>(data, true) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CompanyVH {
        val inflater = LayoutInflater.from(parent.context)
        val itemView = inflater.inflate(R.layout.item_company, parent, false)
        return CompanyVH(itemView)
    }

    override fun onBindViewHolder(holder: CompanyVH, position: Int) {
    }

    inner class CompanyVH (itemView: View) : RecyclerView.ViewHolder(itemView){

    }
}
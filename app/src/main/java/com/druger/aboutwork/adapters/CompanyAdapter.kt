package com.druger.aboutwork.adapters

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView

import androidx.recyclerview.widget.RecyclerView

import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.druger.aboutwork.R
import com.druger.aboutwork.model.Company

/**
 * Created by druger on 28.01.2017.
 */

class CompanyAdapter : BaseRecyclerViewAdapter<Company, RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        var view = inflate(R.layout.item_company, parent)
        val viewHolder = CompanyVH(view)
        if (viewType == TYPE_LOADING) {
            view = inflate(R.layout.item_load, parent)
            return LoadVH(view)
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is CompanyVH) {
            val company = getItem(position)
            val logo = company.logo
            holder.tvName.text = company.name

            Glide.with(holder.itemView.context)
                .load(logo?.logo90 ?: "")
                .placeholder(R.drawable.ic_default_company)
                .error(R.drawable.ic_default_company)
                .fitCenter()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.ivLogo)

            holder.itemView.setOnClickListener { v ->
                if (clickListener != null) {
                    val pos = holder.getAdapterPosition()
                    if (pos != RecyclerView.NO_POSITION) {
                        clickListener!!.onClick(company, pos)
                    }
                }
            }
        } else if (holder is LoadVH) {
            holder.progressBar.visibility = View.VISIBLE
            holder.progressBar.isIndeterminate = true
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (getItem(position) == null) TYPE_LOADING else TYPE_COMPANY
    }

    private class CompanyVH(itemView: View) : BaseViewHolder(itemView) {
        var ivLogo: ImageView = bindView(R.id.ivLogoCompany)
        var tvName: TextView = bindView(R.id.tvNameCompany)
    }

    private class LoadVH(itemView: View) : BaseViewHolder(itemView) {
        var progressBar: ProgressBar = bindView(R.id.progress_bar)
    }

    companion object {
        // TODO Не отображается progress bar
        private val TYPE_COMPANY = 0
        private val TYPE_LOADING = 1
    }
}

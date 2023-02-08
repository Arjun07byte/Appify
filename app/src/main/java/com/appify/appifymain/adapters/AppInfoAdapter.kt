package com.appify.appifymain.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.appify.appifymain.R
import com.appify.appifymain.models.AppInfo
import com.bumptech.glide.Glide

class AppInfoAdapter : RecyclerView.Adapter<AppInfoAdapter.AppInfoViewHolder>() {
    inner class AppInfoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewAppName: TextView = itemView.findViewById(R.id.textView_rv_items_appName)
        val textViewAppDescription: TextView = itemView.findViewById(R.id.textView_rv_items_appDesc)
        val imageViewAppImage: ImageView = itemView.findViewById(R.id.imageView_rvItems_appInfo)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppInfoViewHolder {
        return AppInfoViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.layout_rv_items_home_app_info, parent, false
            )
        )
    }

    private val differCallBack = object : DiffUtil.ItemCallback<AppInfo>() {
        override fun areItemsTheSame(oldItem: AppInfo, newItem: AppInfo): Boolean {
            return oldItem.thumbnail == newItem.thumbnail
        }

        override fun areContentsTheSame(oldItem: AppInfo, newItem: AppInfo): Boolean {
            return oldItem.thumbnail == newItem.thumbnail
        }
    }

    private val myDifferList = AsyncListDiffer(this, differCallBack)

    override fun onBindViewHolder(holder: AppInfoViewHolder, position: Int) {
        val currPosData = myDifferList.currentList[position]

        holder.apply {
            textViewAppName.text = currPosData.name
            textViewAppDescription.text = currPosData.description
            val thumbnailImage = Glide.with(holder.itemView).load(currPosData.thumbnail).fitCenter()
            Glide.with(holder.itemView).load(currPosData.gif).fitCenter().thumbnail(thumbnailImage)
                .into(imageViewAppImage)
        }
    }

    override fun getItemCount(): Int {
        return myDifferList.currentList.size
    }

    fun submitList(givenList: List<AppInfo>) {
        myDifferList.submitList(givenList)
    }

    fun getItemAtIndex(index: Int): AppInfo? {
        return myDifferList.currentList[index] ?: null
    }
}
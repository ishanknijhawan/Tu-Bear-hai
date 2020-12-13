package com.ishanknijhawan.tubearhai.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.ishanknijhawan.tubearhai.R
import com.ishanknijhawan.tubearhai.data.Beer
import com.ishanknijhawan.tubearhai.databinding.BeerItemBinding

class BeerAdapter(private val listener: OnBeerItemClickListener) :
    PagingDataAdapter<Beer, BeerAdapter.ViewHolder>(PHOTO_COMPARITOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = BeerItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = getItem(position)
        if (currentItem != null) {
            holder.bind(currentItem)
        }
    }

    inner class ViewHolder(private val binding: BeerItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnLongClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val item = getItem(position)
                    if (item != null) {
                        listener.onItemLongClick(item)
                    }
                }
                return@setOnLongClickListener false
            }

            binding.btnShare.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val item = getItem(position)
                    if (item != null) {
                        listener.onClickShare(item)
                    }
                }
            }
        }

        fun bind(beer: Beer) {
            binding.apply {
                Glide.with(itemView).load(beer.imageUrl)
                    .centerCrop()
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .error(R.drawable.ic_baseline_error_outline_24)
                    .into(imageView)

                tvBeerName.text = beer.name
                tvTagName.text = beer.tagline
            }
        }
    }

    interface OnBeerItemClickListener {
        fun onItemLongClick(beer: Beer)
        fun onClickShare(beer: Beer)
    }

    companion object {
        private val PHOTO_COMPARITOR = object : DiffUtil.ItemCallback<Beer>() {
            override fun areItemsTheSame(oldItem: Beer, newItem: Beer) = oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Beer, newItem: Beer) = oldItem == newItem

        }
    }
}
package com.example.facefit.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.facefit.databinding.ItemBinding
import com.example.facefit.model.Filter

//recycler view
class FilterAdapter(
    private val filterList: List<Filter>,
    private val onClick: (Filter) -> Unit ) : RecyclerView.Adapter<FilterAdapter.FilterViewHolder>() {

    inner class FilterViewHolder(val binding: ItemBinding) : RecyclerView.ViewHolder(binding.root)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilterViewHolder {
        val binding = ItemBinding.inflate( LayoutInflater.from(parent.context), parent, false )
        return FilterViewHolder(binding)
    }
    override fun onBindViewHolder(holder: FilterViewHolder, position: Int) {
        val filter = filterList[position]
        holder.binding.filterName.text = filter.name
        holder.binding.filterIcon.setImageResource(filter.iconRes)
        holder.itemView.setOnClickListener {
            Log.d("ADAPTER_CLICK", "Item clicked: ${filter.name}")
            onClick(filter)
        }
    }
    override fun getItemCount(): Int = filterList.size
    }

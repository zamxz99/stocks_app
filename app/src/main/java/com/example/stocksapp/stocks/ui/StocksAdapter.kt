package com.example.stocksapp.stocks.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncDifferConfig
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.example.stocksapp.databinding.ItemStockBinding
import com.example.stocksapp.stocks.ui.model.StockUIModel

class StocksAdapter :
    ListAdapter<StockUIModel, StockViewHolder>(AsyncDifferConfig.Builder(STOCK_DIFFER).build()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StockViewHolder {
        val binding = ItemStockBinding.inflate(LayoutInflater.from(parent.context))
        return StockViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StockViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        private val STOCK_DIFFER = object : DiffUtil.ItemCallback<StockUIModel>() {
            override fun areItemsTheSame(
                oldItem: StockUIModel,
                newItem: StockUIModel
            ): Boolean = oldItem.name == newItem.name

            override fun areContentsTheSame(
                oldItem: StockUIModel,
                newItem: StockUIModel
            ): Boolean = oldItem == newItem
        }
    }
}
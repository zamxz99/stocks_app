package com.example.stocksapp.stocks.ui

import androidx.recyclerview.widget.RecyclerView
import com.example.stocksapp.databinding.ItemStockBinding
import com.example.stocksapp.stocks.ui.model.StockUIModel

class StockViewHolder(private val binding: ItemStockBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(uiModel: StockUIModel) {
        binding.uiModel = uiModel
    }
}
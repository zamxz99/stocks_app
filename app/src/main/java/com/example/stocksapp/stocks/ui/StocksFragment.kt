package com.example.stocksapp.stocks.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.lifecycle.Lifecycle.State
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.DividerItemDecoration
import com.example.stocksapp.R
import com.example.stocksapp.databinding.FragmentStocksBinding
import com.example.stocksapp.stocks.vm.StocksViewModel
import kotlinx.coroutines.launch

class StocksFragment : Fragment() {

    private val viewModel: StocksViewModel by hiltNavGraphViewModels(R.id.stocks)
    private var binding: FragmentStocksBinding? = null
    private var stocksAdapter: StocksAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentStocksBinding.inflate(inflater).also {
        binding = it
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUI()
        setupMenu()
        observeData()
    }

    private fun observeData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(State.STARTED) {
                launch {
                    viewModel.stocks.collect { stocksAdapter?.submitList(it) }
                }
                launch {
                    viewModel.status.collect { status ->
                        binding?.run {
                            stocksSwipeRefresh.isRefreshing = status.isLoading
                            this.status = status
                        }
                    }
                }
            }
        }
    }

    private fun setupUI() {
        binding?.run {
            stocksAdapter = StocksAdapter().also { adapter ->
                stockList.adapter = adapter
            }

            stockList.addItemDecoration(
                DividerItemDecoration(
                    this@StocksFragment.requireContext(),
                    DividerItemDecoration.VERTICAL
                )
            )

            stocksSwipeRefresh.setOnRefreshListener {
                viewModel.getStocks()
            }
        }
    }

    private fun setupMenu() {
        val menuHost: MenuHost = requireActivity()

        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.fetch_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.emptyData -> {
                        viewModel.getStocksEmpty()
                        true
                    }
                    R.id.malformedData -> {
                        viewModel.getStocksMalformed()
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, State.RESUMED)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
        stocksAdapter = null
    }
}
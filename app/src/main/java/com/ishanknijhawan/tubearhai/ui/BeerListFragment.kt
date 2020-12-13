package com.ishanknijhawan.tubearhai.ui

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import com.ishanknijhawan.tubearhai.R
import com.ishanknijhawan.tubearhai.adapter.BeerAdapter
import com.ishanknijhawan.tubearhai.adapter.LoadingBeerAdapter
import com.ishanknijhawan.tubearhai.data.Beer
import com.ishanknijhawan.tubearhai.databinding.BeerListFragmentBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BeerListFragment : Fragment(R.layout.beer_list_fragment) {

    private val mViewModel by viewModels<BeerListViewModel>()
    private var _binding: BeerListFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = BeerListFragmentBinding.bind(view)
        val adapter = BeerAdapter()

        binding.apply {
            rvBeers.setHasFixedSize(true)
            rvBeers.adapter = adapter.withLoadStateHeaderAndFooter(
                header = LoadingBeerAdapter {adapter.retry()},
                footer = LoadingBeerAdapter {adapter.retry()}
            )
        }

        mViewModel.beers.observe(viewLifecycleOwner, Observer {
            Log.d("GSON", "coming here with data $it")
            adapter.submitData(viewLifecycleOwner.lifecycle, it)
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}
package com.example.ocr.home

import com.example.ocr.base.BaseFragment
import com.example.ocr.databinding.FragmentHomeBinding
import com.example.ocr.home.history.RecentCopiesAdapter
import com.example.ocr.home.util.MockDataProvider

/**
 * Created by Arun @ak - 14213  on 01/09/24.
 */
class HomeFragment : BaseFragment<FragmentHomeBinding>(FragmentHomeBinding::class.java) {

    private lateinit var clipboardAdapter: RecentCopiesAdapter

    override fun setupUi() {
        clipboardAdapter = RecentCopiesAdapter()
        binding.recentCopiesList.adapter = clipboardAdapter
        clipboardAdapter.submitList(MockDataProvider.recentCopies)
    }

    override fun setupObserver() {

    }




}
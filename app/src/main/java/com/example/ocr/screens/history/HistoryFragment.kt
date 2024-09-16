package com.example.ocr.screens.history

import android.content.ClipboardManager
import android.content.Context.CLIPBOARD_SERVICE
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.ocr.base.BaseFragment
import com.example.ocr.databinding.FragmentHistoryBinding
import com.example.ocr.screens.model.RecentCopy

/**
 * Created by Arun @ak - 14213  on 01/09/24.
 */
class HistoryFragment : BaseFragment<FragmentHistoryBinding>(FragmentHistoryBinding::class.java) {

    private lateinit var clipboardManager: ClipboardManager
    private lateinit var clipboardAdapter: RecentCopiesAdapter
    private val clipboardItems = mutableListOf<RecentCopy>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun setupUi() {
        clipboardAdapter = RecentCopiesAdapter()
        binding.historyList.adapter = clipboardAdapter
    }

    override fun setupObserver() {

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        clipboardManager = requireContext().getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
        fetchClipboardData()
    }


    private fun fetchClipboardData() {
        clipboardItems.clear()

        val clipData = clipboardManager.primaryClip
        if (clipData != null) {
            for (i in 0 until clipData.itemCount) {
                val clipItem = clipData.getItemAt(i)
                val copiedText = clipItem.text.toString()
                val currentTime = System.currentTimeMillis()
                clipboardItems.add(RecentCopy(copiedText, currentTime.toString()))
            }
        }
        clipboardAdapter.submitList(clipboardItems)
    }


}
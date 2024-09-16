package com.example.ocr.screens.home

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.ocr.screens.about.AboutSettingsFragment
import com.example.ocr.screens.history.HistoryFragment
import com.example.ocr.screens.settings.SettingsFragment

class HomeScreenViewPagerAdapter(activity: AppCompatActivity) : FragmentStateAdapter(activity) {
    override fun getItemCount(): Int = 4
    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> HomeFragment()
            1 -> HistoryFragment()
            2 -> SettingsFragment()
            3 -> AboutSettingsFragment()
            else -> throw IllegalArgumentException("Invalid position")
        }
    }
}
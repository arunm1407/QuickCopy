package com.example.ocr.home

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.ocr.home.history.HistoryFragment
import com.example.ocr.home.profile.ProfileFragment
import com.example.ocr.home.settings.SettingsFragment

class HomeScreenViewPagerAdapter(activity: AppCompatActivity) : FragmentStateAdapter(activity) {
    override fun getItemCount(): Int = 3
    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> HomeFragment()
            1 -> HistoryFragment()
            2 -> SettingsFragment()
            3 -> ProfileFragment()
            else -> throw IllegalArgumentException("Invalid position")
        }
    }
}
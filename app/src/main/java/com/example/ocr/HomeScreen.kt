package com.example.ocr

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.ocr.databinding.ActivityHomeScreenBinding
import com.example.ocr.eventBus.EventBus
import com.example.ocr.home.HomeScreenViewPagerAdapter
import com.example.ocr.util.binding.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


/**
 * Created by Arun @ak - 14213  on 01/09/24.
 */

@AndroidEntryPoint
class HomeScreen : AppCompatActivity() {

    @Inject
    lateinit var eventBus: EventBus
    private val binding: ActivityHomeScreenBinding by viewBinding()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        with(binding.viewPager) {
            adapter = HomeScreenViewPagerAdapter(this@HomeScreen)
            isUserInputEnabled = false
        }
        observeBottomNavbar()
    }


    private fun observeBottomNavbar() {
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            val viewPager = binding.viewPager
            when (item.itemId) {
                R.id.home ->  viewPager.currentItem = 0
                R.id.history -> viewPager.currentItem = 1
                R.id.settings -> viewPager.currentItem = 2
                R.id.support -> viewPager.currentItem = 3
            }
            true
        }
    }
}
package com.example.healthmonitorapp.ui.Data

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.healthmonitorapp.R
import com.example.healthmonitorapp.databinding.FragmentDataBinding
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DataFragment : Fragment(R.layout.fragment_data) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentDataBinding.bind(view)

        val fragmentList = listOf(
            ChartFragment(),
            DataListFragment()
        )

        val adapter = ViewPagerAdapter(
            fragmentList,
            requireActivity().supportFragmentManager,
            lifecycle
        )

        binding.viewPager.adapter = adapter

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            if (position == 0) tab.text = "Chart"
            else tab.text = "List"
        }.attach()

    }
}
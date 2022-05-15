package com.example.healthmonitorapp.ui.Data

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.healthmonitorapp.R
import com.example.healthmonitorapp.databinding.FragmentDataBinding
import com.github.aachartmodel.aainfographics.aachartcreator.*
import com.github.aachartmodel.aainfographics.aaoptionsmodel.AAMarker
import com.github.aachartmodel.aainfographics.aatools.AAGradientColor
import com.github.aachartmodel.aainfographics.aatools.AALinearGradientDirection
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_data.*
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class DataFragment : Fragment(R.layout.fragment_data) {

    private val viewModel by viewModels<DataViewModel>()
    private val TAG = "DataFragment"

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
package com.example.healthmonitorapp.ui.Data

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.healthmonitorapp.R
import com.example.healthmonitorapp.database.Day
import com.example.healthmonitorapp.databinding.FragmentDataListBinding
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat

@AndroidEntryPoint
class DataListFragment : Fragment(R.layout.fragment_data_list) {

    private val viewModel by viewModels<DataViewModel>()
    val formatter = SimpleDateFormat("dd MM yyyy")

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentDataListBinding.bind(view)

        fun updateList(days: List<Day>) {
            var adapter = DataListAdapter(days.sortedBy { day -> formatter.parse(day.date) })
            binding.recyclerView.adapter = adapter
        }

        viewModel.days.value?.let { updateList(it) }
        viewModel.days.observe(viewLifecycleOwner) { updateList(it) }
    }
}
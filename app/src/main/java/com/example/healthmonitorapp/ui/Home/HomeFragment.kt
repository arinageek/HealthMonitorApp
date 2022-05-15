package com.example.healthmonitorapp.ui.Home

import android.os.Bundle
import android.view.View
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.healthmonitorapp.R
import com.example.healthmonitorapp.databinding.FragmentHomeBinding
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home) {

    private val viewModel by viewModels<HomeViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentHomeBinding.bind(view)

        fun healthyEmojiChosen() {
            binding.happyEmoji.alpha = 1.0f
            binding.sickEmoji.alpha = 0.5f
        }

        fun sickEmojiChosen() {
            binding.happyEmoji.alpha = 0.5f
            binding.sickEmoji.alpha = 1.0f
        }

        fun noEmojiChosen() {
            binding.happyEmoji.alpha = 0.5f
            binding.sickEmoji.alpha = 0.5f
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.event.collect { event ->
                when(event) {
                    is HomeViewModel.HomeEvent.showFormSubmittedMessage -> {
                        Snackbar.make(view, "Form successfully submitted", Snackbar.LENGTH_SHORT).show()
                    }
                    is HomeViewModel.HomeEvent.showFormNotSubmittedMessage -> {
                        Snackbar.make(view, "Oops! Something went wrong, form hasn't been submitted!", Snackbar.LENGTH_SHORT).show()
                    }
                }
            }
        }

        binding.apply {
            //configure the UI
            viewModel.healthState.observe(viewLifecycleOwner) {
                if (it == HealthState.STATE_NONE) {
                    noEmojiChosen()
                } else if (it == HealthState.STATE_HEALTHY) {
                    healthyEmojiChosen()
                } else {
                    sickEmojiChosen()
                }
            }
            viewModel.comment.observe(viewLifecycleOwner) { editTextComment.setText(it) }

            //Add listeners to UI components
            happyEmoji.setOnClickListener { viewModel.healthState.postValue(HealthState.STATE_HEALTHY) }
            sickEmoji.setOnClickListener { viewModel.healthState.postValue(HealthState.STATE_SICK) }
            buttonSubmit.setOnClickListener {
                if (viewModel.healthState.value == HealthState.STATE_NONE) {
                    Snackbar.make(
                        view,
                        "Choose an emoji that describes your state of health",
                        Snackbar.LENGTH_SHORT
                    ).show()
                } else if(!viewModel.isConnectedToInternet()){
                    Snackbar.make(
                        view,
                        "No internet connection",
                        Snackbar.LENGTH_SHORT
                    ).show()
                }else {
                    viewModel.comment.postValue(editTextComment.text.toString())
                    editTextComment.clearFocus()
                    viewModel.onButtonSubmitClick()
                }
            }
        }
    }
}
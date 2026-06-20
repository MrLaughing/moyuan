package com.mrlaughing.moyuan.ui.profile

import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.mrlaughing.moyuan.databinding.FragmentProfileBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ProfileViewModel by viewModels()
    private var keyVisible = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.apiKey.observe(viewLifecycleOwner) { key ->
            binding.apiKeyInput.setText(key ?: "")
        }

        viewModel.lastSync.observe(viewLifecycleOwner) { updateStats() }
        viewModel.totalMinutes.observe(viewLifecycleOwner) { updateStats() }
        viewModel.plantCount.observe(viewLifecycleOwner) { updateStats() }

        binding.toggleVisibilityButton.setOnClickListener {
            keyVisible = !keyVisible
            val selection = binding.apiKeyInput.selectionStart
            if (keyVisible) {
                binding.apiKeyInput.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                binding.toggleVisibilityButton.text = "隐藏"
            } else {
                binding.apiKeyInput.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                binding.toggleVisibilityButton.text = "显示"
            }
            binding.apiKeyInput.setTypeface(binding.apiKeyInput.typeface)
            binding.apiKeyInput.setSelection(
                selection.coerceAtMost(binding.apiKeyInput.text?.length ?: 0)
            )
        }

        binding.saveApiKeyButton.setOnClickListener {
            val key = binding.apiKeyInput.text?.toString().orEmpty()
            viewModel.saveApiKey(key)
            showFeedback(if (key.isBlank()) "已清空 API Key" else "API Key 已保存")
        }
    }

    private fun showFeedback(message: String) {
        binding.saveFeedbackText.text = message
        binding.saveFeedbackText.visibility = View.VISIBLE
        binding.saveFeedbackText.removeCallbacks(hideFeedbackRunnable)
        binding.saveFeedbackText.postDelayed(hideFeedbackRunnable, 2000)
    }

    private val hideFeedbackRunnable = Runnable {
        binding.saveFeedbackText.visibility = View.GONE
    }

    private fun updateStats() {
        val lastSync = viewModel.lastSync.value ?: "—"
        val total = viewModel.totalMinutes.value ?: 0
        val count = viewModel.plantCount.value ?: 0
        binding.profileStats.text = "植物：$count 株 · 累计：${total} 分钟 · 上次同步：$lastSync"
    }

    override fun onDestroyView() {
        binding.saveFeedbackText.removeCallbacks(hideFeedbackRunnable)
        super.onDestroyView()
        _binding = null
    }
}

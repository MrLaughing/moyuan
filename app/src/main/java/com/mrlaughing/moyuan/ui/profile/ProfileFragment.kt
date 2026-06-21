package com.mrlaughing.moyuan.ui.profile

import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.mrlaughing.moyuan.databinding.FragmentProfileBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ProfileViewModel by viewModels()
    private var keyHidden = true

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

        // API Key 初始状态：使用普通文本输入 + PasswordTransformationMethod，避免触发安全输入法
        binding.apiKeyInput.inputType = EditorInfo.TYPE_CLASS_TEXT
        binding.apiKeyInput.transformationMethod = PasswordTransformationMethod.getInstance()
        binding.toggleVisibilityButton.text = "显示"

        viewModel.apiKey.observe(viewLifecycleOwner) { key ->
            val currentText = binding.apiKeyInput.text?.toString()
            if (currentText != (key ?: "")) {
                binding.apiKeyInput.setText(key ?: "")
            }
        }

        // 观察统计数据
        viewModel.lastSync.observe(viewLifecycleOwner) { updateStats() }
        viewModel.totalMinutes.observe(viewLifecycleOwner) { updateStats() }
        viewModel.plantCount.observe(viewLifecycleOwner) { updateStats() }

        // 切换显示/隐藏按钮
        binding.toggleVisibilityButton.setOnClickListener {
            keyHidden = !keyHidden
            val selection = binding.apiKeyInput.selectionStart
            if (keyHidden) {
                binding.apiKeyInput.transformationMethod = PasswordTransformationMethod.getInstance()
                binding.toggleVisibilityButton.text = "显示"
            } else {
                binding.apiKeyInput.transformationMethod = null
                binding.toggleVisibilityButton.text = "隐藏"
            }
            binding.apiKeyInput.setSelection(selection.coerceAtMost(binding.apiKeyInput.text?.length ?: 0))
        }

        binding.saveApiKeyButton.setOnClickListener {
            val key = binding.apiKeyInput.text?.toString().orEmpty()
            viewModel.saveApiKey(key)
            showFeedback(if (key.isBlank()) "已清空 API Key" else "API Key 已保存")
        }

        binding.syncNowButton.setOnClickListener {
            viewModel.syncNow()
            showFeedback("正在同步…")
        }
    }

    private fun showFeedback(message: String) {
        binding.saveFeedbackText.text = message
        binding.saveFeedbackText.visibility = View.VISIBLE
        binding.saveFeedbackText.removeCallbacks(hideFeedbackRunnable)
        binding.saveFeedbackText.postDelayed(hideFeedbackRunnable, 2500)
    }

    private val hideFeedbackRunnable = Runnable {
        binding.saveFeedbackText.visibility = View.GONE
    }

    private fun updateStats() {
        val lastSync = viewModel.lastSync.value ?: "—"
        val total = viewModel.totalMinutes.value ?: 0
        val count = viewModel.plantCount.value ?: 0
        binding.statPlantCount.text = count.toString()
        binding.statMinutes.text = "$total 分钟"
        binding.statLastSync.text = "上次同步：$lastSync"
    }

    override fun onDestroyView() {
        binding.saveFeedbackText.removeCallbacks(hideFeedbackRunnable)
        super.onDestroyView()
        _binding = null
    }
}

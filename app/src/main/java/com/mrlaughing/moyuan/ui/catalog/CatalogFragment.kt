package com.mrlaughing.moyuan.ui.catalog

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.mrlaughing.moyuan.R
import com.mrlaughing.moyuan.data.local.db.entity.PlantStateEntity
import com.mrlaughing.moyuan.data.model.GrowthLevel
import com.mrlaughing.moyuan.data.model.PlantPath
import com.mrlaughing.moyuan.databinding.FragmentCatalogBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CatalogFragment : Fragment() {

    private var _binding: FragmentCatalogBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CatalogViewModel by viewModels()
    private val adapter = PlantCardAdapter { plant ->
        val bundle = Bundle().apply {
            putString("plantId", plant.plantId)
        }
        findNavController().navigate(
            R.id.action_catalogFragment_to_plantDetailFragment,
            bundle
        )
    }

    private var allPlants: List<PlantStateEntity> = emptyList()
    private var currentPathFilter: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCatalogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.catalogRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.catalogRecyclerView.adapter = adapter

        // 默认选中"全部"标签
        setActiveTag(binding.tagAll)
        currentPathFilter = null

        viewModel.plants.observe(viewLifecycleOwner) { plants ->
            allPlants = plants
            // 更新解锁进度
            binding.tvUnlockProgress.text = "已解锁 ${plants.size}/27"
            applyFilters()
        }

        // 搜索框输入监听
        binding.etSearchPlant.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // 显示/隐藏清除按钮
                binding.ivSearchClear.visibility =
                    if (s.isNullOrEmpty()) View.GONE else View.VISIBLE
                applyFilters()
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        // 清除按钮
        binding.ivSearchClear.setOnClickListener {
            binding.etSearchPlant.text?.clear()
        }

        // 路径筛选标签点击
        binding.tagAll.setOnClickListener {
            currentPathFilter = null
            setActiveTag(binding.tagAll)
            applyFilters()
        }
        binding.tagJimo.setOnClickListener {
            currentPathFilter = "jimo"
            setActiveTag(binding.tagJimo)
            applyFilters()
        }
        binding.tagBingzhu.setOnClickListener {
            currentPathFilter = "bingzhu"
            setActiveTag(binding.tagBingzhu)
            applyFilters()
        }
        binding.tagSuihan.setOnClickListener {
            currentPathFilter = "suihan"
            setActiveTag(binding.tagSuihan)
            applyFilters()
        }
        binding.tagXunfang.setOnClickListener {
            currentPathFilter = "xunfang"
            setActiveTag(binding.tagXunfang)
            applyFilters()
        }
        binding.tagHidden.setOnClickListener {
            currentPathFilter = "hidden"
            setActiveTag(binding.tagHidden)
            applyFilters()
        }
    }

    private fun applyFilters() {
        val searchText = binding.etSearchPlant.text?.toString()?.trim().orEmpty()
        var filtered = allPlants

        // 按路径过滤
        if (currentPathFilter != null) {
            filtered = filtered.filter { it.path == currentPathFilter }
        }

        // 按搜索文字过滤（匹配植物名、路径、等级）
        if (searchText.isNotEmpty()) {
            filtered = filtered.filter { plant ->
                val path = PlantPath.fromString(plant.path)
                val level = GrowthLevel.fromString(plant.level)
                val plantName = "${path.displayName}·${plant.plantId.substringAfterLast('_')}"
                plant.plantId.contains(searchText, ignoreCase = true) ||
                plant.path.contains(searchText, ignoreCase = true) ||
                path.displayName.contains(searchText, ignoreCase = true) ||
                level.displayName.contains(searchText, ignoreCase = true) ||
                plantName.contains(searchText, ignoreCase = true)
            }
        }

        adapter.submitList(filtered)
        val empty = filtered.isEmpty()
        binding.catalogRecyclerView.visibility = if (empty) View.GONE else View.VISIBLE
        binding.emptyCatalogContainer.visibility = if (empty) View.VISIBLE else View.GONE
    }

    private fun setActiveTag(activeTag: View) {
        val allTags = listOf(
            binding.tagAll, binding.tagJimo, binding.tagBingzhu,
            binding.tagSuihan, binding.tagXunfang, binding.tagHidden
        )
        allTags.forEach { tag ->
            if (tag == activeTag) {
                tag.setBackgroundResource(R.drawable.bg_tag_active)
            } else {
                tag.setBackgroundResource(R.drawable.bg_tag_inactive)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

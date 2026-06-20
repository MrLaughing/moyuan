package com.mrlaughing.moyuan.ui.garden

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.mrlaughing.moyuan.R
import com.mrlaughing.moyuan.data.model.Season
import com.mrlaughing.moyuan.data.model.Weather
import com.mrlaughing.moyuan.databinding.FragmentGardenBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GardenFragment : Fragment() {

    private var _binding: FragmentGardenBinding? = null
    private val binding get() = _binding!!
    private val viewModel: GardenViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGardenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.plants.observe(viewLifecycleOwner) { plants ->
            binding.gardenView.setPlants(plants)
            binding.emptyGardenText.visibility =
                if (plants.isEmpty()) View.VISIBLE else View.GONE
        }

        binding.gardenView.setOnPlantClickListener { plant ->
            val bundle = Bundle().apply {
                putString("plantId", plant.plantId)
            }
            findNavController().navigate(
                R.id.action_gardenFragment_to_plantDetailFragment,
                bundle
            )
        }

        viewModel.meta.observe(viewLifecycleOwner) { meta ->
            val season = meta?.currentSeason?.let { Season.fromString(it).displayName } ?: "—"
            val weather = meta?.currentWeather?.let { Weather.fromString(it).displayName } ?: "—"
            binding.seasonInfo.text = "季节：$season · 天气：$weather"
        }

        viewModel.totalMinutes.observe(viewLifecycleOwner) { minutes ->
            binding.titleGarden.text = "花园 · 累计 ${minutes} 分钟"
        }

        viewModel.syncResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is GardenViewModel.SyncResult.Success -> {
                    Toast.makeText(context, "同步成功", Toast.LENGTH_SHORT).show()
                    viewModel.clearSyncResult()
                }
                is GardenViewModel.SyncResult.Failed -> {
                    Toast.makeText(context, "同步失败，请检查网络后重试", Toast.LENGTH_SHORT).show()
                    viewModel.clearSyncResult()
                }
                null -> { }
            }
        }

        binding.syncButton.setOnClickListener { viewModel.syncNow() }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

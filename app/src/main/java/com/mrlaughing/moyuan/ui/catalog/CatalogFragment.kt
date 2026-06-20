package com.mrlaughing.moyuan.ui.catalog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.mrlaughing.moyuan.R
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

        viewModel.plants.observe(viewLifecycleOwner) {
            adapter.submitList(it)
            binding.emptyCatalogText.visibility =
                if (it.isEmpty()) View.VISIBLE else View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

package edu.utap.exerciseapp.nutrition

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.MutableLiveData
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import edu.utap.exerciseapp.MainViewModel
import edu.utap.exerciseapp.api.NutritionApi
import edu.utap.exerciseapp.api.NutritionRepository
import edu.utap.exerciseapp.api.RetNut
import  edu.utap.exerciseapp.databinding.NutritionPageBinding
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NutritionFragment: Fragment() {
    private var _binding: NutritionPageBinding? = null
    private val binding get() = _binding!!

    private val viewModel : MainViewModel by viewModels()
    private fun initAdapter(binding: NutritionPageBinding) {
        val adapter = NutritionAdapter(viewModel)
        viewModel.observeFoods().observe(viewLifecycleOwner) {
            adapter.replaceList(it)
            adapter.notifyDataSetChanged()
        }
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(activity)
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        _binding = NutritionPageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAdapter(binding)
        binding.searchButton.setOnClickListener {
            viewModel.searchFood(binding.searchBar.text.toString())
        }
    }

}
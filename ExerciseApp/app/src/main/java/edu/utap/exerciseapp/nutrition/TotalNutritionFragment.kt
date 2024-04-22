package edu.utap.exerciseapp.nutrition

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import edu.utap.exerciseapp.MainViewModel
import edu.utap.exerciseapp.databinding.NutritionPageBinding
import edu.utap.exerciseapp.databinding.TotalNutritionFragmentBinding

class TotalNutritionFragment : Fragment() {
    private var _binding: TotalNutritionFragmentBinding? = null
    private val binding get() = _binding!!
    private val viewModel : MainViewModel by activityViewModels()
    private var totalCal = 0.0
    private var totalProtein = 0.0
    private var totalFat = 0.0
    private var totalCarb = 0.0

    private fun initAdapter(binding: TotalNutritionFragmentBinding) {
        val adapter = NutritionAdapter(viewModel)
        viewModel.observeFavFoods().observe(viewLifecycleOwner) {
            totalCal = 0.0
            totalProtein = 0.0
            totalFat = 0.0
            totalCarb = 0.0
            it.forEach { ret ->
                totalCal += ret.cal
                totalProtein += ret.protein
                totalFat += ret.fat
                totalCarb += ret.carb
            }
            binding.totalProtein.text = String.format("%.2f", totalProtein)
            binding.totalCals.text = String.format("%.2f", totalCal)
            binding.totalCarb.text = String.format("%.2f", totalCarb)
            binding.totalFat.text = String.format("%.2f", totalFat)
            adapter.replaceList(it)
            adapter.notifyDataSetChanged()
        }
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(activity)
    }

    private fun initSwipeLayout(swipe : SwipeRefreshLayout) {
        swipe.setOnRefreshListener {
            swipe.isRefreshing = false
        }

    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        _binding = TotalNutritionFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAdapter(binding)
        initSwipeLayout(binding.swipeRefreshLayout)
    }
}
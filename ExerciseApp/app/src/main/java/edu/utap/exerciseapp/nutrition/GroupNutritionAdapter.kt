package edu.utap.exerciseapp.nutrition

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import edu.utap.exerciseapp.MainViewModel
import edu.utap.exerciseapp.R
import edu.utap.exerciseapp.api.RetNut
import edu.utap.exerciseapp.databinding.NutritionGroupRowBinding
import edu.utap.exerciseapp.databinding.NutritionRowBinding
import edu.utap.exerciseapp.model.FoodModel

class GroupNutritionAdapter(
    private val viewModel: MainViewModel,
    private val navigateToNut : (FoodModel)->Unit
)  : RecyclerView.Adapter<GroupNutritionAdapter.VH>() {
    private var foods = listOf<FoodModel>()
    inner class VH(val binding : NutritionGroupRowBinding): RecyclerView.ViewHolder(binding.root)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupNutritionAdapter.VH {
        val binding = NutritionGroupRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val binding = holder.binding
        binding.root.setOnClickListener {
            navigateToNut(foods[position])
        }
        foods[position].let {
            binding.groupName.text = it.name
            var totalCal = 0.0
            it.list.forEach { ret ->
                totalCal += ret.cal
            }
            binding.calNumber.text = totalCal.toString()
        }
    }

    override fun getItemCount(): Int {
        return foods.size
    }

    fun replaceList(items : List<FoodModel>) {
        foods = items
    }
}
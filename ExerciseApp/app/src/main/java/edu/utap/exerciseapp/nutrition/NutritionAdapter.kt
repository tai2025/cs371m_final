package edu.utap.exerciseapp.nutrition

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import edu.utap.exerciseapp.MainViewModel
import edu.utap.exerciseapp.program.ProgramAdapter
import edu.utap.exerciseapp.databinding.NutritionRowBinding
import androidx.recyclerview.widget.DiffUtil
import edu.utap.exerciseapp.api.RetNut


class NutritionAdapter(private val viewModel: MainViewModel) : RecyclerView.Adapter<NutritionAdapter.VH>() {

    private var foods = listOf<RetNut>()
    inner class VH(val binding : NutritionRowBinding): RecyclerView.ViewHolder(binding.root)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NutritionAdapter.VH {
        val binding = NutritionRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val binding = holder.binding
        foods[position].let {
            binding.food.text = it.name
            binding.cals.text = it.cal.toString()
            binding.carbs.text = it.carb.toString()
            binding.fats.text = it.fat.toString()
            binding.proteins.text = it.protein.toString()
        }
    }

    override fun getItemCount(): Int {
        return foods.size
    }

    fun replaceList(items : List<RetNut>) {
        foods = items
    }
}
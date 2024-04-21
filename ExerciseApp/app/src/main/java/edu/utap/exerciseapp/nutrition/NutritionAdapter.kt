package edu.utap.exerciseapp.nutrition

import android.opengl.Visibility
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import edu.utap.exerciseapp.MainViewModel
import edu.utap.exerciseapp.program.ProgramAdapter
import edu.utap.exerciseapp.databinding.NutritionRowBinding
import androidx.recyclerview.widget.DiffUtil
import edu.utap.exerciseapp.api.RetNut
import edu.utap.exerciseapp.R


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
            if(it.company.isNullOrBlank()){
                binding.foodComp.visibility = View.GONE
            } else {
                binding.foodComp.text = it.company
            }
            val contain = viewModel.observeFavFoods().value?.contains(it)
            if(contain == true){
                binding.rowFav.setImageResource(R.drawable.ic_favorite_black_24dp)
            } else{
                binding.rowFav.setImageResource(R.drawable.ic_favorite_border_black_24dp)
            }
            binding.rowFav.setOnClickListener {view ->
                if(contain == true){
                    viewModel.removeFav(it)
                    binding.rowFav.setImageResource(R.drawable.ic_favorite_border_black_24dp)
                } else {
                    viewModel.addFav(it)
                    binding.rowFav.setImageResource(R.drawable.ic_favorite_black_24dp)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return foods.size
    }

    fun replaceList(items : List<RetNut>) {
        foods = items
    }
}
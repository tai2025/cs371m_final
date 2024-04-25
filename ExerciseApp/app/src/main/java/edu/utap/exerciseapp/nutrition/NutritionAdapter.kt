package edu.utap.exerciseapp.nutrition

import android.opengl.Visibility
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.RecyclerView
import edu.utap.exerciseapp.MainViewModel
import edu.utap.exerciseapp.program.ProgramAdapter
import edu.utap.exerciseapp.databinding.NutritionRowBinding
import androidx.recyclerview.widget.DiffUtil
import edu.utap.exerciseapp.api.RetNut
import edu.utap.exerciseapp.R
import edu.utap.exerciseapp.model.FoodModel
import io.grpc.Context


class NutritionAdapter(
    private val viewModel: MainViewModel,
    private val spinner : ArrayAdapter<String>?
) : RecyclerView.Adapter<NutritionAdapter.VH>() {

    private var foods = listOf<RetNut>()
    inner class VH(val binding : NutritionRowBinding): RecyclerView.ViewHolder(binding.root)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NutritionAdapter.VH {
        val binding = NutritionRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val binding = holder.binding
        val item = foods[position]
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
        }
        if(spinner == null){
            binding.spinner.visibility = View.GONE
        } else{
            binding.spinner.adapter = spinner
            binding.spinner.onItemSelectedListener = object :
                AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        if(position != 0){
                            viewModel.addToList(position - 1, item)
                        }
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {

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
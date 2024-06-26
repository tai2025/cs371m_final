package edu.utap.exerciseapp.nutrition

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import edu.utap.exerciseapp.MainViewModel
import edu.utap.exerciseapp.databinding.TotalNutritionFragmentBinding

class TotalNutritionFragment() : Fragment() {
    private var _binding: TotalNutritionFragmentBinding? = null
    private val args: TotalNutritionFragmentArgs by navArgs()
    private val binding get() = _binding!!
    private val viewModel : MainViewModel by activityViewModels()
    private var totalCal = 0.0
    private var totalProtein = 0.0
    private var totalFat = 0.0
    private var totalCarb = 0.0

    private fun initSwipeLayout(swipe : SwipeRefreshLayout) {
        swipe.setOnRefreshListener {
            swipe.isRefreshing = false
        }

    }

    private fun getPos(holder: RecyclerView.ViewHolder) : Int {
        val pos = holder.bindingAdapterPosition
        // notifyDataSetChanged was called, so position is not known
        if( pos == RecyclerView.NO_POSITION) {
            return holder.absoluteAdapterPosition
        }
        return pos
    }

    private fun initTouchHelper(): ItemTouchHelper {
        val simpleItemTouchCallback =
            object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.START)
            {
                override fun onMove(recyclerView: RecyclerView,
                                    viewHolder: RecyclerView.ViewHolder,
                                    target: RecyclerView.ViewHolder): Boolean {
                    return true
                }
                override fun onSwiped(viewHolder: RecyclerView.ViewHolder,
                                      direction: Int) {
                    val position = getPos(viewHolder)
                    Log.d(javaClass.simpleName, "Swipe delete $position")
                    viewModel.removeFromList(args.list.name, args.list.list[position])
                }
            }
        return ItemTouchHelper(simpleItemTouchCallback)
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
        val adapter = NutritionAdapter(viewModel, null)
        val list = args.list.list
        totalCal = 0.0
        totalProtein = 0.0
        totalFat = 0.0
        totalCarb = 0.0
        list.forEach {ret ->
            totalCal += ret.cal
            totalProtein += ret.protein
            totalFat += ret.fat
            totalCarb += ret.carb
        }
        binding.totalProtein.text = String.format("%.2f", totalProtein)
        binding.totalCals.text = String.format("%.2f", totalCal)
        binding.totalCarb.text = String.format("%.2f", totalCarb)
        binding.totalFat.text = String.format("%.2f", totalFat)
        adapter.replaceList(list)
        adapter.notifyDataSetChanged()
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(activity)
        initSwipeLayout(binding.swipeRefreshLayout)
        initTouchHelper().attachToRecyclerView(binding.recyclerView)

    }
}
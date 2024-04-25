package edu.utap.exerciseapp.nutrition

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import edu.utap.exerciseapp.MainViewModel
import  edu.utap.exerciseapp.databinding.NutritionGroupFragmentBinding


class GroupNutritionFragment : Fragment() {
    private var _binding: NutritionGroupFragmentBinding? = null
    private val binding get() = _binding!!
    private val viewModel : MainViewModel by activityViewModels()

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
                    viewModel.removeList(position)
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
        _binding = NutritionGroupFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val adapter = GroupNutritionAdapter(viewModel) {
            val action = GroupNutritionFragmentDirections.actionGroupToTotal(it)
            findNavController().navigate(action)
        }
        viewModel.observeFoodList().observe(viewLifecycleOwner) {
            adapter.replaceList(it)
            adapter.notifyDataSetChanged()
        }

        binding.GroupRecyclerView.adapter = adapter
        binding.GroupRecyclerView.layoutManager = LinearLayoutManager(activity)
        initTouchHelper().attachToRecyclerView(binding.GroupRecyclerView)

        binding.addGroup.setOnClickListener {
            if(binding.addName.text.isNullOrBlank()){
                Toast.makeText(binding.root.context, "list needs name", Toast.LENGTH_LONG).show()
            } else {
                viewModel.newFoodList(binding.addName.text.toString())
                binding.addName.setText("")
                adapter.notifyDataSetChanged()
            }
        }
    }

    override fun onPause() {
        super.onPause()
    }
}
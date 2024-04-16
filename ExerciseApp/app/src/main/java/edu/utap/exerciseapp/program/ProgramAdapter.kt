package edu.utap.exerciseapp.program

import android.content.Context
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.core.view.isNotEmpty
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import edu.utap.exerciseapp.R
import edu.utap.exerciseapp.databinding.ProgramFragmentBinding
import edu.utap.exerciseapp.databinding.RowBinding
import edu.utap.exerciseapp.model.WorkoutEntry

/**
 * Created by witchel on 8/25/2019
 */

// https://developer.android.com/reference/androidx/recyclerview/widget/ListAdapter
// Slick adapter that provides submitList, so you don't worry about how to update
// the list, you just submit a new one when you want to change the list and the
// Diff class computes the smallest set of changes that need to happen.
// NB: Both the old and new lists must both be in memory at the same time.
// So you can copy the old list, change it into a new list, then submit the new list.
//
// You can call adapterPosition to get the index of the selected item
class ProgramAdapter(private val workoutlist: List<WorkoutEntry>, val context: Context) : RecyclerView.Adapter<ProgramAdapter.VH>() {
    var list = workoutlist
    override fun getItemCount() = list.size
    inner class VH(val rowBinding : RowBinding)
        : RecyclerView.ViewHolder(rowBinding.root){
        init{
        }

    }
    override fun onBindViewHolder(holder: ProgramAdapter.VH, position: Int) {
        val workout = list.get(position)
        var binding = holder.rowBinding
        val table = binding.workoutTable

        for (exercise in workout.list) {
            val row = LayoutInflater.from(context).inflate(R.layout.exercise_row, null) as TableRow
            table.addView(row)
//            row.findViewById<EditText>(R.id.Exercise).setOnEditorActionListener { /*v*/_, actionId, event ->
//                if ((event != null
//                            &&(event.action == KeyEvent.ACTION_DOWN)
//                            &&(event.keyCode == KeyEvent.KEYCODE_ENTER))
//                    || (actionId == EditorInfo.IME_ACTION_DONE)) {
//                    exercise.Exercise = row.findViewById<EditText>(R.id.Exercise).text.toString()
//
//                    true
//                }
//                false
//            }


        }
        binding.addExercise.setOnClickListener{
            val row = LayoutInflater.from(context).inflate(R.layout.exercise_row, null) as TableRow
            table.addView(row)
            val exercise = WorkoutEntry.Workout()
            workout.addToList(exercise)
            row.findViewById<EditText>(R.id.Exercise).setOnEditorActionListener { /*v*/_, actionId, event ->
                if ((event != null
                            &&(event.action == KeyEvent.ACTION_DOWN)
                            &&(event.keyCode == KeyEvent.KEYCODE_ENTER))
                    || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    //exercise.Exercise = row.findViewById<EditText>(R.id.Exercise).text.toString()

                    true
                }
                false
            }
        }






    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProgramAdapter.VH {
        val rowBinding = RowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(rowBinding)
    }
    class WorkoutDiff : DiffUtil.ItemCallback<WorkoutEntry>() {
        override fun areItemsTheSame(oldItem: WorkoutEntry, newItem: WorkoutEntry): Boolean {
            return false
        }
        override fun areContentsTheSame(oldItem: WorkoutEntry, newItem: WorkoutEntry): Boolean {
            return false

        }
    }
}
package edu.utap.exerciseapp.program

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TableRow
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import edu.utap.exerciseapp.MainViewModel
import edu.utap.exerciseapp.R
import edu.utap.exerciseapp.databinding.RowBinding
import edu.utap.exerciseapp.model.WorkoutEntry
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.w3c.dom.Text
import java.time.LocalDate


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
class ProgramAdapter(private val workoutlist: List<WorkoutEntry>, val context: Context, viewModel: MainViewModel) : RecyclerView.Adapter<ProgramAdapter.VH>() {
    var list = workoutlist


    var db = FirebaseFirestore.getInstance()
    var currentUser = FirebaseAuth.getInstance().currentUser
    val vm = viewModel
    override fun getItemCount() = list.size

    inner class VH(val rowBinding : RowBinding)
        : RecyclerView.ViewHolder(rowBinding.root){
        init{
        }

    }
    override fun onBindViewHolder(holder: ProgramAdapter.VH, position: Int) {
        val workout = list.get(position)
        val binding = holder.rowBinding
        val table = binding.workoutTable
        Log.d("date", "local date is ${LocalDate.now()}")
        binding.date.setText("${workout.getLocalDate()}")
        table.findViewById<TextView>(R.id.tableEntryNum).setText("$position")
        if (table.childCount > 3) {
            table.removeViews(3, table.childCount - 3)
        }
        var count = 0

        for (exercise in workout.list) {
            // populates row fields
            val row = LayoutInflater.from(context).inflate(R.layout.exercise_row, null) as TableRow
            row.findViewById<EditText>(R.id.Exercise).setText(exercise.Exercise)
            row.findViewById<EditText>(R.id.Rep).setText(exercise.Rep)
            row.findViewById<EditText>(R.id.Set).setText(exercise.Set)
            row.findViewById<EditText>(R.id.Weight).setText(exercise.Weight)
            row.findViewById<EditText>(R.id.RPE).setText(exercise.RPE)
            row.findViewById<EditText>(R.id.Note).setText(exercise.Note)
            row.findViewById<TextView>(R.id.entryNum).setText("$position")
            row.findViewById<TextView>(R.id.exerciseNum).setText("$count")
            count++
            table.addView(row)
            // on save, create an exercise class and send to firestore
            row.findViewById<Button>(R.id.saveBut).setOnClickListener{
                val workoutentry = list.get((row.findViewById<TextView>(R.id.entryNum).text.toString()).toInt())
                val exNum = row.findViewById<TextView>(R.id.exerciseNum).text.toString().toInt()
                val ex = WorkoutEntry.Workout()
                // populate exercise class
                ex.Exercise = row.findViewById<EditText>(R.id.Exercise).text.toString()
                ex.Rep = row.findViewById<EditText>(R.id.Rep).text.toString()
                ex.Set = row.findViewById<EditText>(R.id.Set).text.toString()
                ex.Weight = row.findViewById<EditText>(R.id.Weight).text.toString()
                ex.RPE = row.findViewById<EditText>(R.id.RPE).text.toString()
                ex.Note = row.findViewById<EditText>(R.id.Note).text.toString()
                if (exNum < workoutentry.list.size) {
                    // existing exercise update
                    workoutentry.list.set(exNum, ex)
                } else {
                    workoutentry.list.add(ex)
                    Log.d("adding to workoutentry", "adding")
                }
                vm.updateProgList(workout.getEntryNum(), workoutentry)
                // if currentuser is not null send to firestore
//                if (currentUser != null) {
//                    Log.d("Firestore", "Adding workouts to db")
//                    val uid = currentUser!!.uid
//                    // convert to map
//                    val docData: MutableMap<String, Any> = HashMap()
//                    docData["entries"] = list
//                    CoroutineScope(Dispatchers.IO).launch {
//                        db.collection("users").document(uid).collection("workouts")
//                            .document("WorkoutList").set(docData, SetOptions.merge())
//                            .addOnSuccessListener {
//                                Log.d("Firestore", "Success")
//                            }
//                            .addOnFailureListener {
//                                Log.d("Firestore", "Error uploading workouts")
//                            }
//                    }
//                }

            }


        }
        // add button
        binding.addExercise.setOnClickListener{
            val row = LayoutInflater.from(context).inflate(R.layout.exercise_row, null) as TableRow
            table.addView(row)
            // populates row
            val addEntryNum = table.findViewById<TextView>(R.id.tableEntryNum).text
            row.findViewById<TextView>(R.id.entryNum).text = addEntryNum
            row.findViewById<TextView>(R.id.exerciseNum).text = list.get(addEntryNum.toString().toInt()).list.size.toString()
            // save button activities
            row.findViewById<Button>(R.id.saveBut).setOnClickListener{
                val workoutentry = list.get((row.findViewById<TextView>(R.id.entryNum).text.toString()).toInt())
                val exNum = row.findViewById<TextView>(R.id.exerciseNum).text.toString().toInt()

                val ex = WorkoutEntry.Workout()
                ex.Exercise = row.findViewById<EditText>(R.id.Exercise).text.toString()
                ex.Rep = row.findViewById<EditText>(R.id.Rep).text.toString()
                ex.Set = row.findViewById<EditText>(R.id.Set).text.toString()
                ex.Weight = row.findViewById<EditText>(R.id.Weight).text.toString()
                ex.RPE = row.findViewById<EditText>(R.id.RPE).text.toString()
                ex.Note = row.findViewById<EditText>(R.id.Note).text.toString()
                if (exNum < workoutentry.list.size) {
                    // existing exercise update
                    workoutentry.list.set(exNum, ex)
                } else {
                    workoutentry.list.add(ex)
                    Log.d("adding to workoutentry", "adding")
                }
                vm.updateProgList(workout.getEntryNum(), workoutentry)
                Log.d("lskdjflsk", "${vm.getProgList()}")

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
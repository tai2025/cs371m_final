package edu.utap.exerciseapp.program

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import edu.utap.exerciseapp.databinding.ProgramFragmentBinding
import edu.utap.exerciseapp.model.EntryHolder
import edu.utap.exerciseapp.model.WorkoutEntry


class ProgramFragment: Fragment() {
    private var _binding: ProgramFragmentBinding? = null
    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!


    private var list = mutableListOf<WorkoutEntry>()
    private val newlist = mutableListOf<WorkoutEntry>()

    var db = FirebaseFirestore.getInstance()
    var currentUser = FirebaseAuth.getInstance().currentUser



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        _binding = ProgramFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = ProgramFragmentBinding.bind(view)
        binding.swipeRefreshLayout.setOnRefreshListener {
            binding.swipeRefreshLayout.isRefreshing = false
        }
        var adapter = ProgramAdapter(list, requireContext().applicationContext)

        if (currentUser != null) {
            val uid = currentUser!!.uid
            db.collection("users").document(uid).collection("workouts").document("WorkoutList")
                .get().addOnCompleteListener{
                    if (it.isSuccessful) {
                        val document = it.result
                        if (document.exists()) {
                            val values = document.data?.values
                            if (values != null) {
                                for (v in values) {
                                    val l = v as ArrayList<Map<String, Any>>
                                    val map = l[0]
                                    for (m in l) {

                                        val workout = WorkoutEntry()
                                        workout.setEntryNum(m["entryNum"].toString().toInt())
                                        val exlist = m["list"] as ArrayList<Map<String, Any>>

                                        for (ex in exlist) {
                                            val exercise = WorkoutEntry.Workout()
                                            exercise.Exercise = ex["exercise"].toString()
                                            exercise.Note = ex["note"].toString()
                                            exercise.Set = ex["set"].toString()
                                            exercise.RPE = ex["rpe"].toString()
                                            exercise.Weight = ex["weight"].toString()
                                            exercise.Rep = ex["rep"].toString()
                                            workout.addToList(exercise)
                                        }
                                        list.add(workout)
                                    }
                                }
                            }
                            Log.d("list", "$list")
                            adapter.list = list
                            adapter.notifyDataSetChanged()

                        }

                    } else {
                        Log.d("Error", "Error retrieving workout data")
                    }
                }
        }

        Log.d("adapter", "${adapter.itemCount}")
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(binding.recyclerView.context)
        binding.AddBut.setOnClickListener{
            val workout = WorkoutEntry()
            workout.setEntryNum(list.size)
            list.add(workout)
            for (l in list) {
                for (workout in l.list) {
                    Log.d("debugging", "${workout.Exercise}")
                }
                Log.d("lskdjlk", "lsdkjfkl")
            }
            newlist.add(workout)
            adapter.notifyDataSetChanged()
        }

//
//        val calendarView = findViewById<MaterialCalendarView>(R.id.calendarView)
//        calendarView.setOnDateChangedListener { _, date, _ ->
//            // Handle week selection here
//            // For simplicity, let's assume we navigate to WeekWorkoutsFragment directly
//            val weekWorkoutsFragment = WeekWorkoutsFragment.newInstance(date.toString())
//            supportFragmentManager.beginTransaction()
//                .replace(R.id.fragment_container, weekWorkoutsFragment)
//                .addToBackStack(null)
//                .commit()
//        }
    }




}
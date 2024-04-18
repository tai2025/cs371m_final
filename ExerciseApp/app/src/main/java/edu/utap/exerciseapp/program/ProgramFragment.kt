package edu.utap.exerciseapp.program

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import edu.utap.exerciseapp.databinding.ProgramFragmentBinding
import edu.utap.exerciseapp.model.EntryHolder
import edu.utap.exerciseapp.model.WorkoutEntry
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date


class ProgramFragment: Fragment() {
    private var _binding: ProgramFragmentBinding? = null
    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!


    private var list = mutableListOf<WorkoutEntry>()
    private val newlist = mutableListOf<WorkoutEntry>()

    var db = FirebaseFirestore.getInstance()
    var currentUser = FirebaseAuth.getInstance().currentUser

    private val args: ProgramFragmentArgs by navArgs()

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
        val date = args.date
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyy")
        val localdate: LocalDate = LocalDate.parse(date, formatter)
        _binding = ProgramFragmentBinding.bind(view)
        binding.swipeRefreshLayout.setOnRefreshListener {
            binding.swipeRefreshLayout.isRefreshing = false
        }
        var adapter = ProgramAdapter(list, requireContext().applicationContext, localdate)

        if (currentUser != null) {
            list.clear()
            val uid = currentUser!!.uid

            Log.d("date", "$localdate")

            val timestamp = Timestamp(Date.from(localdate.atStartOfDay(ZoneId.systemDefault()).toInstant()))
            db.collection("users").document(uid).collection("workouts").document("WorkoutList")

                .get()
                .addOnCompleteListener{
                    if (it.isSuccessful) {
                        val document = it.result
                        if (document.exists()) {
                            val values = document.data?.values
                            if (values != null) {
                                for (v in values) {
                                    Log.d("val", "$v")
                                    val l = v as ArrayList<Map<String, Any>>
                                    for (m in l) {
                                        val mdate: Map<String, Any> = m["localDate"] as Map<String, Any>

                                        if (mdate["dayOfMonth"].toString().toInt() == localdate.dayOfMonth
                                            && mdate["monthValue"].toString().toInt() == localdate.monthValue
                                            && mdate["year"].toString().toInt() == localdate.year) {
                                            val workout = WorkoutEntry()
                                            workout.setEntryNum(m["entryNum"].toString().toInt())

                                            var mm = "${mdate["monthValue"].toString()}"
                                            if (mm.toInt() < 10) {
                                                mm = "0$mm"
                                            }
                                            var dd = "${mdate["dayOfMonth"].toString()}"
                                            if (dd.toInt() < 10) {
                                                dd = "0$dd"
                                            }
                                            val dateformat = "${dd}/${mm}/${mdate["year"].toString()}"
                                            Log.d("dateformat", "$dateformat")
                                            workout.setLocalDate(dateformat)
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
            workout.setLocalDate(date)
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
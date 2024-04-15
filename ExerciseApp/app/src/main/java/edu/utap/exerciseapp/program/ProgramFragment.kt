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
import edu.utap.exerciseapp.databinding.ProgramFragmentBinding
import edu.utap.exerciseapp.model.WorkoutEntry


class ProgramFragment: Fragment() {
    private var _binding: ProgramFragmentBinding? = null
    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!


    private val list = mutableListOf<WorkoutEntry>()
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

        if (currentUser != null) {
            val uid = currentUser!!.uid
            db.collection("users").document(uid).collection("workouts")
                .get().addOnCompleteListener{
                    if (it.isSuccessful) {
                        for (w in it.result) {
                            list.add(w.toObject(WorkoutEntry::class.java))
                        }
                    } else {
                        Log.d("Error", "Error retrieving workout data")
                    }
                }
        }

        val adapter = ProgramAdapter(list, requireContext().applicationContext)
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(binding.recyclerView.context)
        binding.AddBut.setOnClickListener{
            val workout = WorkoutEntry()
//            val table = view.findViewById<TableLayout>(R.id.workoutTable)
//            val row = LayoutInflater.from(requireContext().applicationContext)
//                .inflate(R.layout.exercise_row, table) as TableRow
//            row.findViewById<EditText>(R.id.Exercise).setText("poop")
//            binding.AddBut.setText(row.findViewById<EditText>(R.id.Exercise).text)
//            val exercise = WorkoutEntry.Workout()
//            workout.addToList(exercise)
            list.add(workout)
            newlist.add(workout)
            adapter.notifyDataSetChanged()
//            row.findViewById<EditText>(R.id.Exercise).setOnEditorActionListener { /*v*/_, actionId, event ->
//                if ((event != null
//                            &&(event.action == KeyEvent.ACTION_DOWN)
//                            &&(event.keyCode == KeyEvent.KEYCODE_ENTER))
//                    || (actionId == EditorInfo.IME_ACTION_DONE)) {
//                    exercise.Exercise = row.findViewById<EditText>(R.id.Exercise).text.toString()
//                    adapter.notifyDataSetChanged()
//                }
//                false
//            }
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

    override fun onDestroyView() {
        if (currentUser != null) {
            Log.d("Firestore", "Adding workouts to db")
            for (workout in newlist) {
                val uid = currentUser!!.uid
                db.collection("users").document(uid).collection("workouts").add(workout)
                    .addOnSuccessListener {
                        Log.d("Firestore", "Success: ${it.id}")
                    }
                    .addOnFailureListener{
                        Log.d("Firestore", "Error uploading workouts")
                    }
            }
        }
        super.onDestroyView()
    }
}
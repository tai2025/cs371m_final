package edu.utap.exerciseapp.program
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CalendarView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import edu.utap.exerciseapp.databinding.CalendarProgramBinding
import androidx.navigation.fragment.findNavController
import androidx.navigation.findNavController
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import edu.utap.exerciseapp.MainViewModel
import edu.utap.exerciseapp.model.WorkoutEntry
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date

class CalendarFragment: Fragment() {
    private var _binding: CalendarProgramBinding? = null
    private val binding get() = _binding!!
    var db = FirebaseFirestore.getInstance()
    var currentUser = FirebaseAuth.getInstance().currentUser
    private val viewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        _binding = CalendarProgramBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = CalendarProgramBinding.bind(view)

        binding.calendarView.setOnDateChangeListener(CalendarView.OnDateChangeListener { view, year, month, dayOfMonth ->
            var mm = "$month"
            if (month < 10) {
                mm = "0$month"
            }
            var dd = "$dayOfMonth"
            if (dayOfMonth < 10) {
                dd = "0$dayOfMonth"
            }
            val date = "${dd}/${mm}/$year"

            val formatter = DateTimeFormatter.ofPattern("dd/MM/yyy")
            val localdate: LocalDate = LocalDate.parse(date, formatter)


            if (currentUser != null && viewModel.isQueried().value == false) {
                viewModel.setQueried(true)
                val uid = currentUser!!.uid

                Log.d("date", "$localdate")

                val timestamp = Timestamp(Date.from(localdate.atStartOfDay(ZoneId.systemDefault()).toInstant()))
                CoroutineScope(Dispatchers.IO).launch {
                    db.collection("users").document(uid).collection("workouts").document("WorkoutList")

                        .get()
                        .addOnCompleteListener {
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

                                                val workout = WorkoutEntry()
                                                workout.setEntryNum(
                                                    m["entryNum"].toString().toInt()
                                                )

                                                var mm = "${mdate["monthValue"].toString()}"
                                                if (mm.toInt() < 10) {
                                                    mm = "0$mm"
                                                }
                                                var dd = "${mdate["dayOfMonth"].toString()}"
                                                if (dd.toInt() < 10) {
                                                    dd = "0$dd"
                                                }
                                                val dateformat =
                                                    "${dd}/${mm}/${mdate["year"].toString()}"
                                                Log.d("dateformat", "$dateformat")
                                                workout.setLocalDate(dateformat)
                                                val exlist =
                                                    m["list"] as ArrayList<Map<String, Any>>

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
                                                viewModel.addToProgList(workout)

                                            }
                                        }
                                    }
//                                    Log.d("list", "$list")
//                                    adapter.list = list
//                                    adapter.notifyDataSetChanged()

                                }

                            } else {
                                Log.d("Error", "Error retrieving workout data")
                            }
                        }
                }
            }


//
            val direction = CalendarFragmentDirections.actionCalFragmentToProgram("${dd}/${mm}/$year")
            findNavController().navigate(direction)


        })
    }
}
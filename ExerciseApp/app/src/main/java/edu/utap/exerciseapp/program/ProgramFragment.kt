package edu.utap.exerciseapp.program

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.toObject
import edu.utap.exerciseapp.MainViewModel
import edu.utap.exerciseapp.databinding.ProgramFragmentBinding
import edu.utap.exerciseapp.model.EntryHolder
import edu.utap.exerciseapp.model.WorkoutEntry
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
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
    private val viewModel: MainViewModel by activityViewModels()

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

        list = viewModel.getProgList().toMutableList()
        Log.d("hi", "$list")
        val adapterList = mutableListOf<WorkoutEntry>()

        for (l in list) {
            val ldate = l.getLocalDate()
            if (ldate.dayOfMonth == localdate.dayOfMonth &&
                ldate.monthValue == localdate.monthValue &&
                ldate.year == localdate.year) {
                adapterList.add(l)
            }
        }

        var adapter = ProgramAdapter(adapterList, requireContext().applicationContext, viewModel)



        Log.d("adapter", "${adapter.itemCount}")
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(binding.recyclerView.context)
        binding.AddBut.setOnClickListener{
            val workout = WorkoutEntry()
            workout.setEntryNum(list.size)
            workout.setLocalDate(date)
            adapterList.add(workout)
            for (l in list) {
                for (workout in l.list) {
                    Log.d("debugging", "${workout.Exercise}")
                }
                Log.d("lskdjlk", "lsdkjfkl")
            }
            newlist.add(workout)
            adapter.notifyDataSetChanged()
        }

    }

    override fun onPause() {
        super.onPause()
        Log.d("what", "${viewModel.getProgList()}")
        if (currentUser != null) {
            Log.d("Firestore", "Adding workouts to db")
            val uid = viewModel.getUID().value!!
            val docData: MutableMap<String, Any> = HashMap()
            docData["entries"] = viewModel.getProgList()
            CoroutineScope(Dispatchers.IO).launch {
                db.collection("users").document(uid).collection("workouts")
                    .document("WorkoutList").set(docData, SetOptions.merge())
                    .addOnSuccessListener {
                        Log.d("Firestore", "Success")
                    }
                    .addOnFailureListener {
                        Log.d("Firestore", "Error uploading workouts")
                    }
            }
        }
    }




}
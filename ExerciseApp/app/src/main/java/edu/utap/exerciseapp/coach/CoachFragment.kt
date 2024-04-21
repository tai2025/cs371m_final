package edu.utap.exerciseapp.coach

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import edu.utap.exerciseapp.databinding.CoachViewBinding
import edu.utap.exerciseapp.databinding.ProgramFragmentBinding
import edu.utap.exerciseapp.model.WorkoutEntry
import edu.utap.exerciseapp.program.ProgramAdapter
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.SetOptions
import edu.utap.exerciseapp.MainViewModel
import edu.utap.exerciseapp.view.HomeFragmentDirections
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CoachFragment: Fragment() {
    private var _binding: CoachViewBinding? = null
    // This property is only valid between onCreateView and onDestroyView.
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
        _binding = CoachViewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = CoachViewBinding.bind(view)
        val list = mutableListOf<String>()

        CoroutineScope(Dispatchers.IO).launch {
            currentUser?.let { it1 ->
                db.collection("users").whereEqualTo("coach", currentUser!!.email)
                    .get()
                    .addOnSuccessListener {
                        Log.d("Firestore", "Success user")
                        val documents = it.documents
                        for (d in documents) {
                            Log.d("d", "$d")
                            Log.d("test", "${d.data?.get("email")}")
                            Log.d("lkj", "${d.data}")
                            list.add(d.data?.get("uid").toString())
//                            list.add(d.key.toString())
                            Log.d("list", "$list")
                        }

                        var adapter = CoachRV(list, viewModel) {
                            viewModel.setUid(it)
                            Log.d("uidcoachfrag", "${viewModel.getUID().value}")
                            var direction = CoachFragmentDirections.actionCoachFragmentToCalendar()
                            findNavController().navigate(direction)
                        }
                        Log.d("list", "$list")
                        binding.recyclerView.adapter = adapter
                        binding.recyclerView.layoutManager = LinearLayoutManager(binding.recyclerView.context)
                    }
                    .addOnFailureListener {
                        Log.d("Firestore", "Error uploading user")
                    }
            }
        }
    }
}
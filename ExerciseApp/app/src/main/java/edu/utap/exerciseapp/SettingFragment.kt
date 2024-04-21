package edu.utap.exerciseapp

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import edu.utap.exerciseapp.databinding.CoachViewBinding
import edu.utap.exerciseapp.databinding.SettingFragBinding
import edu.utap.exerciseapp.model.UserModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SettingFragment : Fragment() {
    private var _binding: SettingFragBinding? = null
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
        _binding = SettingFragBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = SettingFragBinding.bind(view)
        binding.setCoach.setOnClickListener {
            val u = viewModel.getCurUser().value as UserModel
            if (u != null) {
                u.setCoach(binding.coachET.text.toString())
                Log.d("whatsettings", "${currentUser?.uid!!}")
                u.setUID(currentUser?.uid!!)
            }
            Log.d("uidsettings", "${u.getUID()}")
            CoroutineScope(Dispatchers.IO).launch {
                db.collection("users")
                    .whereEqualTo("email", binding.coachET.text.toString())
                    .get()
                    .addOnSuccessListener {
                        val document = it.documents
                        for (d in document) {
                            Log.d("what", "${d.toString()}")
                            val data = d.data
                            Log.d("data", "$data")
                            var clients = data?.get("clients")?.toString()
                            if (clients != null) {
                                clients = clients.substring(1, clients.length - 1)
                            }
                            val clist : MutableList<String> =
                                clients?.split(",")?.toMutableList()!!
                            clist.add(u.getEmail() )
                            val update = hashMapOf("clients" to clist)
                            Log.d("update", "$update")
                            db.collection("users").document(d.toString()).set(update, SetOptions.merge())
                                .addOnSuccessListener  {
                                    Log.d("success", "lets goooo")
                                }

                        }
                    }}
            CoroutineScope(Dispatchers.IO).launch {
                currentUser?.let { it1 ->
                    db.collection("users").document(it1.uid)
                        .set(u)
                        .addOnSuccessListener {
                            Log.d("Firestore", "Success user")
                        }
                        .addOnFailureListener {
                            Log.d("Firestore", "Error uploading user")
                        }
                }
            }
        }

    }
}


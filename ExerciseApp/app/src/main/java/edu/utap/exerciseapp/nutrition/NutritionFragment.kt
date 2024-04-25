package edu.utap.exerciseapp.nutrition

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import edu.utap.exerciseapp.MainViewModel
import edu.utap.exerciseapp.api.RetNut
import  edu.utap.exerciseapp.databinding.NutritionPageBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import edu.utap.exerciseapp.model.FoodModel
import kotlinx.coroutines.CoroutineScope


class NutritionFragment: Fragment() {
    private var _binding: NutritionPageBinding? = null
    private val binding get() = _binding!!
    private val viewModel : MainViewModel by activityViewModels()
    var currentUser = FirebaseAuth.getInstance().currentUser
    var db = FirebaseFirestore.getInstance()


    private fun initAdapter(binding: NutritionPageBinding) {
        var list = viewModel.getListOfNames()
        list.add(0, "")
        val spinnerAdapter = ArrayAdapter(
            binding.root.context,
            android.R.layout.simple_spinner_item,
            list
        )
        val adapter = NutritionAdapter(viewModel, spinnerAdapter)
        viewModel.observeFoods().observe(viewLifecycleOwner) {
            adapter.replaceList(it)
            adapter.notifyDataSetChanged()
        }
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(activity)

    }

    private fun initSwipeLayout(swipe : SwipeRefreshLayout) {
        swipe.setOnRefreshListener {
            swipe.isRefreshing = false
        }

    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        _binding = NutritionPageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (viewModel.getUID().value != null && viewModel.isQueried().value == false) {
            viewModel.setQueried(true)
            val uid = viewModel.getUID().value!!
            CoroutineScope(Dispatchers.IO).launch {
                db.collection("users").document(uid).collection("foods")
                    .document("FoodsList")
                    .get()
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            val document = it.result
                            if (document.exists()) {
                                val values = document.data?.values
                                Log.d("firebase get", "$values")
                                if (values != null) {
                                    for (v in values) {
                                        val l = v as ArrayList<Map<String, Any>>
                                        for (m in l) {
                                            Log.d("firebase get", "$m")
                                            val name = m["name"]
                                            val list = m["list"] as ArrayList<Map<String, Any>>
                                            var retList = emptyList<RetNut>().toMutableList()
                                            for (stat in list){
                                                val carb = stat["carb"]
                                                val cat = stat["cat"]
                                                val protein = stat["protein"]
                                                val fat = stat["fat"]
                                                val name = stat["name"]
                                                val company = stat["company"]
                                                val cal = stat["cal"]
                                                retList.add(
                                                    RetNut(
                                                        name.toString(),
                                                        company.toString(),
                                                        cat.toString(),
                                                        protein.toString().toDouble(),
                                                        fat.toString().toDouble(),
                                                        carb.toString().toDouble(),
                                                        cal.toString().toDouble())
                                                )
                                            }
                                            viewModel.addFoodModel(FoodModel(retList, name.toString()))
                                        }
                                    }
                                }
                            }

                        } else {
                            Log.d("Error", "Error retrieving workout data")
                        }
                    }
            }
        }
        initAdapter(binding)
        initSwipeLayout(binding.swipeRefreshLayout)
        binding.searchButton.setOnClickListener {
            viewModel.searchFood(binding.searchBar.text.toString())
        }
        binding.favButton.setOnClickListener {
            findNavController().navigate(NutritionFragmentDirections.actionNutToTotal())
        }
    }

    override fun onPause() {
        super.onPause()
        viewModel.emptyFoods()
        if (currentUser != null) {
            Log.d("Firestore", "Adding workouts to db")
            val uid = viewModel.getUID().value!!
            val docData: MutableMap<String, Any> = HashMap()
            docData["foodies"] = viewModel.getList()
            CoroutineScope(Dispatchers.IO).launch {
                db.collection("users").document(uid).collection("foods")
                    .document("FoodsList").set(docData, SetOptions.merge())
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
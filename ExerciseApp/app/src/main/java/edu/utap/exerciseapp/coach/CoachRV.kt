package edu.utap.exerciseapp.coach

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TableRow
import android.widget.TextView
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import edu.utap.exerciseapp.MainViewModel
import edu.utap.exerciseapp.R
import edu.utap.exerciseapp.databinding.ClientRowBinding
import edu.utap.exerciseapp.model.WorkoutEntry
import edu.utap.exerciseapp.coach.CoachRV
import edu.utap.exerciseapp.model.UserModel
import java.time.LocalDate

class CoachRV(val list: MutableList<UserModel>, val viewModel: MainViewModel, private val navigateToClient: (String)->Unit ): RecyclerView.Adapter<CoachRV.VH>() {

    val clients = list.toList()
    override fun getItemCount() = clients.size

    inner class VH(val rowBinding : ClientRowBinding)
            : RecyclerView.ViewHolder(rowBinding.root){
            init{
                rowBinding.client.setOnClickListener {
                    viewModel.setUid(rowBinding.client.text.toString())
                    Log.d("rvuid", "${rowBinding.client.text.toString()}")
                    Log.d("vm", "${viewModel.getUID().value}")
                    navigateToClient(rowBinding.uid.text.toString())
                }
            }
            }
        override fun onBindViewHolder(holder: CoachRV.VH, position: Int) {
            val c = clients.get(position)
            val binding = holder.rowBinding
            binding.client.text = c.getEmail()
            binding.uid.text = c.getUID()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CoachRV.VH {
            val rowBinding = ClientRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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
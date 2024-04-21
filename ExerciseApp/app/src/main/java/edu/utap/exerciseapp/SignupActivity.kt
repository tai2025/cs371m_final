package edu.utap.exerciseapp

import android.R.layout
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import edu.utap.exerciseapp.R
import edu.utap.exerciseapp.databinding.ActivitySignUpBinding
import edu.utap.exerciseapp.model.UserModel
import androidx.fragment.app.viewModels
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SignupActivity : AppCompatActivity() {
    private var auth: FirebaseAuth? = null
    private var signupEmail: EditText? = null
    private var signupPassword: EditText? = null
    private var signupButton: Button? = null
    private var loginRedirectText: TextView? = null
    private lateinit var binding: ActivitySignUpBinding
    var db = FirebaseFirestore.getInstance()
    private val viewModel: MainViewModel by viewModels()
//    var currentUser = FirebaseAuth.getInstance().currentUser
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_sign_up)
        auth = FirebaseAuth.getInstance()
        signupEmail = findViewById<EditText>(R.id.signup_email)
        signupPassword = findViewById<EditText>(R.id.signup_password)
        signupButton = findViewById<Button>(R.id.signup_button)
        loginRedirectText = findViewById<TextView>(R.id.loginRedirectText)
        val roles = resources.getStringArray(R.array.roles)
        val spinner = findViewById<Spinner>(R.id.spinner)
        if (spinner != null) {
            val adapter = ArrayAdapter(this,
                layout.simple_spinner_item, roles)
            spinner.adapter = adapter
        }
        signupButton!!.setOnClickListener(View.OnClickListener {
            val user = signupEmail!!.getText().toString().trim { it <= ' ' }
            val pass = signupPassword!!.getText().toString().trim { it <= ' ' }
            if (user.isEmpty()) {
                signupEmail!!.setError("Email cannot be empty")
            }
            if (pass.isEmpty()) {
                signupPassword!!.setError("Password cannot be empty")
            }
            if(spinner.selectedItem == null) {
                Toast.makeText(
                    this@SignupActivity,
                    "Select a role",
                    Toast.LENGTH_LONG
                ).show()
            }
            else {
                auth!!.createUserWithEmailAndPassword(user, pass).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(
                            this@SignupActivity,
                            "SignUp Successful",
                            Toast.LENGTH_SHORT
                        ).show()
                        val userModel = UserModel()
                        userModel.setEmail(user)

                        var currentUser = FirebaseAuth.getInstance().currentUser
                        if (currentUser != null) {
                            userModel.setUID(currentUser.uid)
                        }
                        Log.d("selectedItem", "${spinner.selectedItem.toString()}")
                        userModel.setRole(spinner.selectedItem.toString())
                        viewModel.setCurUser(userModel)
                        task.result.user?.let { it1 ->
                            CoroutineScope(Dispatchers.IO).launch {
                                db.collection("users").document(it1.uid).set(userModel, SetOptions.merge())
                                    .addOnSuccessListener {
                                        Log.d("Firestore", "Success user")
                                    }
                                    .addOnFailureListener{
                                        Log.d("Firestore", "Error uploading user")
                                    }
                            }
                        }

                        startActivity(Intent(this@SignupActivity, LoginActivity::class.java))
                    } else {
                        Toast.makeText(
                            this@SignupActivity,
                            "SignUp Failed" + task.exception!!.message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        })
        loginRedirectText!!.setOnClickListener(View.OnClickListener {
            startActivity(
                Intent(
                    this@SignupActivity,
                    LoginActivity::class.java
                )
            )
        })
    }
}


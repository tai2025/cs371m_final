package edu.utap.exerciseapp

import android.R.layout
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import edu.utap.exerciseapp.R
import edu.utap.exerciseapp.databinding.ActivitySignUpBinding

class SignupActivity : AppCompatActivity() {
    private var auth: FirebaseAuth? = null
    private var signupEmail: EditText? = null
    private var signupPassword: EditText? = null
    private var signupButton: Button? = null
    private var loginRedirectText: TextView? = null
    private lateinit var binding: ActivitySignUpBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_sign_up)
        auth = FirebaseAuth.getInstance()
        signupEmail = findViewById<EditText>(R.id.signup_email)
        signupPassword = findViewById<EditText>(R.id.signup_password)
        signupButton = findViewById<Button>(R.id.signup_button)
        loginRedirectText = findViewById<TextView>(R.id.loginRedirectText)
        signupButton!!.setOnClickListener(View.OnClickListener {
            val user = signupEmail!!.getText().toString().trim { it <= ' ' }
            val pass = signupPassword!!.getText().toString().trim { it <= ' ' }
            if (user.isEmpty()) {
                signupEmail!!.setError("Email cannot be empty")
            }
            if (pass.isEmpty()) {
                signupPassword!!.setError("Password cannot be empty")
            } else {
                auth!!.createUserWithEmailAndPassword(user, pass).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(
                            this@SignupActivity,
                            "SignUp Successful",
                            Toast.LENGTH_SHORT
                        ).show()
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


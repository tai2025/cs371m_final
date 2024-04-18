package edu.utap.exerciseapp

import android.R.layout
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.developer.gbuttons.GoogleSignInButton
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import edu.utap.exerciseapp.databinding.ActivityLoginBinding
import edu.utap.exerciseapp.databinding.ActivityMainBinding
import edu.utap.exerciseapp.model.UserModel

class LoginActivity : AppCompatActivity() {
    private var loginEmail: EditText? = null
    private var loginPassword: EditText? = null
    private var signupRedirectText: TextView? = null
    private var loginButton: Button? = null
    private var auth: FirebaseAuth? = null
    var forgotPassword: TextView? = null
    var googleBtn: GoogleSignInButton? = null
    var gOptions: GoogleSignInOptions? = null
    var gClient: GoogleSignInClient? = null
    private lateinit var binding: ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_login)
        loginEmail = findViewById(R.id.login_email)
        loginPassword = findViewById(R.id.login_password)
        loginButton = findViewById(R.id.login_button)
        signupRedirectText = findViewById(R.id.signUpRedirectText)
        forgotPassword = findViewById(R.id.forgot_password)
        googleBtn = binding.googleBtn
        auth = FirebaseAuth.getInstance()
        loginButton!!.setOnClickListener(View.OnClickListener {
            val email = loginEmail!!.getText().toString()
            val pass = loginPassword!!.getText().toString()
            if (!email.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                if (!pass.isEmpty()) {
                    auth!!.signInWithEmailAndPassword(email, pass)
                        .addOnSuccessListener {
                            Toast.makeText(
                                this@LoginActivity,
                                "Login Successful",
                                Toast.LENGTH_SHORT
                            ).show()
                            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                            finish()
                        }.addOnFailureListener {
                            Toast.makeText(
                                this@LoginActivity,
                                "Login Failed",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                } else {
                    loginPassword!!.setError("Empty fields are not allowed")
                }
            } else if (email.isEmpty()) {
                loginEmail!!.setError("Empty fields are not allowed")
            } else {
                loginEmail!!.setError("Please enter correct email")
            }
        })
        signupRedirectText!!.setOnClickListener(View.OnClickListener {
            startActivity(
                Intent(
                    this@LoginActivity,
                    SignupActivity::class.java
                )
            )
        })
        forgotPassword!!.setOnClickListener(View.OnClickListener {
            val builder = AlertDialog.Builder(this@LoginActivity)
            val dialogView: View = layoutInflater.inflate(R.layout.dialog_forgot, null)
            val emailBox = dialogView.findViewById<EditText>(R.id.emailBox)
            builder.setView(dialogView)
            val dialog = builder.create()
            dialogView.findViewById<View>(R.id.btnReset).setOnClickListener(View.OnClickListener {
                val userEmail = emailBox.getText().toString()
                if (TextUtils.isEmpty(userEmail) && !Patterns.EMAIL_ADDRESS.matcher(userEmail)
                        .matches()
                ) {
                    Toast.makeText(
                        this@LoginActivity,
                        "Enter your registered email id",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@OnClickListener
                }
                auth!!.sendPasswordResetEmail(userEmail).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(
                            this@LoginActivity,
                            "Check your email",
                            Toast.LENGTH_SHORT
                        ).show()
                        dialog.dismiss()
                    } else {
                        Toast.makeText(
                            this@LoginActivity,
                            "Unable to send, failed",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            })
            dialogView.findViewById<View>(R.id.btnCancel).setOnClickListener { dialog.dismiss() }
            if (dialog.window != null) {
                dialog.window!!.setBackgroundDrawable(ColorDrawable(0))
            }
            dialog.show()
        })
        //Inside onCreate
        gOptions =
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build()
        gClient = GoogleSignIn.getClient(this, gOptions!!)
        val gAccount = GoogleSignIn.getLastSignedInAccount(this)
        if (gAccount != null) {
            finish()
            val intent = Intent(this@LoginActivity, MainActivity::class.java)
            startActivity(intent)
        }
        val activityResultLauncher =
            registerForActivityResult<Intent, ActivityResult>(
                ActivityResultContracts.StartActivityForResult(),
                object : ActivityResultCallback<ActivityResult?> {
                    override fun onActivityResult(result: ActivityResult?) {
                        if (result != null) {
                            if (result.resultCode == RESULT_OK) {
                                val data = result.data
                                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                                try {
                                    task.getResult(ApiException::class.java)
                                    finish()
                                    val intent = Intent(this@LoginActivity, MainActivity::class.java)
                                    startActivity(intent)
                                } catch (e: ApiException) {
                                    Toast.makeText(
                                        this@LoginActivity,
                                        "Something went wrong",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        }
                    }
                })
        googleBtn!!.setOnClickListener(View.OnClickListener {
            val signInIntent = gClient!!.signInIntent
            activityResultLauncher.launch(signInIntent)
        })
    }
}
package edu.utap.exerciseapp

//import android.R

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import edu.utap.exerciseapp.databinding.ActivityMainBinding
import edu.utap.exerciseapp.model.UserModel
import edu.utap.exerciseapp.program.CalendarFragment
import edu.utap.exerciseapp.program.ProgramFragment
import edu.utap.exerciseapp.view.HomeFragmentDirections
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var authUser : AuthUser
    private val viewModel: MainViewModel by viewModels()


    var userName: TextView? = null
    var logout: Button? = null
    var gClient: GoogleSignInClient? = null
    var gOptions: GoogleSignInOptions? = null
    var db = FirebaseFirestore.getInstance()
    var currentUser = FirebaseAuth.getInstance().currentUser
    companion object {
        const val TAG = "MainActivity"
    }

//    fun progressBarOn() {
//        binding.indeterminateBar.visibility = View.VISIBLE
//    }
//
//    fun progressBarOff() {
//        binding.indeterminateBar.visibility = View.GONE
//    }

    private fun initMenu() {
        addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                // Inflate the menu; this adds items to the action bar if it is present.
                menuInflater.inflate(R.menu.menu_main, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.menuLogout -> {
                        if (currentUser != null)
                            Log.d("Firestore", "Adding workouts to db")
                        val uid = currentUser!!.uid
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
                        gClient!!.signOut().addOnCompleteListener {
                           if (it.isSuccessful) {


                               finish()
                               startActivity(Intent(this@MainActivity, LoginActivity::class.java))
                           } else {
                               Log.d("logout", "failed to signout")
                           }
                        }

//                        FirebaseAuth.getInstance().signOut()
                        true
                    }
                    else -> false
                }
            }
        })
    }

    private fun setCurrentFragment(fragment:Fragment)=
    supportFragmentManager.beginTransaction().apply {
        replace(R.id.mainFrame,fragment)
        commit()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate")

        // Set the layout for the layout we created
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        gOptions =
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build()
        gClient = GoogleSignIn.getClient(this, gOptions!!)
        initMenu()

        // Set up our nav graph
        navController = findNavController(R.id.mainFrame)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)
//
//        currentUser?.let {
//            CoroutineScope(Dispatchers.IO).launch {
//                db.collection("users").document(it.uid)
//
//                    .get()
//                    .addOnCompleteListener {
//                        val u = it.result.data as Map<String, Any>
//                        val value = u.values
//                        Log.d("val", "=$value")
//
//                        if (value.isNotEmpty()) {
//                            val um = UserModel()
//                            val cstring = u["clients"].toString()
//                            val list = cstring.substring(1, cstring.length - 1).split(",")
//                            um.setClients(list.toMutableList())
//                            um.setRole(u["role"].toString())
//                            um.setEmail(u["email"].toString())
//                            Log.d("role", "${um.getRole()}")
//                            viewModel.setCurUser(um)
//                            if (um.getRole().equals("Coach")) {
//                                navController.safeNavigate(HomeFragmentDirections.actionHomeToCoach())
//                            }
//                        }
//
//                        // No need to override onSupportNavigateUp(), because no up navigation
//
//                    }
//            }
//        }
        binding.bottomNavigationView.setOnItemSelectedListener {
            when(it.itemId){
                R.id.nutrition -> navController.safeNavigate(HomeFragmentDirections.actionHomeToNut())
                R.id.programs -> navController.safeNavigate(HomeFragmentDirections.actionHomeToCal())
                R.id.settings -> navController.safeNavigate(HomeFragmentDirections.actionHomeToSet())
            }
            true
        }
    }

    private fun NavController.safeNavigate(direction: NavDirections) {
        currentDestination?.
                getAction(direction.actionId)?.
                run{
                    navigate(direction)
                }
    }

    // We can only safely initialize AuthUser once onCreate has completed.
    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart")
        // Create authentication object.  This will log the user in if needed
        authUser = gClient?.let { AuthUser(activityResultRegistry, this, it) }!!
        // authUser needs to observe our lifecycle so it can run login activity
        lifecycle.addObserver(authUser)
        authUser.observeUser().observe(this) {
            // XXX Write me, user status has changed
            viewModel.setCurrentAuthUser(it)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp()
                || super.onSupportNavigateUp()
    }
}

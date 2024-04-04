package edu.utap.exerciseapp

//import android.R
import android.R.layout
import android.R.menu
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import edu.utap.exerciseapp.LoginActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import edu.utap.exerciseapp.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var authUser : AuthUser
    private val viewModel: MainViewModel by viewModels()


    var userName: TextView? = null
    var logout: Button? = null
    var gClient: GoogleSignInClient? = null
    var gOptions: GoogleSignInOptions? = null
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
                        authUser.logout()
                        true
                    }
                    else -> false
                }
            }
        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate")
        // Set the layout for the layout we created
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initMenu()


        // Set up our nav graph
        navController = findNavController(R.id.mainFrame)
        val appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)
        // No need to override onSupportNavigateUp(), because no up navigation
    }

    // We can only safely initialize AuthUser once onCreate has completed.
    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart")
        // Create authentication object.  This will log the user in if needed
        authUser = AuthUser(activityResultRegistry)
        // authUser needs to observe our lifecycle so it can run login activity
        lifecycle.addObserver(authUser)
        viewModel.fetchPhotoMeta {  }
        authUser.observeUser().observe(this) {
            // XXX Write me, user status has changed
            viewModel.setCurrentAuthUser(it)
        }
    }
}

package edu.utap.exerciseapp

//import android.R
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
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import edu.utap.exerciseapp.databinding.ActivityMainBinding

class SecondFragment:Fragment(R.layout.home_page)

class ThirdFragment:Fragment(R.layout.profile_page)

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
        initMenu()
        gOptions =
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build()
        gClient = GoogleSignIn.getClient(this, gOptions!!)

        val secondFragment=SecondFragment()
        val thirdFragment=ThirdFragment()

        // Set up our nav graph
        navController = findNavController(R.id.mainFrame)
        val appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)

        // No need to override onSupportNavigateUp(), because no up navigation
        binding.bottomNavigationView.setOnItemSelectedListener {
            when(it.itemId){
                R.id.nutrition -> setCurrentFragment(secondFragment)
                R.id.programs -> setCurrentFragment(secondFragment)
                R.id.settings -> setCurrentFragment(thirdFragment)

            }
            true
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
        viewModel.fetchPhotoMeta {  }
        authUser.observeUser().observe(this) {
            // XXX Write me, user status has changed
            viewModel.setCurrentAuthUser(it)
        }
    }
}

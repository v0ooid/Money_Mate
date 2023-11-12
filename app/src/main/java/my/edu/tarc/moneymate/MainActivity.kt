package my.edu.tarc.moneymate

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Window
import android.view.WindowManager
import androidx.core.view.ContentInfoCompat.Flags
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import my.edu.tarc.moneymate.databinding.ActivityAppLock6DigitBinding
import my.edu.tarc.moneymate.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Log.w("Testing2","mainactivity")
        val navView:BottomNavigationView = binding.navView
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_container) as NavHostFragment
        navController = navHostFragment.navController
        Log.d("Testing","testing main activity")
        window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        window.setFlags(WindowManager.LayoutParams.FLAG_SECURE,WindowManager.LayoutParams.FLAG_SECURE)

//        val appBarConfiguration = AppBarConfiguration(
//            setOf(
//                R.id.transactionFragment,R.id.navigation_budgetNgoal,R.id.navigation_feature,R.id.navigation_user
//            )
//        )
//        setupActionBarWithNavController(navController,appBarConfiguration)
        navView.setupWithNavController(navController)
import android.view.View
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import my.edu.tarc.moneymate.Profile.SignInActivity
import my.edu.tarc.moneymate.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    @SuppressLint("RestrictedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Thread.sleep(3000)
        installSplashScreen()

        //Check login state
        if (!isLoggedIn()) {
            // User is not logged in, navigate to the login screen
            navigateToLogin()

        } else {
            binding =  ActivityMainBinding.inflate(layoutInflater)
            setContentView(binding.root)

            val navView: BottomNavigationView = binding.navView
            val navController = findNavController(R.id.nav_host_fragment_container)
            navView.setupWithNavController(navController)
            navController.addOnDestinationChangedListener { _, destination, _ ->
                supportActionBar?.setShowHideAnimationEnabled(false)
                if(destination.id == R.id.monetaryAccountFragment) {
                    navView.visibility = View.GONE
                }else{
                    navView.visibility = View.VISIBLE
                }
            }

        }
    }

    private fun isLoggedIn(): Boolean {
        val sharedPref = getSharedPreferences("UserDetails", Context.MODE_PRIVATE)
        val userId = sharedPref.getString("userId", "")
        return userId?.isNotEmpty() ?: false
    }

    private fun navigateToLogin() {
        // Start your LoginActivity or navigate to the login screen here
        // Example: Launch a new LoginActivity
        val intent = Intent(this, SignInActivity::class.java)
        startActivity(intent)
        finish() // Optional: Finish the MainActivity to prevent going back to it after logging in
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_container)
        val appBarConfiguration = AppBarConfiguration(navController.graph)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}
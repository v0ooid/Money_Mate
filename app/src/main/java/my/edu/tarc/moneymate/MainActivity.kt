package my.edu.tarc.moneymate

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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
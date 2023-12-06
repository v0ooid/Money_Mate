package my.edu.tarc.moneymate

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController

import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.edit
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.findNavController
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.storage.FirebaseStorage
import my.edu.tarc.moneymate.AppLock.AppLock6Activity
import my.edu.tarc.moneymate.AppLock.AppLock4Activity
import my.edu.tarc.moneymate.AppLock.AppLockCustPassActivity
import my.edu.tarc.moneymate.Profile.SignInActivity
import my.edu.tarc.moneymate.databinding.ActivityMainBinding
import java.time.LocalDate

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var incomerecyclerView: RecyclerView
    private lateinit var expenserecyclerView: RecyclerView
    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("RestrictedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Thread.sleep(3000)
        installSplashScreen()

        val sharedPreferences = getSharedPreferences("APP_LOCK_PREFS", Context.MODE_PRIVATE)
        val appLockUnlock = sharedPreferences.getBoolean("APP_LOCK_UNLOCKED", true)

        Log.e("status",appLockUnlock.toString())

        if (!isLoggedIn()) {

            // User is not logged in, navigate to the login screen
            navigateToLogin()

        } else {
            if (!appLockUnlock) {
                checkAppLockStatus()
            } else {
                onUserLogin()

                val navView: BottomNavigationView = binding.navView
                val navHostFragment =
                    supportFragmentManager.findFragmentById(R.id.nav_host_fragment_container) as NavHostFragment
                navController = navHostFragment.navController
                navController.addOnDestinationChangedListener { _, destination, _ ->
                    supportActionBar?.setShowHideAnimationEnabled(false)
                    if (destination.id == R.id.monetaryAccountFragment
                        || destination.id == R.id.appLockSetup4DigitFragment || destination.id == R.id.appLockSetup6DigitFragment
                        || destination.id == R.id.appLockSetupCusPassFragment || destination.id == R.id.appLockFragment
                        || destination.id == R.id.appLockTypeFragment || destination.id == R.id.appLock4DigitFragment
                        || destination.id == R.id.appLock6DigitFragment || destination.id == R.id.appLockCustomPasswordFragment || destination.id == R.id.transactionFragment
                        ||destination.id == R.id.setAlarmFragment ||destination.id == R.id.alarmNotificationFragment||destination.id == R.id.goalFragment||destination.id == R.id.goalCreateFragment
                        ||destination.id == R.id.reportFragment || destination.id == R.id.financialAdvisorFragment2|| destination.id == R.id.dataExportFragment || destination.id == R.id.dataSyncFragment
                        ) {
                        navView.visibility = View.GONE
                    } else {
                        navView.visibility = View.VISIBLE
                    }
                }
                Log.d("Testing", "testing main activity")
                window.setFlags(
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
                )
                window.setFlags(
                    WindowManager.LayoutParams.FLAG_SECURE,
                    WindowManager.LayoutParams.FLAG_SECURE
                )

                navView.setupWithNavController(navController)
            }
        }

    }

    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {
        // Disable the back button functionality
        // Remove the line below to re-enable the back button
        // super.onBackPressed()
    }

    override fun onDestroy() {
        resetAppLockUnlockedPreference()
        super.onDestroy()
        Log.e("onDestory", "onDestory")
    }

    override fun onPause() {
        resetAppLockUnlockedPreference()
        super.onPause()
        Log.e("onPause", "onPause")
    }

    override fun onStop() {
        resetAppLockUnlockedPreference()
        super.onStop()
        Log.e("onStop", "onStop")
    }

    private fun resetAppLockUnlockedPreference() {
        val sharedPreferences = getSharedPreferences("APP_LOCK_PREFS", Context.MODE_PRIVATE)
        val profilePref = getSharedPreferences("APP_PROFILE_PREFS", Context.MODE_PRIVATE)
        val budgetPref = getSharedPreferences("APP_BUDGET_PREFS", Context.MODE_PRIVATE)
        val forumPref = getSharedPreferences("APP_FORUM_PREFS", Context.MODE_PRIVATE)


        if (sharedPreferences.getBoolean("APP_LOCK_ENABLED_KEY", false) == true) {
            sharedPreferences.edit().putBoolean("APP_LOCK_UNLOCKED", false).apply()
            profilePref.edit().putBoolean("Locked", true).apply()
            budgetPref.edit().putBoolean("Locked", true).apply()
            forumPref.edit().putBoolean("Locked", true).apply()
        } else {
            sharedPreferences.edit().putBoolean("APP_LOCK_UNLOCKED", true).apply()
            profilePref.edit().putBoolean("Locked", false).apply()
            budgetPref.edit().putBoolean("Locked", false).apply()
            forumPref.edit().putBoolean("Locked", false).apply()
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
//        finish() // Optional: Finish the MainActivity to prevent going back to it after logging in
    }

    private fun checkAppLockStatus() {
        val sharedPreferences = getSharedPreferences("APP_LOCK_PREFS", Context.MODE_PRIVATE)

        val appLockStatus = sharedPreferences.getBoolean("APP_LOCK_ENABLED_KEY", false)
        val appLockType = sharedPreferences.getString("SECURITY_TYPE_KEY", "")
        val appLockPIN = sharedPreferences.getString("PIN_KEY", "")

        Log.e("Tag1", appLockStatus.toString())
        Log.e("Tag2", sharedPreferences.getBoolean("APP_LOCK_UNLOCKED", true).toString())

        if (appLockStatus) {
            if (appLockType != null && appLockType.isNotEmpty() && appLockPIN != null && appLockPIN.isNotEmpty()) {

                if (appLockType == "4Digit"){
                    val intent = Intent(this, AppLock4Activity::class.java)
                    intent.putExtra("FRAGMENT_LOCK", "MAIN_ACTIVITY")
                    startActivity(intent)

                } else if (appLockType == "6Digit"){
                    val intent = Intent(this, AppLock6Activity::class.java)
                    intent.putExtra("FRAGMENT_LOCK", "MAIN_ACTIVITY")
                    startActivity(intent)
                } else {
                    val intent = Intent(this, AppLockCustPassActivity::class.java)
                    intent.putExtra("FRAGMENT_LOCK", "MAIN_ACTIVITY")
                    startActivity(intent)
                }
            } else {
                // App lock is enabled but some information is missing
                val errorMessage = "App lock configuration is incomplete."

                showToast(errorMessage)
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }


    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_container)
        val appBarConfiguration = AppBarConfiguration(navController.graph)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun onUserLogin() {
        val sharedPreferences = getSharedPreferences("GamificationPref", Context.MODE_PRIVATE)
        val currentDay = LocalDate.now().toString()

        val lastLoginDate = sharedPreferences.getString("LastLoginDate", "")
        val loggedInDays = sharedPreferences.getInt("LoggedInDays", 0)
        val consecutiveLogins = sharedPreferences.getInt("ConsecutiveLogins", 0)

        Log.e("LastLoginDate", lastLoginDate.toString())
        Log.e("LoggedInDays", loggedInDays.toString())
        Log.e("ConsecutiveLogins", consecutiveLogins.toString())

        if (loggedInDays < 5){
            if (currentDay != lastLoginDate) {
                // New login for the day
                sharedPreferences.edit {
                    putString("LastLoginDate", currentDay)
                    putInt("LoggedInDays", loggedInDays + 1)
                    putInt("ConsecutiveLogins", consecutiveLogins + 1)

                }
            } else {
                // Same day login, check for consecutive logins
                sharedPreferences.edit {
                    putInt("ConsecutiveLogins", consecutiveLogins.coerceAtLeast(1)) // Keep at least 1 for today
                }
            }

            // Check for consecutive logins reaching the goal
            val updatedConsecutiveLogins = sharedPreferences.getInt("ConsecutiveLogins", 0)
            if (updatedConsecutiveLogins >= 5) {
                // Award the badge or level up the user
            }
        }

    }



}
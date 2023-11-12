package my.edu.tarc.moneymate

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
    }
}
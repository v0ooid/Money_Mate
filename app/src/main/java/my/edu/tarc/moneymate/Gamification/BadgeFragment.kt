package my.edu.tarc.moneymate.Gamification

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import my.edu.tarc.moneymate.R
import my.edu.tarc.moneymate.databinding.FragmentBadgeBinding

class BadgeFragment : Fragment() {

    private var _binding: FragmentBadgeBinding? = null
    private val binding get() = _binding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentBadgeBinding.inflate(inflater, container, false)


        binding.btnBackMAccount.setOnClickListener{
            val navController = findNavController()
            navController.popBackStack()

        }

        val sharedPreferences = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val userLevel = sharedPreferences.getInt("Level", 1)
        showBadgesForLevel(userLevel)

        binding.contraintLevel1.setOnClickListener {
            if (userLevel >= 1) {
                // Update badge status and save it in SharedPreferences
                setBadgeStatus(1)
            }
        }

        binding.contraintLevel2.setOnClickListener {
            if (userLevel >= 2) {
                // Update badge status and save it in SharedPreferences
                setBadgeStatus(2)
            }
        }

        binding.contraintLevel3.setOnClickListener {
            if (userLevel >= 3) {
                // Update badge status and save it in SharedPreferences
                setBadgeStatus(3)
            }
        }


        return binding.root
    }

    private fun setBadgeStatus(badgeLevel: Int) {
        val sharedPreferences = requireContext().getSharedPreferences("GamificationPref", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        // Save equipped badge status based on badgeLevel
        when (badgeLevel) {
            1 -> {
                editor.putInt("BadgeEquiped", R.drawable.lv1)
                binding.tvLevel1Equip.text = "Equipped"
                binding.tvLevel2Equip.text = "Equip"
                binding.tvLevel3Equip.text = "Equip"
            }
            2 -> {
                editor.putInt("BadgeEquiped", R.drawable.lv2)
                binding.tvLevel1Equip.text = "Equip"
                binding.tvLevel2Equip.text = "Equipped"
                binding.tvLevel3Equip.text = "Equip"
            }
            3 -> {
                editor.putInt("BadgeEquiped", R.drawable.lv3)
                binding.tvLevel1Equip.text = "Equip"
                binding.tvLevel2Equip.text = "Equip"
                binding.tvLevel3Equip.text = "Equipped"
            }
        }

        editor.apply()
    }

    fun showBadgesForLevel(levelAchieved: Int) {
        when (levelAchieved) {
            1 -> {
                binding.contraintLevel1.visibility = View.VISIBLE
                binding.tvLevel1.visibility = View.VISIBLE
                binding.ivLevel1.visibility = View.VISIBLE

                binding.contraintLevel2.visibility = View.GONE
                binding.tvLevel2.visibility = View.GONE
                binding.ivLevel2.visibility = View.GONE

                binding.contraintLevel3.visibility = View.GONE
                binding.tvLevel3.visibility = View.GONE
                binding.ivLevel3.visibility = View.GONE

                binding.tvEmptyBadge.visibility = View.GONE

            }
            2 -> {
                binding.contraintLevel1.visibility = View.VISIBLE
                binding.tvLevel1.visibility = View.VISIBLE
                binding.ivLevel1.visibility = View.VISIBLE

                binding.contraintLevel2.visibility = View.VISIBLE
                binding.tvLevel2.visibility = View.VISIBLE
                binding.ivLevel2.visibility = View.VISIBLE

                binding.contraintLevel3.visibility = View.GONE
                binding.tvLevel3.visibility = View.GONE
                binding.ivLevel3.visibility = View.GONE

                binding.tvEmptyBadge.visibility = View.GONE
            }
            3 -> {
                binding.contraintLevel1.visibility = View.VISIBLE
                binding.tvLevel1.visibility = View.VISIBLE
                binding.ivLevel1.visibility = View.VISIBLE

                binding.contraintLevel2.visibility = View.VISIBLE
                binding.tvLevel2.visibility = View.VISIBLE
                binding.ivLevel2.visibility = View.VISIBLE

                binding.contraintLevel3.visibility = View.VISIBLE
                binding.tvLevel3.visibility = View.VISIBLE
                binding.ivLevel3.visibility = View.VISIBLE

                binding.tvEmptyBadge.visibility = View.GONE
            }
            else -> {
                binding.levelBadgeContraint.visibility = View.GONE

                binding.contraintLevel1.visibility = View.GONE
                binding.tvLevel1.visibility = View.GONE
                binding.ivLevel1.visibility = View.GONE

                binding.contraintLevel2.visibility = View.GONE
                binding.tvLevel2.visibility = View.GONE
                binding.ivLevel2.visibility = View.GONE

                binding.contraintLevel3.visibility = View.GONE
                binding.tvLevel3.visibility = View.GONE
                binding.ivLevel3.visibility = View.GONE

                binding.tvEmptyBadge.visibility = View.VISIBLE
            }
        }
    }

}
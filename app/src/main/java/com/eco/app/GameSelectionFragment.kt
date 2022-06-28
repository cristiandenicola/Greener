package com.eco.app

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.eco.app.databinding.FragmentGameSelectionBinding
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

/**
 * A simple [Fragment] subclass.
 */
class GameSelectionFragment : Fragment() {
    private lateinit var binding: FragmentGameSelectionBinding
    private lateinit var currentUser: FirebaseUser

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding= FragmentGameSelectionBinding.inflate(inflater,container,false)

        val navController=findNavController()

        binding.trashBinPlayBtn.setOnClickListener {
             navController.navigate(GameSelectionFragmentDirections.actionGameSelectionFragmentToTrashBinGame())
        }

        binding.garbageSorterPlayBtn.setOnClickListener {
            navController.navigate(GameSelectionFragmentDirections.actionGameSelectionFragmentToGarbageSorterGame())
        }

        binding.quizPlayBtn.setOnClickListener {
            navController.navigate(GameSelectionFragmentDirections.actionGameSelectionFragmentToQuizFragment())
        }

        binding.growingTreePlayBtn.setOnClickListener {
            navController.navigate(GameSelectionFragmentDirections.actionGameSelectionFragmentToGrowingTreeFragment())
        }

        return binding.root
    }

}
package com.eco.app

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.PermissionChecker
import androidx.core.content.PermissionChecker.checkSelfPermission
import com.eco.app.databinding.ActivityProfileBinding
import com.eco.app.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase

class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding
    private  lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase

    val register = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK) {
            Toast.makeText(requireContext(), "APPOSTO REGISTER LAUNCHER", Toast.LENGTH_SHORT).show()
            val imguri: Uri? = it.data?.data
            binding.imgProfile.setImageURI(imguri)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=FragmentProfileBinding.inflate(inflater,container,false)
        auth = Firebase.auth
        val user = auth.currentUser;
        database = Firebase.database(RegisterPage.PATHTODB)
        val usersReference = database.getReference("Users")

        if(user==null){
            Log.i("LoginInfo","Non sei loggato")
        }else{
            val UID = user.uid
            getInfos(usersReference,UID)
            checkPermissionForImage()
            binding.imgProfile.setOnClickListener {
                pickImage()
            }
        }

        return binding.root
    }

    private fun getInfos(usersReference : DatabaseReference, UID : String) {
        usersReference.child(UID).get().addOnSuccessListener {
            val username : CharSequence = it.child("username").value as CharSequence
            val binScore : Long= it.child("bin_score").value as Long
            val quizScore : Long = it.child("quiz_score").value as Long
            val carbonFootprint : Long = it.child("carbon_footprint").value as Long
            binding.tvName.text = username
            binding.tvQuizscore1.text = quizScore.toString()
            binding.tvTrashscore1.text = binScore.toString()
            binding.tvCarbon1.text = carbonFootprint.toString()

        }.addOnFailureListener{
            Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkPermissionForImage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if ((checkSelfPermission(requireContext(),Manifest.permission.READ_EXTERNAL_STORAGE) == PermissionChecker.PERMISSION_DENIED)
                && (checkSelfPermission(requireContext(),Manifest.permission.WRITE_EXTERNAL_STORAGE) == PermissionChecker.PERMISSION_DENIED)
            ) {
                val permission = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
                val permissionCoarse = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)

                requestPermissions(
                    permission,
                    1001
                ) // GIVE AN INTEGER VALUE FOR PERMISSION_CODE_READ LIKE 1001
                requestPermissions(
                    permissionCoarse,
                    1002
                ) // GIVE AN INTEGER VALUE FOR PERMISSION_CODE_WRITE LIKE 1002
            } else {
                //Toast.makeText(requireContext(), "Permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun pickImage() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        register.launch(intent)
    }

}
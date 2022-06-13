package com.eco.app

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.eco.app.databinding.FragmentRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class RegisterFragment : Fragment() {
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var binding: FragmentRegisterBinding

    companion object{
        val PATHTODB="https://ecoapp-706b8-default-rtdb.europe-west1.firebasedatabase.app/"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=FragmentRegisterBinding.inflate(inflater,container,false)

        //assegno l'oggetto grafico della UI alla variabile
        val txtLogIn = binding.txtLogIn

        //metodo onClick della txtSignUp per far diventare la txt un link per la activity RegisterPage
        txtLogIn.setOnClickListener {
            findNavController().navigate(RegisterFragmentDirections.actionFromRegisterToLogin())
        }
        auth = FirebaseAuth.getInstance()
        database = Firebase.database(RegisterFragment.PATHTODB)

        binding.btnRegister.setOnClickListener {
            createAccount()
        }

        return binding.root
    }

    fun createAccount() {
        if(auth.currentUser==null){
            val name = binding.edtNome.text.toString()
            val email = binding.edtEmail.text.toString()
            val psw = binding.edtPsw.text.toString()

            if (name.equals("")) {
                binding.edtNome.setError("Check name")
                return
            }
            if (email.equals("")) {
                binding.edtEmail.setError("Check mail")
                return
            }

            if (psw.equals("")) {
                binding.edtPsw.setError("Check password")
                return
            }

            auth.createUserWithEmailAndPassword(email, psw).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(requireContext(), "Utente registrato correttamente", Toast.LENGTH_SHORT)
                        .show()
                    val userUid = task.result.user!!.uid
                    val usersReference = database.getReference("Users")
                    usersReference.child(userUid).child("username").setValue(name)
                    usersReference.child(userUid).child("quiz_score").setValue(0)
                    usersReference.child(userUid).child("bin_score").setValue(0)
                    usersReference.child(userUid).child("carbon_footprint").setValue(0)
                } else {
                    Toast.makeText(requireContext(), "Errore nella registrazione", Toast.LENGTH_SHORT).show()
                }
            }
        }else{
            Toast.makeText(requireContext(), "Sei gia loggato", Toast.LENGTH_SHORT).show()
            Log.d("Register","GIA LOGGATO")
        }
    }

}
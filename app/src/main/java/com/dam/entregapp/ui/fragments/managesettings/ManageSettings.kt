package com.dam.entregapp.ui.fragments.managesettings

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.dam.entregapp.LocationApp.Companion.prefs
import com.dam.entregapp.R
import com.dam.entregapp.data.model.User
import com.dam.entregapp.databinding.FragmentManageSettingsBinding
import com.dam.entregapp.ui.MainActivity
import com.dam.entregapp.ui.viewmodels.UserViewModel
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class ManageSettings : Fragment(R.layout.fragment_manage_settings) {

    private lateinit var userViewModel: UserViewModel
    private var name = ""
    private var email = ""
    private var password = ""
    private var telephone = ""

    private var _binding: FragmentManageSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentManageSettingsBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)

        binding.btnGuardar.setOnClickListener {
            updateUserDB()
        }

/*        binding.btnTemporal.setOnClickListener {
            addUserDB()
        }*/


    }

    private fun updateUserDB() {
        //Get text from editTexts
        name = binding.name.text.toString()
        email = binding.email.text.toString()
        password = binding.password.text.toString()
        telephone = binding.telephone.text.toString()

        //Check that the form is complete before submitting data to the database
        if (!(name.isEmpty() || email.isEmpty() || password.isEmpty() || telephone.isEmpty())) {
            val user = User(prefs.getCurrentUserID(), name, email, password, telephone.toInt())

            //add the user if all the fields are filled

            //TODO actualizar online tambien
/*
            val userFirebase = Firebase.auth.currentUser
            val newPassword = password
            try {
                userFirebase!!.updatePassword(newPassword)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Log.d("FirebaseUpdate", "User password updated.")
                        } else {
                            Log.d("FirebaseUpdate", "User password not updated.")
                        }
                    }
            }catch (e: FirebaseAuthRecentLoginRequiredException) {
                val credential = EmailAuthProvider
                    .getCredential(prefs.getEmail(), "alfredo")

                // Prompt the user to re-provide their sign-in credentials
                userFirebase?.reauthenticate(credential)
                    ?.addOnCompleteListener { Log.d("FirebaseUpdate", "User re-authenticated.") }
                Log.d("FirebaseUpdate", "$e")
            }*/
            userViewModel.updateUser(user)
            Toast.makeText(context, "Data updated successfully!", Toast.LENGTH_SHORT).show()



            //navigate back to our home fragment
            findNavController().navigate(R.id.action_manageSettings_to_mainMenu)
        } else {
            Toast.makeText(context, "Please fill all the fields", Toast.LENGTH_SHORT).show()
        }
    }

    //TODO crear para que?
/*    private fun addUserDB() {
        //Get text from editTexts
        name = binding.name.text.toString()
        email = binding.email.text.toString()
        password = binding.password.text.toString()
        telephone = binding.telephone.text.toString().toInt()

        //Check that the form is complete before submitting data to the database
        if (!(name.isEmpty() || email.isEmpty() || password.isEmpty() || telephone == 0)) {
            val user = User(0, name, email, password, telephone)

            //add the user if all the fields are filled
            userViewModel.addUser(user)
            Toast.makeText(context, "Data updated successfully!", Toast.LENGTH_SHORT).show()

            //navigate back to our home fragment
            findNavController().navigate(R.id.action_manageSettings_to_mainMenu)
        } else {
            Toast.makeText(context, "Please fill all the fields", Toast.LENGTH_SHORT).show()
        }
    }*/
}
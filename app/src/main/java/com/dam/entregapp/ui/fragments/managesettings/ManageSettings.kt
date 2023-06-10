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
import com.dam.entregapp.location.LocationApp.Companion.prefs
import com.dam.entregapp.R
import com.dam.entregapp.data.model.User
import com.dam.entregapp.databinding.FragmentManageSettingsBinding
import com.dam.entregapp.firestore.FirestoreUser
import com.dam.entregapp.ui.viewmodels.UserViewModel
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase


class ManageSettings : Fragment(R.layout.fragment_manage_settings) {

    private lateinit var userViewModel: UserViewModel
    private var oldpassword = ""
    private var newpassword = ""
    private var newpassword2 = ""
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

        binding.btnCambioContrasena.setOnClickListener {
            updateUserDB()
        }

        binding.btnGuardarTelefono.setOnClickListener {
            updateTelefono()
        }
    }

    private fun updateTelefono() {
        telephone = binding.telephone.editText?.text.toString()
        if (!(telephone.isEmpty())) {
            val user =
                User(prefs.getCurrentUserID(), prefs.getName(), prefs.getEmail(), "no necesario" ,telephone.toInt())
            //add the user if all the fields are filled
            userViewModel.addUser(user)

            val userFirestore = FirestoreUser(prefs.getEmail(),prefs.getName(),telephone)
            FirebaseAuth.getInstance().currentUser?.let {
                FirebaseFirestore.getInstance().collection("users").document(it.uid).set(userFirestore)
            }
            Toast.makeText(context, "Número de teléfono actualizado correctamente", Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.action_manageSettings_to_mainMenu)
        }
        else{
            Toast.makeText(context, "Introduce un número de teléfono", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateUserDB() {
        //Get text from editTexts
        oldpassword = binding.oldPassword.editText?.text.toString()
        newpassword = binding.newPassword.editText?.text.toString()
        newpassword2 = binding.newPassword2.editText?.text.toString()

        //Check that the form is complete before submitting data to the database
        if (!(oldpassword.isEmpty() || newpassword.isEmpty() || newpassword2.isEmpty())) {
            if (newpassword == newpassword2) {
                val user =
                    User(prefs.getCurrentUserID(), prefs.getName(), prefs.getEmail(), newpassword,prefs.getPhone().toInt())
                //add the user if all the fields are filled
                userViewModel.addUser(user)
                val userFirebase = Firebase.auth.currentUser
                val newPassword = newpassword

                try {
                    if (userFirebase != null) {
                        val credential =
                            EmailAuthProvider.getCredential(userFirebase.email!!, oldpassword)

                        userFirebase.reauthenticate(credential)
                            .addOnCompleteListener { reauthTask ->
                                if (reauthTask.isSuccessful) {
                                    // Re-authentication successful, update the password
                                    userFirebase.updatePassword(newPassword)
                                        .addOnCompleteListener { updateTask ->
                                            if (updateTask.isSuccessful) {
                                                Log.d("FirebaseUpdate", "User password updated.")
                                                Toast.makeText(context, "Contraseña actualizada correctamente", Toast.LENGTH_SHORT).show()
                                                findNavController().navigate(R.id.action_manageSettings_to_mainMenu)
                                            } else {
                                                Toast.makeText(context, "Error: ${updateTask.exception?.message}", Toast.LENGTH_SHORT).show()
                                                Log.d(
                                                    "FirebaseUpdate",
                                                    "User password not updated. Error: ${updateTask.exception?.message}"
                                                )
                                            }
                                        }
                                } else {
                                    Toast.makeText(context, "Error: ${reauthTask.exception?.message}", Toast.LENGTH_SHORT).show()
                                    Log.d(
                                        "FirebaseUpdate",
                                        "User re-authentication failed. Error: ${reauthTask.exception?.message}"
                                    )
                                }
                            }
                    } else {
                        Log.d("FirebaseUpdate", "User is null.")
                    }
                } catch (e: Exception) {
                    Log.e("FirebaseUpdate", "Error updating user password: ${e.message}")
                }

            } else {
                Toast.makeText(context, "La nuevas contraseñas no coinciden", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(context, "Rellena todos los campos", Toast.LENGTH_SHORT).show()
        }
    }
}
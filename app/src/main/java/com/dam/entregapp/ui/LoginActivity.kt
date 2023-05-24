package com.dam.entregapp.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.dam.entregapp.data.model.User
import com.dam.entregapp.databinding.ActivityLoginBinding
import com.dam.entregapp.firestore.FirestoreUser
import com.dam.entregapp.ui.viewmodels.LogInViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class LoginActivity : AppCompatActivity() {
    private lateinit var logInViewModel: LogInViewModel
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        logInViewModel = ViewModelProvider(this).get(LogInViewModel::class.java)
        setup()
    }

    private fun setup() {

        binding.btnLogin.setOnClickListener {
            val emailText = binding.email.text.toString()
            val passwordText = binding.password.text.toString()

            if (emailText.isEmpty() || passwordText.isEmpty()) {
                Toast.makeText(this, "Por favor, introduce tus datos", Toast.LENGTH_SHORT).show()
            } else {
                //LogIn en FireBase
                FirebaseAuth.getInstance().signInWithEmailAndPassword(
                    binding.email.text.toString(),
                    binding.password.text.toString()
                ).addOnCompleteListener {
                    if (it.isSuccessful) {
                        userFromFirestore()
                    } else {
                        showAlert()
                    }
                }
            }
        }

        binding.registro.setOnClickListener {
            // abrimos la actividad de registro
            val a = Intent(this, RegisterActivity::class.java)
            startActivity(a)
        }

    }

    private fun userFromFirestore() = CoroutineScope(Dispatchers.IO).launch {
        try {

            FirebaseAuth.getInstance().currentUser?.let { it ->
                FirebaseFirestore.getInstance().collection("users").document(it.uid).get()
                    .addOnSuccessListener { documentSnapshot ->
                        val firestoreUser = documentSnapshot.toObject<FirestoreUser>()
                        Log.d("LogIn", "Usuario de firestore: $firestoreUser")
                        if (firestoreUser != null) {

                            checkIfUserAlreadyExist(firestoreUser)
                        }
                    }
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(this@LoginActivity, e.message, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun checkIfUserAlreadyExist(firestoreUser: FirestoreUser) =
        CoroutineScope(Dispatchers.IO).launch {
            var userID = logInViewModel.getUserID(firestoreUser.email)
            if (userID.isEmpty()) {
                userToDB(firestoreUser)
            } else {
                showHome(firestoreUser.email)
            }

        }

    private fun userToDB(firestoreUser: FirestoreUser) {
        //TODO eliminar contraseña probablemente del usuario
        val user =
            User(
                0,
                firestoreUser.username,
                firestoreUser.email,
                "no es necesario",
                firestoreUser.phone.toInt()
            )

        lifecycleScope.launch {
            logInViewModel.addUser(user) // Call the suspend function
            showHome(firestoreUser.email)
        }

        Log.d("LogIn", "Usuario a la base de datos local: $user")

    }

    private fun showAlert() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage("Se ha producido un error autenticando al ususario")
        builder.setPositiveButton("Aceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun showHome(email: String) {

        val homeIntent = Intent(this, MainActivity::class.java).apply {
            putExtra("email", email)
        }
        startActivity(homeIntent)
    }
}
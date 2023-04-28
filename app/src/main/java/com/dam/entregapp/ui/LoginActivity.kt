package com.dam.entregapp.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.dam.entregapp.data.model.User
import com.dam.entregapp.databinding.ActivityLoginBinding
import com.dam.entregapp.firestore.FirestoreUser
import com.dam.entregapp.ui.viewmodels.LogInViewModel
import com.dam.entregapp.ui.viewmodels.RegisterViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
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
        //val screenSplash = installSplashScreen()

        //Splash
        //Thread.sleep(300)
        //screenSplash.setKeepOnScreenCondition{false}

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
                        //TODO Hacer que al iniciar sesion tamien se cree en local el usuario

                        userFromFirestore()
                        showHome(it.result?.user?.email.toString())
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
                            userToDB(firestoreUser)
                        }
                    }
            }

            /*FirebaseAuth.getInstance().currentUser?.let { it ->
                val querySnapshot =
                    FirebaseFirestore.getInstance().collection("users").get().await()
                for (document in querySnapshot.documents) {
                    Log.d("LogIn", "Usuario de firestore: $document")
                }
            }*/


            /*FirebaseAuth.getInstance().currentUser?.let { it ->
                FirebaseFirestore.getInstance().collection("users").document(it.uid).get()
                    .addOnSuccessListener {

                        val firestoreUser = it.toObject(FirestoreUser::class.java)

                        Log.d("LogIn", "Usuario de firestore: $firestoreUser")
                        if (firestoreUser != null) {
                            userToDB(
                                firestoreUser.email,
                                firestoreUser.username,
                                firestoreUser.phone
                            )
                        }
                    }
            }*/
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(this@LoginActivity, e.message, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun userToDB(firestoreUser: FirestoreUser) {
        var username = firestoreUser.username
        val user =
                    User(0, firestoreUser.username, firestoreUser.email, "no es necesario", firestoreUser.phone.toInt())

        logInViewModel.addUser(user)

        Log.d("LogIn", "Usuario a la base de datos local: $user")

    }

    /*    private fun userFromFirestore() {
            FirebaseAuth.getInstance().currentUser?.let {
                FirebaseFirestore.getInstance().collection("users")
                    .document(it.uid)
                    .get(firestoreUser)
                    .addOnSuccessListener {
                        Log.d("Registro", "Usuario agregado a Firestore")
                    }
                    .addOnFailureListener { e ->
                        Log.e("Registro", "Error al agregar usuario a Firestore", e)
                    }
            }
        }*/

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
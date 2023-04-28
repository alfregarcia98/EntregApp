package com.dam.entregapp.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.dam.entregapp.data.model.User
import com.dam.entregapp.databinding.ActivityRegisterBinding
import com.dam.entregapp.firestore.FirestoreUser
import com.dam.entregapp.ui.viewmodels.RegisterViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class RegisterActivity : AppCompatActivity() {
    private lateinit var registerViewModel: RegisterViewModel
    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        registerViewModel = ViewModelProvider(this).get(RegisterViewModel::class.java)

        setup()
    }

    private fun setup() {

        binding.btnLoginNow.setOnClickListener {
            finish()
        }

        binding.btnRegistrarCorreo.setOnClickListener {
            registerUser()
        }
    }

    private fun showAlert() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage("Se ha producido un error registrando al ususario")
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

    private fun registerUser() {
        val usuarioText = binding.usuario.text.toString()
        val phoneText = binding.phone.text.toString()
        val emailText = binding.email.text.toString()
        val contrasenaText = binding.contrasena.text.toString()
        val contrasena2Text = binding.contrasena2.text.toString()

        // comprobamos que se rellenan todos los campos antes de mandarlo a la base de datos
        if (usuarioText.isEmpty() || phoneText.isEmpty() || emailText.isEmpty() || contrasenaText.isEmpty()) {
            Toast.makeText(this, "Rellene todos los campos", Toast.LENGTH_LONG).show()
        } else if (!contrasenaText.equals(contrasena2Text)) {
            Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
            //TODO comprobar los campos mejor, entre ellos el numero de telefono
        } else {
            Log.d("Registro", "Antes de registrar")
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(emailText, contrasenaText)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        val user =
                            User(0, usuarioText, emailText, contrasenaText, phoneText.toInt())
                        registerViewModel.addUser(user)

                        //Almacenamos el usuario en firestore
                        //addUserToFirestore(usuarioText, phoneText)

                        val userFirestore = FirestoreUser(emailText,usuarioText,phoneText)
                        saveUser(userFirestore)


                        Log.d("Registro", "Pues parece que funciona")
                        showHome(it.result?.user?.email.toString())
                    } else {
                        showAlert()
                    }
                }
        }
    }


    private fun saveUser(user: FirestoreUser) = CoroutineScope(Dispatchers.IO).launch {
        try {
            FirebaseAuth.getInstance().currentUser?.let {
                FirebaseFirestore.getInstance().collection("users").add(user).await()
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@RegisterActivity,
                        "Successfully saved data.",
                        Toast.LENGTH_LONG
                    )
                        .show()
                }
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(this@RegisterActivity, e.message, Toast.LENGTH_LONG).show()
            }
        }
    }
/*
    private fun addUserToFirestore(usuarioText: String, phoneTxt: String) {
        //val firestoreUser = FirebaseAuth.getInstance().currentUser?.let { it1 -> FirestoreUser(emailText,usuarioText,phoneText, it1.uid) }

        val firestoreUser = hashMapOf(
            "email" to (FirebaseAuth.getInstance().currentUser?.email ?: ""),
            "username" to usuarioText,
            "phone" to phoneTxt,
            "uid" to (FirebaseAuth.getInstance().currentUser?.uid ?: "")
        )

        FirebaseAuth.getInstance().currentUser?.let {
            FirebaseFirestore.getInstance().collection("users")
                .document(it.uid)
                .set(firestoreUser)
                .addOnSuccessListener {
                    Log.d("Registro", "Usuario agregado a Firestore")
                }
                .addOnFailureListener { e ->
                    Log.e("Registro", "Error al agregar usuario a Firestore", e)
                }
        }
    }*/
}
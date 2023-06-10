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

    private fun showHome(email: String) {

        val homeIntent = Intent(this, MainActivity::class.java).apply {
            putExtra("email", email)
        }
        startActivity(homeIntent)
    }

    private fun registerUser() {
        val usuarioText = binding.usuario.editText?.text.toString()
        val phoneText = binding.phone.editText?.text.toString()
        val emailText = binding.email.editText?.text.toString()
        val contrasenaText = binding.contrasena.editText?.text.toString()
        val contrasena2Text = binding.contrasena2.editText?.text.toString()

        // comprobamos que se rellenan todos los campos antes de mandarlo a la base de datos
        if (usuarioText.isEmpty() || phoneText.isEmpty() || emailText.isEmpty() || contrasenaText.isEmpty()) {
            Toast.makeText(this, "Rellene todos los campos", Toast.LENGTH_LONG).show()
        } else if (contrasenaText.length < 6) {
            Toast.makeText(this, "Contraseña min. 6 caracteres", Toast.LENGTH_SHORT).show()
        } else if (!contrasenaText.equals(contrasena2Text)) {
            Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
            //TODO comprobar los campos mejor, entre ellos el numero de telefono
        } else {
            Log.d("Registro", "Antes de registrar")
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(emailText, contrasenaText)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val user =
                            User(0, usuarioText, emailText, contrasenaText, phoneText.toInt())
                        registerViewModel.addUser(user)

                        //Almacenamos el usuario en firestore
                        val userFirestore = FirestoreUser(emailText, usuarioText, phoneText)
                        saveUser(userFirestore)
                        showHome(task.result?.user?.email.toString())
                    } else {
                        Log.d("Registro", "createUserWithEmail:failure", task.exception)
                        Toast.makeText(
                            baseContext,
                            "Register Failed: ${task.exception?.message}",
                            Toast.LENGTH_SHORT,
                        ).show()
                    }
                }

        }
    }

    private fun saveUser(user: FirestoreUser) = CoroutineScope(Dispatchers.IO).launch {
        try {
            FirebaseAuth.getInstance().currentUser?.let {
                FirebaseFirestore.getInstance().collection("users").document(it.uid).set(user)
            }

        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(this@RegisterActivity, e.message, Toast.LENGTH_LONG).show()
            }
        }
    }
}
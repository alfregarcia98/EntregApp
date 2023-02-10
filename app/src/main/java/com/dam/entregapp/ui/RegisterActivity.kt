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
import com.dam.entregapp.ui.viewmodels.RegisterViewModel
import com.google.firebase.auth.FirebaseAuth

class RegisterActivity : AppCompatActivity() {
    private lateinit var registerViewModel: RegisterViewModel
    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        registerViewModel = ViewModelProvider(this).get(RegisterViewModel::class.java)
        /**        val user = User(null,"alfredo","a@gmail.com","hola",665)
        registerViewModel.addUser(user)
         **/

        setup()
    }

    private fun setup() {

        binding.btnLoginNow.setOnClickListener {
            finish()
        }

        binding.btnRegistrarCorreo.setOnClickListener {
            userToDB()
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

    private fun showHome(email: String, provider: ProviderType) {

        val homeIntent = Intent(this, MainActivity::class.java).apply {
            putExtra("email", email)
            putExtra("provider", provider.name)
        }
        startActivity(homeIntent)
    }

    private fun userToDB() {
        val usuarioText = binding.usuario.text.toString()
        val phoneText = binding.phone.text.toString()
        val emailText = binding.email.text.toString()
        val contrasenaText = binding.contrasena.text.toString()
        val contrasena2Text = binding.contrasena2.text.toString()

        Log.d("Registro", "Al hacer click")

        // comprobamos que se rellenan todos los campos antes de mandarlo a la base de datos
        if (usuarioText.isEmpty() || phoneText.isEmpty() || emailText.isEmpty() || contrasenaText.isEmpty()) {
            Toast.makeText(this, "Rellene todos los campos", Toast.LENGTH_LONG).show()
        } else if (!contrasenaText.equals(contrasena2Text)) {
            Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
        } else {
            Log.d("Registro", "Antes de registrar")
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(emailText, contrasenaText)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        val user =
                            User(null, usuarioText, emailText, contrasenaText, phoneText.toInt())
                        registerViewModel.addUser(user)
                        Log.d("Registro", "Pues parece que funciona")
                        showHome(it.result?.user?.email.toString(), ProviderType.BASIC)
                    } else {
                        showAlert()
                    }
                }
        }
    }
}
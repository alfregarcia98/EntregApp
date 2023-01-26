package com.dam.entregapp.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.dam.entregapp.databinding.ActivityLoginBinding
import com.dam.entregapp.databinding.ActivitySigninBinding
import com.google.firebase.auth.FirebaseAuth

class SigninActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySigninBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySigninBinding.inflate(layoutInflater)

        setup()
    }

    private fun setup(){

        binding.btnLoginNow.setOnClickListener {
            finish()
        }

        binding.btnRegistrarCorreo.setOnClickListener {
            val usuarioText = binding.usuario.text.toString()
            val phoneText = binding.phone.text.toString()
            val emailText = binding.email.text.toString()
            val contrasenaText = binding.contrasena.text.toString()
            val contrasena2Text = binding.contrasena2.text.toString()

            // comprobamos que se rellenan todos los campos antes de mandarlo a la base de datos
            if (usuarioText.isEmpty() || phoneText.isEmpty() || emailText.isEmpty() || contrasenaText.isEmpty()) {
                Toast.makeText(this, "Rellene todos los campos", Toast.LENGTH_LONG).show()
            }else if (!contrasenaText.equals(contrasena2Text)){
                Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
            }else{
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(binding.email.text.toString(), binding.contrasena.text.toString()).addOnCompleteListener {
                    if (it.isSuccessful){
                        showHome(it.result?.user?.email.toString(), ProviderType.BASIC)
                    } else {
                        showAlert()
                    }
                }
            }
        }
    }
    private fun showAlert(){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage("Se ha producido un error registrando al ususario")
        builder.setPositiveButton("Aceptar",null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
    private fun showHome(email: String, provider: ProviderType){

        val homeIntent = Intent(this, MainActivity::class.java).apply {
            putExtra("email", email)
            putExtra("provider", provider.name)
        }
        startActivity(homeIntent)
    }
}